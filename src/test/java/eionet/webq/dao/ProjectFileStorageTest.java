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
import eionet.webq.dto.WebFormUpload;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class ProjectFileStorageTest {

    @Autowired
    ProjectFileStorage projectFileStorage;
    @Autowired
    JdbcTemplate template;

    private ProjectEntry projectEntry = testProjectEntry();
    private WebFormUpload testFileForUpload = testWebForm();

    @Before
    public void removeAllProjectFiles() {
        template.update("DELETE FROM project_file");
    }

    @Test
    public void saveWebformWithoutException() throws Exception {
        ProjectEntry projectEntry = testProjectEntry();
        WebFormUpload webFormUpload = testWebForm();
        projectFileStorage.save(projectEntry, webFormUpload);
    }

    @Test
    public void emptyCollectionIfFilesForProjectNotFound() throws Exception {
        assertThat(projectFileStorage.allFilesFor(testProjectEntry()).size(), equalTo(0));
    }

    @Test
    public void saveWebformAndRetrieveItBackWithSameData() throws Exception {
        addOneFile();

        WebFormUpload uploadedFile = getUploadedFileAndAssertThatItIsTheOnlyOne();

        assertThat(uploadedFile.getFile(), equalTo(testFileForUpload.getFile()));
        assertThat(uploadedFile.getProjectId(), equalTo(projectEntry.getId()));
        assertThat(uploadedFile.getTitle(), equalTo(testFileForUpload.getTitle()));
        assertThat(uploadedFile.getDescription(), equalTo(testFileForUpload.getDescription()));
        assertThat(uploadedFile.getUserName(), equalTo(testFileForUpload.getUserName()));
        assertThat(uploadedFile.getXmlSchema(), equalTo(testFileForUpload.getXmlSchema()));
        assertThat(uploadedFile.isActive(), equalTo(testFileForUpload.isActive()));
        assertThat(uploadedFile.isMainForm(), equalTo(testFileForUpload.isMainForm()));
    }

    private WebFormUpload getUploadedFileAndAssertThatItIsTheOnlyOne() {
        Collection<WebFormUpload> webFormUploads = projectFileStorage.allFilesFor(projectEntry);
        assertThat(webFormUploads.size(), equalTo(1));

        return webFormUploads.iterator().next();
    }

    @Test
    public void allowToRemoveFilesByFileId() throws Exception {
        addOneFile();

        WebFormUpload uploadedFile = getUploadedFileAndAssertThatItIsTheOnlyOne();

        projectFileStorage.remove(uploadedFile.getId());

        assertThat(projectFileStorage.allFilesFor(projectEntry).size(), equalTo(0));
    }

    @Test
    public void allowToEditProjectFile() throws Exception {
        addOneFile();
        WebFormUpload beforeUpdate = getUploadedFileAndAssertThatItIsTheOnlyOne();
        beforeUpdate.setTitle("brand new title");
        beforeUpdate.setXmlSchema("brand new schema");
        beforeUpdate.setFile("brand new content".getBytes());
        beforeUpdate.setDescription("brand new description");
        beforeUpdate.setActive(true);
        beforeUpdate.setMainForm(true);

        projectFileStorage.update(beforeUpdate);

        WebFormUpload updatedFile = getUploadedFileAndAssertThatItIsTheOnlyOne();
        assertThat(updatedFile.getTitle(), equalTo(beforeUpdate.getTitle()));
        assertThat(updatedFile.getXmlSchema(), equalTo(beforeUpdate.getXmlSchema()));
        assertThat(updatedFile.getFile(), equalTo(beforeUpdate.getFile()));
        assertThat(updatedFile.getDescription(), equalTo(beforeUpdate.getDescription()));
        assertThat(updatedFile.isActive(), equalTo(beforeUpdate.isActive()));
        assertThat(updatedFile.isMainForm(), equalTo(beforeUpdate.isMainForm()));
    }

    @Test
    public void updateWillNotChangeProjectId() throws Exception {
        addOneFile();
        WebFormUpload beforeUpdate = getUploadedFileAndAssertThatItIsTheOnlyOne();
        beforeUpdate.setProjectId(10000);

        projectFileStorage.update(beforeUpdate);

        WebFormUpload updatedFile = getUploadedFileAndAssertThatItIsTheOnlyOne();

        assertTrue(updatedFile.getProjectId() != beforeUpdate.getProjectId());
    }

    private void addOneFile() {
        projectFileStorage.save(projectEntry, testFileForUpload);
    }

    private ProjectEntry testProjectEntry() {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setId(1);
        return projectEntry;
    }

    private WebFormUpload testWebForm() {
        WebFormUpload webFormUpload = new WebFormUpload();
        webFormUpload.setActive(true);
        webFormUpload.setMainForm(true);
        webFormUpload.setTitle("Main form");
        webFormUpload.setDescription("Main web form for questionnaire");
        webFormUpload.setUserName("User Name");
        webFormUpload.setFile("Web-form content".getBytes());
        webFormUpload.setXmlSchema("test-xml-schema");
        return webFormUpload;
    }
}
