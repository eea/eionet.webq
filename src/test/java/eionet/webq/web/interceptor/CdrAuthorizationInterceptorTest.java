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

import eionet.webq.converter.CookiesToStringBidirectionalConverter;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static eionet.webq.web.interceptor.CdrAuthorizationInterceptor.ALLOWED_AUTHORIZATION_FAILURES_COUNT;
import static eionet.webq.web.interceptor.CdrAuthorizationInterceptor.AUTHORIZATION_FAILED_ATTRIBUTE;
import static eionet.webq.web.interceptor.CdrAuthorizationInterceptor.AUTHORIZATION_TRY_COUNT;
import org.apache.http.HttpResponse;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class CdrAuthorizationInterceptorTest {
    private final String loginUrl = "login";
    @Mock
    CookiesToStringBidirectionalConverter cookiesConverter;
    @InjectMocks
    private CdrAuthorizationInterceptor interceptor;
    @Mock
    private RestOperations restOperations;
    @Mock
    private HttpSession session;

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
        MockHttpServletRequest request = requestWithNonEmptyAuthHeader();
        when(restOperations.postForEntity(anyString(), anyObject(), any(Class.class))).thenReturn(
                new ResponseEntity(HttpStatus.FOUND));

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), null));
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_extractUrlFromBaseUriRequestParameter() throws Exception {
        MockHttpServletRequest request = requestWithNonEmptyAuthHeader();
        String expectedValue = "http://cdr.eu";
        request.setParameter("base_uri", expectedValue);
        assertUrlIsExtracted(request, "http://cdr.eu");
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_extractUrlFromEnvelopeRequestParameter() throws Exception {
        MockHttpServletRequest request = requestWithNonEmptyAuthHeader();
        String expectedValue = "http://cdr.eu";
        request.setParameter("envelope", expectedValue + "/envelope");
        assertUrlIsExtracted(request, "http://cdr.eu");
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_extractUrlFromInstanceRequestParameterAndUseCookies() throws Exception {
        MockHttpServletRequest request = requestWithCookies();
        String expectedValue = "http://cdr.eu/envelope";
        request.setParameter("instance", "http://cdr.eu/envelope/instance.xml");
        assertUrlIsExtractedFromCookieRequest(request, "http://cdr.eu/envelope");
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_extractUrlFromInstanceRequestParameter() throws Exception {
        MockHttpServletRequest request = requestWithNonEmptyAuthHeader();
        String expectedValue = "http://cdr.eu";
        request.setParameter("instance", expectedValue + "/instance");
        assertUrlIsExtracted(request, "http://cdr.eu");
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_IfResponseCodeExceptionThrown_CommenceBasicAuthorization() throws Exception {
        restClientWillThrowException();

        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean proceed = interceptor.preHandle(requestWithNonEmptyAuthHeader(), response, null);

        assertFalse(proceed);
        assertThatResponseIsBasicAuthorizationCommence(response);
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_IfLoginAttemptIsUnsuccessful_FailedAuthAttemptsCountIs1() throws Exception {
        restClientWillThrowException();

        assertFalse(interceptor.preHandle(requestWithNonEmptyAuthHeader(), new MockHttpServletResponse(), null));

        verify(session).setAttribute(AUTHORIZATION_TRY_COUNT, 1);
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_IfThereWas1UnsuccessfulLoginAttemptAndThereIsAnotherFailedAttempt_FailedAttemptsCountIs2()
            throws Exception {
        when(session.getAttribute(AUTHORIZATION_TRY_COUNT)).thenReturn(1);
        restClientWillThrowException();

        assertFalse(interceptor.preHandle(requestWithNonEmptyAuthHeader(), new MockHttpServletResponse(), null));

        verify(session).setAttribute(AUTHORIZATION_TRY_COUNT, 2);
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_IfTUnsuccessfulLoginAttemptEqualsToAllowedFailsCount_AllowRequestToProceed()
            throws Exception {
        when(session.getAttribute(AUTHORIZATION_TRY_COUNT)).thenReturn(ALLOWED_AUTHORIZATION_FAILURES_COUNT);
        restClientWillThrowException();

        assertTrue(interceptor.preHandle(requestWithNonEmptyAuthHeader(), new MockHttpServletResponse(), null));
    }

    @Test
    public void whenQueryForUrlThroughInterceptor_IfAllowsToProceedByFailedAttemptsCount_SetRequestAttributeAndResetCounter()
            throws Exception {
        when(session.getAttribute(AUTHORIZATION_TRY_COUNT)).thenReturn(ALLOWED_AUTHORIZATION_FAILURES_COUNT);
        restClientWillThrowException();

        MockHttpServletRequest request = requestWithNonEmptyAuthHeader();

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), null));
        assertNotNull(request.getAttribute(AUTHORIZATION_FAILED_ATTRIBUTE));
        verify(session).removeAttribute(AUTHORIZATION_TRY_COUNT);
    }

    private void restClientWillThrowException() {
        when(restOperations.postForEntity(anyString(), anyObject(), any(Class.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
    }

    private void assertUrlIsExtracted(MockHttpServletRequest request, String expectedValue) throws Exception {
        when(restOperations.postForEntity(anyString(), anyObject(), any(Class.class))).thenReturn(
                new ResponseEntity(HttpStatus.FOUND));

        interceptor.preHandle(request, new MockHttpServletResponse(), null);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restOperations).postForEntity(urlCaptor.capture(), anyObject(), any(Class.class));

        assertThat(urlCaptor.getValue(), containsString(expectedValue));
    }

    private void assertUrlIsExtractedFromCookieRequest(MockHttpServletRequest request, String expectedValue) throws Exception {

        interceptor = spy(interceptor);
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 2, 0), 200, "OK"));
        doReturn(response).
                when(interceptor).fetchUrlWithoutRedirection(anyString(), (HttpHeaders) anyObject());
        interceptor.preHandle(request, new MockHttpServletResponse(), null);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(interceptor).fetchUrlWithoutRedirection(urlCaptor.capture(), any(HttpHeaders.class));

        assertThat(urlCaptor.getValue(), containsString(expectedValue));
    }

    private MockHttpServletRequest requestWithNonEmptyAuthHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic 1jkahsd==");
        return request;
    }

    private MockHttpServletRequest requestWithCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("_ZopeId", "\"68673848A6sbSTxqyEQ\"");
        Cookie[] cookies = {cookie};
        request.setCookies(cookies);
        return request;
    }

    private void assertThatResponseIsBasicAuthorizationCommence(MockHttpServletResponse response) {
        assertThat(response.getHeader("WWW-Authenticate"), containsString("Basic"));
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_UNAUTHORIZED));
    }
}
