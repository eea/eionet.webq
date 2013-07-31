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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eionet.webq.dao.FileStorage;
import eionet.webq.dto.UploadedXmlFile;

/**
 * Base controller for front page actions.
 *
 * @author Enriko Käsper
 */
@Controller
@RequestMapping("/")
public class BaseController {
    /**
     * File storage for user uploaded files.
     */
    @Autowired
    private FileStorage storage;

    /**
     * Action to be performed on http GET method and path '/'.
     *
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcome(Model model) {
        model.addAttribute("uploadedFiles", storage.allUploadedFiles());
        return "index";
    }

    /**
     * Upload action.
     *
     * @param uploadedXmlFile converted from {@link org.springframework.web.multipart.MultipartFile}
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/uploadXml", method = RequestMethod.POST)
    public String upload(@RequestParam UploadedXmlFile uploadedXmlFile, Model model) {
        model.addAttribute("message", "File '" + uploadedXmlFile.getName() + "' uploaded successfully");
        storage.save(uploadedXmlFile);
        return welcome(model);
    }

    /**
     * Download uploaded file action.
     *
     * @param fileId requested file id
     * @param response http response to write file
     */
    @RequestMapping(value = "/download")
    public void downloadFile(@RequestParam int fileId, HttpServletResponse response) {
        UploadedXmlFile file = storage.getById(fileId);
        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.addHeader("Content-Disposition", "attachment;filename=" + file.getName());
        ServletOutputStream output = null;
        try {
            byte[] fileContent = file.getContent();
            response.setContentLength(fileContent.length);

            output = response.getOutputStream();
            output.write(fileContent);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * Update file content action. The action is called from XForms and it returns XML formatted result.
     * @param fileId
     * @param request
     * @return response as text/xml
     */
    @RequestMapping(value = "/saveXml", method = RequestMethod.POST)
    public String saveXml(@RequestParam int fileId, HttpServletRequest request) {
        UploadedXmlFile file = new UploadedXmlFile();
        InputStream input = null;
        try {
            input = request.getInputStream();
            byte[] fileContent = IOUtils.toByteArray(input);
            file.setContent(fileContent);
            file.setSizeInBytes(fileContent.length);
            file.setId(fileId);
            storage.updateContent(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }

        //FIXME return save result in XML format for XForm
        return "";
    }
}
