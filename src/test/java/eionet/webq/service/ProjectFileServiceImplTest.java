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

import eionet.webq.converter.XmlSchemaExtractor;
import eionet.webq.dao.FileStorage;
import eionet.webq.dao.ProjectFileStorageImpl;
import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.ProjectFile;
import eionet.webq.dto.ProjectFileType;
import eionet.webq.dto.UploadedFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

import static eionet.webq.dto.ProjectFileType.FILE;
import static eionet.webq.dto.ProjectFileType.WEBFORM;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ProjectFileServiceImplTest {
    ProjectFileServiceImpl service = new ProjectFileServiceImpl();
    FileStorage<ProjectEntry, ProjectFile> projectFileStorage = mock(ProjectFileStorageImpl.class);
    XmlSchemaExtractor xmlSchemaExtractor = mock(XmlSchemaExtractor.class);
    ProjectFile testFile = new ProjectFile();
    ProjectEntry testProject = new ProjectEntry();

    @Before
    public void setUp() throws Exception {
        service.projectFileStorage = projectFileStorage;
        service.xmlSchemaExtractor = xmlSchemaExtractor;
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(projectFileStorage);
        verifyNoMoreInteractions(xmlSchemaExtractor);
    }

    @Test
    public void saveFileIfNoIdSet() throws Exception {
        testFile.setFileType(FILE);
        service.saveOrUpdate(testFile, testProject);

        verify(projectFileStorage).save(testFile, testProject);
        verify(xmlSchemaExtractor).extractXmlSchema(null);
    }

    @Test
    public void whenSavingFileXmlSchemaWillBeExtractedAndSet() throws Exception {
        String expectedXmlSchema = "expected-xml-schema";
        when(xmlSchemaExtractor.extractXmlSchema(any(byte[].class))).thenReturn(expectedXmlSchema);
        testFile.setFile(new UploadedFile("test.xml", "dummy-content".getBytes()));
        testFile.setFileType(FILE);

        service.saveOrUpdate(testFile, testProject);

        assertThat(testFile.getXmlSchema(), equalTo(expectedXmlSchema));
        verify(projectFileStorage).save(testFile, testProject);
        verify(xmlSchemaExtractor).extractXmlSchema(any(byte[].class));
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionIfProjectTypeNotSet() throws Exception {
        service.saveOrUpdate(testFile, testProject);
    }

    @Test
    public void testFileById() throws Exception {
        service.getById(1);

        verify(projectFileStorage).fileById(1);
    }

    @Test
    public void updateFileIfIdIsSet() throws Exception {
        testFile.setId(1);
        service.saveOrUpdate(testFile, testProject);

        verify(projectFileStorage).update(testFile, testProject);
    }

    @Test
    public void testAllFilesFor() throws Exception {
        service.filesDividedByTypeFor(testProject);

        verify(projectFileStorage).allFilesFor(testProject);
    }

    @Test
    public void whenFetchingAllFilesTheyAreDividedByType() throws Exception {
        ProjectFile projectXmlFile = fileWithType(FILE);
        ProjectFile webform = fileWithType(WEBFORM);
        when(projectFileStorage.allFilesFor(testProject))
                .thenReturn(Arrays.asList(projectXmlFile, webform));

        MultiValueMap<ProjectFileType,ProjectFile> filesByType = service.filesDividedByTypeFor(testProject);

        assertThat(filesByType.get(FILE), equalTo(Arrays.asList(projectXmlFile)));
        assertThat(filesByType.get(WEBFORM), equalTo(Arrays.asList(webform)));
        verify(projectFileStorage).allFilesFor(testProject);
    }

    @Test
    public void testFileContentBy() throws Exception {
        service.fileContentBy(1, testProject);

        verify(projectFileStorage).fileContentBy(1, testProject);
    }

    @Test
    public void testRemove() throws Exception {
        service.remove(1, testProject);

        verify(projectFileStorage).remove(testProject, 1);
    }

    private ProjectFile fileWithType(ProjectFileType type) {
        ProjectFile file = new ProjectFile();
        file.setFileType(type);
        return file;
    }
}
