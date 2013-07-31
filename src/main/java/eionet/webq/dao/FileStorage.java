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

import eionet.webq.dto.UploadedXmlFile;

import java.util.Collection;

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
     */
    void save(UploadedXmlFile file);

    /**
     * Fetches uploaded file from storage by specified id.
     * User access to this file must be checked.
     * Only {@link UploadedXmlFile#name} and {@link UploadedXmlFile#fileContent} will be set.
     *
     * @param id file id
     * @return uploaded file
     */
    UploadedXmlFile getById(int id);

    /**
     * All uploaded files by current session user.
     * {@link UploadedXmlFile#fileContent} is not included into resulting collection.
     * Use {@link FileStorage#getById(int)} for fetching specific file content.
     *
     * @return  All uploaded files by current session user.
     */
    Collection<UploadedXmlFile> allUploadedFiles();
}
