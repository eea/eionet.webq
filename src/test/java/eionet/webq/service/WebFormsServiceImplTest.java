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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 */
public class WebFormsServiceImplTest {
    @Mock
    private WebFormStorage storage;
    @InjectMocks
    private WebFormsServiceImpl webFormService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void asksStorageForFullListOfActiveWebForms() throws Exception {
        Collection<ProjectFile> expected = Arrays.asList(new ProjectFile());
        when(storage.getAllActiveWebForms(WebFormType.LOCAL)).thenReturn(expected);

        Collection<ProjectFile> actual = webFormService.getAllActiveWebForms();

        assertTrue(expected == actual);
        verify(storage).getAllActiveWebForms(WebFormType.LOCAL);
        verifyNoMoreInteractions(storage);
    }

    @Test
    public void findActiveWebFormById() throws Exception {
        ProjectFile expectedResult = new ProjectFile();
        when(storage.getActiveWebFormById(WebFormType.LOCAL, 1)).thenReturn(expectedResult);

        ProjectFile actualResult = webFormService.findActiveWebFormById(1);

        assertTrue(expectedResult == actualResult);
        verify(storage).getActiveWebFormById(WebFormType.LOCAL, 1);
    }

    @Test
    public void whenFilteringByXmlSchemas_performsSearchInStorage() throws Exception {
        List<String> xmlSchemas = Arrays.asList("1", "2");
        List<ProjectFile> result = Arrays.asList(new ProjectFile());
        when(storage.findWebFormsForSchemas(WebFormType.LOCAL, xmlSchemas))
                .thenReturn(result);

        Collection<ProjectFile> webForms = webFormService.findWebFormsForNotEmptyXmlSchemas(xmlSchemas);

        assertTrue(webForms == result);
        verify(storage).findWebFormsForSchemas(WebFormType.LOCAL, xmlSchemas);
    }
}
