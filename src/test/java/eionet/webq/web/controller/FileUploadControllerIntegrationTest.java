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

import eionet.webq.dto.Conversion;
import eionet.webq.dto.ListConversionResponse;
import eionet.webq.dto.UploadedXmlFile;
import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestOperations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileUploadControllerIntegrationTest extends AbstractContextControllerTests {
    private final String FILE_CONTENT_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<foo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"bar\" />";
    private final byte[] FILE_CONTENT = FILE_CONTENT_STRING.getBytes();

    @Autowired
    RestOperations operations;

    @Before
    public void mockConversionServiceApiCall() {
        ListConversionResponse listConversionResponse = new ListConversionResponse();
        listConversionResponse.setConversions(new ArrayList<Conversion>());
        when(operations.getForObject(anyString(), eq(ListConversionResponse.class), any())).thenReturn(listConversionResponse);
    }

    @Test
    public void successfulUploadProducesMessage() throws Exception {
        MockMultipartFile file = createMockMultipartFile("orig");
        uploadFile(file).andExpect(model().attribute("message", "File 'orig' uploaded successfully"));
    }

    @Test
    public void downloadReturnsUploadedXmlFile() throws Exception {

        UploadedXmlFile uploadedXmlFile = uploadFileAndTakeFirstUploadedFile();

        downloadFile(uploadedXmlFile.getId()).andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(content().bytes(FILE_CONTENT)).andReturn();
    }

    @Test
    public void after3FilesUploadModelContainsSameAmountOfFiles() throws Exception {
        uploadFile(createMockMultipartFile("file"));
        uploadFile(createMockMultipartFile("file1"));
        List<UploadedXmlFile> uploadedFiles = uploadFileAndExtractUploadedFiles(createMockMultipartFile("file2"));

        assertThat(uploadedFiles.size(), is(3));
    }

    @Test
    public void allowToStoreFilesWithSameName() throws Exception {
        String file = "file.xml";
        uploadFile(createMockMultipartFile(file));
        List<UploadedXmlFile> uploadedXmlFiles = uploadFileAndExtractUploadedFiles(createMockMultipartFile(file));

        assertThat(uploadedXmlFiles.size(), is(2));
    }

    @Test
    public void allowFileContentUpdateInStorage() throws Exception {
        UploadedXmlFile uploadedXmlFile = uploadFileAndTakeFirstUploadedFile();
        String newContent = FILE_CONTENT_STRING.replace("/>", "><foobar></foobar></bar>");
        mvc().perform(
                postWithMockSession("/saveXml").param("fileId", Integer.toString(uploadedXmlFile.getId()))
                        .content(newContent.getBytes()));

        downloadFile(uploadedXmlFile.getId()).andExpect(content().string(newContent));
    }

    private UploadedXmlFile uploadFileAndTakeFirstUploadedFile() throws Exception {
        return uploadFileAndTakeFirstUploadedFile(createMockMultipartFile("file.xml"));
    }

    protected MockMultipartFile createMockMultipartFile(String fileName) {
        return createMockMultipartFile(fileName, FILE_CONTENT);
    }

    private ResultActions downloadFile(int fileId) throws Exception {
        return mvc().perform(postWithMockSession("/download").param("fileId", Integer.toString(fileId)));
    }
}
