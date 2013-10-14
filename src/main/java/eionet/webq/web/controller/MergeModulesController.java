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
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String listMergeModules(Model model) {
        model.addAttribute("allMergeModules", mergeModulesStorage.findAll());
        return "";
    }

    /**
     * Shows add model page.
     *
     * @param model model
     * @return view name
     */
    public String addModule(Model model) {
        model.addAttribute("newMergeModule", new MergeModule());
        return "";
    }

    /**
     * Saves module to db.
     *
     * @param module module to save
     * @return view name
     */
    public String save(MergeModule module) {
        mergeModulesStorage.save(module);
        return "";
    }
}
