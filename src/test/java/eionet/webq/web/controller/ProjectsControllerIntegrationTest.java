package eionet.webq.web.controller;

import eionet.webq.dao.ProjectFolders;
import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.ProjectFile;
import eionet.webq.dto.ProjectFileType;
import eionet.webq.dto.UploadedFile;
import eionet.webq.service.ProjectFileService;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;

import java.util.Collection;

import static eionet.webq.web.controller.ProjectsController.PROJECT_ENTRY_MODEL_ATTRIBUTE;
import static eionet.webq.web.controller.ProjectsController.WEB_FORM_UPLOAD_ATTRIBUTE;
import static java.lang.String.valueOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
@RunWith(SpringJUnit4ClassRunner.class)
public class ProjectsControllerIntegrationTest extends AbstractProjectsControllerTests {
    private static final String ADD_EDIT_PROJECT_VIEW = "add_edit_project";
    private static final String DEFAULT_PROJECT_ID = "DEFAULT_PROJECT_ID";
    public static final String PROJECT_ID_PARAM = "projectId";
    @Autowired
    private ProjectFolders projectFolders;
    @Autowired
    private ProjectFileService projectFileService;

    @Test
    public void returnsAllProjectsViewName() throws Exception {
        request(get("/projects/")).andExpect(view().name("projects"));
    }

    @Test
    public void modelCollectionIsEmptyIfNoProjects() throws Exception {
        Collection<ProjectEntry> allProjects = getAllProjectEntries();
        assertThat(allProjects.size(), equalTo(0));
    }

    @Test
    public void allProjectsStoredInDataStorageArePresentInModel() throws Exception {
        saveProjectWithId("1");
        saveProjectWithId("2");

        assertThat(getAllProjectEntries().size(), equalTo(2));
    }

    @Test
    public void allowToAddNewProject() throws Exception {
        addNewProject("1", "short description");
        assertThat(getAllProjectEntries().size(), equalTo(1));
    }

    @Test
    public void allowToRemoveProject() throws Exception {
        String projectId = "projectToRemove";
        addNewProject(projectId, "project");
        assertThat(getAllProjectEntries().size(), equalTo(1));

        request(get("/projects/remove?projectId=" + projectId));
        assertThat(getAllProjectEntries().size(), equalTo(0));
    }

    @Test
    public void emptyObjectIsLoadedToModelWhenCreatingNewProject() throws Exception {
        ResultActions actions = request(get("/projects/add"));
        ProjectEntry projectEntry = assertViewNameAndReturnProjectEntryFromModel(actions, ADD_EDIT_PROJECT_VIEW);

        assertThat(projectEntry.getId(), equalTo(0));
        assertNull(projectEntry.getProjectId());
        assertNull(projectEntry.getDescription());
        assertNull(projectEntry.getCreated());
    }

    @Test
    public void loadsRequiredProjectForEdit() throws Exception {
        String projectToEdit = "projectToEdit";
        saveProjectWithId(projectToEdit);
        ResultActions actions = request(get("/projects/edit").param(PROJECT_ID_PARAM, projectToEdit));

        ProjectEntry projectEntry = assertViewNameAndReturnProjectEntryFromModel(actions, ADD_EDIT_PROJECT_VIEW);

        assertThat(projectEntry.getProjectId(), equalTo(projectToEdit));
        assertNotNull(projectEntry.getId());
        assertNotNull(projectEntry.getCreated());
    }

    @Test
    public void allowToEditProject() throws Exception {
        String projectId = "projectToEdit";
        saveProjectWithId(projectId);
        ProjectEntry byProjectId = projectFolders.getByProjectId(projectId);

        String projectIdUpdated = "projectIdUpdated";
        String newDescription = "newDescription";
        MockHttpServletRequestBuilder post =
                post("/projects/save").param("id", Integer.toString(byProjectId.getId()))
                        .param(PROJECT_ID_PARAM, projectIdUpdated).param("description", newDescription);
        request(post);

        ProjectEntry updatedProject = projectFolders.getByProjectId(projectIdUpdated);

        assertThat(updatedProject.getId(), equalTo(byProjectId.getId()));
        assertThat(updatedProject.getProjectId(), equalTo(projectIdUpdated));
        assertThat(updatedProject.getDescription(), equalTo(newDescription));
    }

    @Test
    public void allowToViewProjectFolderContent() throws Exception {
        saveProjectWithId(DEFAULT_PROJECT_ID);
        ResultActions request = getProjectViewResult();
        ProjectEntry project = assertViewNameAndReturnProjectEntryFromModel(request, "view_project");

        assertThat(project.getProjectId(), equalTo(DEFAULT_PROJECT_ID));
    }

    @Test
    public void allowToAddNewWebForm() throws Exception {
        assertNewProjectFileViewNameAndModelFileType("/webform/add", ProjectFileType.WEBFORM);
    }

    @Test
    public void allowToAddNewProjectFile() throws Exception {
        assertNewProjectFileViewNameAndModelFileType("/file/add", ProjectFileType.FILE);
    }

    @Test
    public void allowToUploadAWebFormForAProject() throws Exception {
        saveProjectWithId(DEFAULT_PROJECT_ID);
        ProjectFile projectFile = testWebFormUpload();
        ResultActions request = uploadWebFormForDefaultProject(projectFile);
        ProjectFile uploaded = (ProjectFile) request.andReturn().getModelAndView().getModel().get(WEB_FORM_UPLOAD_ATTRIBUTE);

        assertThat(uploaded.getTitle(), equalTo(projectFile.getTitle()));
        assertThat(uploaded.getFileContent(), equalTo(projectFile.getFileContent()));
        assertThat(uploaded.isActive(), equalTo(projectFile.isActive()));
        assertThat(uploaded.getDescription(), equalTo(projectFile.getDescription()));
    }

    @Test
    public void savesUploadedWebFormToStorage() throws Exception {
        uploadFilesForDefaultProject(1);
        ProjectEntry defaultProject = projectFolders.getByProjectId(DEFAULT_PROJECT_ID);

        Collection<ProjectFile> uploadedFilesForProject = projectFileService.allFilesFor(defaultProject);

        assertThat(uploadedFilesForProject.size(), equalTo(1));
    }

    @Test
    public void loadsAllProjectFilesToModel() throws Exception {
        uploadFilesForDefaultProject(2);
        ResultActions projectViewResult = getProjectViewResult();
        MultiValueMap<ProjectFileType, ProjectFile> allProjectFiles = allWebFormUploads(projectViewResult);

        assertThat(allProjectFiles.get(testWebFormUpload().getFileType()).size(), equalTo(2));
    }

    @Test
    public void allowToRemoveProjectFile() throws Exception {
        uploadFilesForDefaultProject(1);
        ProjectFile projectFile = theOnlyOneUploadedFile();

        request(get("/projects/" + DEFAULT_PROJECT_ID + "/webform/remove").param("fileId", valueOf(projectFile.getId())));

        assertThat(allFilesForDefaultProject().size(), equalTo(0));
    }

    @Test
    public void projectFileIsAddedToModelForEdit() throws Exception {
        uploadFilesForDefaultProject(1);
        final ProjectFile projectFile = theOnlyOneUploadedFile();
        request(post("/projects/" + DEFAULT_PROJECT_ID + "/webform/edit").param("fileId", valueOf(projectFile.getId())))
                .andExpect(view().name("add_edit_project_file"))
                .andExpect(model().attribute(WEB_FORM_UPLOAD_ATTRIBUTE, new BaseMatcher<ProjectFile>() {
                    @Override
                    public boolean matches(Object o) {
                        ProjectFile upload = (ProjectFile) o;
                        return projectFile.getId() == upload.getId() && projectFile.getTitle().equals(upload.getTitle());
                    }

                    @Override
                    public void describeTo(Description description) {
                    }
                }));
    }

    @Test
    public void allowToEditProjectFile() throws Exception {
        uploadFilesForDefaultProject(1);
        ProjectFile projectFile = theOnlyOneUploadedFile();
        String newTitle = "new title";
        request(post("/projects/" + DEFAULT_PROJECT_ID + "/webform/save").param("title", newTitle)
                .param("userName", projectFile.getUserName()).param("id", valueOf(projectFile.getId())));

        ProjectFile updated = theOnlyOneUploadedFile();
        assertThat(updated.getTitle(), equalTo(newTitle));
    }

    @Test
    public void allowToDownloadWebFormContent() throws Exception {
        uploadFilesForDefaultProject(1);
        ProjectFile projectFile = theOnlyOneUploadedFile();

        request(get("/download/project/" + DEFAULT_PROJECT_ID + "/file/" + projectFile.getId())).andExpect(
                content().bytes(testWebFormUpload().getFileContent()));
    }

    private ProjectFile theOnlyOneUploadedFile() {
        Collection<ProjectFile> projectFiles = allFilesForDefaultProject();
        assertThat(projectFiles.size(), equalTo(1));
        return projectFiles.iterator().next();
    }

    private Collection<ProjectFile> allFilesForDefaultProject() {
        ProjectEntry defaultProject = projectFolders.getByProjectId(DEFAULT_PROJECT_ID);
        return projectFileService.allFilesFor(defaultProject);
    }

    @SuppressWarnings("unchecked")
    private MultiValueMap<ProjectFileType, ProjectFile> allWebFormUploads(ResultActions projectViewResult) {
        return (MultiValueMap<ProjectFileType, ProjectFile>) projectViewResult.andReturn().getModelAndView().getModel()
                .get(ProjectsController.ALL_PROJECT_FILES_ATTRIBUTE);
    }

    private ResultActions getProjectViewResult() throws Exception {
        return request(get("/projects/" + DEFAULT_PROJECT_ID + "/view"));
    }

    private ProjectFile uploadFilesForDefaultProject(int amount) throws Exception {
        saveProjectWithId(DEFAULT_PROJECT_ID);
        ProjectFile projectFile = testWebFormUpload();
        for (int i = 0; i < amount; i++) {
            projectFile.setFileName(projectFile.getFileName() + Integer.toString(i));
            uploadWebFormForDefaultProject(projectFile);
        }
        return projectFile;
    }

    private ProjectFile testWebFormUpload() {
        ProjectFile projectFile = new ProjectFile();
        projectFile.setDescription("test description");
        projectFile.setTitle("title");
        projectFile.setFileType(ProjectFileType.WEBFORM);
        projectFile.setFile(new UploadedFile("file-name", "test-content".getBytes()));
        projectFile.setUserName("test-user");
        return projectFile;
    }

    private ResultActions uploadWebFormForDefaultProject(ProjectFile projectFile) throws Exception {
        return request(fileUpload("/projects/" + DEFAULT_PROJECT_ID + "/webform/save")
                .file(new MockMultipartFile("file", projectFile.getFileName(), MediaType.APPLICATION_XML_VALUE, projectFile.getFileContent()))
                .param("title", projectFile.getTitle()).param("active", Boolean.toString(projectFile.isActive()))
                .param("description", projectFile.getDescription()).param("userName", projectFile.getUserName())
                .param("fileType", projectFile.getFileType().name()));
    }

    private void saveProjectWithId(String projectId) {
        projectFolders.save(projectEntryWith(projectId));
    }

    private ProjectEntry assertViewNameAndReturnProjectEntryFromModel(ResultActions actions, String viewName) throws Exception {
        MvcResult mvcResult = actions.andExpect(view().name(viewName)).andReturn();
        return (ProjectEntry) mvcResult.getModelAndView().getModel().get(PROJECT_ENTRY_MODEL_ATTRIBUTE);
    }

    private ProjectEntry projectEntryWith(String id) {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setProjectId(id);
        return projectEntry;
    }

    @SuppressWarnings("unchecked")
    private Collection<ProjectEntry> getAllProjectEntries() throws Exception {
        MvcResult mvcResult = request(get("/projects/")).andReturn();
        return (Collection<ProjectEntry>) mvcResult.getModelAndView().getModelMap().get("allProjects");
    }

    private void assertNewProjectFileViewNameAndModelFileType(String path, ProjectFileType type) throws Exception {
        saveProjectWithId(DEFAULT_PROJECT_ID);
        request(get("/projects/" + DEFAULT_PROJECT_ID + path)).andExpect(view().name("add_edit_project_file"))
                .andExpect(model().attribute(WEB_FORM_UPLOAD_ATTRIBUTE, new FileTypeMatcher(type)));
    }

    private static class FileTypeMatcher extends BaseMatcher<ProjectFile> {
        private final ProjectFileType expectedFileType;

        private FileTypeMatcher(ProjectFileType type) {
            expectedFileType = type;
        }

        @Override
        public boolean matches(Object o) {
            ProjectFile file = (ProjectFile) o;
            return file.getFileType() == expectedFileType;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("File type not matched.").appendValue(expectedFileType);
        }
    }
}
