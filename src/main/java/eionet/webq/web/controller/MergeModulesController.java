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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 */
@Controller
@RequestMapping("/merge")
public class MergeModulesController {
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
    @RequestMapping(value = "/module/add")
    public String addModule(Model model) {
        model.addAttribute("newMergeModule", new MergeModule());
        return "add_merge_module";
    }

    /**
     * Saves merge module to storage.
     *
     * @param newMergeModel module.
     * @param bindingResult result of newMergeModel binding
     * @param model model
     * @return view name.
     */
    @RequestMapping(value = "/module/save", method = RequestMethod.POST)
    public String save(@ModelAttribute MergeModule newMergeModel, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "add_merge_module";
        }
        mergeModulesStorage.save(newMergeModel);
        model.addAttribute("message", "New module successfully saved!");
        return listMergeModules(model);
    }

    /**
     * Removes modules by specified ids.
     *
     * @param modulesToRemove ids of modules
     * @param model model
     */
    @RequestMapping("/modules/remove")
    public String remove(@RequestParam int[] modulesToRemove, Model model) {
        mergeModulesStorage.remove(modulesToRemove);
        model.addAttribute("message", "Selected modules successfully removed.");
        return listMergeModules(model);
    }
}
