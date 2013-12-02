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

import eionet.webq.dao.orm.ProjectFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractWebFormsServiceTest {
    @Spy
    private AbstractWebFormsService webFormsService = new AbstractWebFormsService() {
        @Override
        public Collection<ProjectFile> getAllActiveWebForms() {
            return null;
        }

        @Override
        public ProjectFile findActiveWebFormById(int id) {
            return null;
        }

        @Override
        protected Collection<ProjectFile> findWebFormsForNotEmptyXmlSchemas(Collection<String> xmlSchemas) {
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
        when(webFormsService.findWebFormsForNotEmptyXmlSchemas(anyCollectionOf(String.class)))
                .thenReturn(Arrays.asList(file1));

        Collection<ProjectFile> xForms = webFormsService.findWebFormsForSchemas(Arrays.asList(file1.getXmlSchema()));

        assertThat(xForms.size(), equalTo(1));
        assertThat(xForms.iterator().next(), equalTo(file1));
        verify(webFormsService).findWebFormsForNotEmptyXmlSchemas(anyCollectionOf(String.class));
    }

    private ProjectFile webFormWithXmlSchema(String fileName) {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setXmlSchema("schema" + fileName);
        return projectFile;
    }
}
