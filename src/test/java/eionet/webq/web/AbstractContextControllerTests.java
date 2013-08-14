package eionet.webq.web;

import configuration.ApplicationTestContext;
import eionet.webq.dto.UploadedXmlFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class AbstractContextControllerTests {
    protected MockHttpSession mockHttpSession = new MockHttpSession();
    @Autowired
    private WebApplicationContext wac;

    protected MockMvc mvc() {
        return webAppContextSetup(this.wac).build();
    }

    protected ResultActions request(RequestBuilder requestBuilder) throws Exception {
        return mvc().perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isOk());
    }

    protected MockMultipartFile createMockMultipartFile(String fileName, byte[] content) {
        return new MockMultipartFile("uploadedXmlFile", fileName, MediaType.APPLICATION_XML_VALUE, content);
    }

    protected ResultActions uploadFile(MockMultipartFile file) throws Exception {
        return request(fileUpload("/uploadXml").file(file).session(mockHttpSession));
    }

    protected MockHttpServletRequestBuilder postWithMockSession(String path) {
        return post(path).session(mockHttpSession);
    }

    @SuppressWarnings("unchecked")
    protected List<UploadedXmlFile> uploadFileAndExtractUploadedFiles(MockMultipartFile file) throws Exception {
        return (List<UploadedXmlFile>) uploadFile(file).andReturn().getModelAndView().getModelMap().get("uploadedFiles");
    }

    protected UploadedXmlFile uploadFileAndTakeFirstUploadedFile(MockMultipartFile mockMultipartFile) throws Exception {
        List<UploadedXmlFile> uploadedXmlFiles = uploadFileAndExtractUploadedFiles(mockMultipartFile);
        return uploadedXmlFiles.iterator().next();
    }
}
