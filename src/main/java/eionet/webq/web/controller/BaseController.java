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

import eionet.webq.dao.FileStorage;
import eionet.webq.dto.UploadedXmlFile;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Base controller for front page actions.
 *
 * @author Enriko Käsper
 */
@Controller
@RequestMapping("/")
public class BaseController {

    @Autowired
    private FileStorage storage;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcome(Model model) {
        model.addAttribute("uploadedFiles", storage.allUploadedFiles());
        return "index";
    }

    @RequestMapping(value = "/uploadXml", method = RequestMethod.POST)
    public String upload(@RequestParam UploadedXmlFile uploadedXmlFile, Model model) {
        model.addAttribute("message", "File '" + uploadedXmlFile.getName() + "' uploaded successfully");
        storage.save(uploadedXmlFile);
        return welcome(model);
    }

    @RequestMapping(value = "/download")
    public void downloadFile(@RequestParam int fileId, HttpServletResponse response) {
        UploadedXmlFile file = storage.getById(fileId);
        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.addHeader("Content-Disposition", "attachment;filename=" + file.getName());
        ServletOutputStream output = null;
        try {
            byte[] fileContent = file.getFileContent();
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
}
