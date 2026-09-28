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

import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.service.WebFormService;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AvailableFormsServiceTest {
    @InjectMocks
    private AvailableFormsService availableFormsService;
    @Mock
    private WebFormService webFormService;
    @Mock
    private XmlRpcClient xmlRpcClient;
    private ProjectFile file1 = webFormWithXmlSchemaAndName("1");

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void nullArrayReferenceToGetFormParameterTransformedToEmptyListOfXmlSchemas() {
        availableFormsService.getForm(null);
    
        ArgumentCaptor<Collection> xmlSchemasCollection = ArgumentCaptor.forClass(Collection.class);
        verify(webFormService).findWebFormsForSchemas(xmlSchemasCollection.capture());
        assertTrue(xmlSchemasCollection.getValue().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void emptyArrayParameterToGetFormWillBeTransformedToEmptyCollection() {
        availableFormsService.getForm(new Object[0]);

        ArgumentCaptor<Collection> xmlSchemasCollection = ArgumentCaptor.forClass(Collection.class);
        verify(webFormService).findWebFormsForSchemas(xmlSchemasCollection.capture());
        assertTrue(xmlSchemasCollection.getValue().isEmpty());
    }

    @Test
    public void arrayWithValuesPassedToGetFormWillBeTransformedToCollectionWithValues() {
        availableFormsService.getForm(new Object[] {file1.getXmlSchema()});

        ArgumentCaptor<Collection> xmlSchemasCollection = ArgumentCaptor.forClass(Collection.class);
        verify(webFormService).findWebFormsForSchemas(xmlSchemasCollection.capture());

        assertThat(xmlSchemasCollection.getValue().size(), equalTo(1));
    }

    @Test
    public void returnsMapContainingXmlSchemaAsAKeyAndFileNameAsValue() {
        when(webFormService.findWebFormsForSchemas(anyCollection())).thenReturn(Collections.singletonList(file1));

        Map<String, String> forms = availableFormsService.getForm(null);
        assertThat(forms.size(), equalTo(1));
        assertThat(forms.get(file1.getXmlSchema()), equalTo(file1.getFileName()));
    }

    @Test
    public void returnsOnlyFirstFileNameForTheSameSchema() {
        ProjectFile fileWithSameSchemaAsFile1 = webFormWithXmlSchemaAndName("fileWithSameSchemaAsFile1", file1.getXmlSchema());
        when(webFormService.findWebFormsForSchemas(anyCollection()))
                .thenReturn(Arrays.asList(file1, fileWithSameSchemaAsFile1));

        Map<String, String> forms = availableFormsService.getForm(null);

        assertThat(forms.size(), equalTo(1));
        assertThat(forms.get(file1.getXmlSchema()), equalTo(file1.getFileName()));
    }

    @Test
    @Ignore
    // TODO: make this check for 404 error
    public void whenGetXFrom_ifNoFormsFound_askFormsFromWebQ1() {
        when(webFormService.findWebFormsForSchemas(anyCollection()))
                .thenReturn(Collections.<ProjectFile>emptyList());

        availableFormsService.getForm(null);
    }

    @Test
    public void whenGetForm_ifWebFormsFound_doNotAskWebQ1() {
        when(webFormService.findWebFormsForSchemas(anyCollection()))
                .thenReturn(Collections.singletonList(file1));

        verifyNoMoreInteractions(xmlRpcClient);
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
