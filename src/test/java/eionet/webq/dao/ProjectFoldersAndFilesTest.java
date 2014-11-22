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
import eionet.webq.dao.orm.ProjectEntry;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.ProjectFileType;
import eionet.webq.dao.orm.UploadedFile;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static eionet.webq.dao.FileContentUtil.getFileContentRowsCount;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class ProjectFoldersAndFilesTest {
    @Autowired
    ProjectFileStorage projectFileStorage;
    @Autowired
    SessionFactory sessionFactory;
    @Autowired
    private ProjectFolders folders;
    private Session currentSession;

    @Before
    public void setUp() throws Exception {
        currentSession = sessionFactory.getCurrentSession();
        sessionFactory.getCurrentSession().setFlushMode(FlushMode.ALWAYS);
    }

    @Test
    public void removingProjectFolderWillRemoveOnlyRelatedFiles() throws Exception {

        String project1Id = "project1";
        String project2Id = "project2";

        //create 2 project folders
        folders.save(projectEntry(project1Id));
        ProjectEntry savedProject1 = folders.getByProjectId(project1Id);
        int webformFile1Id = projectFileStorage.save(projectWebFormFile(), savedProject1);

        folders.save(projectEntry(project2Id));
        ProjectEntry savedProject2 = folders.getByProjectId(project2Id);
        int webformFile2Id = projectFileStorage.save(projectWebFormFile(), savedProject2);

        // assert that project files and project folder is related
        Collection<ProjectFile> project1Files = projectFileStorage.findAllFilesFor(savedProject1);
        assertThat(project1Files.size(), equalTo(1));
        assertThat(getFileContentRowsCount(sessionFactory), equalTo(2));

        // remove project
        folders.remove(savedProject1.getProjectId());

        // assert that project files and file contents have been deleted
        Collection<ProjectFile> removedProjectFiles = projectFileStorage.findAllFilesFor(savedProject1);
        assertThat(removedProjectFiles.size(), equalTo(0));
        Collection<ProjectFile> removedProject2Files = projectFileStorage.findAllFilesFor(savedProject2);
        assertThat(removedProject2Files.size(), equalTo(1));

        currentSession.clear();
        assertThat(folders.getById(savedProject1.getId()), nullValue());
        assertThat(projectFileStorage.findById(webformFile1Id), nullValue());
        assertThat(projectFileStorage.findById(webformFile2Id).getFileName(), equalTo(projectWebFormFile().getFileName()));
        assertThat(getFileContentRowsCount(sessionFactory), equalTo(1));
    }

    @Test
    public void renamingProjectFolderWillAlsoRemoveProjectFiles() throws Exception {

        String projectId = "project";

        // create project folder and 2 project files
        folders.save(projectEntry(projectId));
        ProjectEntry savedProject = folders.getByProjectId(projectId);
        ProjectFile webformFile = projectWebFormFile();
        ProjectFile simpleFile = projectSimpleFile();

        int simpleFileId = projectFileStorage.save(webformFile, savedProject);
        int webformFileId = projectFileStorage.save(simpleFile, savedProject);

        // check if files are related to project folder
        Collection<ProjectFile> projectFiles = projectFileStorage.findAllFilesFor(savedProject);
        assertThat(projectFiles.size(), equalTo(2));

        // rename project
        savedProject.setProjectId("NewIdentifier");
        savedProject.setDescription("NewDescription");
        folders.save(savedProject);

        // assert files remained related to project folder
        Collection<ProjectFile> removedProjectFiles = projectFileStorage.findAllFilesFor(savedProject);
        assertThat(removedProjectFiles.size(), equalTo(2));

        currentSession.clear();
        for (ProjectFile projectFile : removedProjectFiles) {
            if (projectFile.getFileType() == ProjectFileType.WEBFORM) {
                assertThat(projectFile.getFileName(), equalTo(webformFile.getFileName()));
            }
            if (projectFile.getFileType() == ProjectFileType.FILE) {
                assertThat(projectFile.getFileName(), equalTo(simpleFile.getFileName()));
            }
        }
        assertThat(getFileContentRowsCount(sessionFactory), equalTo(2));
    }

    private ProjectEntry projectEntry(String projectId) {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setProjectId(projectId);
        projectEntry.setDescription("description");
        return projectEntry;
    }

    @Test
    public void removingProjectFolderWillAlsoRemoveProjectFiles() throws Exception {

        String projectId = "project";

        //create project folder and 2 project files
        folders.save(projectEntry(projectId));
        ProjectEntry savedProject = folders.getByProjectId(projectId);
        int simpleFileId = projectFileStorage.save(projectSimpleFile(), savedProject);
        int webformFileId = projectFileStorage.save(projectWebFormFile(), savedProject);

        // assert that project files and project folder is related
        Collection<ProjectFile> projectFiles = projectFileStorage.findAllFilesFor(savedProject);
        assertThat(projectFiles.size(), equalTo(2));
        assertThat(getFileContentRowsCount(sessionFactory), equalTo(2));

        // remove project
        folders.remove(savedProject.getProjectId());

        // assert that project files and file contents have been deleted
        Collection<ProjectFile> removedProjectFiles = projectFileStorage.findAllFilesFor(savedProject);
        assertThat(removedProjectFiles.size(), equalTo(0));

        currentSession.clear();
        assertThat(folders.getById(savedProject.getId()), nullValue());
        assertThat(projectFileStorage.findById(simpleFileId), nullValue());
        assertThat(projectFileStorage.findById(webformFileId), nullValue());
        assertThat(getFileContentRowsCount(sessionFactory), equalTo(0));
    }

    private ProjectFile projectWebFormFile() {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setFileType(ProjectFileType.WEBFORM);
        projectFile.setActive(true);
        projectFile.setLocalForm(true);
        projectFile.setRemoteForm(true);
        projectFile.setTitle("Main form");
        projectFile.setDescription("Main web form for questionnaire");
        projectFile.setUserName("User Name");
        projectFile.setFile(new UploadedFile("test-webform.html", "Web-form content".getBytes()));
        projectFile.setRemoteFileUrl("localhost/test-webform.html");
        projectFile.setEmptyInstanceUrl("empty-instance-url");
        projectFile.setNewXmlFileName("new-xml-file-name");
        projectFile.setXmlSchema("test-xml-schema");
        return projectFile;
    }

    private ProjectFile projectSimpleFile() {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setFileType(ProjectFileType.FILE);
        projectFile.setTitle("Simple file");
        projectFile.setUserName("User Name");
        projectFile.setFile(new UploadedFile("test-file.xml", "Web-form content".getBytes()));
        projectFile.setRemoteFileUrl("localhost/test-file.xml");
        return projectFile;
    }

}
