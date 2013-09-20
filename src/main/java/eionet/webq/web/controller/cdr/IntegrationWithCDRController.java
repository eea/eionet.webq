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
package eionet.webq.web.controller.cdr;

import eionet.webq.converter.RequestToWebQMenuParameters;
import eionet.webq.dto.WebQMenuParameters;
import eionet.webq.service.CDREnvelopeService;
import eionet.webq.service.WebFormService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;

import static eionet.webq.service.CDREnvelopeService.XmlFile;

/**
 * Provides integration options with CDR.
 */
@Controller
public class IntegrationWithCDRController {

    /**
     * Converts request to WebQMenuParameters.
     */
    @Autowired
    private RequestToWebQMenuParameters converter;
    /**
     * CDR envelope service.
     */
    @Autowired
    private CDREnvelopeService envelopeService;
    /**
     * Operations with web forms.
     */
    @Autowired
    private WebFormService webFormService;

    /**
     * Deliver with WebForms.
     *
     * @param request parameters of this action
     * @param model model
     * @return view name
     */
    @RequestMapping("/WebQMenu")
    public String menu(HttpServletRequest request, Model model) {
        WebQMenuParameters parameters = converter.convert(request);
        MultiValueMap<String, XmlFile> xmlFiles = envelopeService.getXmlFiles(parameters);
        Collection<String> requiredSchemas =
                StringUtils.isNotEmpty(parameters.getSchema()) ? Arrays.asList(parameters.getSchema()) : xmlFiles.keySet();

        model.addAttribute("parameters", parameters);
        model.addAttribute("xmlFiles", xmlFiles);
        model.addAttribute("availableWebForms", webFormService.findWebFormsForSchemas(requiredSchemas));
        return "deliver_menu";
    }

    /**
     * Edit envelope file with web form.
     *
     * @param formId web form id
     * @param request current request
     * @return view name
     */
    @RequestMapping("/cdr/edit/file")
    public String editWithWebForm(@RequestParam int formId, HttpServletRequest request) {
        return "redirect:/xform/?formId=" + formId  + "&fileId=1&base_uri=" + request.getContextPath();
    }
}
