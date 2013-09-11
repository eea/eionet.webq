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
 */
package eionet.webq.web.controller;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import eionet.webq.dto.UserRole;
import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest extends AbstractContextControllerTests {
    @Autowired
    UserDetailsManager userManagementService;

    private static final String TEST_USER = "test-user";

    @Test
    public void userAuthoritiesPageExist() throws Exception {
        request(get("/users/new"));
    }

    @Test
    public void allRolesAreInModel() throws Exception {
        request(get("/users/new")).andExpect(model().attribute("allRoles", UserRole.values()));
    }

    @Test
    public void newUserView() throws Exception {
        request(get("/users/new")).andExpect(view().name("new_user"));
    }

    @Test
    public void addNewUser() throws Exception {
        addUserWith(UserRole.DEVELOPER);

        assertUserHasOnlyOneRole(UserRole.DEVELOPER);
    }

    @Test
    public void changeUserRole() throws Exception {
        addUserWith(UserRole.DEVELOPER);
        assertUserHasOnlyOneRole(UserRole.DEVELOPER);

        addUserWith(UserRole.ADMIN);
        assertUserHasOnlyOneRole(UserRole.ADMIN);
    }

    private void addUserWith(UserRole role) throws Exception {
        request(post("/users/add").param("userName", TEST_USER).param("role", role.name()));
    }

    private void assertUserHasOnlyOneRole(UserRole role) {
        Collection<? extends GrantedAuthority> authorities = userManagementService.loadUserByUsername(TEST_USER).getAuthorities();
        assertThat(authorities.size(), equalTo(1));
        assertThat(authorities.iterator().next().getAuthority(), equalTo(role.name()));
    }
}
