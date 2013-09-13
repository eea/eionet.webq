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

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class RemoveExpiredUserFilesTaskTest {
    @Mock
    private SessionFactory factory;
    @Mock
    private Session session;
    @Mock
    private Query query;
    @Mock
    private Properties properties;
    @InjectMocks
    private RemoveExpiredUserFilesTask removeExpiredUserFilesTask;
    private int expirationTimeInHours = 1;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(properties.getProperty("user.file.expiration.hours")).thenReturn(Integer.toString(expirationTimeInHours));
        when(factory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setTimestamp(anyString(), any(Timestamp.class))).thenReturn(query);
    }

    @Test
    public void performsRemovalBasedOnConfiguredProperties() throws Exception {
        removeExpiredUserFilesTask.removeExpiredUserFiles();

        ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);
        verify(query).setTimestamp(anyString(), dateCaptor.capture());

        Date expectedDate = DateUtils.addHours(new Date(), -expirationTimeInHours);
        assertEquals(expectedDate.getTime(), dateCaptor.getValue().getTime(), 1000);
    }
}
