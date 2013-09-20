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
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.WebQMenuParameters;
import eionet.webq.service.CDREnvelopeService;
import eionet.webq.service.FileNotAvailableException;
import eionet.webq.service.UserFileService;
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
     * User file service.
     */
    @Autowired
    private UserFileService userFileService;


    /**
     * Deliver with WebForms.
     *
     * @param request parameters of this action
     * @param model model
     * @return view name
     * @throws eionet.webq.service.FileNotAvailableException if one redirect to xform remote file not found.
     */
    @RequestMapping("/WebQMenu")
    public String menu(HttpServletRequest request, Model model) throws FileNotAvailableException {
        WebQMenuParameters parameters = converter.convert(request);
        MultiValueMap<String, XmlFile> xmlFiles = envelopeService.getXmlFiles(parameters);
        Collection<String> requiredSchemas =
                StringUtils.isNotEmpty(parameters.getSchema()) ? Arrays.asList(parameters.getSchema()) : xmlFiles.keySet();
        Collection<ProjectFile> webForms = webFormService.findWebFormsForSchemas(requiredSchemas);

        if (hasOnlyOneFileAndWebFormForSameSchema(xmlFiles, webForms)) {
            return redirectToEditWebForm(request, xmlFiles, webForms);
        }
        model.addAttribute("parameters", parameters);
        model.addAttribute("xmlFiles", xmlFiles);
        model.addAttribute("availableWebForms", webForms);
        return "deliver_menu";
    }

    /**
     * Edit envelope file with web form.
     *
     * @param formId web form id
     * @param fileName file name
     * @param remoteFileUrl remote file url
     * @param request current request
     * @return view name
     * @throws eionet.webq.service.FileNotAvailableException if remote file not available
     */
    @RequestMapping("/cdr/edit/file")
    public String editWithWebForm(@RequestParam int formId, @RequestParam String fileName,
                                  @RequestParam String remoteFileUrl, HttpServletRequest request) throws FileNotAvailableException {
        ProjectFile webForm = webFormService.findActiveWebFormById(formId);
        UserFile userFile = new UserFile(new UploadedFile(fileName, new byte[0]), webForm.getXmlSchema());

        int fileId = userFileService.saveWithContentFromRemoteLocation(userFile, remoteFileUrl);
        return "redirect:/xform/?formId=" + formId + "&fileId=" + fileId + "&base_uri=" + request.getContextPath();
    }

    /**
     * Check whether there is only 1 file and 1 schema available and their xml schemas match.
     *
     * @param xmlFiles xml files
     * @param webForms webforms
     * @return true iff there are only 1 file and schema with equal xml schema
     */
    private boolean hasOnlyOneFileAndWebFormForSameSchema(MultiValueMap<String, XmlFile> xmlFiles, Collection<ProjectFile> webForms) {
        return webForms.size() == 1
                && xmlFiles.size() == 1
                && xmlFiles.get(webForms.iterator().next().getXmlSchema()).size() == 1;
    }

    /**
     * Redirects to edit form.
     *
     * @param request current request
     * @param xmlFiles xml files
     * @param webForms web forms
     * @return redirect string
     * @throws FileNotAvailableException if remote file not available
     */
    private String redirectToEditWebForm(HttpServletRequest request, MultiValueMap<String, XmlFile> xmlFiles, Collection<ProjectFile> webForms) throws FileNotAvailableException {
        ProjectFile onlyOneAvailableForm = webForms.iterator().next();
        XmlFile onlyOneAvailableFile = xmlFiles.getFirst(onlyOneAvailableForm.getXmlSchema());
        return editWithWebForm(onlyOneAvailableForm.getId(), onlyOneAvailableFile.getTitle(), onlyOneAvailableFile.getFullName(), request);
    }
}
