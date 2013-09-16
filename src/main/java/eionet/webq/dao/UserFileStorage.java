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

import eionet.webq.dto.UserFile;

import java.util.Collection;

/**
 */
public interface UserFileStorage {
    /**
     * Saves user file for userId.
     *
     * @param file file
     * @param userId userId
     * @return id in database
     */
    int save(UserFile file, String userId);

    /**
     * Get file content by file id and userId.
     *
     * @param id file id.
     * @param userId userId.
     * @return file content
     */
    UserFile fileContentBy(int id, String userId);

    /**
     * Updates user file.
     *
     * @param file file
     * @param userId userId
     */
    void update(UserFile file, String userId);

    /**
     * Lists all files for user id.
     *
     * @param userId user id
     * @return files collection
     */
    @SuppressWarnings("unchecked")
    Collection<UserFile> allFilesFor(String userId);

    /**
     * Removes files which belongs to user by file id.
     * @param userId user id
     * @param ids file ids
     */
    void remove(String userId, int... ids);
}
