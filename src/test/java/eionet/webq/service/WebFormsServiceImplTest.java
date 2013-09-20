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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
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
    private ProjectFile file1 = webFormWithXmlSchema("1");
    private ProjectFile file2 = webFormWithXmlSchema("2");
    private ProjectFile file3 = webFormWithXmlSchema("3");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void asksStorageForFullListOfActiveWebForms() throws Exception {
        webFormService.getAllActiveWebForms();

        verify(storage).getAllActiveWebForms();
        verifyNoMoreInteractions(storage);
    }

    @Test
    public void returnsAllAvailableFormsIfProvidedXmlSchemasArrayIsEmpty() throws Exception {
        when(storage.getAllActiveWebForms()).thenReturn(Arrays.asList(file1, file2, file3));

        assertThat(webFormService.findWebFormsForSchemas(new ArrayList<String>()).size(), equalTo(3));
    }

    @Test
    public void forNullXmlSchemasArgumentReturnTheSameResultAsForEmptyArray() throws Exception {
        when(storage.getAllActiveWebForms()).thenReturn(Arrays.asList(file1, file2));

        assertThat(webFormService.findWebFormsForSchemas(null), equalTo(webFormService.findWebFormsForSchemas(new ArrayList<String>())));
    }

    @Test
    public void findWebFormsForSchemasReturnSpecificResultForSchemaInParameter() throws Exception {
        when(storage.getAllActiveWebForms()).thenReturn(Arrays.asList(file1, file2));

        Collection<ProjectFile> xForms = webFormService.findWebFormsForSchemas(Arrays.asList(file1.getXmlSchema()));

        assertThat(xForms.size(), equalTo(1));
        assertThat(xForms.iterator().next(), equalTo(file1));
    }

    @Test
    public void allowToSpecifyMoreThanOneXmlSchema() throws Exception {
        when(storage.getAllActiveWebForms()).thenReturn(Arrays.asList(file1, file2, file3));

        Collection<ProjectFile> xForms = webFormService.findWebFormsForSchemas(Arrays.asList(file1.getXmlSchema(), file3.getXmlSchema()));

        assertThat(xForms.size(), equalTo(2));
        Iterator<ProjectFile> it = xForms.iterator();
        assertThat(it.next(), equalTo(file1));
        assertThat(it.next(), equalTo(file3));
    }

    @Test
    public void findActiveWebFormById() throws Exception {
        int webFormId = Integer.MAX_VALUE / 42;
        webFormService.findActiveWebFormById(webFormId);

        verify(storage).getActiveWebFormById(webFormId);
    }

    private ProjectFile webFormWithXmlSchema(String fileName) {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setXmlSchema("schema" + fileName);
        return projectFile;
    }

}
