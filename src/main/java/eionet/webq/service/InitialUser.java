/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is WebQ 2.0
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.
 *
 * Contributor(s):
 */
package eionet.webq.service;

import eionet.webq.dto.UserRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

/**
 * Create the initial user.
 */
public class InitialUser {

    /** * Service for user management.  */
    private UserManagementService userManagementService;

    public void setUserManagementService(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    /** Inital username. Injected from configuration. */
    private String initialUsername;

    public void setInitialUsername(String initialUsername) {
        this.initialUsername = initialUsername;
    }

    /** Inital user's password. Injected from configuration. */
    private String initialPassword;

    public void setInitialPassword(String initialPassword) {
        this.initialPassword = initialPassword;
    }

    private Log logger = LogFactory.getLog(InitialUser.class);

    /**
     * Adds new user to database when bean is constructed. In the XML configuration
     * for the bean add the attribute init-method="createUser".
     */
    public void createUser() {
        if (initialUsername == null || initialUsername.trim().equals("")) {
            logger.info("No initial user to create");
            return;
        }
        if (!userManagementService.userExists(initialUsername)) {
            ArrayList<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<SimpleGrantedAuthority>(1);
            for (UserRole authority : UserRole.values()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority.toString()));
                break; // Only take the first as the database can't hold two roles for one user.
            }
            User userDetails = new User(initialUsername, initialPassword, grantedAuthorities);
            userManagementService.createUser(userDetails);
            logger.info("Initial user " + initialUsername + " created");
        } else {
            logger.info("Initial user " + initialUsername + " exists already");
        }
    }

}
