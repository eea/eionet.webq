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
package eionet.webq.service;

import eionet.webq.dao.FileStorage;
import eionet.webq.dto.UserFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * {@link UploadedXmlFileService} implementation.
 */
@Service
public class UploadedXmlFileServiceImpl implements UploadedXmlFileService {
    /**
     * Uploaded files storage.
     */
    @Autowired
    @Qualifier("user-files")
    FileStorage<String, UserFile> storage;
    /**
     * Current http session.
     */
    @Autowired
    HttpSession session;
    /**
     * Service for getting conversion available for file.
     */
    @Autowired
    ConversionService conversionService;
    /**
     * Static logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(UploadedXmlFileServiceImpl.class);

    @Override
    public void save(UserFile file) {
        LOGGER.info("Saving uploaded file=" + file);
        storage.save(file, userId());
    }

    @Override
    public UserFile getById(int id) {
        UserFile userFile = storage.fileContentBy(id, userId());
        byte[] content = userFile.getContent();
        LOGGER.info("File loaded. Name=" + userFile.getName() + ", content size=" + (content != null ? content.length : 0));
        return userFile;
    }

    @Override
    public Collection<UserFile> allUploadedFiles() {
        String userId = userId();
        Collection<UserFile> userFiles = storage.allFilesFor(userId);
        LOGGER.info("Loaded " + userFiles.size() + " files for user=" + userId);
        return userFiles;
    }

    @Override
    public void updateContent(UserFile file) {
        String userId = userId();
        LOGGER.info("Updating file id=" + file.getId() + " for user=" + userId);
        storage.update(file, userId);
    }

    /**
     * Provides current http session id.
     *
     * @return current http session id
     */
    private String userId() {
        return session.getId();
    }
}
