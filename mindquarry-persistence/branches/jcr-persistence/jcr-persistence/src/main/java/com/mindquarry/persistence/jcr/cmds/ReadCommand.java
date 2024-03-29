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
package com.mindquarry.persistence.jcr.cmds;

import com.mindquarry.persistence.jcr.JcrNode;
import com.mindquarry.persistence.jcr.JcrSession;
import com.mindquarry.persistence.jcr.Persistence;
import com.mindquarry.persistence.jcr.Pool;
import com.mindquarry.persistence.jcr.trafo.TransformationManager;
import com.mindquarry.persistence.jcr.trafo.Transformer;

/**
 * Add summary documentation here.
 *
 * @author 
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
class ReadCommand implements Command {

    private JcrNode entityNode_;
    private Persistence persistence_;    
        
    public void initialize(Persistence persistence, Object... objects) {
        entityNode_ = (JcrNode) objects[0];
        persistence_ = persistence;
    }
    
    /**
     * @see com.mindquarry.persistence.jcr.mapping.Command#execute(javax.jcr.Session)
     */
    public Object execute(JcrSession session) {
        Object entity;
        Pool pool = session.getPool();
        if (pool.containsEntryForNode(entityNode_)) {
            entity = pool.entityByNode(entityNode_);
        }
        else {
            String folder = entityNode_.getParent().getName();
            entity = entityTransformer(folder).readFromJcr(entityNode_);            
            pool.put(entity, entityNode_);
        }        
        return entity; 
    }
    
    private Transformer entityTransformer(String folder) {
        return getTransformationManager().entityTransformerByFolder(folder);
    }
    
    private TransformationManager getTransformationManager() {
        return persistence_.getTransformationManager();
    }
}
