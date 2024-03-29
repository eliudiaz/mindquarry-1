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
package com.mindquarry.persistence.jcr.pool;

import java.util.Map;

import static com.mindquarry.persistence.jcr.Operations.READ;

import com.mindquarry.persistence.jcr.JcrNode;
import com.mindquarry.persistence.jcr.Operations;
import com.mindquarry.persistence.jcr.Persistence;

/**
 * Add summary documentation here.
 *
 * @author
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
public class Pool {

    private Persistence persistence_;
    private Map<Object, Object> store_;
    
    public Pool(Persistence persistence) {
        persistence_ = persistence;
    }
    
    public Object serve(Operations operation, Object...objects) {
        if (operation == READ) {
            JcrNode entityNode = (JcrNode) objects[0];
            String entityId = entityNode.getName();
            String entityFolder = entityNode.getParent().getName();
        }
        return null;
    }
}
