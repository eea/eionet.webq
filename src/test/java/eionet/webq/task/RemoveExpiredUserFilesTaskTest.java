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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 */
public class RemoveExpiredUserFilesTaskTest {
    @Mock
    JdbcTemplate template;
    
    @Mock
    Properties properties;

    @InjectMocks
    RemoveExpiredUserFilesTask removeExpiredUserFilesTask;
    private int expirationTimeInHours = 1;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(properties.getProperty("user.file.expiration.hours")).thenReturn(Integer.toString(expirationTimeInHours));
    }

    @Test
    public void performsRemovalBasedOnConfiguredProperties() throws Exception {
        removeExpiredUserFilesTask.removeExpiredUserFiles();

        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(template).update(anyString(), dateCaptor.capture());

        Date expectedDate = DateUtils.addHours(new Date(), -expirationTimeInHours);
        assertEquals(expectedDate.getTime(), dateCaptor.getValue().getTime(), 1000);
    }
}
