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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import eionet.webq.converter.JsonXMLBidirectionalConverter;
import eionet.webq.dao.MergeModules;
import eionet.webq.dao.orm.MergeModule;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.service.ConversionService;
import eionet.webq.service.FileNotAvailableException;
import eionet.webq.service.ProjectFileService;
import eionet.webq.service.ProjectService;
import eionet.webq.service.RemoteFileService;
import eionet.webq.service.UserFileMergeService;
import eionet.webq.service.UserFileService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Spring controller for WebQ file download.
 */
@Controller
@RequestMapping(value = {"download", "webform"})
public class FileDownloadController {
    /**
     * Logger for this class.
     */
    public static final Logger LOGGER = Logger.getLogger(PublicPageController.class);
    /**
     * Service for getting user file content from storage.
     */
    @Autowired
    private UserFileService userFileService;
    /**
     * Service for getting user file content from storage.
     */
    @Autowired
    private ProjectService projectService;
    /**
     * Service for getting project file content from storage.
     */
    @Autowired
    private ProjectFileService projectFileService;
    /**
     * File conversion service.
     */
    @Autowired
    private ConversionService conversionService;
    /**
     * Merge modules repository.
     */
    @Autowired
    private MergeModules mergeModules;
    /**
     * User files merge service.
     */
    @Autowired
    private UserFileMergeService mergeService;
    /**
     * Json to XML converter.
     */
    @Autowired
    JsonXMLBidirectionalConverter jsonXMLConverter;

    /**
     * Service for downloading remote files.
     */
    @Autowired
    RemoteFileService remoteFileService;

    /**
     * Convert and download the user XML file in JSON format if request header Accept content type is application/json.
     *
     * @param fileId   user file id.
     * @param response http response to write file
     * @throws FileNotAvailableException if user file is not available for given id
     */
    @RequestMapping(value = {"/converted_user_file", "user_file"}, produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET)
    @Transactional
    public void downloadUserFileJson(@RequestParam int fileId, HttpServletRequest request, HttpServletResponse response)
            throws FileNotAvailableException {

        UserFile file = getUserFile(fileId, request);

        if (file.isFromCdr() && file.getContent() == null) {
            file.setContent(remoteFileService.fileContent(file.getEnvelope() + "/" + file.getName()));
        }
        byte[] json = jsonXMLConverter.convertXmlToJson(file.getContent());
        setContentType(response, MediaType.APPLICATION_JSON);
        writeToResponse(response, json);
    }

    /**
     * Convert the user XML file into JSON format and then back to XML format to be able to evaluate the conversion. The method is
     * called when request header Accept content type is text/xml.
     *
     * @param fileId   user file id.
     * @param response http response to write file
     * @throws FileNotAvailableException user file is not available for given id
     */
    @RequestMapping(value = "/converted_user_file", produces = MediaType.APPLICATION_XML_VALUE, method = RequestMethod.GET)
    @Transactional
    public void downloadUserFileJsonToXml(@RequestParam int fileId, HttpServletRequest request, HttpServletResponse response)
            throws FileNotAvailableException {

        UserFile file = getUserFile(fileId, request);

        byte[] xml = jsonXMLConverter.convertJsonToXml(jsonXMLConverter.convertXmlToJson(file.getContent()));

        writeXmlFileToResponse("json.xml", xml, response);
    }

    /**
     * Download uploaded file action.
     *
     * @param fileId   requested file id
     * @param response http response to write file
     * @throws FileNotAvailableException if user file is not available for given id
     */
    @RequestMapping(value = "/user_file")
    @Transactional
    public void downloadUserFile(@RequestParam int fileId, HttpServletRequest request, HttpServletResponse response)
            throws FileNotAvailableException {

        UserFile file = getUserFile(fileId, request);

        writeXmlFileToResponse(file.getName(), file.getContent(), response);
    }

    /**
     * Download uploaded file action.
     *
     * @param projectId project id for file download
     * @param fileName  requested file name
     * @param request   http request
     * @param response  http response to write file
     * @throws FileNotAvailableException if project file is not available for given id
     */
    @RequestMapping(value = "/project/{projectId}/file/{fileName:.*}")
    @Transactional
    public void downloadProjectFile(@PathVariable String projectId, @PathVariable String fileName, HttpServletRequest request,
            HttpServletResponse response) throws FileNotAvailableException {
        ProjectFile projectFile = projectFileService.fileContentBy(fileName, projectService.getByProjectId(projectId));
        if (projectFile == null) {
            throw new FileNotAvailableException("The requested project file is not available with path: /project/" +
                    projectId + "/file/" + fileName);
        }
        String disposition = request.getServletPath().contains("/download/") ? "attachment" : "inline";
        writeProjectFileToResponse(fileName, projectFile, response, disposition);
    }

    /**
     * Allows to download merge module file.
     *
     * @param moduleName module name.
     * @param response   http response to write file
     */
    @RequestMapping("/merge/file/{moduleName:.*}")
    @Transactional
    public void downloadMergeFile(@PathVariable String moduleName, HttpServletResponse response) {
        MergeModule module = mergeModules.findByFileName(moduleName);
        UploadedFile xslFile = module.getXslFile();
        writeXmlFileToResponse(xslFile.getName(), xslFile.getContent().getFileContent(), response);
    }

    /**
     * Merge selected user files.
     *
     * @param selectedUserFile ids of user files
     * @param mergeModule      module required to merge files.
     * @param response         http response
     * @throws TransformerException      if transformation fails
     * @throws IOException               if content operations fail.
     * @throws FileNotAvailableException if file is not available for given id
     */
    @RequestMapping("/merge/files")
    @Transactional
    public void mergeFiles(@RequestParam(required = false) List<Integer> selectedUserFile,
            @RequestParam(required = false) Integer mergeModule, HttpServletRequest request, HttpServletResponse response)
            throws TransformerException, IOException, FileNotAvailableException {
        if (selectedUserFile == null || selectedUserFile.isEmpty()) {
            throw new IllegalArgumentException("No files selected");
        }
        if (selectedUserFile.size() == 1) {
            downloadUserFile(selectedUserFile.get(0), request, response);
            return;
        }

        Collection<UserFile> userFiles = Collections2.transform(selectedUserFile, new Function<Integer, UserFile>() {
            @Override
            public UserFile apply(Integer id) {
                return userFileService.getById(id);
            }
        });

        if (mergeModule != null) {
            MergeModule module = mergeModules.findById(mergeModule);
            mergeFiles(userFiles, module, response);
            return;
        }

        Set<String> xmlSchemas = ImmutableSet.copyOf(Collections2.transform(userFiles, new Function<UserFile, String>() {
            @Override
            public String apply(UserFile userFile) {
                return userFile.getXmlSchema();
            }
        }));

        Collection<MergeModule> mergeModulesFound = mergeModules.findByXmlSchemas(xmlSchemas);
        if (mergeModulesFound.size() == 1) {
            mergeFiles(userFiles, mergeModulesFound.iterator().next(), response);
            return;
        }

        throw new MergeModuleChoiceRequiredException(userFiles, mergeModulesFound);
    }

    /**
     * Handler for case where automatic merge could not be performed. Such cases are:
     * <ul>
     * <li>Multiple modules found</li>
     * <li>No modules found</li>
     * </ul>
     *
     * @param e custom exception, holding modules and selected files
     * @return model and view
     */
    @ExceptionHandler(MergeModuleChoiceRequiredException.class)
    public ModelAndView mergeSelect(MergeModuleChoiceRequiredException e) {
        Map<String, Object> model = new HashMap<String, Object>();
        Collection<MergeModule> modules = e.getMergeModules();
        if (modules.isEmpty()) {
            modules = mergeModules.findAll();
        }
        model.put("mergeModules", modules);
        model.put("userFiles", e.getUserFiles());
        return new ModelAndView("merge_options", model);
    }

    /**
     * Performs conversion of specified {@link eionet.webq.dao.orm.UserFile} to specific format. Format is defined by conversionId.
     *
     * @param fileId       file id or xsl name, which will be used to convert file
     * @param conversionId id of conversion to be used
     * @param response     object where conversion result will be written
     */
    @RequestMapping("/convert")
    @Transactional
    public void convertXmlFile(@RequestParam int fileId, @RequestParam String conversionId, HttpServletResponse response) {
        UserFile fileContent = userFileService.getById(fileId);
        ResponseEntity<byte[]> convert = conversionService.convert(fileContent, conversionId);
        HttpHeaders headers = convert.getHeaders();
        setContentType(response, headers.getContentType());
        setContentDisposition(response, headers.getFirst("Content-Disposition"));
        writeToResponse(response, convert.getBody());
    }

    /**
     * Merge selected files.
     *
     * @param userFiles   list of selected files ids
     * @param mergeModule module used for merge
     * @param response    http response
     * @throws TransformerException if transformation fails.
     * @throws IOException          if content operations fail.
     */
    private void mergeFiles(Collection<UserFile> userFiles,
            MergeModule mergeModule, HttpServletResponse response) throws TransformerException, IOException {
        byte[] mergeResult = mergeService.mergeFiles(userFiles, mergeModule);
        writeXmlFileToResponse("merged_files.xml", mergeResult, response);
    }

    /**
     * Writes project file to response.
     *
     * @param name        file name
     * @param projectFile project file object
     * @param response    http response
     * @param disposition inline or attachment
     */
    private void
    writeProjectFileToResponse(String name, ProjectFile projectFile, HttpServletResponse response, String disposition) {

        ConfigurableMimeFileTypeMap mimeTypesMap = new ConfigurableMimeFileTypeMap();
        String contentType = mimeTypesMap.getContentType(name);

        // check if default
        if (mimeTypesMap.getContentType("").equals(contentType)) {
            if (name.endsWith(".xhtml")) {
                contentType = MediaType.APPLICATION_XHTML_XML_VALUE;
            } else if (name.endsWith(".js")) {
                contentType = "application/javascript";
            } else if (name.endsWith(".json")) {
                contentType = MediaType.APPLICATION_JSON_VALUE;
            } else {
                contentType = MediaType.APPLICATION_XML_VALUE;
            }
            // TODO check if there are more missing mime types
        }

        if (contentType.startsWith("text")) {
            contentType += ";charset=UTF-8";
        }
        response.setContentType(contentType);
        setContentDisposition(response, disposition + ";filename=" + name);
        if (projectFile.getUpdated() != null) {
            response.setDateHeader("Last-Modified", projectFile.getUpdated().getTime());
        } else if (projectFile.getCreated() != null) {
            response.setDateHeader("Last-Modified", projectFile.getCreated().getTime());
        }
        writeToResponse(response, projectFile.getFileContent());
    }

    /**
     * Writes xml files to response.
     *
     * @param name     file name
     * @param content  file content
     * @param response http response
     */
    private void writeXmlFileToResponse(String name, byte[] content, HttpServletResponse response) {
        addXmlFileHeaders(response, encodeAsUrl(name));
        writeToResponse(response, content);
    }

    /**
     * Sets headers required to xml file download.
     *
     * @param response http response
     * @param fileName file name
     */
    private void addXmlFileHeaders(HttpServletResponse response, String fileName) {
        setContentType(response, MediaType.APPLICATION_XML);
        setContentDisposition(response, "attachment;filename=" + fileName);
    }

    /**
     * Sets content disposition to response.
     *
     * @param response           {@link HttpServletResponse}
     * @param contentDisposition content disposition header value.
     */
    private void setContentDisposition(HttpServletResponse response, String contentDisposition) {
        if (contentDisposition != null) {
            response.setHeader("Content-Disposition", contentDisposition);
        }
    }

    /**
     * Sets content type header to response.
     *
     * @param response    http response
     * @param contentType content type
     */
    private void setContentType(HttpServletResponse response, MediaType contentType) {
        if (contentType != null) {
            response.setContentType(contentType.toString());
            response.setCharacterEncoding("utf-8");
        }
    }

    /**
     * {@link URLEncoder#encode(String, String)} with default value.
     *
     * @param path string to encode, also default value
     * @return encoded string or default value.
     */
    private String encodeAsUrl(String path) {
        try {
            return URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return path;
        }
    }

    /**
     * Writes specified content to http response.
     *
     * @param response http response
     * @param data     content to be written to response
     */
    private void writeToResponse(HttpServletResponse response, byte[] data) {
        ServletOutputStream output = null;
        try {
            response.setContentLength(data.length);
            boolean noCache = true;

            if (response.getContentType() != null && response.getContentType().startsWith("image")) {
                noCache = false;
            }
            if (noCache) {
                response.addHeader("Cache-control", "no-cache");
            }

            output = response.getOutputStream();
            IOUtils.write(data, output);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException("Unable to write response", e);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * Exception indicating that merge module choice is required.
     */
    public static class MergeModuleChoiceRequiredException extends RuntimeException {
        /**
         * Selected user files.
         */
        private Collection<UserFile> userFiles;
        /**
         * Available merge modules.
         */
        private Collection<MergeModule> mergeModules;

        /**
         * Initializes this object with user files and modules.
         *
         * @param userFiles    user files
         * @param mergeModules merge modules
         */
        public MergeModuleChoiceRequiredException(Collection<UserFile> userFiles, Collection<MergeModule> mergeModules) {
            this.userFiles = userFiles;
            this.mergeModules = mergeModules;
        }

        public Collection<UserFile> getUserFiles() {
            return userFiles;
        }

        public Collection<MergeModule> getMergeModules() {
            return mergeModules;
        }
    }

    /**
     * Get user file from database, using HTTP session jsessionid attribute or sessionid request paramater.
     *
     * @param fileId  file identifier
     * @param request HTTP request
     * @return UserFile object
     * @throws FileNotAvailableException if file is not found from database
     */
    private UserFile getUserFile(int fileId, HttpServletRequest request) throws FileNotAvailableException {

        UserFile file = userFileService.download(fileId);
        if (file == null && request.getParameter("sessionid") != null) {
            LOGGER.info("Could not find file by HTTP session ID. Let's try by sessionid parameter.");
            file = userFileService.getByIdAndUser(fileId, request.getParameter("sessionid"));
        }
        if (file == null) {
            LOGGER.error("Could not find file reference from database for session=" + request.getSession().getId());
        }

        if (file == null) {
            throw new FileNotAvailableException("The requested user file is not available with fileId: " + fileId);
        }
        return file;
    }
}
