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
package com.mindquarry.auth;

import com.mindquarry.user.AbstractUserRO;


/**
 * The interface defines the administrative / management contract with
 * the Authorization component. You can create a Right representing 
 * a single operation at a resource. Multiple right provides can be
 * bundled to a profile. Rights as well as profile can be granted 
 * and withdrawn users and groups.    
 *
 * @author 
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
public interface AuthorizationAdmin extends AuthorizationCheck {
    
    public static final String ROLE = AuthorizationAdmin.class.getName();
    
    void deleteResource(String resourceUri);
    
    ActionRO createAction(String resourceUri, String operation);
    ActionRO actionBy(String resourceUri, String operation);
    
    void addAllowance(ActionRO action, AbstractUserRO user);
    void removeAllowance(ActionRO action, AbstractUserRO user);
    
    void addDenial(ActionRO action, AbstractUserRO user);
    void removeDenial(ActionRO action, AbstractUserRO user);
}

