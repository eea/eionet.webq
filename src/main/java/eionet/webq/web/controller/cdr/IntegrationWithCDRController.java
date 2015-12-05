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

import eionet.webq.converter.CdrRequestConverter;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.CdrRequest;
import eionet.webq.service.CDREnvelopeService;
import eionet.webq.service.CDREnvelopeService.XmlFile;
import eionet.webq.service.FileNotAvailableException;
import eionet.webq.service.UserFileService;
import eionet.webq.service.WebFormService;
import eionet.webq.web.controller.util.WebformUrlProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Provides integration options with CDR.
 */
@Controller
public class IntegrationWithCDRController {
    /**
     * Latest cdr request session attribute.
     */
    public static final String LATEST_CDR_REQUEST = "latestCdrRequest";
    /**
     * This class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(IntegrationWithCDRController.class);
    /**
     * Webform URL provider.
     */
    @Autowired
    WebformUrlProvider webformUrlProvider;
    /**
     * WebQ1 URL.
     */
    @Value("${webq1.url}")
    String webQFallBackUrl;
    /**
     * Converts request to CdrRequest.
     */
    @Autowired
    private CdrRequestConverter converter;
    /**
     * CDR envelope service.
     */
    @Autowired
    private CDREnvelopeService envelopeService;
    /**
     * Operations with web forms.
     */
    @Autowired
    @Qualifier("remoteWebForms")
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
     * @param model   model
     * @return view name
     * @throws eionet.webq.service.FileNotAvailableException if one redirect to xform remote file not found.
     */
    @RequestMapping("/WebQMenu")
    public String webQMenu(HttpServletRequest request, Model model)
            throws FileNotAvailableException {
        CdrRequest parameters = convertAndPutResultIntoSession(request);

        LOGGER.info("Received WebQMenu request with parameters:" + parameters.toString());

        MultiValueMap<String, XmlFile> xmlFiles = envelopeService.getXmlFiles(parameters);
        Collection<String> requiredSchemas =
                StringUtils.isNotEmpty(parameters.getSchema()) ? Arrays.asList(parameters.getSchema()) : xmlFiles.keySet();
        Collection<ProjectFile> webForms = webFormService.findWebFormsForSchemas(requiredSchemas);
        if (webForms.isEmpty()) {
            throw new IllegalArgumentException("no web forms available.");
        }
        if (hasOnlyOneFileAndWebFormForSameSchema(xmlFiles, webForms, parameters)) {
            return redirectToEditWebForm(parameters, xmlFiles, webForms);
        }
        if (oneWebFormAndNoFilesButNewFileCreationIsAllowed(xmlFiles, webForms, parameters)) {
            return startNewForm(parameters, webForms.iterator().next());
        }
        return deliverMenu(webForms, xmlFiles, parameters, model);
    }

    /**
     * WebQEdit request handler.
     *
     * @param request current request
     * @param model   model
     * @return view name
     * @throws FileNotAvailableException if remote file not available.
     */
    @RequestMapping("/WebQEdit")
    public String webQEdit(HttpServletRequest request, Model model) throws FileNotAvailableException {
        CdrRequest parameters = convertAndPutResultIntoSession(request);

        LOGGER.info("Received WebQEdit request with parameters:" + parameters.toString());

        String schema = parameters.getSchema();
        if (StringUtils.isEmpty(schema)) {
            throw new IllegalArgumentException("schema parameter is required");
        }

        Collection<ProjectFile> webForms = webFormService.findWebFormsForSchemas(Arrays.asList(schema));
        if (webForms.isEmpty()) {
            throw new IllegalArgumentException("no web forms for '" + schema + "' schema found");
        }
        String instanceUrl = parameters.getInstanceUrl();
        String fileName = parameters.getInstanceName();
        if (webForms.size() > 1) {
            LinkedMultiValueMap<String, XmlFile> xmlFiles = new LinkedMultiValueMap<String, XmlFile>();
            xmlFiles.add(schema, new XmlFile(instanceUrl, fileName));
            return deliverMenu(webForms, xmlFiles, parameters, model);
        }
        return editFile(webForms.iterator().next(), fileName, instanceUrl, parameters);
    }

    /**
     * IllegalArgumentException handler for this class. If request parameters cannot be handled by this application, redirect to
     * webQ1.
     *
     * @param request  current request
     * @param response http response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    public void redirectToWebQ(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Location", webQFallBackUrl + request.getServletPath() + '?' + request.getQueryString());
    }

    /**
     * Edit envelope file with web form.
     *
     * @param formId        web form id
     * @param fileName      file name
     * @param remoteFileUrl remote file url
     * @param request       current request
     * @return view name
     * @throws eionet.webq.service.FileNotAvailableException if remote file not available
     */
    @RequestMapping("/cdr/edit/file")
    public String editWithWebForm(@RequestParam int formId, @RequestParam String fileName,
            @RequestParam String remoteFileUrl, HttpServletRequest request) throws FileNotAvailableException {
        CdrRequest cdrRequest = (CdrRequest) request.getSession().getAttribute(LATEST_CDR_REQUEST);
        return editFile(webFormService.findActiveWebFormById(formId), fileName, remoteFileUrl, cdrRequest);
    }

    /**
     * Add new envelope file with web form.
     *
     * @param formId  web form id
     * @param request current request
     * @return view name
     * @throws eionet.webq.service.FileNotAvailableException if remote file not available
     */
    @RequestMapping("/cdr/add/file")
    public String addWithWebForm(@RequestParam int formId, HttpServletRequest request)
            throws FileNotAvailableException {
        CdrRequest cdrRequest = (CdrRequest) request.getSession().getAttribute(LATEST_CDR_REQUEST);
        ProjectFile webform = webFormService.findActiveWebFormById(formId);
        return startNewForm(cdrRequest, webform);
    }

    /**
     * Start new web form.
     *
     * @param parameters cdr parameters
     * @param webform    project web form
     * @return redirect string
     * @throws FileNotAvailableException if remote file not available
     */
    private String startNewForm(CdrRequest parameters, ProjectFile webform)
            throws FileNotAvailableException {
        int fileId =
                userFileService
                        .saveBasedOnWebForm(userFileBasedOn(parameters), webFormService.findActiveWebFormById(webform.getId()));

        LOGGER.info("Received ADD NEW file request from CDR with parameters: " + parameters.toString() +
                "; sessionid=" + md5Hex(parameters.getSessionId()));

        String webformPath = webformUrlProvider.getWebformPath(webform);

        String redirect = "redirect:" + webformPath + "fileId=" + fileId + "&base_uri="
                + parameters.getContextPath()
                + "&envelope=" + StringUtils.defaultString(parameters.getEnvelopeUrl())
                + StringUtils.defaultString(parameters.getAdditionalParametersAsQueryString())
                + "&sessionid=" + md5Hex(parameters.getSessionId());

        LOGGER.info("Redirect to: " + redirect);

        return redirect;
    }

    /**
     * Creates new user file object based on {@link eionet.webq.dto.CdrRequest}.
     *
     * @param parameters cdr parameters
     * @return UserFile
     */
    private UserFile userFileBasedOn(CdrRequest parameters) {
        UserFile userFile = new UserFile();
        userFile.setFromCdr(true);
        userFile.setName(parameters.getNewFileName());
        userFile.setEnvelope(parameters.getEnvelopeUrl());
        userFile.setAuthorization(parameters.getBasicAuthorization());
        userFile.setAuthorized(parameters.isAuthorizationSet());
        userFile.setCookies(parameters.getCookies());
        userFile.setTitle(parameters.getInstanceTitle());
        return userFile;
    }

    /**
     * Sets passed parameters to model attributes and returns view name.
     *
     * @param webForms   web forms
     * @param xmlFiles   xml files
     * @param parameters cdr parameters
     * @param model      model
     * @return view name
     */
    private String deliverMenu(Collection<ProjectFile> webForms, MultiValueMap<String, XmlFile> xmlFiles, CdrRequest parameters,
            Model model) {
        model.addAttribute("parameters", parameters);
        model.addAttribute("xmlFiles", xmlFiles);
        model.addAttribute("availableWebForms", webForms);
        return "deliver_menu";
    }

    /**
     * Saves new user file to db and returns redirect url to web form edit.
     *
     * @param webForm       web form to be used for edit.
     * @param fileName      new file name
     * @param remoteFileUrl remote file url
     * @param request       current request
     * @return redirect url
     * @throws FileNotAvailableException if remote file not available
     */
    private String editFile(ProjectFile webForm, String fileName, String remoteFileUrl, CdrRequest request)
            throws FileNotAvailableException {
        UserFile userFile = userFileBasedOn(request);
        userFile.setName(fileName);
        userFile.setXmlSchema(webForm.getXmlSchema());

        int fileId = userFileService.save(userFile);
        String envelopeParam = (request.getEnvelopeUrl() != null) ? "&envelope=" + request.getEnvelopeUrl() : "";

        LOGGER.info("Received EDIT file request from CDR with parameters: " + request.toString() +
                ";  sessionid=" + md5Hex(request.getSessionId()));

        String webformPath = webformUrlProvider.getWebformPath(webForm);

        String redirect = "redirect:" + webformPath + "instance=" + remoteFileUrl + "&fileId="
                + fileId
                + "&base_uri=" + request.getContextPath() + envelopeParam
                + request.getAdditionalParametersAsQueryString()
                + "&sessionid=" + md5Hex(request.getSessionId());

        LOGGER.info("Redirect to: " + redirect);

        return redirect;
    }

    /**
     * Check whether there is only 1 file and 1 schema available and their xml schemas match.
     *
     * @param xmlFiles   xml files
     * @param webForms   web forms
     * @param parameters request parameters
     * @return true iff there are only 1 file and schema with equal xml schema
     */
    private boolean hasOnlyOneFileAndWebFormForSameSchema(MultiValueMap<String, XmlFile> xmlFiles,
            Collection<ProjectFile> webForms, CdrRequest parameters) {
        if (webForms.size() == 1 && !parameters.isNewFormCreationAllowed()) {
            List<XmlFile> filesForSchema = xmlFiles.get(webForms.iterator().next().getXmlSchema());
            if (filesForSchema != null && filesForSchema.size() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether there is no files and only one form available. Adding new files must be allowed.
     *
     * @param xmlFiles   xml files
     * @param webForms   web forms
     * @param parameters request parameters
     * @return true iff only one form, no files and creation of new files allowed.
     */
    private boolean oneWebFormAndNoFilesButNewFileCreationIsAllowed(MultiValueMap<String, XmlFile> xmlFiles,
            Collection<ProjectFile> webForms, CdrRequest parameters) {
        if (webForms.size() == 1 && parameters.isNewFormCreationAllowed()) {
            List<XmlFile> filesForSchema = xmlFiles.get(webForms.iterator().next().getXmlSchema());
            if (filesForSchema == null || filesForSchema.size() == 0) {
                return true;
            }

        }
        return false;
    }

    /**
     * Redirects to edit form.
     *
     * @param request  parsed cdr request
     * @param xmlFiles xml files
     * @param webForms web forms
     * @return redirect string
     * @throws FileNotAvailableException if remote file not available
     */
    private String redirectToEditWebForm(CdrRequest request, MultiValueMap<String, XmlFile> xmlFiles,
            Collection<ProjectFile> webForms) throws FileNotAvailableException {
        ProjectFile onlyOneAvailableForm = webForms.iterator().next();
        XmlFile onlyOneAvailableFile = xmlFiles.getFirst(onlyOneAvailableForm.getXmlSchema());

        String fileName = null;
        if (null != onlyOneAvailableFile.getFullName() && !onlyOneAvailableFile.getFullName().isEmpty()) {
            int index = onlyOneAvailableFile.getFullName().lastIndexOf('/');
            fileName = onlyOneAvailableFile.getFullName().substring(index + 1);
        }
        return editFile(onlyOneAvailableForm, fileName , onlyOneAvailableFile.getFullName(), request);
    }

    /**
     * Performs request conversion and sets result as a session attribute.
     *
     * @param request current request
     * @return cdr request parameters
     */
    private CdrRequest convertAndPutResultIntoSession(HttpServletRequest request) {
        CdrRequest cdrRequest = converter.convert(request);
        request.getSession().setAttribute(LATEST_CDR_REQUEST, cdrRequest);
        return cdrRequest;
    }
}
