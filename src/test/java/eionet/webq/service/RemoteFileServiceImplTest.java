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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URL;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class RemoteFileServiceImplTest {
    private final byte[] FILE_CONTENT_IN_RESPONSE = "test file content".getBytes();
    @InjectMocks
    private RemoteFileServiceImpl remoteFileService;
    @Mock
    RestOperations restOperations;
    private final String url = "http://file.url";

    private static final String LOCAL_FILE_NAME = "test-localfile-content.txt";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fetchesFileContentFromUrl() throws Exception {
        httpResponseWithBytes();
        byte[] bytes = remoteFileService.fileContent(url);
        assertThat(bytes, equalTo(FILE_CONTENT_IN_RESPONSE));
    }

    @Test(expected = FileNotAvailableException.class)
    public void throwsExceptionIfResponseIsNotOK() throws Exception {
        when(restOperations.getForEntity(url, byte[].class))
                .thenThrow(new RestClientException("Download is not possible"));

        remoteFileService.fileContent(url);
    }

    @Test(expected = FileNotAvailableException.class)
    public void throwsExceptionIfResponseDoesNotHaveBody() throws Exception {
        httpResponseWithBytes(null);

        remoteFileService.fileContent(url);
    }

    @Test
    public void checksumForFilesMustBeEqual() throws Exception {
        httpResponseWithBytes();
        assertTrue(remoteFileService.isChecksumMatches(FILE_CONTENT_IN_RESPONSE, url));
    }

    @Test
    public void filesMustMismatch() throws Exception {
        httpResponseWithBytes();
        byte[] otherContent = "some other test file content".getBytes();
        assertFalse(remoteFileService.isChecksumMatches(otherContent, url));
    }

    @Test
    public void fetchesFileContentFromFileUri() throws Exception {
        URL resource = getClass().getClassLoader().getResource(LOCAL_FILE_NAME);
        if (resource == null) {
            fail("No test file available.");
        }
        String localFileUrl = "file://" + resource.getFile();
        byte[] bytes = remoteFileService.fileContent(localFileUrl);
        assertThat(bytes, equalTo(FILE_CONTENT_IN_RESPONSE));
    }

    @Test(expected = FileNotAvailableException.class)
    public void throwsExceptionIfFileUriNotAvailable() throws Exception {
        String localFileUrl = "file:///unknown-file.txt";
        remoteFileService.fileContent(localFileUrl);
    }

    @Test(expected = FileNotAvailableException.class)
    public void throwsExceptionIfFileUriIsInvalidUri() throws Exception {
        String localFileUrl = "file://invalid-uri.txt";
        remoteFileService.fileContent(localFileUrl);
    }

    private void httpResponseWithBytes() {
        httpResponseWithBytes(FILE_CONTENT_IN_RESPONSE);
    }

    private void httpResponseWithBytes(byte[] responseBody) {
        when(restOperations.getForEntity(any(URI.class), eq(byte[].class)))
                .thenReturn(new ResponseEntity<byte[]>(responseBody, HttpStatus.OK));
    }
}
