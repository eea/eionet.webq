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
package eionet.webq.service;

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.Conversion;
import eionet.webq.dto.ListConversionResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import util.CacheCleaner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class ConversionServiceImplTest {
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private CacheCleaner cacheCleaner;
    @Autowired
    private RestOperations restOperations;
    private Cache conversionsCache;
    private final String xmlSchema = "schema.xsd";

    @Before
    public void prepare() throws Exception {
        conversionsCache = cacheCleaner.cleanConversionsCacheAndReturnIt();
    }

    @After
    public void tearDown() throws Exception {
        reset(restOperations);
    }

    @Test
    public void getsConversionForFileUsingXmlSchema() throws Exception {
        restTemplateWillReturnConversionsForSchema(createResponse(new Conversion()), xmlSchema);

        List<Conversion> conversions = conversionService.conversionsFor(xmlSchema);

        assertThat(conversions.size(), equalTo(1));
    }

    @Test
    public void forTheSameSchemaConversionsAreTakenFromCache() throws Exception {
        restTemplateWillReturnConversionsForSchema(createResponse(new Conversion()), xmlSchema);
        conversionService.conversionsFor(xmlSchema);
        conversionService.conversionsFor(xmlSchema);

        @SuppressWarnings("unchecked")
        Collection<Object> entries = (Collection<Object>) conversionsCache.get(xmlSchema).get();

        assertThat(entries.size(), equalTo(1));
        verify(restOperations).getForObject(anyString(), eq(ListConversionResponse.class), eq(xmlSchema));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void convertRequestShouldContainRequiredAttributes() throws Exception {
        final byte[] testContent = "test content".getBytes();
        final int convertId = 1;

        when(restOperations.postForEntity(anyString(), any(), eq(byte[].class)))
                .thenReturn(new ResponseEntity<byte[]>("response".getBytes(), HttpStatus.OK));
        UserFile userFile = new UserFile();
        userFile.setContent(testContent);
        conversionService.convert(userFile, 1);

        ArgumentCaptor<MultiValueMap> postParameters = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(restOperations).postForEntity(anyString(), postParameters.capture(), eq(byte[].class));

        assertPostParametersAreCorrect(((MultiValueMap<String, Object>)postParameters.getValue()), testContent, convertId);
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionIfRetrievedAnswerFromConversionWasNotOk() throws Exception {
        when(restOperations.postForEntity(anyString(), any(), eq(byte[].class))).thenReturn(new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST));

        conversionService.convert(new UserFile(new UploadedFile("file", "file-content".getBytes()), "xml-schema"), 1);
    }

    @SuppressWarnings("unchecked")
    private void assertPostParametersAreCorrect(MultiValueMap<String, Object> postParameters, byte[] content, int convertId) {
        for (List<Object> objects : postParameters.values()) {
            assertThat(objects.size(), equalTo(1));
        }

        Map<String, Object> singleValues = postParameters.toSingleValueMap();
        assertThat(singleValues.size(), equalTo(2));

        HttpEntity<byte[]> convertFile = (HttpEntity<byte[]>) singleValues.get("convert_file");
        assertThat(convertFile.getBody(), equalTo(content));
        assertContentTypeAndContentDispositionSetForConvertFileParameter(convertFile);

        HttpEntity<String> convertIdParameter = (HttpEntity<String>) singleValues.get("convert_id");
        assertThat(convertIdParameter.getBody(), equalTo(Integer.toString(convertId)));
    }

    private void assertContentTypeAndContentDispositionSetForConvertFileParameter(HttpEntity<byte[]> convertFile) {
        HttpHeaders headers = convertFile.getHeaders();
        assertThat(headers.getContentType(), equalTo(MediaType.MULTIPART_FORM_DATA));

        List<String> contentDispositions = headers.get("Content-Disposition");
        assertThat(contentDispositions.size(), equalTo(1));
        assertThat(contentDispositions.iterator().next(), equalTo("form-data; name=\"convert_file\""));
    }

    private ListConversionResponse createResponse(Conversion... conversions) {
        ListConversionResponse response = new ListConversionResponse();
        response.setConversions(Arrays.asList(conversions));
        return response;
    }

    private void restTemplateWillReturnConversionsForSchema(ListConversionResponse conversionResponse, String xmlSchema) {
        when(restOperations.getForObject(anyString(), eq(ListConversionResponse.class), eq(xmlSchema))).thenReturn(
                conversionResponse);
    }
}
