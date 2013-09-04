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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static eionet.webq.dto.ProjectFileType.FILE;
import static eionet.webq.dto.ProjectFileType.WEBFORM;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class ProjectFileStorageImplTest {

    @Autowired
    @Qualifier("project-files")
    FileStorage<ProjectEntry, ProjectFile> projectFileStorage;
    @Autowired
    JdbcTemplate template;

    private ProjectEntry projectEntry = testProjectEntry();
    private ProjectFile testFileForUpload = projectFileWithoutTypeSet();

    @Before
    public void removeAllProjectFiles() {
        template.update("DELETE FROM project_file");
    }

    @Test
    public void saveWebformWithoutException() throws Exception {
        ProjectEntry projectEntry = testProjectEntry();
        ProjectFile projectFile = projectFileWithoutTypeSet();
        projectFileStorage.save(projectFile, projectEntry);
    }

    @Test
    public void emptyCollectionIfFilesForProjectNotFound() throws Exception {
        assertThat(projectFileStorage.allFilesFor(testProjectEntry()).size(), equalTo(0));
    }

    @Test
    public void allFilesQueryDoesNotReturnFileContent() throws Exception {
        addOneFile();
        addOneFile();

        Collection<ProjectFile> projectFiles = projectFileStorage.allFilesFor(testProjectEntry());
        assertThat(projectFiles.size(), equalTo(2));

        for (ProjectFile projectFile : projectFiles) {
            assertNull(projectFile.getFileContent());
        }
    }

    @Test
    public void saveWebformAndRetrieveItBackWithSameData() throws Exception {
        addOneFile();

        ProjectFile uploadedFile = getUploadedFileAndAssertThatItIsTheOnlyOne();

        assertThat(uploadedFile.getFileContent(), equalTo(testFileForUpload.getFileContent()));
        assertThat(uploadedFile.getProjectId(), equalTo(projectEntry.getId()));
        assertThat(uploadedFile.getTitle(), equalTo(testFileForUpload.getTitle()));
        assertThat(uploadedFile.getDescription(), equalTo(testFileForUpload.getDescription()));
        assertThat(uploadedFile.getUserName(), equalTo(testFileForUpload.getUserName()));
        assertThat(uploadedFile.getXmlSchema(), equalTo(testFileForUpload.getXmlSchema()));
        assertThat(uploadedFile.isActive(), equalTo(testFileForUpload.isActive()));
        assertThat(uploadedFile.isMainForm(), equalTo(testFileForUpload.isMainForm()));
        assertThat(uploadedFile.getRemoteFileUrl(), equalTo(testFileForUpload.getRemoteFileUrl()));
    }

    @Test
    public void allowToRemoveFilesByFileId() throws Exception {
        addOneFile();

        ProjectFile uploadedFile = getUploadedFileAndAssertThatItIsTheOnlyOne();

        projectFileStorage.remove(projectEntry, uploadedFile.getId());

        assertThat(projectFileStorage.allFilesFor(projectEntry).size(), equalTo(0));
    }

    @Test
    public void allowToBulkRemoveFilesByFileId() throws Exception {
        addOneFile();
        addOneFile();

        Iterator<ProjectFile> it = projectFileStorage.allFilesFor(projectEntry).iterator();

        projectFileStorage.remove(projectEntry, it.next().getId(), it.next().getId());

        assertThat(projectFileStorage.allFilesFor(projectEntry).size(), equalTo(0));
    }

    @Test
    public void allowToEditProjectFile() throws Exception {
        addOneFile();
        ProjectFile beforeUpdate = getUploadedFileAndAssertThatItIsTheOnlyOne();
        beforeUpdate.setTitle("brand new title");
        beforeUpdate.setXmlSchema("brand new schema");
        beforeUpdate.setFile(new UploadedFile("new file name", "brand new content".getBytes()));
        beforeUpdate.setDescription("brand new description");
        beforeUpdate.setEmptyInstanceUrl("brand new instance url");
        beforeUpdate.setNewXmlFileName("brand new xml file name");
        beforeUpdate.setRemoteFileUrl("brand-new-remote-file-url");
        beforeUpdate.setActive(true);
        beforeUpdate.setMainForm(true);

        projectFileStorage.update(beforeUpdate, projectEntry);

        ProjectFile updatedFile = projectFileStorage.fileById(beforeUpdate.getId());
        assertFieldsEquals(beforeUpdate, updatedFile);
    }

    @Test
    public void fileNameIsImmutableAfterSave() throws Exception {
        String immutableName = "ImmutableName";
        testFileForUpload.setFileName(immutableName);
        int fileId = projectFileStorage.save(testFileForUpload, projectEntry);

        ProjectFile fileFromStorage = projectFileStorage.fileById(fileId);
        fileFromStorage.setFileName("ChangeName");
        projectFileStorage.update(fileFromStorage, projectEntry);

        assertThat(projectFileStorage.fileById(fileId).getFileName(), equalTo(immutableName));
    }

    @Test
    public void doNotAllowToOverwriteFileWithEmptyFile() throws Exception {
        addOneFile();
        ProjectFile beforeUpdate = getUploadedFileAndAssertThatItIsTheOnlyOne();

        beforeUpdate.setFileContent(new byte[0]);
        projectFileStorage.update(beforeUpdate, projectEntry);

        ProjectFile projectFile = projectFileStorage.fileById(beforeUpdate.getId());
        assertNotNull(projectFile.getFileContent());
    }

    @Test
    public void updateWillNotChangeProjectId() throws Exception {
        addOneFile();
        ProjectFile beforeUpdate = getUploadedFileAndAssertThatItIsTheOnlyOne();
        beforeUpdate.setProjectId(10000);

        projectFileStorage.update(beforeUpdate, projectEntry);

        ProjectFile updatedFile = getUploadedFileAndAssertThatItIsTheOnlyOne();

        assertTrue(updatedFile.getProjectId() != beforeUpdate.getProjectId());
    }

    @Test
    public void updateChangesUpdatedField() throws Exception {
        addOneFile();
        clearUpdatedColumnForAllFiles();

        ProjectFile beforeUpdate = getUploadedFileAndAssertThatItIsTheOnlyOne();
        Date updatedTime = beforeUpdate.getUpdated();
        assertNull(updatedTime);

        projectFileStorage.update(beforeUpdate, projectEntry);

        ProjectFile updatedFile = getUploadedFileAndAssertThatItIsTheOnlyOne();
        Date updatedAfterFileUpdate = updatedFile.getUpdated();

        assertNotNull(updatedAfterFileUpdate);
    }

    @Test
    public void allowToGetFileById() throws Exception {
        addOneFile();
        ProjectFile uploadedFile = getUploadedFileAndAssertThatItIsTheOnlyOne();

        ProjectFile byId = projectFileStorage.fileById(uploadedFile.getId());

        assertFieldsEquals(testFileForUpload, byId);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getContentByFileId() throws Exception {
        projectFileStorage.fileContentBy(1, projectEntry);
    }

    @Test
    public void getIdAfterSave() throws Exception {
        ProjectEntry projectEntry = testProjectEntry();
        ProjectFile projectFile = projectFileWithoutTypeSet();

        int fileId = projectFileStorage.save(projectFile, projectEntry);
        int maxId = template.queryForInt("SELECT MAX(id) from project_file");

        assertThat(fileId, equalTo(maxId));
    }

    @Test
    public void webFormFileTypeCouldBeSavedToDatabase() throws Exception {
        ProjectFile projectFile = saveAndGetBackProjectFileWithFileType(WEBFORM);

        assertThat(projectFile.getFileType(), equalTo(WEBFORM));
    }

    @Test
    public void projectFileTypeCouldBeSavedToDatabase() throws Exception {
        ProjectFile projectFile = saveAndGetBackProjectFileWithFileType(ProjectFileType.FILE);

        assertThat(projectFile.getFileType(), equalTo(ProjectFileType.FILE));
    }

    @Test
    public void fileTypeCouldNotBeUpdated() throws Exception {
        ProjectFile projectFile = saveAndGetBackProjectFileWithFileType(WEBFORM);
        projectFile.setFileType(ProjectFileType.FILE);

        projectFileStorage.update(projectFile, projectEntry);

        assertThat(projectFileStorage.fileById(projectFile.getId()).getFileType(), equalTo(WEBFORM));
    }

    @Test
    public void whenListingAllFilesFileTypeIsSet() throws Exception {
        setFileTypeAndSave(FILE);
        setFileTypeAndSave(WEBFORM);

        Collection<ProjectFile> projectFiles = projectFileStorage.allFilesFor(projectEntry);
        assertThat(projectFiles.size(), equalTo(2));

        Iterator<ProjectFile> iterator = projectFiles.iterator();
        assertThat(iterator.next().getFileType(), equalTo(FILE));
        assertThat(iterator.next().getFileType(), equalTo(WEBFORM));
    }

    @Test(expected = DuplicateKeyException.class)
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

    private int setFileTypeAndSave(ProjectFileType fileType) {
        testFileForUpload.setFileType(fileType);
        return addOneFile();
    }

    private ProjectFile saveAndGetBackProjectFileWithFileType(ProjectFileType type) {
        return projectFileStorage.fileById(setFileTypeAndSave(type));
    }

    private void clearUpdatedColumnForAllFiles() {
        template.update("UPDATE project_file SET updated = NULL");
    }

    private ProjectFile getUploadedFileAndAssertThatItIsTheOnlyOne() {
        Collection<ProjectFile> projectFiles = projectFileStorage.allFilesFor(projectEntry);
        assertThat(projectFiles.size(), equalTo(1));

        return projectFileStorage.fileById(projectFiles.iterator().next().getId());
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

    private int addOneFile() {
        testFileForUpload.setFileName(testFileForUpload.getFileName() + "+");//unique name
        return projectFileStorage.save(testFileForUpload, projectEntry);
    }

    private ProjectEntry testProjectEntry() {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setId(1);
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
}
