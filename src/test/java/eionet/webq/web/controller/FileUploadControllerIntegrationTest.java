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
import eionet.webq.dto.UserFile;
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
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        UserFile userFile = uploadFileAndTakeFirstUploadedFile();

        downloadFile(userFile.getId()).andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(content().bytes(FILE_CONTENT)).andReturn();
    }

    @Test
    public void after3FilesUploadModelContainsSameAmountOfFiles() throws Exception {
        uploadFile(createMockMultipartFile("file"));
        uploadFile(createMockMultipartFile("file1"));
        List<UserFile> uploadedFiles = uploadFileAndExtractUploadedFiles(createMockMultipartFile("file2"));

        assertThat(uploadedFiles.size(), is(3));
    }

    @Test
    public void allowToStoreFilesWithSameName() throws Exception {
        String file = "file.xml";
        uploadFile(createMockMultipartFile(file));
        List<UserFile> userFiles = uploadFileAndExtractUploadedFiles(createMockMultipartFile(file));

        assertThat(userFiles.size(), is(2));
    }

    @Test
    public void allowFileContentUpdateInStorage() throws Exception {
        UserFile userFile = uploadFileAndTakeFirstUploadedFile();
        String newContent = FILE_CONTENT_STRING.replace("/>", "><foobar></foobar></bar>");
        request(
                postWithMockSession("/saveXml").param("fileId", Integer.toString(userFile.getId()))
                        .content(newContent.getBytes()));

        downloadFile(userFile.getId()).andExpect(content().string(newContent));
    }

    @Test
    public void allowToRemoveFiles() throws Exception {
        uploadFile(createMockMultipartFile("file1"));
        Iterator<UserFile> it = uploadFileAndExtractUploadedFiles(createMockMultipartFile("file2")).iterator();

        String[] fileIds = {String.valueOf(it.next().getId()), String.valueOf(it.next().getId())};
        request(postWithMockSession("/remove/files").param("selectedUserFile", fileIds));

        List<UserFile> userFiles = extractUserFilesFromMvcResult(request(get("/").session(mockHttpSession)).andReturn());
        assertThat(userFiles.size(), equalTo(0));
    }

    private UserFile uploadFileAndTakeFirstUploadedFile() throws Exception {
        return uploadFileAndTakeFirstUploadedFile(createMockMultipartFile("file.xml"));
    }

    protected MockMultipartFile createMockMultipartFile(String fileName) {
        return createMockMultipartFile(fileName, FILE_CONTENT);
    }

    private ResultActions downloadFile(int fileId) throws Exception {
        return request(postWithMockSession("/download/user_file").param("fileId", Integer.toString(fileId)));
    }
}
