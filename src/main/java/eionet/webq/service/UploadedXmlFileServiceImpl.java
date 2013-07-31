package eionet.webq.service;

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

import eionet.webq.dao.FileStorage;
import eionet.webq.dto.UploadedXmlFile;
import java.util.Collection;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link UploadedXmlFileService} implementation.
 */
@Component
public class UploadedXmlFileServiceImpl implements UploadedXmlFileService {
    /**
     * Uploaded files storage.
     */
    @Autowired
    private FileStorage storage;
    /**
     * Current http session.
     */
    @Autowired
    private HttpSession session;
    /**
     * Static logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(UploadedXmlFileServiceImpl.class);

    @Override
    public void save(UploadedXmlFile file) {
        LOGGER.info("Saving uploaded file=" + file);
        storage.save(file, userId());
    }

    @Override
    public UploadedXmlFile getById(int id) {
        UploadedXmlFile uploadedXmlFile = storage.getById(id, userId());
        LOGGER.info("File loaded. Name=" + uploadedXmlFile.getName() + ", content size=" + uploadedXmlFile.getContent().length);
        return uploadedXmlFile;
    }

    @Override
    public Collection<UploadedXmlFile> allUploadedFiles() {
        String userId = userId();
        Collection<UploadedXmlFile> uploadedXmlFiles = storage.allUploadedFiles(userId);
        LOGGER.info("Loaded " + uploadedXmlFiles.size() + " files for user=" + userId);
        return uploadedXmlFiles;
    }

    /**
     * Provides current http session id.
     * @return current http session id
     */
    private String userId() {
        return session.getId();
    }
}
