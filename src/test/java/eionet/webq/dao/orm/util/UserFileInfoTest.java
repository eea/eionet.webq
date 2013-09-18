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
package eionet.webq.dao.orm.util;

import eionet.webq.dao.orm.UserFile;
import org.junit.Test;

import java.util.Date;

import static eionet.webq.dao.orm.util.UserFileInfo.isDownloadedAfterUpdate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserFileInfoTest {
    private static final Date NOW = new Date();
    private static final Date ONE_MILLISECOND_BEFORE_NOW = new Date(NOW.getTime() - 1);
    private static final Date ONE_MILLISECOND_AFTER_NOW = new Date(NOW.getTime() + 1);

    @Test
    public void fileNotDownloadedAfterUpdate() throws Exception {
        UserFile userFile = fileWithUpdatedAndDownloadedSet(NOW, ONE_MILLISECOND_BEFORE_NOW);
        assertFalse(isDownloadedAfterUpdate(userFile));
    }

    @Test
    public void fileWasNeverDownloadedAfterUpdate() throws Exception {
        UserFile userFile = fileWithUpdatedAndDownloadedSet(NOW, null);
        assertFalse(isDownloadedAfterUpdate(userFile));
    }

    @Test
    public void fileDownloadedAfterUpdate() throws Exception {
        assertTrue(isDownloadedAfterUpdate(fileWithUpdatedAndDownloadedSet(ONE_MILLISECOND_BEFORE_NOW, NOW)));
    }

    @Test
    public void fileUpdatedAfterUpload() throws Exception {
        UserFile userFile = fileWithCreatedAndUpdatedSet(ONE_MILLISECOND_BEFORE_NOW, NOW);
        assertTrue(UserFileInfo.isUpdatedAfterUpload(userFile));
    }

    @Test
    public void fileNotUpdatedAfterUpload() throws Exception {
        UserFile userFile = fileWithCreatedAndUpdatedSet(NOW, NOW);
        assertFalse(UserFileInfo.isUpdatedAfterUpload(userFile));
    }

    @Test
    public void fileDownloadedAfterUpdateUsingForm() throws Exception {
        UserFile userFile = fileWithCreatedUpdatedDownloadedSet(ONE_MILLISECOND_BEFORE_NOW, NOW, ONE_MILLISECOND_AFTER_NOW);
        assertTrue(UserFileInfo.isNotUpdatedOrDownloadedAfterUpdateUsingForm(userFile));
    }

    @Test
    public void fileNotDownloadedAfterUpdateUsingFormUpdateTimeEqualsDownloadTime() throws Exception {
        UserFile userFile = fileWithCreatedUpdatedDownloadedSet(ONE_MILLISECOND_BEFORE_NOW, NOW, NOW);
        assertFalse(UserFileInfo.isNotUpdatedOrDownloadedAfterUpdateUsingForm(userFile));
    }

    @Test
    public void fileNotDownloadedAfterUpdateUsingFormDownloadTimeLessThanUpdateTime() throws Exception {
        UserFile userFile = fileWithCreatedUpdatedDownloadedSet(ONE_MILLISECOND_BEFORE_NOW, NOW, ONE_MILLISECOND_BEFORE_NOW);
        assertFalse(UserFileInfo.isNotUpdatedOrDownloadedAfterUpdateUsingForm(userFile));
    }

    @Test
    public void fileNotDownloadedAfterUpdateUsingFormDownloadTimeIsNull() throws Exception {
        UserFile userFile = fileWithCreatedUpdatedDownloadedSet(ONE_MILLISECOND_BEFORE_NOW, NOW, null);
        assertFalse(UserFileInfo.isNotUpdatedOrDownloadedAfterUpdateUsingForm(userFile));
    }
    @Test
    public void fileNotUpdatedAndDownloaded() throws Exception {
        UserFile userFile = fileWithCreatedUpdatedDownloadedSet(NOW, NOW, ONE_MILLISECOND_AFTER_NOW);
        assertTrue(UserFileInfo.isNotUpdatedOrDownloadedAfterUpdateUsingForm(userFile));
    }

    private UserFile fileWithUpdatedAndDownloadedSet(Date updated, Date downloaded) {
        return fileWithCreatedUpdatedDownloadedSet(null, updated, downloaded);
    }

    private UserFile fileWithCreatedAndUpdatedSet(Date created, Date updated) {
        return fileWithCreatedUpdatedDownloadedSet(created, updated, null);
    }

    private  UserFile fileWithCreatedUpdatedDownloadedSet(Date created, Date updated, Date downloaded) {
        UserFile userFile = new UserFile();
        userFile.setCreated(created);
        userFile.setUpdated(updated);
        userFile.setDownloaded(downloaded);
        return userFile;
    }
}
