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
 *        Enriko Käsper
 */
package eionet.webq.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import eionet.webq.model.UploadedXmlFile;

/**
 * Base controller for front page actions.
 *
 * @author Enriko Käsper
 */
@Controller
@RequestMapping("/")
// @SessionAttributes({"uploadedXmlFile"})
public class BaseController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcome(ModelMap model) {

        model.addAttribute(new UploadedXmlFile());
        return "index";

    }

    // FIXME just for testing
    @RequestMapping(value = "/testParam/{testParam}", method = RequestMethod.GET)
    public String testParam(@PathVariable String testParam, ModelMap model) {

        model.addAttribute(new UploadedXmlFile());
        model.addAttribute(testParam);
        return "index";

    }

    @RequestMapping(value = "/uploadXml", method = RequestMethod.POST)
    public String upload(@RequestParam MultipartFile uploadedXmlFile, Model model) {

        model.addAttribute("message", "File '" + uploadedXmlFile.getOriginalFilename() + "' uploaded successfully");

        // FIXME remove debug lines
        System.out.println("-------------------------------------------");
        System.out.println("Test upload: " + uploadedXmlFile.getOriginalFilename());
        System.out.println("Test upload: " + uploadedXmlFile.getSize());
        System.out.println("-------------------------------------------");

        return "index";

    }
}