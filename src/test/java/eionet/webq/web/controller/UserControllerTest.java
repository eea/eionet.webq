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
 * The Original Code is Web Questionnaires 2
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Anton Dmitrijev
 *        Raptis Dimos
 */
package eionet.webq.web.controller;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import eionet.webq.dto.UserRole;
import eionet.webq.service.UserManagementService;
import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import static org.junit.Assert.assertFalse;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest extends AbstractContextControllerTests {
    @Autowired
    UserManagementService userManagementService;

    private static final String TEST_USER = "test-user";

    @Test
    public void userAuthoritiesPageExist() throws Exception {
        request(get("/users/view"));
    }

    @Test
    public void allRolesAreInModel() throws Exception {
        request(get("/users/new")).andExpect(model().attribute("allRoles", UserRole.values()));
    }

    @Test
    public void newUserView() throws Exception {
        request(get("/users/view")).andExpect(view().name("view_users"));
    }

    @Test
    public void addNewUser() throws Exception {
        addUserWith(TEST_USER, UserRole.DEVELOPER);

        assertUserHasOnlyOneRole(TEST_USER, UserRole.DEVELOPER);
    }

    @Test
    public void changeUserRole() throws Exception {
        addUserWith(TEST_USER, UserRole.DEVELOPER);
        assertUserHasOnlyOneRole(TEST_USER, UserRole.DEVELOPER);

        editUserTo(TEST_USER, UserRole.ADMIN);
        assertUserHasOnlyOneRole(TEST_USER, UserRole.ADMIN);
        
        deleteUser(TEST_USER);
        assertFalse("User should be deleted", UserExists(TEST_USER));
    }

    private void addUserWith(String username, UserRole role) throws Exception {
        requestWithRedirect(post("/users/add").param("userName", username).param("role", role.name()));
    }
    
    private void editUserTo(String username, UserRole role) throws Exception {
        requestWithRedirect(post("/users/edit").param("userName", username).param("role", role.name()));
    }
    
    private void deleteUser(String username) throws Exception {
        requestWithRedirect(post("/users/delete").param("userName", username));
    }
    
    private boolean UserExists(String username) throws Exception {
        return userManagementService.userExists(username);
    }

    private void assertUserHasOnlyOneRole(String username, UserRole role) {
        Collection<? extends GrantedAuthority> authorities = userManagementService.loadUserByUsername(username).getAuthorities();
        assertThat(authorities.size(), equalTo(1));
        assertThat(authorities.iterator().next().getAuthority(), equalTo(role.name()));
    }
}
