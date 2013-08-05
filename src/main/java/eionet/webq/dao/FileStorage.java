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

package eionet.webq.dao;

import java.util.Collection;

import eionet.webq.dto.UploadedXmlFile;

/**
 * Interface for storing uploaded files.
 *
 * @see UploadedXmlFile
 */
public interface FileStorage {
    /**
     * Save data from uploaded file to storage.
     *
     * @param file uploaded file to be saved to storage
     * @param userId current user id
     */
    void save(UploadedXmlFile file, String userId);

    /**
     * Fetches uploaded file content from storage by specified id.
     * User access to this file must be checked.
     * Only {@link UploadedXmlFile#name} and {@link UploadedXmlFile#content} will be set to result.
     *
     *
     * @param id file id
     * @param userId current user id
     * @return uploaded file
     */
    UploadedXmlFile fileContentById(int id, String userId);

    /**
     * All uploaded files by current session user.
     * {@link UploadedXmlFile#content} is not included into resulting collection.
     * Use {@link FileStorage#fileContentById(int, String)} for fetching specific file content.
     *
     * @param userId current user id
     * @return  All uploaded files by current session user.
     */
    Collection<UploadedXmlFile> allUploadedFiles(String userId);

    /**
     * Update file content and updated time in storage.
     * Only {@link UploadedXmlFile#content}, {@link UploadedXmlFile#sizeInBytes} and {@link UploadedXmlFile#updated} will be set.
     *
     * @param file content to be updated in storage
     * @param userId current user id
     */
    void updateContent(UploadedXmlFile file, String userId);
}
