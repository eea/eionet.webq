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
package eionet.webq.web.controller;

import eionet.webq.dto.Conversion;
import eionet.webq.dto.ListConversionResponse;
import eionet.webq.dto.UserFile;
import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;
import util.CacheCleaner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ConvertFileControllerIntegrationTest extends AbstractContextControllerTests {
    private final String xmlSchema = "schema";
    private final byte[] FILE_CONTENT =
            ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<foo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"" + xmlSchema + "\" />")
                    .getBytes();
    @Autowired
    private RestOperations restOperations;
    @Autowired
    private CacheCleaner cacheCleaner;

    @Before
    public void cleanCache() {
        cacheCleaner.cleanConversionsCacheAndReturnIt();
    }

    @Test
    public void uploadFileAndConvertItWithContentTypeAndContentDispositionResponseHeadersSet() throws Exception {
        byte[] conversionResponse = "conversionSuccess".getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_XML);
        String contentDispositionValue = "content-disposition";
        String contentDispositionHeaderName = "Content-Disposition";
        headers.add(contentDispositionHeaderName, contentDispositionValue);

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(conversionResponse, headers, HttpStatus.OK);
        returnListOfConversionsFromRestApi(new ListConversionResponse());
        when(restOperations.postForEntity(Mockito.anyString(), any(), eq(byte[].class))).thenReturn(responseEntity);

        UserFile userFile =
                uploadFileAndTakeFirstUploadedFile(createMockMultipartFile("test-file.xml", FILE_CONTENT));

        request(get("/download/convert?fileId={fileId}&conversionId={convId}", userFile.getId(), 1).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.content().bytes(conversionResponse))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type", APPLICATION_XML_VALUE))
                .andExpect(MockMvcResultMatchers.header().string(contentDispositionHeaderName, contentDispositionValue));
    }

    @Test
    public void forUploadedFilesConversionOptionsAreSet() throws Exception {
        ListConversionResponse listOfConversions = new ListConversionResponse();
        listOfConversions.setConversions(Arrays.asList(new Conversion()));
        returnListOfConversionsFromRestApi(listOfConversions);
        
        uploadFile(createMockMultipartFile("file.xml", FILE_CONTENT));

        MvcResult mvcResult = request(get("/").session(mockHttpSession)).andReturn();

        @SuppressWarnings("unchecked")
        List<UserFile> uploadedFiles = (List<UserFile>) mvcResult.getModelAndView().getModel().get("uploadedFiles");
        assertThat(uploadedFiles.size(), equalTo(1));
        assertThat(uploadedFiles.iterator().next().getAvailableConversions().size(), equalTo(1));
    }

    private void returnListOfConversionsFromRestApi(ListConversionResponse listOfConversions) {
        when(restOperations.getForObject(anyString(), eq(ListConversionResponse.class), any())).thenReturn(listOfConversions);
    }
}
