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

import eionet.webq.dao.WebFormStorage;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dto.WebFormType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoteWebFormServiceTest {
    @Mock
    private WebFormStorage storage;
    @InjectMocks
    private RemoteWebFormService webFormService;

    @Test
    public void fetchAllRemoteWebForms() throws Exception {
        Collection<ProjectFile> expected = Arrays.asList(new ProjectFile());
        when(storage.getAllActiveWebForms(WebFormType.REMOTE)).thenReturn(expected);

        Collection<ProjectFile> actual = webFormService.getAllActiveWebForms();

        assertTrue(expected == actual);
        verify(storage).getAllActiveWebForms(WebFormType.REMOTE);
    }

    @Test
    public void fetchRemoteWebFormById() throws Exception {
        ProjectFile expected = new ProjectFile();
        when(storage.getActiveWebFormById(WebFormType.REMOTE, 1)).thenReturn(expected);

        ProjectFile actual = webFormService.findActiveWebFormById(1);

        assertTrue(expected == actual);
        verify(storage).getActiveWebFormById(WebFormType.REMOTE, 1);
    }

    @Test
    public void fetchRemoteWebFormsFilteredByXmlSchemas() throws Exception {
        List<String> xmlSchemas = Arrays.asList("1", "2", "3");
        List<ProjectFile> expected = Arrays.asList(new ProjectFile());
        when(storage.findWebFormsForSchemas(WebFormType.REMOTE, xmlSchemas))
                .thenReturn(expected);

        Collection<ProjectFile> actual = webFormService.findWebFormsForNotEmptyXmlSchemas(xmlSchemas);

        assertTrue(expected == actual);
        verify(storage).findWebFormsForSchemas(WebFormType.REMOTE, xmlSchemas);
    }
}
