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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides integration options with CDR.
 */
@Controller
public class CDRIntegrationController {

    /**
     * Converts request to WebQMenuParameters.
     */
    @Autowired
    private RequestToWebQMenuParameters converter;

    /**
     * Deliver with WebForms.
     *
     * @param request parameters of this action
     * @return view name
     */
    @RequestMapping("/WebQMenu")
    public String menu(HttpServletRequest request) {
        WebQMenuParameters parameters = converter.convert(request);
        return "index";
    }
}