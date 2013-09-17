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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class AvailableFormsServiceTest {
    @InjectMocks
    private AvailableFormsService availableFormsService;
    @Mock
    private WebFormService webFormService;
    private ProjectFile file1 = webFormWithXmlSchemaAndName("1");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void nullArrayReferenceToGetXFormParameterTransformedToEmptyListOfXmlSchemas() throws Exception {
        availableFormsService.getXForm(null);
    
        ArgumentCaptor<Collection> xmlSchemasCollection = ArgumentCaptor.forClass(Collection.class);
        verify(webFormService).findWebFormsForSchemas(xmlSchemasCollection.capture());
        assertTrue(xmlSchemasCollection.getValue().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void emptyArrayParameterToGetXFormWillBeTransformedToEmptyCollection() throws Exception {
        availableFormsService.getXForm(new Object[0]);

        ArgumentCaptor<Collection> xmlSchemasCollection = ArgumentCaptor.forClass(Collection.class);
        verify(webFormService).findWebFormsForSchemas(xmlSchemasCollection.capture());
        assertTrue(xmlSchemasCollection.getValue().isEmpty());
    }

    @Test
    public void arrayWithValuesPassedToGetXFormWillBeTransformedToCollectionWithValues() throws Exception {
        availableFormsService.getXForm(new Object[] {file1.getXmlSchema()});

        ArgumentCaptor<Collection> xmlSchemasCollection = ArgumentCaptor.forClass(Collection.class);
        verify(webFormService).findWebFormsForSchemas(xmlSchemasCollection.capture());

        assertThat(xmlSchemasCollection.getValue().size(), equalTo(1));
    }

    @Test
    public void returnsMapContainingXmlSchemaAsAKeyAndFileNameAsValue() throws Exception {
        when(webFormService.findWebFormsForSchemas(anyCollectionOf(String.class))).thenReturn(Arrays.asList(file1));

        Map<String, String> xForms = availableFormsService.getXForm(null);
        assertThat(xForms.size(), equalTo(1));
        assertThat(xForms.get(file1.getXmlSchema()), equalTo(file1.getFileName()));
    }

    @Test
    public void returnsOnlyFirstFileNameForTheSameSchema() throws Exception {
        ProjectFile fileWithSameSchemaAsFile1 = webFormWithXmlSchemaAndName("fileWithSameSchemaAsFile1", file1.getXmlSchema());
        when(webFormService.findWebFormsForSchemas(anyCollectionOf(String.class)))
                .thenReturn(Arrays.asList(file1, fileWithSameSchemaAsFile1));

        Map<String, String> xForms = availableFormsService.getXForm(null);

        assertThat(xForms.size(), equalTo(1));
        assertThat(xForms.get(file1.getXmlSchema()), equalTo(file1.getFileName()));
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
