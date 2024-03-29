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
package com.mindquarry.persistence.jcr;

import static com.mindquarry.common.lang.ReflectionUtil.findMethod;
import static com.mindquarry.common.lang.ReflectionUtil.invoke;

import java.lang.reflect.Method;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;

import com.mindquarry.persistence.api.PersistenceException;

/**
 * @author
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
public class JcrNode {

    private Node node_;
    private JcrSession session_;
    
    protected JcrNode(Node node, JcrSession session) {
        node_ = node;
        session_ = session;
    }    
    
    public Node getWrappedNode() {
        return node_;
    }
    
    public void addMixin(String mixinName) {
        invoke("addMixin", node_, mixinName);
    }

    public JcrNode addNode(String relPath) {
        return new JcrNode((Node) invoke("addNode", node_, relPath), session_);
    }

    public JcrNode addNode(String relPath, String primaryNodeTypeName) {
        Object result = invoke("addNode", node_, relPath, primaryNodeTypeName);
        return new JcrNode((Node) result, session_);
    }

    public void checkout() {
        invoke("checkout", node_);
    }

    /*
    public void doneMerge(Version version) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public Version getBaseVersion() throws UnsupportedRepositoryOperationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCorrespondingNodePath(String workspaceName) throws ItemNotFoundException, NoSuchWorkspaceException, AccessDeniedException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeDefinition getDefinition() throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getIndex() throws RepositoryException {
        // TODO Auto-generated method stub
        return 0;
    }

    public Lock getLock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeType[] getMixinNodeTypes() throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }*/

    public JcrNode getNode(String relPath) {
        return new JcrNode(getNodeInternal(relPath), session_);
    }
    
    protected Node getNodeInternal(String relPath) {
        return (Node) invoke("getNode", node_, relPath);
    }

    public JcrNode findNode(String relPath) {
        JcrNode result = getNode(relPath);
        if (result == null) {
            throw new PersistenceException("could not find child: " + relPath);
        }
        return result;
    }

    public JcrNodeIterator getNodes() {
        Object result = invoke("getNodes", node_);
        return new JcrNodeIterator((NodeIterator) result, session_);
    }

    public JcrNodeIterator getNodes(String namePattern) {
        Object result = invoke("getNodes", node_, namePattern);
        return new JcrNodeIterator((NodeIterator) result, session_);
    }

    /*
    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeType getPrimaryNodeType() throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public JcrPropertyIterator getProperties() throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public JcrPropertyIterator getProperties(String namePattern) throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }*/

    public JcrProperty getProperty(String relPath) {
        Object result = invoke("getProperty", node_, relPath);
        return new JcrProperty((Property) result, session_);
    }

    public JcrPropertyIterator getReferences() {
        Object o = invoke("getReferences", node_);
        return new JcrPropertyIterator((PropertyIterator) o, session_);
    }

    public String getUUID() {
        return (String) invoke("getUUID", node_);
    }

    /*
    public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }*/

    public boolean hasNode(String relPath) {
        return (Boolean) invoke("hasNode", node_, relPath);
    }
    
/*
    public boolean hasNodes() throws RepositoryException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasProperties() throws RepositoryException {
        // TODO Auto-generated method stub
        return false;
    }
    */

    public boolean hasProperty(String relPath) {
        return (Boolean) invoke("hasProperty", node_, relPath);
    }
    /*
    public boolean holdsLock() throws RepositoryException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isCheckedOut() throws RepositoryException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isLocked() throws RepositoryException {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isNodeType(String nodeTypeName) throws RepositoryException {
        // TODO Auto-generated method stub
        return false;
    }

    public Lock lock(boolean isDeep, boolean isSessionScoped) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public NodeIterator merge(String srcWorkspace, boolean bestEffort) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public void orderBefore(String srcChildRelPath, String destChildRelPath) throws UnsupportedRepositoryOperationException, VersionException, ConstraintViolationException, ItemNotFoundException, LockException, RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public void removeMixin(String mixinName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public void restore(String versionName, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public void restore(Version version, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public void restore(Version version, String relPath, boolean removeExisting) throws PathNotFoundException, ItemExistsException, VersionException, ConstraintViolationException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public void restoreByLabel(String versionLabel, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public Property setProperty(String name, Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public Property setProperty(String name, Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public Property setProperty(String name, String[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    } */

    public JcrProperty setProperty(String name, String value) {
        Object result = invoke("setProperty", node_, name, value);
        return new JcrProperty((Property) result, session_);
    }

    /*
    public Property setProperty(String name, InputStream value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public Property setProperty(String name, boolean value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public Property setProperty(String name, double value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }*/

    public JcrProperty setProperty(String name, long value) {
        Class<?>[] paramTypes = new Class<?>[] {String.class, long.class};
        Method method = findMethod(Node.class, "setProperty", paramTypes); 
        Object result = invoke(method, node_, name, value);
        return new JcrProperty((Property) result, session_);
    }

    /*
    public Property setProperty(String name, Calendar value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }
    */

    public JcrProperty setProperty(String name, JcrNode value) {
        Class<?>[] paramTypes = new Class<?>[] {String.class, Node.class};
        Method method = findMethod(Node.class, "setProperty", paramTypes);        
        Object result = invoke(method, node_, name, value.getWrappedNode());
        return new JcrProperty((Property) result, session_);
    }
    
/*
    public Property setProperty(String name, Value value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public Property setProperty(String name, Value[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public Property setProperty(String name, String[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public Property setProperty(String name, String value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public void unlock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public void update(String srcWorkspaceName) throws NoSuchWorkspaceException, AccessDeniedException, LockException, InvalidItemStateException, RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public void accept(ItemVisitor visitor) throws RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public Item getAncestor(int depth) throws ItemNotFoundException, AccessDeniedException, RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getDepth() throws RepositoryException {
        // TODO Auto-generated method stub
        return 0;
    }
*/
    public String getName() {
        return (String) invoke("getName", node_);
    }

    public JcrNode getParent() {
        Object result = invoke("getParent", node_);
        return new JcrNode((Node) result, session_);
    }
/*
    public String getPath() throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }
    */

    public JcrSession getSession() {
        return session_;
    }

    /*
    public boolean isModified() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isNode() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSame(Item otherItem) throws RepositoryException {
        // TODO Auto-generated method stub
        return false;
    }

    public void refresh(boolean keepChanges) throws InvalidItemStateException, RepositoryException {
        // TODO Auto-generated method stub
        
    }*/

    public void remove() {
        invoke("remove", node_);
    }

    /*
    public void save() throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException, ReferentialIntegrityException, VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
        // TODO Auto-generated method stub
        
    }
    */
    
    public String toString() {
        return getName();
    }
}
