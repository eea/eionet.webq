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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eionet.webq.converter.UserFileToFileInfoConverter;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.FileInfo;
import eionet.webq.dto.UploadForm;
import eionet.webq.dto.XmlSaveResult;
import eionet.webq.service.CDREnvelopeService;
import eionet.webq.service.ConversionService;
import eionet.webq.service.FileNotAvailableException;
import eionet.webq.service.UserFileService;
import eionet.webq.service.WebFormService;

/**
 * Base controller for front page actions.
 *
 * @author Enriko Käsper
 */
@Controller
@RequestMapping
public class PublicPageController {
    /**
     * Service for user uploaded files.
     */
    @Autowired
    UserFileService userFileService;
    /**
     * File conversion service.
     */
    @Autowired
    private ConversionService conversionService;

    /**
     * WebForms storage.
     */
    @Autowired
    private WebFormService webFormService;
    /**
     * Cdr envelope service.
     */
    @Autowired
    private CDREnvelopeService envelopeService;

    /**
     * Converts user xml metadata to file info object.
     */
    @Autowired
    private UserFileToFileInfoConverter fileInfoConverter;

    /**
     * Action to be performed on http GET method and path '/'.
     *
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/")
    public String welcome(Model model) {
        model.addAttribute("uploadedFiles", allFilesWithConversions());
        model.addAttribute("allWebForms", allWebForms());
        String uploadForm = "uploadForm";
        if (!model.containsAttribute(uploadForm)) {
            model.addAttribute(uploadForm, new UploadForm());
        }
        return "index";
    }

    /**
     * Action to be performed on http GET method and path '/'.
     *
     * @param model holder for model attributes
     * @param session http session
     * @return view name
     */
    @RequestMapping(value = "/coordinator")
    public String coordinator(Model model, HttpSession session) {
        session.setAttribute("isCoordinator", true);
        return welcome(model);
    }

    /**
     * Action to be performed on http GET method and path '/'.
     *
     * @param model holder for model attributes
     * @param session http session
     * @return view name
     */
    @RequestMapping(value = "/sessionfiles")
    public String sessionfiles(Model model, HttpSession session) {
        session.setAttribute("isCoordinator", false);
        return welcome(model);
    }

    /**
     * Redirects to welcome page after login.
     *
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/login")
    public String login(Model model) {
        return welcome(model);
    }

    /**
     * Shows page which allows to perform SingleSignOut.
     *
     * @return view name
     */
    @RequestMapping(value = "/logout")
    public String logout() {
        return "logout_all_apps";
    }

    /**
     * Upload action.
     *
     * @param uploadForm represents form used in UI, {@link UploadForm#userFiles} will be converted from
     *            {@link org.springframework.web.multipart.MultipartFile}
     * @param result binding result, contains validation errors
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/uploadXml", method = RequestMethod.POST)
    public String upload(@Valid @ModelAttribute UploadForm uploadForm, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            saveFiles(uploadForm);
        }
        return welcome(model);
    }

    /**
     * Upload action. If there is only one file uploaded and one form available for this file, redirect to this form.
     *
     * @param uploadForm represents form used in UI, {@link UploadForm#userFiles} will be converted from
     *            {@link org.springframework.web.multipart.MultipartFile}
     * @param result binding result, contains validation errors
     * @param model holder for model attributes
     * @param request http request
     * @return view name
     */
    @RequestMapping(value = "/uploadXmlWithRedirect", method = RequestMethod.POST)
    public String uploadWithRedirectToWebForm(@Valid @ModelAttribute UploadForm uploadForm, BindingResult result, Model model,
            HttpServletRequest request) {
        if (!result.hasErrors()) {
            saveFiles(uploadForm);
            Collection<UserFile> files = uploadForm.getUserFiles();
            if (files.size() == 1) {
                UserFile file = files.iterator().next();
                Collection<ProjectFile> availableWebForms =
                        webFormService.findWebFormsForSchemas(Arrays.asList(file.getXmlSchema()));
                if (availableWebForms.size() == 1) {
                    int fileId = file.getId();
                    int formId = availableWebForms.iterator().next().getId();
                    String contextPath = request.getContextPath();
                    String downloadUrl = contextPath + "/download/user_file?fileId=" + fileId;
                    return "redirect:/xform/?formId=" + formId + "&fileId=" + fileId + "&instance=" + downloadUrl
                            + "&base_uri=" + contextPath;
                }
                model.addAttribute("message", "File '" + file.getName() + "' uploaded successfully");
            }
        }
        return welcome(model);
    }

    /**
     * Removes selected user files.
     *
     * @param selectedUserFile ids of files to be removed
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/remove/files")
    public String removeUserFiles(@RequestParam(required = false) int[] selectedUserFile, Model model) {
        if (selectedUserFile != null) {
            userFileService.removeFilesById(selectedUserFile);
            model.addAttribute("message", "Selected files removed successfully");
        }
        return welcome(model);
    }

    /**
     * Update file content action. The action is called from XForms and it returns XML formatted result.
     *
     * @param fileId file id to update
     * @param request current request
     * @return response as application/xml generated by {@link org.springframework.oxm.jaxb.Jaxb2Marshaller}
     */
    @RequestMapping(value = "/saveXml", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public XmlSaveResult saveXml(@RequestParam int fileId, HttpServletRequest request) {
        UserFile file = userFileService.getById(fileId);
        XmlSaveResult saveResult = XmlSaveResult.valueOfSuccess();
        InputStream input = null;
        try {
            input = request.getInputStream();
            byte[] fileContent = IOUtils.toByteArray(input);
            file.setContent(fileContent);
            if (file.isFromCdr()) {
                String restricted = request.getParameter("restricted");
                file.setApplyRestriction(StringUtils.isNotEmpty(restricted));
                file.setRestricted(Boolean.valueOf(restricted));
                file.setConversionId(request.getParameter("xsl"));
                return envelopeService.pushXmlFile(file);
            }
            userFileService.updateContent(file);
        } catch (Exception e) {
            saveResult = XmlSaveResult.valueOfError(e.toString());
        } finally {
            IOUtils.closeQuietly(input);
        }
        return saveResult;
    }

    /**
     * This is STEP 1 in generating new WebForm. New file with form parameters will be saved to storage.
     *
     * @param formId webform id
     * @param request current request
     * @return redirection URL of webform with correct parameters
     * @throws FileNotAvailableException if empty instance URL is filled for selected webform, but the resource is not available.
     */
    @RequestMapping(value = "/startWebform")
    public String startWebFormSaveFile(@RequestParam int formId, HttpServletRequest request)
            throws FileNotAvailableException {
        int fileId = userFileService.saveBasedOnWebForm(new UserFile(), webFormService.findActiveWebFormById(formId));
        return "redirect:/xform/?formId=" + formId + "&fileId=" + fileId + "&base_uri=" + request.getContextPath();
    }

    /**
     * This is STEP 2 in generating new WebForm. Form content will be loaded from storage and written to response. After that
     * response must be handled by {@link de.betterform.agent.web.filter.XFormsFilter}. Filter mapping in web.xml should match with
     * mapping of this method.
     *
     * @param formId webform id
     * @param response current response
     * @throws IOException in case if writing of xForm to response failed
     */
    @RequestMapping(value = "/xform")
    @Transactional
    public void startWebFormWriteFormToResponse(@RequestParam int formId, HttpServletResponse response) throws IOException {
        ProjectFile webForm = webFormService.findActiveWebFormById(formId);
        byte[] fileContent = webForm.getFileContent();
        response.setContentLength(fileContent.length);
        response.setContentType("application/xhtml+html");
        OutputStream outputStream = response.getOutputStream();
        IOUtils.write(fileContent, outputStream);
        outputStream.flush();
    }

    /**
     * Returns FileInfo object in XML format. FileInfo holds public data about UserFile and relevant conversions. The returned XML
     * contains links to download, delete and convert the file.
     *
     * @param fileId UserFile id
     * @param request http request
     * @param response http response
     * @return response as application/xml generated by {@link org.springframework.oxm.jaxb.Jaxb2Marshaller}
     */
    @RequestMapping(value = "/file/info", method = RequestMethod.GET)
    @ResponseBody
    public FileInfo getFileInfo(@RequestParam int fileId, HttpServletRequest request, HttpServletResponse response) {

        UserFile userFile = userFileService.getById(fileId);
        FileInfo fileInfo = new FileInfo();
        if (userFile == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            fileInfo = fileInfoConverter.convert(userFile);
        }
        return fileInfo;
    }

    /**
     * Save files attached to upload form.
     *
     * @param uploadForm upload form
     */
    private void saveFiles(UploadForm uploadForm) {
        for (UserFile userFile : uploadForm.getUserFiles()) {
            userFileService.save(userFile);
        }
    }

    /**
     * Loads and sets conversions for files uploaded by user.
     *
     * @return all uploaded files with available conversions set.
     */
    private Collection<UserFile> allFilesWithConversions() {
        Collection<UserFile> userFiles = userFileService.allUploadedFiles();
        for (UserFile userFile : userFiles) {
            userFile.setAvailableConversions(conversionService.conversionsFor(userFile.getXmlSchema()));
        }
        return userFiles;
    }

    /**
     * Loads all active webforms.
     *
     * @return collection of active webforms.
     */
    private Collection<ProjectFile> allWebForms() {
        return webFormService.getAllActiveWebForms();
    }
}
