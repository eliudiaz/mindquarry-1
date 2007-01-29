/**
 * Copyright (C) 2006 Mindquarry GmbH, All Rights Reserved
 */
package com.mindquarry.jcr.xml.source.helper.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.cocoon.util.WildcardMatcherHelper;
import org.apache.excalibur.source.SourceException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.mindquarry.common.index.IndexClient;
import com.mindquarry.jcr.xml.source.JCRSourceFactory;
import com.mindquarry.jcr.xml.source.handler.SAXToJCRNodesConverter;
import com.mindquarry.jcr.xml.source.helper.XMLFileSourceHelper;

/**
 * OutputStream to be used for writing to the {@link XMLFileSourceHelper}.
 * 
 * @author <a href="mailto:alexander(dot)saar(at)mindquarry(dot)com">Alexander
 *         Saar</a>
 */
public class JCROutputStream extends ByteArrayOutputStream {
    private boolean isClosed = false;

    private final Node node;

    private final Session session;

    private IndexClient iClient;

    private String uri;

    public JCROutputStream(Node node, Session session, IndexClient iClient,
            String uri) {
        this.node = node;
        this.session = session;
        this.iClient = iClient;
        this.uri = uri;
    }

    /**
     * @see java.io.ByteArrayOutputStream#close()
     */
    @Override
    public void close() throws IOException {
        if (!isClosed) {
            super.close();
            isClosed = true;
            try {
                // node.lock(true, true);
                try {
                    node.getNode("jcr:content"); //$NON-NLS-1$
                    if (isXML()) {
                        deleteChildren();
                        if (canParse()) {
                            writeXML();
                        } else {
                            throw new IOException("XML is not well-formed");
                        }
                    } else {
                        writeBinary();
                    }
                } catch (PathNotFoundException e) {
                    boolean isXML = canParse();
                    if (isXML) {
                        createXML();
                    } else {
                        createBinary();
                    }
                }
                // don't forget to commit
                // node.unlock();
                session.save();
            } catch (RepositoryException e) {
                throw new IOException("Unable to write to repository "
                        + e.getLocalizedMessage());
            }
        }
        // check if the path of the JCR source matches one of the excludes
        // patterns
        boolean index = true;
        for (String template : JCRSourceFactory.iExcludes) {
            if (WildcardMatcherHelper.match(template, uri) != null) {
                index = false;
                break;
            }
        }
        if (index) {
            // use index client to notify the indexer about the delete
            List<String> changedPaths = new ArrayList<String>();
            List<String> deletedPaths = new ArrayList<String>();
            changedPaths.add(uri);
            iClient.index(changedPaths, deletedPaths);
        }
    }

    private void createBinary() throws IOException {
        try {
            node.addNode("jcr:content", "nt:resource"); //$NON-NLS-1$ //$NON-NLS-2$
            writeBinary();
        } catch (ItemExistsException e) {
            throw new IOException("Content node already exists: "
                    + e.getLocalizedMessage());
        } catch (PathNotFoundException e) {
            throw new IOException("Path not found: " + e.getLocalizedMessage());
        } catch (NoSuchNodeTypeException e) {
            throw new IOException("Node type does not exist: "
                    + e.getLocalizedMessage());
        } catch (LockException e) {
            throw new IOException("Resource is locked: "
                    + e.getLocalizedMessage());
        } catch (VersionException e) {
            throw new IOException("Invalid version: " + e.getLocalizedMessage());
        } catch (ConstraintViolationException e) {
            throw new IOException("Constraints are violated: "
                    + e.getLocalizedMessage());
        } catch (RepositoryException e) {
            throw new IOException("Unable to write to repository: "
                    + e.getLocalizedMessage());
        }
    }

    private void createXML() throws IOException {
        try {
            node.addNode("jcr:content", "xt:document"); //$NON-NLS-1$ //$NON-NLS-2$
            writeXML();
        } catch (ItemExistsException e) {
            throw new IOException("Content node already exists: "
                    + e.getLocalizedMessage());
        } catch (PathNotFoundException e) {
            throw new IOException("Path not found: " + e.getLocalizedMessage());
        } catch (NoSuchNodeTypeException e) {
            throw new IOException("Node type does not exist: "
                    + e.getLocalizedMessage());
        } catch (LockException e) {
            throw new IOException("Resource is locked: "
                    + e.getLocalizedMessage());
        } catch (VersionException e) {
            throw new IOException("Invalid version: " + e.getLocalizedMessage());
        } catch (ConstraintViolationException e) {
            throw new IOException("Constraints are violated: "
                    + e.getLocalizedMessage());
        } catch (RepositoryException e) {
            throw new IOException("Unable to write to repository: "
                    + e.getLocalizedMessage());
        }
    }

    private void deleteChildren() throws IOException {
        // remove old content
        try {
            NodeIterator nit = node.getNode("jcr:content").getNodes(); //$NON-NLS-1$
            while (nit.hasNext()) {
                nit.nextNode().remove();
            }
        } catch (UnsupportedRepositoryOperationException e) {
            throw new IOException("Locking is not supported by repository: "
                    + e.getLocalizedMessage());
        } catch (LockException e) {
            throw new IOException("Unable to get lock: "
                    + e.getLocalizedMessage());
        } catch (AccessDeniedException e) {
            throw new IOException("Access to repository denied: "
                    + e.getLocalizedMessage());
        } catch (InvalidItemStateException e) {
            throw new IOException("Item state is invalid: "
                    + e.getLocalizedMessage());
        } catch (RepositoryException e) {
            throw new IOException("Unable to write to repository "
                    + e.getLocalizedMessage());
        }
    }

    private boolean canParse() {
        try {
            createSaxParser().parse(
                    new ByteArrayInputStream(this.toByteArray()),
                    new DefaultHandler());
        } catch (SAXException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ParserConfigurationException e) {
            return false;
        }
        return true;
    }

    private SAXParser createSaxParser() throws ParserConfigurationException,
            SAXException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        return parserFactory.newSAXParser();
    }

    private void writeXML() throws IOException {
        try {
            createSaxParser().parse(
                    new ByteArrayInputStream(this.toByteArray()),
                    new SAXToJCRNodesConverter(node));
        } catch (PathNotFoundException e) {
            throw new IOException("Path not found: " + e.getLocalizedMessage());
        } catch (SAXException e) {
            throw new SourceException("Unable to parse: ", e);
        } catch (ParserConfigurationException e) {
            throw new SourceException("Unable to configure parser: ", e);
        } catch (RepositoryException e) {
            throw new SourceException("Unable to write to repository: ", e);
        }
    }

    private void writeBinary() throws IOException {
        try {
            node.getNode("jcr:content").setProperty("jcr:data", //$NON-NLS-1$ //$NON-NLS-2$
                    new ByteArrayInputStream(this.toByteArray()));
            node.getNode("jcr:content").setProperty("jcr:mimeType",  //$NON-NLS-1$//$NON-NLS-2$
                    "application/octetstream"); //$NON-NLS-1$
            node.getNode("jcr:content").setProperty("jcr:lastModified", //$NON-NLS-1$ //$NON-NLS-2$
                    new GregorianCalendar());
        } catch (ValueFormatException e) {
            throw new IOException("Invalid value format: "
                    + e.getLocalizedMessage());
        } catch (VersionException e) {
            throw new IOException("Invalid Version" + e.getLocalizedMessage());
        } catch (LockException e) {
            throw new IOException("Resource is locked"
                    + e.getLocalizedMessage());
        } catch (ConstraintViolationException e) {
            throw new IOException("Constrains violated: "
                    + e.getLocalizedMessage());
        } catch (PathNotFoundException e) {
            throw new IOException("Path not found: " + e.getLocalizedMessage());
        } catch (RepositoryException e) {
            throw new IOException("Unable to write to repository: "
                    + e.getLocalizedMessage());
        }
    }

    private boolean isXML() throws IOException {
        try {
            return node.getNode("jcr:content").isNodeType("xt:document"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (PathNotFoundException e) {
            throw new IOException(
                    "Path not found, cannot determine content type of node: "
                            + e.getLocalizedMessage());
        } catch (RepositoryException e) {
            throw new IOException(
                    "Reading data from repository failed, cannot determine content of node: "
                            + e.getLocalizedMessage());
        }
    }

    public boolean canCancel() {
        return !isClosed;
    }

    public void cancel() throws IOException {
        if (isClosed) {
            throw new IllegalStateException("Cannot cancel: "
                    + "outputstrem is already closed");
        }
    }
}