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
 *        Enriko Käsper
 */
package eionet.webq.web.controller;

import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileUploadControllerTest extends AbstractContextControllerTests {

    @Test
    public void readString() throws Exception {
        MockMultipartFile file = createMockMultipartFile("orig", null, "bar".getBytes());
        uploadFile(file).andExpect(model().attribute("message", "File 'orig' uploaded successfully"));
    }

    @Test
    public void downloadReturnsUploadedXmlFile() throws Exception {
        String fileName = "file.xml";
        byte[] fileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\">".getBytes();

        uploadFile(createMockMultipartFile(fileName, MediaType.APPLICATION_XML_VALUE, fileContent));

        mvc().perform(post("/download").param("fileName", fileName))
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(content().bytes(fileContent)).andReturn();
    }

    private MockMultipartFile createMockMultipartFile(String fileName, String mediaType, byte[] fileContent) {
        return new MockMultipartFile("uploadedXmlFile", fileName, mediaType, fileContent);
    }

    private ResultActions uploadFile(MockMultipartFile file) throws Exception {
        return mvc().perform(fileUpload("/uploadXml").file(file));
    }

    private MockMvc mvc() {
        return webAppContextSetup(this.wac).build();
    }
}
