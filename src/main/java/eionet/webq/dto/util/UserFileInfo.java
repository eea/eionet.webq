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
package eionet.webq.dto.util;

import eionet.webq.dto.UserFile;

import java.util.Date;

/**
 * Provides utility methods for {@link eionet.webq.dto.UserFile}.
 */
public final class UserFileInfo {
    /**
     * Do not instantiate.
     */
    private UserFileInfo() {
    }

    /**
     * Checks whether this file was downloaded after change using form.
     *
     * @param userFile file
     * @return is downloaded after update using form.
     */
    public static boolean isNotUpdatedOrDownloadedAfterUpdateUsingForm(UserFile userFile) {
        return notUpdated(userFile) || (isUpdatedAfterUpload(userFile) && isDownloadedAfterUpdate(userFile));
    }

    /**
     * Checks whether file was downloaded after last update.
     *
     * @param userFile file
     * @return was file downloaded after update
     */
    public static boolean isDownloadedAfterUpdate(UserFile userFile) {
        Date downloaded = userFile.getDownloaded();
        return !(downloaded == null) && userFile.getUpdated().before(downloaded);
    }

    /**
     * Checks whether this file was updated after upload.
     *
     * @param userFile file
     * @return is updated after upload.
     */
    public static boolean isUpdatedAfterUpload(UserFile userFile) {
        return userFile.getUpdated().after(userFile.getCreated());
    }

    /**
     * Checks whether file was not updated.
     * @param userFile file
     * @return is file updated.
     */
    public static boolean notUpdated(UserFile userFile) {
        return userFile.getCreated().equals(userFile.getUpdated());
    }
}
