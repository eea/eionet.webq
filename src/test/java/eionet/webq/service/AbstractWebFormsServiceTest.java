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
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractWebFormsServiceTest {

    @Mock
    private WebFormStorage webFormStorage ;

    @InjectMocks
    private TestWebFormsService webFormsService;

    private final ProjectFile file1 = webFormWithXmlSchema("1");
    private final ProjectFile file2 = webFormWithXmlSchema("2");
    private final ProjectFile file3 = webFormWithXmlSchema("3");

    private static class TestWebFormsService extends AbstractWebFormsService {
        private List<ProjectFile> mockActiveForms = Collections.emptyList();

        public void setMockActiveForms(List<ProjectFile> mockActiveForms) {
            this.mockActiveForms = mockActiveForms;
        }

        @Override
        public List<ProjectFile> getAllActiveWebForms() {
            return this.mockActiveForms;
        }

        @Override
        protected WebFormType webFormsForType() {
            return WebFormType.LOCAL;
        }
    }

    @Test
    public void returnsAllAvailableFormsIfProvidedXmlSchemasArrayIsEmpty() {
        webFormsService.setMockActiveForms(Arrays.asList(file1, file2, file3));
        assertThat(webFormsService.findWebFormsForSchemas(new ArrayList<String>()).size(), equalTo(3));
    }

    @Test
    public void forNullXmlSchemasArgumentReturnTheSameResultAsForEmptyArray() {
        webFormsService.setMockActiveForms(Arrays.asList(file1, file2));
        assertThat(webFormsService.findWebFormsForSchemas(null), equalTo(webFormsService.findWebFormsForSchemas(new ArrayList<String>())));
    }

    @Test
    public void findWebFormsForSchemasReturnSpecificResultForSchemaInParameter() {
        when(webFormStorage.findWebFormsForSchemas(any(WebFormType.class), anyCollection()))
                .thenReturn(Collections.singletonList(file1));

        Collection<ProjectFile> forms = webFormsService.findWebFormsForSchemas(Collections.singletonList(file1.getXmlSchema()));

        assertThat(forms.size(), equalTo(1));
        assertThat(forms.iterator().next(), equalTo(file1));
        verify(webFormStorage).findWebFormsForSchemas(any(WebFormType.class), anyCollection());
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
