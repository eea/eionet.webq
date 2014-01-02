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

import eionet.webq.dao.UserFileDownload;
import eionet.webq.dao.UserFileStorage;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.UserFileIdUpdate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * {@link UserFileService} implementation.
 */
@Service
public class UserFileServiceImpl implements UserFileService {
    /**
     * Uploaded files storage.
     */
    @Autowired
    UserFileStorage storage;
    /**
     * Current http request.
     */
    @Autowired(required = false)
    HttpServletRequest request;
    /**
     * User id provider.
     */
    @Autowired
    UserIdProvider userIdProvider;
    /**
     * Service for getting conversion available for file.
     */
    @Autowired
    ConversionService conversionService;
    /**
     * Operation for file download.
     */
    @Autowired
    UserFileDownload userFileDownload;
    /**
     * Remote file service.
     */
    @Autowired
    RemoteFileService remoteFileService;

    /**
     * Static logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(UserFileServiceImpl.class);

    @Override
    public int save(UserFile file) {
        String userAgent = request != null ? request.getHeader("user-agent") : null;
        file.setUserAgent(userAgent);

        LOGGER.info("Saving uploaded file=" + file + ", user agent=" + userAgent);
        return storage.save(file, userId());
    }

    @Override
    public int saveBasedOnWebForm(UserFile file, ProjectFile webForm) throws FileNotAvailableException {
        String emptyInstanceUrl = webForm.getEmptyInstanceUrl();
        file.setName(defaultIfEmpty(file.getName(), defaultIfEmpty(webForm.getNewXmlFileName(), "new_form.xml")));
        file.setXmlSchema(webForm.getXmlSchema());
        return isNotEmpty(emptyInstanceUrl)
                ? saveWithContentFromRemoteLocation(file, emptyInstanceUrl)
                : save(file);
    }

    @Override
    public UserFile getById(int id) {
        UserFile userFile = storage.findFile(id, userId());
        LOGGER.info("Loaded user file=" + userFile);
        return userFile;
    }

    @Override
    public UserFile getByIdAndUser(int id, String userId) {
        UserFile userFile = storage.findFile(id, userId);
        LOGGER.info("Loaded user file=" + userFile);
        return userFile;
    }

    @Override
    public UserFile download(int id) {
        userFileDownload.updateDownloadTime(id);
        return getById(id);
    }

    @Override
    public Collection<UserFile> allUploadedFiles() {
        String userId = userId();
        Collection<UserFile> userFiles = storage.findAllUserFiles(userId);
        LOGGER.info("Loaded " + userFiles.size() + " files for user=" + userId);
        return userFiles;
    }

    @Override
    public void updateContent(UserFile file) {
        String userId = userId();
        LOGGER.info("Updating file id=" + file.getId() + " for user=" + userId);
        storage.update(file, userId);
    }

    @Override
    public void removeFilesById(int[] fileIds) {
        String userId = userId();
        LOGGER.info("Removing files:" + Arrays.toString(fileIds) + " for user=" + userId);
        storage.remove(userId, fileIds);
    }

    @Override
    public void updateUserId(String oldUserId, String newUserId) {
        if (userId().equals(newUserId)) {
            UserFileIdUpdate updateData = new UserFileIdUpdate();
            updateData.setNewUserId(newUserId);
            updateData.setOldUserId(oldUserId);
            storage.updateUserId(updateData);
        }
    }

    /**
     * Set file content from remote location and saves it.
     * @param file file
     * @param url file content remote location
     * @return file id in storage
     * @throws FileNotAvailableException if file not available from remote location
     */
    private int saveWithContentFromRemoteLocation(UserFile file, String url) throws FileNotAvailableException {
        file.setContent(remoteFileService.fileContent(url));
        return save(file);
    }
    /**
     * Provides current http session id.
     *
     * @return current http session id
     */
    String userId() {
        return userIdProvider.getUserId();
    }
}
