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

import eionet.webq.dto.UserRole;
import eionet.webq.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User managing controller.
 */
@Controller
@RequestMapping("/users")
public class UserController {
    
    public static final String VIEW_USERS_JSP = "view_users";
    public static final String NEW_USER_JSP = "new_user";
    public static final String EXISTING_USER_JSP = "existing_user";
    
    /**
     * Service for user management.
     */
    @Autowired
    UserManagementService userManagementService;
    
    /**
     * View for all users
     * @param model model
     * @param message
     * @return view name
     */
    @RequestMapping("/view")
    public String viewUsers(Model model, @RequestParam(required = false) String message) {
        model.addAttribute("allUsers", userManagementService.getAllUsers());
        if(message != null) model.addAttribute("message", message);
        return VIEW_USERS_JSP;
    }
    
    /**
     * Form for adding a new user
     * @param model
     * @param message
     * @return view name
     */
    @RequestMapping("/new")
    public String newUser(Model model, @RequestParam(required = false) String message) {
        model.addAttribute("allRoles", UserRole.values());
        if(message != null) model.addAttribute("message", message);
        return NEW_USER_JSP;
    }

    /**
     * Adds new user to database.
     * @param userName user name
     * @param role role
     * @param model model
     * @return view name
     */
    @RequestMapping("/add")
    public String addUser(@RequestParam String userName, @RequestParam UserRole role, Model model) {  
        if( userName.trim().equals("") ){
            model.addAttribute("message", "User's username cannot be empty");
            model.addAttribute("allRoles", UserRole.values());
            return "redirect:new";
        }
        
        User user = new User(userName, "", Arrays.asList(new SimpleGrantedAuthority(role.name())));
        if (userManagementService.userExists(userName)) {
            model.addAttribute("message", "User " + userName + " already exists");
            model.addAttribute("allRoles", UserRole.values());
            return "redirect:new";
        }
        
            
        userManagementService.createUser(user);
        model.addAttribute("message", "User " + userName + " added with role " + role);
        return "redirect:view";
          
    }
    
    /**
     * Form for editing existing user
     * @param userName
     * @param model
     * @param message
     * @return view name
     */
    @RequestMapping("/existing")
    public String existingUser(@RequestParam String userName, Model model, @RequestParam(required = false) String message) {
        model.addAttribute("allRoles", UserRole.values());
        model.addAttribute("userName", userName);
        UserDetails user = userManagementService.loadUserByUsername(userName);
        model.addAttribute("role", user.getAuthorities());
        if(message != null) model.addAttribute("message", message);
        return EXISTING_USER_JSP;
    }
    
    /**
     * Edit user
     * @param userName
     * @param role
     * @param model
     * @return view name
     */
    @RequestMapping("/edit")
    public String editUser(@RequestParam String userName, @RequestParam UserRole role, Model model) {    
        User user = new User(userName, "", Arrays.asList(new SimpleGrantedAuthority(role.name())));
        userManagementService.updateUser(user);
        model.addAttribute("message", "User " + userName + " updated with role " + role);
        return "redirect:view";
    }
    
    /**
     * Deletes user
     * @param userName
     * @param model
     * @return view name
     */
    @RequestMapping("/delete")
    public String deleteUser(@RequestParam String userName, Model model) {
        if (!userManagementService.userExists(userName)){
            model.addAttribute("message", "User " + userName + " was not deleted, because it does not exist ");
        }
        else{
            userManagementService.deleteUser(userName);
            model.addAttribute("message", "User " + userName + " deleted ");
        }
        return "redirect:view";
    }
}
