/**
 * Copyright (C) 2006 MindQuarry GmbH, All Rights Reserved
 */
package com.mindquarry.jcr.source.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.xml.sax.XMLizable;
import org.xml.sax.SAXException;

import com.mindquarry.jcr.xml.source.wrapper.XMLFileSource;

/**
 * Test cases for the XMLFileSource implementation.
 * 
 * @author <a href="mailto:alexander(dot)saar(at)mindquarry(dot)com">Alexander
 *         Saar</a>
 */
public class JCRSourceTests extends JCRSourceTestBase {
    private Source source;

    public void testXMLFileRetrieval() throws ServiceException, IOException {
        source = resolveSource(BASE_URL + "users/alexander.saar");
        assertNotNull(source);
    }

    public void testReadXMLFile() throws ServiceException, IOException {
        source = resolveSource(BASE_URL + "users/alexander.saar");
        assertNotNull(source);

        InputStream is = source.getInputStream();
        assertNotNull(is);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int b;
        while ((b = is.read()) != -1) {
            os.write(b);
        }
        System.out.println(new String(os.toByteArray()));
    }

    public void testWriteXMLFile() throws ServiceException, IOException,
            TransformerConfigurationException, SAXException {
        source = resolveSource(BASE_URL + "users/alexander.saar");
        assertNotNull(source);

        OutputStream os = ((ModifiableSource)source).getOutputStream();
        assertNotNull(os);

        String newContent = "<user><name>Alexander Saar der Zweite</name></user>";
        os.write(newContent.getBytes());
        os.flush();
        os.close();

        source = (XMLFileSource) resolveSource(BASE_URL
                + "users/alexander.saar");
        assertNotNull(source);

        SAXTransformerFactory stfactory = (SAXTransformerFactory) SAXTransformerFactory
                .newInstance();
        TransformerHandler handler = stfactory.newTransformerHandler();
        handler.setResult(new StreamResult(System.out));
        ((XMLizable)source).toSAX(handler);
    }

    public void testXMLizableXMLFileSource() throws ServiceException,
            IOException, SAXException, TransformerFactoryConfigurationError,
            TransformerException {
        source = resolveSource(BASE_URL + "users/alexander.saar");
        assertNotNull(source);

        SAXTransformerFactory stfactory = (SAXTransformerFactory) SAXTransformerFactory
                .newInstance();
        TransformerHandler handler = stfactory.newTransformerHandler();
        handler.setResult(new StreamResult(System.out));
        ((XMLizable)source).toSAX(handler);
    }
}