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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
    @Qualifier("inMemory")
    private FileStorage storage;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String welcome(Model model) {
        model.addAttribute("uploadedFiles", storage.allUploadedFiles());
        return "index";
    }

    @RequestMapping(value = "/uploadXml", method = RequestMethod.POST)
    public String upload(@RequestParam MultipartFile uploadedXmlFile, Model model) {
        model.addAttribute("message", "File '" + uploadedXmlFile.getOriginalFilename() + "' uploaded successfully");
        storage.save(uploadedXmlFile);
        return welcome(model);
    }

    @RequestMapping(value = "/download")
    public void downloadFile(@RequestParam String fileName, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        ServletOutputStream output = null;
        try {
            byte[] responseBytes = FileUtils.readFileToByteArray(storage.getByFilename(fileName));
            response.setContentLength(responseBytes.length);

            output = response.getOutputStream();
            output.write(responseBytes);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
}
