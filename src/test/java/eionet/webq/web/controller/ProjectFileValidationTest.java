package eionet.webq.web.controller;

import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.ProjectFileType;
import eionet.webq.service.ProjectService;
import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringJUnit4ClassRunner.class)
public class ProjectFileValidationTest extends AbstractContextControllerTests {
    public static final String TEST_PROJECT_ID = "test-project";
    public static final String SAVE_PROJECT_FILE_URL = "/projects/" + TEST_PROJECT_ID + "/webform/save";
    @Autowired
    private ProjectService projectService;
    @Autowired
    private JdbcTemplate template;

    @Before
    public void addDefaultProject() {
        template.update("DELETE FROM project_folder");
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setProjectId(TEST_PROJECT_ID);
        projectEntry.setDescription("test-description");
        projectService.saveOrUpdate(projectEntry);
    }

    @Test
    public void titleIsEmpty() throws Exception {
        MvcResult result = request(SaveRequestBuilder.fileUploadBuilder().withPrincipal().build()).andReturn();

        assertFieldError(getFirstAndOnlyFieldError(result), "title", "NotEmpty.projectFile.title");
    }

    @Test
    public void noFileAttached() throws Exception {
        MvcResult mvcResult = request(SaveRequestBuilder.postBuilder().withPrincipal().withTitle().build()).andReturn();
        assertFieldError(getFirstAndOnlyFieldError(mvcResult), "file", "project.file.null");
    }

    @Test
    public void noPrincipal() throws Exception {
        MvcResult mvcResult = request(SaveRequestBuilder.fileUploadBuilder().withTitle().build()).andReturn();
        assertFieldError(getFirstAndOnlyFieldError(mvcResult), "userName", "NotEmpty.projectFile.userName");
    }

    @Test
    public void fileNameWithProjectIdMustBeUnique() throws Exception {
        allRequiredFieldsFilledInWithDefaultValues();
        MvcResult mvcResult = allRequiredFieldsFilledInWithDefaultValues().andReturn();
        assertFieldError(getFirstAndOnlyFieldError(mvcResult), "file", "project.file.duplicate.name");
    }

    private ResultActions allRequiredFieldsFilledInWithDefaultValues() throws Exception {
        return request(SaveRequestBuilder.fileUploadBuilder().withTitle().withPrincipal().withFileType().build());
    }

    @Override
    protected String bindingResultPropertyNameInModel() {
        return "org.springframework.validation.BindingResult.projectFile";
    }
    
    private static class SaveRequestBuilder {
        private  MockHttpServletRequestBuilder request;

        private SaveRequestBuilder(MockHttpServletRequestBuilder request) {
            this.request = request;
        }

        private static SaveRequestBuilder fileUploadBuilder() {
            return new SaveRequestBuilder(fileUpload(SAVE_PROJECT_FILE_URL).file("file", "test-file".getBytes()));
        }

        public static SaveRequestBuilder postBuilder() {
            return new SaveRequestBuilder(post(SAVE_PROJECT_FILE_URL));
        }

        public SaveRequestBuilder withPrincipal() {
            request.principal(new Principal() {
                @Override
                public String getName() {
                    return "test-userName";
                }
            });
            return this;
        }

        public SaveRequestBuilder withTitle() {
            request.param("title", "test-title");
            return this;
        }

        public SaveRequestBuilder withFileType() {
            request.param("fileType", ProjectFileType.WEBFORM.name());
            return this;
        }

        public MockHttpServletRequestBuilder build() {
            return request;
        }
    }
}
