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
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 */
@Controller
@RequestMapping("/known_hosts")
public class KnownHostsController {
    /**
     * Successful save/update message.
     */
    public static final String KNOWN_HOST_SAVED_MESSAGE = "Known host saved";
    /**
     * Host removed message.
     */
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
        return editView(new KnownHost(), model);
    }

    /**
     * Save new known host to storage.
     *
     * @param knownHost host to save
     * @param bindingResult bindingResult
     * @param model model
     * @return view name
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute KnownHost knownHost, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return editView(knownHost, model);
        }
        try {
            if (knownHost.getId() > 0) {
                knownHostsService.update(knownHost);
            } else {
                knownHostsService.save(knownHost);
            }
        } catch (ConstraintViolationException e) {
            bindingResult.rejectValue("hostURL", "duplicate.host.url");
            return editView(knownHost, model);
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
        model.addAttribute("knownHost", knownHostsService.findById(id));
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

    /**
     * Prepares edit view.
     *
     * @param host host.
     * @param model model.
     * @return view name
     */
    private String editView(KnownHost host, Model model) {
        model.addAttribute("knownHost", host);
        return "add_edit_known_host";
    }
}
