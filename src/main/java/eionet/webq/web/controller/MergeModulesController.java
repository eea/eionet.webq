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

import eionet.webq.dao.MergeModules;
import eionet.webq.dao.orm.MergeModule;
import eionet.webq.dao.orm.util.WebQFileInfo;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;

/**
 */
@Controller
@RequestMapping("/merge")
public class MergeModulesController {
    /**
     * Edit view name.
     */
    public static final String ADD_EDIT_MERGE_MODULE_VIEW = "add_edit_merge_module";
    /**
     * Merge modules storage.
     */
    @Autowired
    private MergeModules mergeModulesStorage;

    /**
     * List all merge modules.
     *
     * @param model model
     * @return view name
     */
    @RequestMapping(value = "/modules", method = RequestMethod.GET)
    public String listMergeModules(Model model) {
        model.addAttribute("allMergeModules", mergeModulesStorage.findAll());
        return "merge_modules";
    }

    /**
     * Shows add model page.
     *
     * @param model model
     * @return view name
     */
    @RequestMapping("/module/add")
    public String addModule(Model model) {
        model.addAttribute("mergeModule", new MergeModule());
        return ADD_EDIT_MERGE_MODULE_VIEW;
    }

    /**
     * Saves merge module to storage.
     *
     * @param mergeModule module.
     * @param bindingResult result of mergeModule binding
     * @param model model
     * @param principal user principal
     * @return view name.
     */
    @RequestMapping(value = "/module/save", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute MergeModule mergeModule, BindingResult bindingResult, Model model,
                       Principal principal) {
        boolean isSave = WebQFileInfo.isNew(mergeModule);
        if (isSave && WebQFileInfo.fileIsEmpty(mergeModule.getXslFile())) {
            bindingResult.rejectValue("xslFile", "mergeFile.empty.on.save");
        }
        if (bindingResult.hasErrors()) {
            return ADD_EDIT_MERGE_MODULE_VIEW;
        }
        mergeModule.setUserName(principal.getName());
        try {
            if (isSave) {
                mergeModulesStorage.save(mergeModule);
            } else {
                mergeModulesStorage.update(mergeModule);
            }
        } catch (ConstraintViolationException e) {
            bindingResult.rejectValue("name", "merge.module.duplicate.name");
            return ADD_EDIT_MERGE_MODULE_VIEW;
        }

        model.addAttribute("message", "Module successfully created/updated.");
        return listMergeModules(model);
    }

    /**
     * Shows edit view.
     *
     * @param id module to edit
     * @param model model
     * @return view name
     */
    @RequestMapping("/module/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        MergeModule module = mergeModulesStorage.findById(id);
        model.addAttribute("mergeModule", module);
        return ADD_EDIT_MERGE_MODULE_VIEW;
    }

    /**
     * Removes modules by specified ids.
     *
     * @param modulesToRemove ids of modules
     * @param model model
     * @return view name
     */
    @RequestMapping("/modules/remove")
    public String remove(@RequestParam(required = false) int[] modulesToRemove, Model model) {
        if (modulesToRemove != null) {
            mergeModulesStorage.remove(modulesToRemove);
            model.addAttribute("message", "Selected modules successfully removed.");
        }
        return listMergeModules(model);
    }
}
