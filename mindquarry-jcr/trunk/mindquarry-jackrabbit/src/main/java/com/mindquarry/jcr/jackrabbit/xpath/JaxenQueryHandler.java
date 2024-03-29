/*
 * Copyright (C) 2006-2007 Mindquarry GmbH, All Rights Reserved
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package com.mindquarry.jcr.jackrabbit.xpath;

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;

import org.apache.jackrabbit.core.ItemManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.query.ExecutableQuery;
import org.apache.jackrabbit.core.query.lucene.SearchIndex;
import org.jaxen.JaxenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxenQueryHandler extends SearchIndex {

    public static final String FULL_XPATH = "full_xpath";

    private static final Logger log = LoggerFactory
            .getLogger(JaxenQueryHandler.class);

    /**
     * @see org.apache.jackrabbit.core.query.lucene.SearchIndex#setPath(java.lang.String)
     */
    @Override
    public void setPath(String path) {
        super.setPath(path);
    }

    /**
     * @see org.apache.jackrabbit.core.query.lucene.SearchIndex#createExecutableQuery(org.apache.jackrabbit.core.SessionImpl, org.apache.jackrabbit.core.ItemManager, java.lang.String, java.lang.String)
     */
    @Override
    public ExecutableQuery createExecutableQuery(SessionImpl session,
            ItemManager itemMgr, String statement, String language)
            throws InvalidQueryException {
        if (language.equals(FULL_XPATH)) {
            try {
                log.debug("Full XPath Query: " + statement);
                JackrabbitXPath xpath = new JackrabbitXPath(statement, session);
                NamespaceRegistry nsReg = namespaceRegistry(session);
                for (String uri : nsReg.getURIs()) {
                    xpath.addNamespace(nsReg.getPrefix(uri), uri);
                }
                return xpath;
            } catch (JaxenException je) {
                throw new InvalidQueryException(je);
            } catch (NamespaceException e) {
                throw new InvalidQueryException(e);
            } catch (RepositoryException e) {
                throw new InvalidQueryException(e);
            }
        } else {
            return super.createExecutableQuery(session, itemMgr, statement,
                    language);
        }
    }

    private NamespaceRegistry namespaceRegistry(Session session)
            throws RepositoryException {

        return session.getWorkspace().getNamespaceRegistry();
    }
}
