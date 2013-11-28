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

import eionet.webq.dao.orm.KnownHost;
import eionet.webq.service.KnownHostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 */
@Controller
@RequestMapping("/known_hosts")
public class KnownHostsController {
    /**
     * Successful save/update message.
     */
    public static final String KNOWN_HOST_SAVED_MESSAGE = "Known host saved";
    public static final String HOST_REMOVED_MESSAGE = "Host removed";
    /**
     * Known hosts service.
     */
    @Autowired
    private KnownHostsService knownHostsService;
    /**
     * Lists all known hosts.
     *
     * @param model model
     * @return view name
     */
    @RequestMapping({ "/", "" })
    public String listKnownHosts(Model model) {
        model.addAttribute("allKnownHosts", knownHostsService.findAll());
        return "known_hosts_list";
    }

    /**
     * Shows save page.
     *
     * @param model model
     * @return view name
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String savePage(Model model) {
        model.addAttribute("host", new KnownHost());
        return "add_edit_known_host";
    }

    /**
     * Save new known host to storage.
     *
     * @param host host to save
     * @param model model
     * @return view name
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute KnownHost host, Model model) {
        //TODO validation
        if (host.getId() > 0) {
            knownHostsService.update(host);
        } else {
            knownHostsService.save(host);
        }
        model.addAttribute("message", KNOWN_HOST_SAVED_MESSAGE);
        return listKnownHosts(model);
    }

    /**
     * Opens edit form.
     *
     * @param id host id
     * @param model model
     * @return view name
     */
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String save(@PathVariable int id, Model model) {
        model.addAttribute("host", knownHostsService.findById(id));
        return "add_edit_known_host";
    }

    /**
     * Removes host from storage.
     *
     * @param id host id
     * @param model model
     * @return view name
     */
    @RequestMapping("/remove/{id}")
    public String remove(@PathVariable int id, Model model) {
        model.addAttribute("message", HOST_REMOVED_MESSAGE);
        knownHostsService.remove(id);
        return listKnownHosts(model);
    }
}
