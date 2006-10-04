/**
 * Copyright (C) 2005 MindQuarry GmbH, All Rights Reserved
 */
package com.mindquarry.persistence.xmlbeans;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.ModifiableTraversableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.TraversableSource;
import org.apache.xmlbeans.XmlObject;

import com.mindquarry.common.persistence.Session;
import com.mindquarry.persistence.xmlbeans.config.PersistenceConfiguration;
import com.mindquarry.persistence.xmlbeans.creation.XmlBeansDocumentCreator;
import com.mindquarry.persistence.xmlbeans.creation.XmlBeansEntityCreator;
import com.mindquarry.persistence.xmlbeans.reflection.EntityClassUtil;
import com.mindquarry.persistence.xmlbeans.source.JcrSourceResolverBase;

/**
 * Add summary documentation here.
 *
 * @author
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
class XmlBeansSession implements Session {
    
    private List<XmlObject> pooledEntities_;
    private List<XmlObject> deletedEntities_;
    
    private final PersistenceConfiguration configuration_;
    
    private final JcrSourceResolverBase jcrSourceResolver_;

    /**
     * @param configuration
     * @param documentCreator
     * @param entityCreator
     * @param jcrSourceResolver
     */
    XmlBeansSession(final PersistenceConfiguration configuration, 
            final JcrSourceResolverBase jcrSourceResolver) {
        
        super();
        configuration_ = configuration;
        jcrSourceResolver_ = jcrSourceResolver;
        
        pooledEntities_ = new LinkedList<XmlObject>();
        deletedEntities_ = new LinkedList<XmlObject>();
    }

    public Object newEntity(Class entityClazz) {
        validateEntityClass(entityClazz);
        XmlObject result = entityCreator().newEntityFor(entityClazz);
        pooledEntities_.add(result);
        return result;
    }

    public void persist(Object transientInstance) {
        
        Class entityClazz = entityClazz(transientInstance);        
        XmlObject xmlTransientInstance = (XmlObject) transientInstance;
                
        ModifiableSource source = resolveJcrSource(xmlTransientInstance);
        
        XmlObject entityDocument = documentCreator().newDocumentFor(
                xmlTransientInstance, entityClazz);        
        try {
            OutputStream out = source.getOutputStream();
            entityDocument.save(out, null);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new XmlBeansPersistenceException(
                    "could not write xml content to jcr source", e);
        }        
    }
    
    public boolean delete(Object object) {
        boolean isDeleted = false;
        
        if (pooledEntities_.contains(object)) {            
            XmlObject entity = (XmlObject) object;
            deletedEntities_.add(entity);
            pooledEntities_.remove(entity);
            isDeleted = true;
        }
        
        return isDeleted;
    }
    
    public void commit() {
        
        Iterator<XmlObject> pooledEntitiesIt = pooledEntities_.iterator();
        while (pooledEntitiesIt.hasNext()) {
            XmlObject entity = pooledEntitiesIt.next();
            persist(entity);
            pooledEntitiesIt.remove();
        }
        
        Iterator<XmlObject> deletedEntitiesIt = deletedEntities_.iterator();
        while (deletedEntitiesIt.hasNext()) {
            XmlObject entity = deletedEntitiesIt.next();
            ModifiableSource source = resolveJcrSource(entity);
            try {
                source.delete();
                deletedEntitiesIt.remove();
            } catch (SourceException e) {
                throw new XmlBeansPersistenceException(
                     "could not delete xml content from jcr source", e);
            }
        }
    }
    
    private XmlBeansDocumentCreator documentCreator() {
        return configuration_.getDocumentCreator();
    }
    
    private XmlBeansEntityCreator entityCreator() {
        return configuration_.getEntityCreator();
    }
    
    private String entityId(XmlObject entity) {
        Class clazz = entity.getClass();
        try {
            Method getIdMethod = clazz.getMethod("getId", new Class[0]);
            return (String) getIdMethod.invoke(entity, new Object[0]);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object> query(String queryKey, Object[] params) {
        
        List<Object> result = new LinkedList<Object>();
        
        String query = configuration_.query(queryKey);
        String preparedQuery = prepareQuery(query, params);
        
        TraversableSource source = resolveJcrSource(preparedQuery);
        
        if (source.exists()) {
            String clazzName = configuration_.queryResultClass(queryKey);            
            if (source.isCollection()) {
                for (Object child : getChildren(source)) {
                    ModifiableSource childSource = (ModifiableSource) child;
                    XmlObject entity = createEntity(childSource, clazzName);
                    result.add(entity);
                }
            } else {
                XmlObject entity = createEntity(source, clazzName);
                result.add(entity);
            }            
        }
        return result;
    }
    
    private Collection getChildren(TraversableSource parentSource) {
        try {
            return parentSource.getChildren();
        } catch (SourceException e) {
            throw new XmlBeansPersistenceException(
                    "could not get children from source: " + 
                    parentSource.getURI(), e);
        }
    }
    
    private XmlObject createEntity(Source source, String clazzName) {
        InputStream sourceIn = loadSourceContent(source);
        XmlObject result = entityCreator().newEntityFrom(sourceIn, clazzName);
        pooledEntities_.add(result);
        return result;
    }
    
    private InputStream loadSourceContent(Source source) {
        try {
            return source.getInputStream();
        } catch (SourceNotFoundException e) {
            throw new XmlBeansPersistenceException(
                    "could load content from source: " + source.getURI());
        } catch (IOException e) {
            throw new XmlBeansPersistenceException(
                    "could load content from source: " + source.getURI());
        }
    }

    private String prepareQuery(String query, Object[] params) {
        return new QueryPreparer(query, params).prepare();
    }
    
    private ModifiableTraversableSource resolveJcrSource(String path) {
        return jcrSourceResolver_.resolveJcrSource(path);
    }
    
    private ModifiableTraversableSource resolveJcrSource(XmlObject entity) {
        String entityPath = buildEntityPath(entity);
        return resolveJcrSource(entityPath);
    }
    
    private String buildEntityPath(XmlObject xmlEntity) {
        String id = entityId(xmlEntity);
        Class entityClazz = entityClazz(xmlEntity);  
        String basePath = configuration_.entityBasePath(entityClazz);
        return basePath + "/" + id;
    }
    
    private Class entityClazz(Object entity) {
        return EntityClassUtil.entityClazz(entity, configuration_);
    }
    
    private void validateEntityClass(Class entityClazz) {
        EntityClassUtil.validateEntityClass(entityClazz, configuration_);
    }
}
