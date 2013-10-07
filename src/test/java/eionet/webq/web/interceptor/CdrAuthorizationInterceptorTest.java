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
package eionet.webq.web.interceptor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class CdrAuthorizationInterceptorTest {
    @InjectMocks
    private CdrAuthorizationInterceptor interceptor;
    @Mock
    private RestOperations restOperations;
    private final String loginUrl = "login";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interceptor.setCdrLoginMethod(loginUrl);
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_ifAuthorizationHeaderNotSpecified_CommenceBasicAuthorization() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean proceed = interceptor.preHandle(new MockHttpServletRequest(), response, null);

        assertFalse(proceed);
        assertThatResponseIsBasicAuthorizationCommence(response);
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_ifAuthorizationNotEmptyAndResponseCodeIsFound_AllowToProceed() throws Exception {
        MockHttpServletRequest request = authenticationHeaderWillNotBeEmpty();
        when(restOperations.postForEntity(anyString(), anyObject(), any(Class.class))).thenReturn(
                new ResponseEntity(HttpStatus.FOUND));

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), null));
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_extractUrlFromBaseUriRequestParameter() throws Exception {
        MockHttpServletRequest request = authenticationHeaderWillNotBeEmpty();
        String expectedValue = "http://cdr.eu";
        request.setParameter("base_uri", expectedValue);
        assertUrlIsExtracted(request, "http://cdr.eu");
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_extractUrlFromEnvelopeRequestParameter() throws Exception {
        MockHttpServletRequest request = authenticationHeaderWillNotBeEmpty();
        String expectedValue = "http://cdr.eu";
        request.setParameter("envelope", expectedValue + "/envelope");
        assertUrlIsExtracted(request, "http://cdr.eu");
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_extractUrlFromInstanceRequestParameter() throws Exception {
        MockHttpServletRequest request = authenticationHeaderWillNotBeEmpty();
        String expectedValue = "http://cdr.eu";
        request.setParameter("instance", expectedValue + "/instance");
        assertUrlIsExtracted(request, "http://cdr.eu");
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_IfResponseCodeExceptionThrown_CommenceBasicAuthorization() throws Exception {
        MockHttpServletRequest request = authenticationHeaderWillNotBeEmpty();
        when(restOperations.postForEntity(anyString(), anyObject(), any(Class.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean proceed = interceptor.preHandle(request, response, null);

        assertFalse(proceed);
        assertThatResponseIsBasicAuthorizationCommence(response);
    }

    private void assertUrlIsExtracted(MockHttpServletRequest request, String expectedValue) throws Exception {
        when(restOperations.postForEntity(anyString(), anyObject(), any(Class.class))).thenReturn(
                new ResponseEntity(HttpStatus.FOUND));

        interceptor.preHandle(request, new MockHttpServletResponse(), null);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restOperations).postForEntity(urlCaptor.capture(), anyObject(), any(Class.class));

        assertThat(urlCaptor.getValue(), containsString(expectedValue));
    }

    private MockHttpServletRequest authenticationHeaderWillNotBeEmpty() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic 1jkahsd==");
        return request;
    }

    private void assertThatResponseIsBasicAuthorizationCommence(MockHttpServletResponse response) {
        assertThat(response.getHeader("WWW-Authenticate"), containsString("Basic"));
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_UNAUTHORIZED));
    }
}
