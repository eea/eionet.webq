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
package eionet.webq.dao;

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.ProjectFile;
import eionet.webq.dto.ProjectFileType;
import eionet.webq.dto.UploadedFile;
import eionet.webq.dto.util.ProjectFileInfo;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Iterator;

import static eionet.webq.dto.ProjectFileType.FILE;
import static eionet.webq.dto.ProjectFileType.WEBFORM;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
@Transactional
public class ProjectFileStorageImplTest {

    @Autowired
    ProjectFileStorage projectFileStorage;
    @Autowired
    SessionFactory sessionFactory;

    private Session currentSession;
    private ProjectEntry projectEntry = testProjectEntry(1);
    private ProjectFile testFileForUpload = projectFileWithoutTypeSet();
    private ProjectFile defaultProjectFile;

    @Before
    public void setUp() throws Exception {
        defaultProjectFile = projectFileWithFileType(WEBFORM);
        defaultProjectFile.setFileName("UniqueName");
        projectFileStorage.save(defaultProjectFile, testProjectEntry(2));
        currentSession = sessionFactory.getCurrentSession();
    }

    @Test
    public void saveWebformWithoutException() throws Exception {
        ProjectFile projectFile = projectFileWithoutTypeSet();
        projectFileStorage.save(projectFile, projectEntry);
    }

    @Test
    public void emptyCollectionIfFilesForProjectNotFound() throws Exception {
        assertThat(projectFileStorage.allFilesFor(projectEntry).size(), equalTo(0));
    }

    @Test(expected = LazyInitializationException.class)
    public void allFilesQueryDoesNotReturnFileContent() throws Exception {
        addOneFile("fileName1");
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.clear();

        Collection<ProjectFile> projectFiles = projectFileStorage.allFilesFor(projectEntry);
        assertThat(projectFiles.size(), equalTo(1));
        ProjectFile file = projectFiles.iterator().next();
        currentSession.evict(file);

        file.getFileContent();
    }

    @Test
    public void saveWebformAndRetrieveItBackWithSameData() throws Exception {
        ProjectFile projectFile = addOneFile("fileName1");

        assertThat(projectFile.getFileContent(), equalTo(testFileForUpload.getFileContent()));
        assertThat(projectFile.getProjectId(), equalTo(projectEntry.getId()));
        assertThat(projectFile.getTitle(), equalTo(testFileForUpload.getTitle()));
        assertThat(projectFile.getDescription(), equalTo(testFileForUpload.getDescription()));
        assertThat(projectFile.getUserName(), equalTo(testFileForUpload.getUserName()));
        assertThat(projectFile.getXmlSchema(), equalTo(testFileForUpload.getXmlSchema()));
        assertThat(projectFile.isActive(), equalTo(testFileForUpload.isActive()));
        assertThat(projectFile.isMainForm(), equalTo(testFileForUpload.isMainForm()));
        assertThat(projectFile.getRemoteFileUrl(), equalTo(testFileForUpload.getRemoteFileUrl()));
        assertThat(projectFile.getFileSizeInBytes(), equalTo(testFileForUpload.getFileSizeInBytes()));
    }

    @Test
    public void allowToRemoveFilesByFileId() throws Exception {
        ProjectEntry project = testProjectEntry(defaultProjectFile.getProjectId());
        projectFileStorage.remove(project, defaultProjectFile.getId());

        assertThat(projectFileStorage.allFilesFor(project).size(), equalTo(0));
    }

    @Test
    public void allowToBulkRemoveFilesByFileId() throws Exception {
        ProjectFile file1 = addOneFile("fileName1");
        ProjectFile file2 = addOneFile("fileName2");

        projectFileStorage.remove(projectEntry, file1.getId(), file2.getId());

        assertThat(projectFileStorage.allFilesFor(projectEntry).size(), equalTo(0));
    }

    @Test
    public void allowToEditProjectFile() throws Exception {
        defaultProjectFile.setTitle("brand new title");
        defaultProjectFile.setXmlSchema("brand new schema");
        defaultProjectFile.setFile(new UploadedFile("new file name", "brand new content".getBytes()));
        defaultProjectFile.setDescription("brand new description");
        defaultProjectFile.setEmptyInstanceUrl("brand new instance url");
        defaultProjectFile.setNewXmlFileName("brand new xml file name");
        defaultProjectFile.setRemoteFileUrl("brand-new-remote-file-url");
        defaultProjectFile.setActive(true);
        defaultProjectFile.setMainForm(true);

        projectFileStorage.update(defaultProjectFile, projectEntry);

        ProjectFile updatedFile = projectFileStorage.fileById(defaultProjectFile.getId());
        assertFieldsEquals(defaultProjectFile, updatedFile);
    }

    @Test
    public void fileNameIsImmutableAfterSave() throws Exception {
        String fileNameBeforeUpdate = defaultProjectFile.getFileName();
        String newName = "ChangeName";
        assertNotEquals(fileNameBeforeUpdate, newName);

        defaultProjectFile.setFileName(newName);
        projectFileStorage.update(defaultProjectFile, projectEntry);

        currentSession.clear();
        assertThat(projectFileStorage.fileById(defaultProjectFile.getId()).getFileName(), equalTo(fileNameBeforeUpdate));
    }

    @Test
    public void doNotAllowToOverwriteFileWithEmptyFile() throws Exception {
        ProjectFile projectFile = projectFileWithFileType(WEBFORM);
        projectFile.setFileName(defaultProjectFile.getFileName());
        projectFile.setFileContent(new byte[0]);
        projectFile.setId(defaultProjectFile.getId());
        projectFileStorage.update(projectFile, testProjectEntry(defaultProjectFile.getProjectId()));

        currentSession.refresh(defaultProjectFile);

        assertFalse(ProjectFileInfo.fileIsEmpty(defaultProjectFile));
        assertNotNull(defaultProjectFile.getFileContent());
    }

    @Test
    public void updateWillNotChangeProjectId() throws Exception {
        int newProjectId = 10000;
        defaultProjectFile.setProjectId(newProjectId);

        projectFileStorage.update(defaultProjectFile, projectEntry);
        currentSession.refresh(defaultProjectFile);

        assertTrue(defaultProjectFile.getProjectId() != newProjectId);
    }

    @Test
    public void updateChangesUpdatedField() throws Exception {
        currentSession.createQuery("update ProjectFile set updated = null").executeUpdate();
        currentSession.refresh(defaultProjectFile);

        projectFileStorage.update(defaultProjectFile, testProjectEntry(defaultProjectFile.getProjectId()));
        currentSession.refresh(defaultProjectFile);

        assertNotNull(defaultProjectFile.getUpdated());
    }

    @Test
    public void allowToGetFileById() throws Exception {
        int fileId = projectFileStorage.save(projectFileWithoutTypeSet(), projectEntry);

        ProjectFile byId = projectFileStorage.fileById(fileId);

        assertFieldsEquals(testFileForUpload, byId);
    }

    @Test
    public void getIdAfterSave() throws Exception {
        ProjectFile projectFile = projectFileWithoutTypeSet();

        int fileId = projectFileStorage.save(projectFile, projectEntry);
        int maxId = (Integer) sessionFactory.getCurrentSession().createQuery("SELECT MAX(id) from ProjectFile").uniqueResult();

        assertThat(fileId, equalTo(maxId));
    }

    @Test
    public void webFormFileTypeCouldBeSavedToDatabase() throws Exception {
        ProjectFile file = projectFileWithFileType(ProjectFileType.WEBFORM);
        projectFileStorage.save(file, projectEntry);

        currentSession.refresh(file);

        assertThat(file.getFileType(), equalTo(WEBFORM));
    }

    @Test
    public void projectFileTypeCouldBeSavedToDatabase() throws Exception {
        ProjectFile file = projectFileWithFileType(ProjectFileType.FILE);
        projectFileStorage.save(file, projectEntry);

        currentSession.refresh(file);

        assertThat(file.getFileType(), equalTo(ProjectFileType.FILE));
    }

    @Test
    public void fileTypeCouldNotBeUpdated() throws Exception {
        ProjectFile projectFile = projectFileWithFileType(WEBFORM);
        projectFileStorage.save(projectFile, projectEntry);

        projectFile.setFileType(ProjectFileType.FILE);
        projectFileStorage.update(projectFile, projectEntry);

        currentSession.clear();
        assertThat(projectFileStorage.fileById(projectFile.getId()).getFileType(), equalTo(WEBFORM));
    }

    @Test
    public void whenListingAllFilesFileTypeIsSet() throws Exception {
        ProjectFile file1 = projectFileWithFileType(FILE);
        ProjectFile file2 = projectFileWithFileType(WEBFORM);
        file2.setFileName("WEBFORM");
        projectFileStorage.save(file1, projectEntry);
        projectFileStorage.save(file2, projectEntry);

        Collection<ProjectFile> projectFiles = projectFileStorage.allFilesFor(projectEntry);
        assertThat(projectFiles.size(), equalTo(2));

        Iterator<ProjectFile> iterator = projectFiles.iterator();
        assertThat(iterator.next().getFileType(), equalTo(FILE));
        assertThat(iterator.next().getFileType(), equalTo(WEBFORM));
    }

    @Test(expected = ConstraintViolationException.class)
    public void fileNameMustBeUnique() throws Exception {
        ProjectFile file = new ProjectFile();
        file.setFileName("file.xml");
        projectFileStorage.save(file, projectEntry);
        projectFileStorage.save(file, projectEntry);
    }

    @Test
    public void fetchFileContentByFileName() throws Exception {
        projectFileStorage.save(testFileForUpload, projectEntry);

        ProjectFile file = projectFileStorage.fileContentBy(testFileForUpload.getFileName(), projectEntry);

        assertThat(file.getFileContent(), equalTo(testFileForUpload.getFileContent()));
    }

    private void assertFieldsEquals(ProjectFile before, ProjectFile after) {
        assertThat(after.getTitle(), equalTo(before.getTitle()));
        assertThat(after.getXmlSchema(), equalTo(before.getXmlSchema()));
        assertThat(after.getFileContent(), equalTo(before.getFileContent()));
        assertThat(after.getFileSizeInBytes(), equalTo(before.getFileSizeInBytes()));
        assertThat(after.getEmptyInstanceUrl(), equalTo(before.getEmptyInstanceUrl()));
        assertThat(after.getNewXmlFileName(), equalTo(before.getNewXmlFileName()));
        assertThat(after.getDescription(), equalTo(before.getDescription()));
        assertThat(after.isActive(), equalTo(before.isActive()));
        assertThat(after.isMainForm(), equalTo(before.isMainForm()));
        assertThat(after.getRemoteFileUrl(), equalTo(before.getRemoteFileUrl()));
    }

    private ProjectFile addOneFile(String name) {
        ProjectFile file = projectFileWithoutTypeSet();
        file.setFileName(name);
        projectFileStorage.save(file, projectEntry);
        return file;
    }

    private ProjectEntry testProjectEntry(int id) {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setId(id);
        return projectEntry;
    }

    private ProjectFile projectFileWithoutTypeSet() {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setActive(true);
        projectFile.setMainForm(true);
        projectFile.setTitle("Main form");
        projectFile.setDescription("Main web form for questionnaire");
        projectFile.setUserName("User Name");
        projectFile.setFile(new UploadedFile("test-filename", "Web-form content".getBytes()));
        projectFile.setRemoteFileUrl("localhost/test-file.xml");
        projectFile.setEmptyInstanceUrl("empty-instance-url");
        projectFile.setNewXmlFileName("new-xml-file-name");
        projectFile.setXmlSchema("test-xml-schema");
        return projectFile;
    }

    private ProjectFile projectFileWithFileType(ProjectFileType type) {
        ProjectFile projectFile = projectFileWithoutTypeSet();
        projectFile.setFileType(type);
        return projectFile;
    }

    private ProjectFile getPreparedProjectFile() {
        return projectFileStorage.fileById(1);
    }
}
