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

import eionet.webq.dto.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

/**
 * User managing controller.
 */
@Controller
@RequestMapping("/users")
public class UserController {
    /**
     * Service for user management.
     */
    @Autowired
    UserDetailsManager userManagementService;
    /**
     * Set required model parameters and returns view name.
     *
     * @param model model
     * @return view name
     */
    @RequestMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("allRoles", UserRole.values());
        return "new_user";
    }

    /**
     * Adds new user to database.
     *
     * @param userName user name
     * @param role role
     * @param model model
     * @return view name
     */
    @RequestMapping("/add")
    public String addUser(@RequestParam String userName, @RequestParam UserRole role, Model model) {
        User user = new User(userName, "", Arrays.asList(new SimpleGrantedAuthority(role.name())));
        if (userManagementService.userExists(userName)) {
            userManagementService.updateUser(user);
        } else {
            userManagementService.createUser(user);
        }
        model.addAttribute("message", "User " + userName + " added/updated with role " + role);
        return newUser(model);
    }
}
