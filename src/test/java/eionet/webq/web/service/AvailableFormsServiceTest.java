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
package eionet.webq.web.service;

import eionet.webq.dto.ProjectFile;
import eionet.webq.service.WebFormService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 */
public class AvailableFormsServiceTest {
    @InjectMocks
    private AvailableFormsService availableFormsService;
    @Mock
    private WebFormService webFormService;
    private ProjectFile file1 = webFormWithXmlSchemaAndName("1");
    private ProjectFile file2 = webFormWithXmlSchemaAndName("2");
    private ProjectFile file3 = webFormWithXmlSchemaAndName("3");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void returnsAllAvailableFormsIfProvidedXmlSchemasArrayIsEmpty() throws Exception {
        when(webFormService.allActiveWebForms()).thenReturn(Arrays.asList(file1, file2, file3));

        assertThat(availableFormsService.getXForm(new Object[0]).size(), equalTo(3));
    }

    @Test
    public void forNullXmlSchemasArgumentToGetXFormReturnTheSameResultAsForEmptyArray() throws Exception {
        when(webFormService.allActiveWebForms()).thenReturn(Arrays.asList(file1, file2));

        assertThat(availableFormsService.getXForm(null), equalTo(availableFormsService.getXForm(new Object[0])));
    }

    @Test
    public void returnsMapContainingXmlSchemaAsAKeyAndFileNameAsValue() throws Exception {
        when(webFormService.allActiveWebForms()).thenReturn(Arrays.asList(file1));

        Map<String, String> xForms = availableFormsService.getXForm(null);
        assertThat(xForms.size(), equalTo(1));
        assertThat(xForms.get(file1.getXmlSchema()), equalTo(file1.getFileName()));
    }

    @Test
    public void returnsOnlyFirstFileNameForTheSameSchema() throws Exception {
        ProjectFile fileWithSameSchemaAsFile1 = webFormWithXmlSchemaAndName("fileWithSameSchemaAsFile1", file1.getXmlSchema());
        when(webFormService.allActiveWebForms()).thenReturn(Arrays.asList(file1, fileWithSameSchemaAsFile1));

        Map<String, String> xForms = availableFormsService.getXForm(null);

        assertThat(xForms.size(), equalTo(1));
        assertThat(xForms.get(file1.getXmlSchema()), equalTo(file1.getFileName()));
    }

    @Test
    public void getXFormReturnSpecificResultForSchemaInParameter() throws Exception {
        when(webFormService.allActiveWebForms()).thenReturn(Arrays.asList(file1, file2));

        Map<String, String> xForms = availableFormsService.getXForm(new Object[]{file1.getXmlSchema()});

        assertThat(xForms.size(), equalTo(1));
        assertTrue(xForms.containsKey(file1.getXmlSchema()));
        assertTrue(xForms.containsValue(file1.getFileName()));
    }

    @Test
    public void allowToSpecifyMoreThanOneXmlSchema() throws Exception {
        when(webFormService.allActiveWebForms()).thenReturn(Arrays.asList(file1, file2, file3));

        Map<String, String> xForms = availableFormsService.getXForm(new Object[]{file1.getXmlSchema(), file3.getXmlSchema()});

        assertThat(xForms.size(), equalTo(2));
        assertTrue(xForms.containsKey(file1.getXmlSchema()));
        assertTrue(xForms.containsKey(file3.getXmlSchema()));
    }

    private ProjectFile webFormWithXmlSchemaAndName(String fileName) {
        return webFormWithXmlSchemaAndName(fileName, "schema" + fileName);
    }

    private ProjectFile webFormWithXmlSchemaAndName(String fileName, String xmlSchema) {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setFileName(fileName);
        projectFile.setXmlSchema(xmlSchema);
        return projectFile;
    }
}
