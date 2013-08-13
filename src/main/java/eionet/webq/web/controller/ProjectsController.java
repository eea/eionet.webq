package eionet.webq.web.controller;

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

import eionet.webq.dao.ProjectFolders;
import eionet.webq.dto.ProjectEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;


/**
 * Spring controller to manage projects.
 *
 * @see Controller
 */
@Controller
@RequestMapping("projects")
public class ProjectsController {
    /**
     * Access to project folders.
     */
    @Autowired
    private ProjectFolders projectFolders;

    /**
     * All projects handler.
     *
     * @param model model attributes holder
     * @return view name
     */
    @RequestMapping("/")
    public String allProjects(Model model) {
        model.addAttribute("allProjects", projectFolders.getAllFolders());
        if (!model.containsAttribute("projectEntry")) {
            model.addAttribute("projectEntry", new ProjectEntry());
        }
        return "projects";
    }

    /**
     * Adds new project.
     *
     * @param project new project
     * @param bindingResult request binding to {@link ProjectEntry} result
     * @param model model attribute holder
     * @return view name
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addProject(@Valid @ModelAttribute ProjectEntry project, BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            try {
                projectFolders.save(project);
            } catch (DuplicateKeyException e) {
                bindingResult.rejectValue("projectId", "duplicate.project.id");
            }
        }
        return allProjects(model);
    }

    /**
     * Removes project by project id.
     *
     * @param projectId project id for project to be removed
     * @param model model attributes holder
     * @return view name
     */
    @RequestMapping(value = "/remove")
    public String removeProject(@RequestParam String projectId, Model model) {
        projectFolders.remove(projectId);
        return allProjects(model);
    }
}
