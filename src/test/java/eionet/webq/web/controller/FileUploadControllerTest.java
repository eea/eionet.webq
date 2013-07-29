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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import eionet.webq.web.AbstractContextControllerTests;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileUploadControllerTest extends AbstractContextControllerTests {
    private MockHttpSession mockHttpSession = new MockHttpSession();
    private final byte[] FILE_CONTENT = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<foo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"bar\" />").getBytes();

    @Test
    public void successfulUploadProducesMessage() throws Exception {
        MockMultipartFile file = createMockMultipartFile("orig");
        uploadFile(file).andExpect(model().attribute("message", "File 'orig' uploaded successfully"));
    }

    @Test
    public void downloadReturnsUploadedXmlFile() throws Exception {
        String fileName = "file.xml";

        uploadFile(createMockMultipartFile(fileName));

        mvc().perform(post("/download").param("fileName", fileName).session(mockHttpSession))
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(content().bytes(FILE_CONTENT)).andReturn();
    }

    @Test
    public void after3FilesUploadModelContainsAllUploadedFileNames() throws Exception {
        String fileName = "file";
        String fileName1 = "file1";
        String fileName2 = "file2";
        uploadFile(createMockMultipartFile(fileName));
        uploadFile(createMockMultipartFile(fileName1));
        uploadFile(createMockMultipartFile(fileName2))
                .andExpect(model().attribute("uploadedFiles", IsCollectionContaining.<String>hasItems(fileName, fileName1, fileName2)));
    }

    private MockMultipartFile createMockMultipartFile(String fileName) {
        return new MockMultipartFile("uploadedXmlFile", fileName, MediaType.APPLICATION_XML_VALUE, FILE_CONTENT);
    }

    private ResultActions uploadFile(MockMultipartFile file) throws Exception {
        return mvc().perform(fileUpload("/uploadXml").file(file).session(mockHttpSession));
    }

    private MockMvc mvc() {
        return webAppContextSetup(this.wac).build();
    }
}
