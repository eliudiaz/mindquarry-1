/**
 * Copyright (C) 2006 Mindquarry GmbH, All Rights Reserved
 */
package com.mindquarry.user;

import java.util.Set;

import com.mindquarry.teamspace.TeamspaceRO;

/**
 * Add summary documentation here.
 *
 * @author 
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
public interface UserRO extends AbstractUserRO {

    /**
     * Getter for name.
     *
     * @return the name
     */
    String getName();
    
    /**
     * Getter for surname.
     *
     * @return the surname
     */
    String getSurname();

    /**
     * Getter for teamspaces.
     *
     * @return an unmodifiable view of the teamspaces
     */
    Set<String> getTeamspaceReferences();

    /**
     * determines if this user is a member of the specified teamspace
     */
    boolean isMemberOf(TeamspaceRO teamspace);

    /**
     * Getter for email.
     *
     * @return the email
     */
    String getEmail();
    
    /**
     * Getter for skills.
     *
     * @return the skills
     */
    String getSkills();
}