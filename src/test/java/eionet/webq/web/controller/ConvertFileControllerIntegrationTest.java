package eionet.webq.web.controller;

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

import eionet.webq.dto.Conversion;
import eionet.webq.dto.ListConversionResponse;
import eionet.webq.dto.UploadedXmlFile;
import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
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
    public void uploadFileAndConvertIt() throws Exception {
        String conversionResponse = "conversionSuccess";
        returnListOfConversionsFromRestApi(new ListConversionResponse());
        when(restOperations.postForObject(Mockito.anyString(), any(), eq(String.class))).thenReturn(conversionResponse);
        UploadedXmlFile uploadedXmlFile =
                uploadFileAndTakeFirstUploadedFile(createMockMultipartFile("test-file.xml", FILE_CONTENT));

        request(get("/convert?fileId={fileId}&conversionId={convId}", uploadedXmlFile.getId(), 1).session(mockHttpSession))
                .andExpect(MockMvcResultMatchers.content().bytes(conversionResponse.getBytes()));
    }

    @Test
    public void forUploadedFilesConversionOptionsAreSet() throws Exception {
        ListConversionResponse listOfConversions = new ListConversionResponse();
        listOfConversions.setConversions(Arrays.asList(new Conversion()));
        returnListOfConversionsFromRestApi(listOfConversions);
        
        uploadFile(createMockMultipartFile("file.xml", FILE_CONTENT));

        MvcResult mvcResult = request(get("/").session(mockHttpSession)).andReturn();

        @SuppressWarnings("unchecked")
        List<UploadedXmlFile> uploadedFiles = (List<UploadedXmlFile>) mvcResult.getModelAndView().getModel().get("uploadedFiles");
        assertThat(uploadedFiles.size(), equalTo(1));
        assertThat(uploadedFiles.iterator().next().getAvailableConversions().size(), equalTo(1));
    }

    private void returnListOfConversionsFromRestApi(ListConversionResponse listOfConversions) {
        when(restOperations.getForObject(anyString(), eq(ListConversionResponse.class), any())).thenReturn(listOfConversions);
    }
}
