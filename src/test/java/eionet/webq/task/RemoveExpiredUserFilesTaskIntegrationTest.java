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
package eionet.webq.task;

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dto.UploadedFile;
import eionet.webq.dto.UserFile;
import eionet.webq.service.UserFileService;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
@Transactional
public class RemoveExpiredUserFilesTaskIntegrationTest {
    @Autowired
    RemoveExpiredUserFilesTask task;
    @Autowired
    UserFileService userFileService;
    @Autowired
    SessionFactory factory;

    @Before
    public void setUp() throws Exception {
        UserFile file = new UserFile(
                new UploadedFile("file-to-remove", "content-to-remove".getBytes()), "schema");
        userFileService.save(file);
    }

    @Test
    public void justCreatedFileWillNotBeRemoved() throws Exception {
        task.removeExpiredUserFiles();

        assertThat(userFileService.allUploadedFiles().size(), equalTo(1));
    }

    @Test
    public void removeFileIfItIsExpired() throws Exception {
        Date expired = DateUtils.addSeconds(DateUtils.addHours(new Date(), -task.getExpirationHours()), -1);
        factory.getCurrentSession().createQuery("UPDATE UserFile SET created=:created")
                .setTimestamp("created", new Timestamp(expired.getTime())).executeUpdate();

        assertThat(userFileService.allUploadedFiles().size(), equalTo(1));

        task.removeExpiredUserFiles();

        assertThat(userFileService.allUploadedFiles().size(), equalTo(0));
    }
}
