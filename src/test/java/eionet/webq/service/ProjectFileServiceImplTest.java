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
import eionet.webq.dao.ProjectFileStorage;
import eionet.webq.dao.orm.ProjectEntry;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.ProjectFileType;
import eionet.webq.dao.orm.UploadedFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

import static eionet.webq.dao.orm.ProjectFileType.FILE;
import static eionet.webq.dao.orm.ProjectFileType.WEBFORM;
import eionet.webq.service.impl.project.export.ArchiveFile;
import eionet.webq.service.impl.project.export.ImportProjectResult;
import eionet.webq.service.impl.project.export.ProjectMetadata;
import eionet.webq.service.impl.project.export.json.ProjectMetadataJsonSerializer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import static org.hamcrest.core.IsEqual.equalTo;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import util.ArchivingUtil;

public class ProjectFileServiceImplTest {
    
    @Spy
    private ProjectMetadataJsonSerializer projectMetadataSerializer;
    
    @Mock
    private ProjectFileStorage projectFileStorage;
    
    @Mock
    private XmlSchemaExtractor xmlSchemaExtractor;
    
    @InjectMocks
    private ProjectFileService service = new ProjectFileServiceImpl();
    
    private ProjectFile testFile = new ProjectFile();
    private ProjectEntry testProject = new ProjectEntry();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
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

        verify(projectFileStorage).findById(1);
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

        verify(projectFileStorage).findAllFilesFor(testProject);
    }

    @Test
    public void whenFetchingAllFilesTheyAreDividedByType() throws Exception {
        ProjectFile projectXmlFile = fileWithType(FILE);
        ProjectFile webform = fileWithType(WEBFORM);
        when(projectFileStorage.findAllFilesFor(testProject))
                .thenReturn(Arrays.asList(projectXmlFile, webform));

        MultiValueMap<ProjectFileType,ProjectFile> filesByType = service.filesDividedByTypeFor(testProject);

        assertThat(filesByType.get(FILE), equalTo(Arrays.asList(projectXmlFile)));
        assertThat(filesByType.get(WEBFORM), equalTo(Arrays.asList(webform)));
        verify(projectFileStorage).findAllFilesFor(testProject);
    }

    @Test
    public void testFileContentBy() throws Exception {
        service.fileContentBy("name", testProject);

        verify(projectFileStorage).findByNameAndProject("name", testProject);
    }

    @Test
    public void testRemove() throws Exception {
        service.remove(testProject, 1);

        verify(projectFileStorage).remove(testProject, 1);
    }

    @Test
    public void callsAllProjectFiles() throws Exception {
        ProjectEntry project = new ProjectEntry();
        service.allFilesFor(project);

        verify(projectFileStorage).findAllFilesFor(project);
    }

    @Test
    public void savesNewFileContent() throws Exception {
        when(projectFileStorage.findById(1)).thenReturn(testFile);
        service.updateContent(1, "new-content".getBytes(), testProject);

        verify(projectFileStorage).findById(1);
        verify(projectFileStorage).update(testFile, testProject);
    }
    
    @Test
    public void testExportProject() throws Exception {
        ProjectFile dummyProjectFile = new ProjectFile();
        dummyProjectFile.setFileName("test.html");
        dummyProjectFile.setFileContent(new byte[] { 1 });
        when(projectFileStorage.findAllFilesFor(any(ProjectEntry.class))).thenReturn(Arrays.asList(dummyProjectFile));
        byte[] archiveContent = service.exportToArchive(testProject);
        
        List<ArchiveFile> files = ArchivingUtil.extractArchive(archiveContent);
        Assert.assertEquals(2, files.size());
        
        ArchiveFile metadataFile;
        ArchiveFile archiveFile = files.get(0);
        
        if (archiveFile.getName().endsWith(".metadata")) {
            metadataFile = archiveFile;
            archiveFile = files.get(1);
        }
        else {
            metadataFile = files.get(1);
        }
        
        ProjectMetadata metadata = this.projectMetadataSerializer.deserialize(new String(metadataFile.getContent(), Charset.forName("UTF-8")));
        Assert.assertEquals(1, metadata.getProjectFiles().length);
        ProjectFile fileMetadata = metadata.getProjectFiles()[0];
        Assert.assertEquals(archiveFile.getName(), fileMetadata.getFileName());
        Assert.assertEquals(dummyProjectFile.getFileName(), fileMetadata.getFileName());
        
        verify(projectFileStorage).findAllFilesFor(any(ProjectEntry.class));
    }
    
    @Test
    public void testImportFailureNoMetadata() throws Exception {
        ArchiveFile file1 = new ArchiveFile("dummy.html", new byte[] { });
        byte[] archiveContent = ArchivingUtil.createArchive(Arrays.asList(file1));
        ImportProjectResult result = this.service.importFromArchive(testProject, archiveContent, "user");
        
        Assert.assertEquals(ImportProjectResult.ErrorType.ARCHIVE_METADATA_NOT_FOUND, result.getErrorType());
    }
    
    @Test
    public void testImportFailureMalformedMetadata() throws Exception {
        final String metadata = "{ \"projectFiles\":[ }";
        ArchiveFile metadataFile = new ArchiveFile(ProjectFileServiceImpl.PROJECT_EXPORT_METADATA_FILE, metadata.getBytes("UTF-8"));
        byte[] archiveContent = ArchivingUtil.createArchive(Arrays.asList(metadataFile));
        ImportProjectResult result = this.service.importFromArchive(testProject, archiveContent, "user");
        
        Assert.assertEquals(ImportProjectResult.ErrorType.MALFORMED_ARCHIVE_METADATA, result.getErrorType());
    }
    
    @Test
    public void testImportFailureInvalidMetadata() throws Exception {
        final String metadata = "{ \"projectFiles\":[ { \"file\": { \"name\": \"\" } } ] }";
        ArchiveFile metadataFile = new ArchiveFile(ProjectFileServiceImpl.PROJECT_EXPORT_METADATA_FILE, metadata.getBytes("UTF-8"));
        byte[] archiveContent = ArchivingUtil.createArchive(Arrays.asList(metadataFile));
        ImportProjectResult result = this.service.importFromArchive(testProject, archiveContent, "user");
        
        Assert.assertEquals(ImportProjectResult.ErrorType.INVALID_ARCHIVE_METADATA, result.getErrorType());
    }
    
    @Test
    public void testImportFailureInvalidArchiveStructure() throws Exception {
        final String metadata = "{ \"projectFiles\":[ { \"file\": { \"name\": \"somefile.html\" } } ] }";
        ArchiveFile metadataFile = new ArchiveFile(ProjectFileServiceImpl.PROJECT_EXPORT_METADATA_FILE, metadata.getBytes("UTF-8"));
        ArchiveFile file1 = new ArchiveFile("someotherfile.html", new byte[] { 1, 2, 3 });
        byte[] archiveContent = ArchivingUtil.createArchive(Arrays.asList(file1, metadataFile));
        ImportProjectResult result = this.service.importFromArchive(testProject, archiveContent, "user");
        
        Assert.assertEquals(ImportProjectResult.ErrorType.INVALID_ARCHIVE_STRUCTURE, result.getErrorType());
    }
    
    @Test
    public void testImportZeroFiles() throws Exception {
        final String metadata = "{ \"projectFiles\":[] }";
        ArchiveFile metadataFile = new ArchiveFile(ProjectFileServiceImpl.PROJECT_EXPORT_METADATA_FILE, metadata.getBytes("UTF-8"));
        byte[] archiveContent = ArchivingUtil.createArchive(Arrays.asList(metadataFile));
        ImportProjectResult result = this.service.importFromArchive(testProject, archiveContent, "user");
        
        Assert.assertEquals(ImportProjectResult.ErrorType.NONE, result.getErrorType());
        
        verify(this.projectFileStorage).cleanInsert(any(ProjectEntry.class), any(Collection.class));
    }
    
    @Test
    public void testImportOneFile() throws Exception {
        final String metadata = "{ \"projectFiles\":[ { \"file\": { \"name\": \"somefile.html\" } } ] }";
        ArchiveFile metadataFile = new ArchiveFile(ProjectFileServiceImpl.PROJECT_EXPORT_METADATA_FILE, metadata.getBytes("UTF-8"));
        ArchiveFile file1 = new ArchiveFile("somefile.html", new byte[] { 1, 2, 3 });
        byte[] archiveContent = ArchivingUtil.createArchive(Arrays.asList(file1, metadataFile));
        ImportProjectResult result = this.service.importFromArchive(testProject, archiveContent, "user");
        
        Assert.assertEquals(ImportProjectResult.ErrorType.NONE, result.getErrorType());
         
        verify(this.projectFileStorage).cleanInsert(any(ProjectEntry.class), any(Collection.class));
    }

    private ProjectFile fileWithType(ProjectFileType type) {
        ProjectFile file = new ProjectFile();
        file.setFileType(type);
        return file;
    }
}
