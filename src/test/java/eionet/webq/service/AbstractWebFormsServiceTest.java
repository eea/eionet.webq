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
 *        Raptis Dimos
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractWebFormsServiceTest {
    @Mock
    private WebFormStorage storage;
    @InjectMocks
    private AbstractWebFormsService webFormsService = new AbstractWebFormsService() {
        @Override
        protected WebFormType webFormsForType() {
            return null;
        }
    };

    private ProjectFile file1 = webFormWithXmlSchema("1");
    private ProjectFile file2 = webFormWithXmlSchema("2");
    private ProjectFile file3 = webFormWithXmlSchema("3");


    @Test
    public void returnsAllAvailableFormsIfProvidedXmlSchemasArrayIsEmpty() throws Exception {
        when(webFormsService.getAllActiveWebForms()).thenReturn(Arrays.asList(file1, file2, file3));

        assertThat(webFormsService.findWebFormsForSchemas(new ArrayList<String>()).size(), equalTo(3));
    }

    @Test
    public void forNullXmlSchemasArgumentReturnTheSameResultAsForEmptyArray() throws Exception {
        when(webFormsService.getAllActiveWebForms()).thenReturn(Arrays.asList(file1, file2));

        assertThat(webFormsService.findWebFormsForSchemas(null), equalTo(webFormsService.findWebFormsForSchemas(new ArrayList<String>())));
    }

    @Test
    public void findWebFormsForSchemasReturnSpecificResultForSchemaInParameter() throws Exception {
        when(storage.findWebFormsForSchemas(any(WebFormType.class), anyCollectionOf(String.class)))
                .thenReturn(Arrays.asList(file1));

        Collection<ProjectFile> xForms = webFormsService.findWebFormsForSchemas(Arrays.asList(file1.getXmlSchema()));

        assertThat(xForms.size(), equalTo(1));
        assertThat(xForms.iterator().next(), equalTo(file1));
        verify(storage).findWebFormsForSchemas(any(WebFormType.class), anyCollectionOf(String.class));
    }
    
    @Test
    public void testWebformSorting(){
        ProjectFile p1 = new ProjectFile();
        ProjectFile p2 = new ProjectFile();
        ProjectFile p3 = new ProjectFile();
        p1.setTitle("a");
        p2.setTitle("b");
        p3.setTitle("c");
        
        Collection<ProjectFile> webformsSet = new HashSet<ProjectFile>(Arrays.asList(p2,p1,p3));
        List<ProjectFile> expectedList = new ArrayList<ProjectFile> (Arrays.asList(p1,p2,p3));
        
        List<ProjectFile> orderedList = webFormsService.sortWebformsAlphabetically(webformsSet);
        
        assertEquals("Size of ordered set of webforms does not match", expectedList.size(), webformsSet.size());
        
        for(int i=0; i<orderedList.size() ; i++)
            assertEquals("Element from ordered set of webforms does not match", expectedList.get(i).getTitle(), orderedList.get(i).getTitle());
        
    }

    private ProjectFile webFormWithXmlSchema(String fileName) {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setXmlSchema("schema" + fileName);
        return projectFile;
    }
}
