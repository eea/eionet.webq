package eionet.webq.web;

import configuration.ApplicationTestContext;
import eionet.webq.dao.orm.UserFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationTestContext.class})
public abstract class AbstractContextControllerTests {
    protected MockHttpSession mockHttpSession = new MockHttpSession();
    @Autowired
    private WebApplicationContext wac;

    protected MockMvc mvc() {
        return webAppContextSetup(this.wac).build();
    }

    /**
     * Expects returning 200(OK) as a http status.
     * @param requestBuilder request builder
     * @return result actions
     * @throws Exception
     */
    protected ResultActions request(RequestBuilder requestBuilder) throws Exception {
        return mvc().perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
    }

    protected MockMultipartFile createMockMultipartFile(String fileName, byte[] content) {
        return new MockMultipartFile("userFile", fileName, MediaType.APPLICATION_XML_VALUE, content);
    }

    protected ResultActions uploadFile(MockMultipartFile file) throws Exception {
        return request(fileUpload("/uploadXml").file(file).session(mockHttpSession));
    }

    protected MockHttpServletRequestBuilder postWithMockSession(String path) {
        return post(path).session(mockHttpSession);
    }

    protected List<UserFile> uploadFileAndExtractUploadedFiles(MockMultipartFile file) throws Exception {
        return extractUserFilesFromMvcResult(uploadFile(file).andReturn());
    }

    @SuppressWarnings("unchecked")
    protected List<UserFile> extractUserFilesFromMvcResult(MvcResult result) {
        return (List<UserFile>) result.getModelAndView().getModelMap().get("uploadedFiles");
    }

    protected UserFile uploadFileAndTakeFirstUploadedFile(MockMultipartFile mockMultipartFile) throws Exception {
        List<UserFile> userFiles = uploadFileAndExtractUploadedFiles(mockMultipartFile);
        return userFiles.iterator().next();
    }

    protected void assertFieldError(FieldError fieldError, String field, String code) {
        assertThat(fieldError.getField(), equalTo(field));
        assertTrue(Arrays.asList(fieldError.getCodes()).contains(code));
    }

    protected void assertNoFieldErrors(MvcResult result) {
        List<FieldError> errorList = getFieldErrorsFromMvcResultAndAssertThatFieldErrorCountIs(result, 0);

        assertTrue(errorList.isEmpty());
    }

    protected FieldError getFirstAndOnlyFieldError(MvcResult mvcResult) {
        List<FieldError> fieldErrors = getFieldErrorsFromMvcResultAndAssertThatFieldErrorCountIs(mvcResult, 1);
        return fieldErrors.get(0);
    }

    protected List<FieldError> getFieldErrorsFromMvcResultAndAssertThatFieldErrorCountIs(MvcResult result, int size) {
        BeanPropertyBindingResult bindingResult =
                (BeanPropertyBindingResult) result.getModelAndView().getModelMap()
                        .get(bindingResultPropertyNameInModel());

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        assertThat("Field errors=" + fieldErrors, bindingResult.getFieldErrorCount(), equalTo(size));
        return fieldErrors;
    }
    
    protected String bindingResultPropertyNameInModel() {
        throw new UnsupportedOperationException(
                "Please override this method with giving proper binding result property name in model");
    }
}
