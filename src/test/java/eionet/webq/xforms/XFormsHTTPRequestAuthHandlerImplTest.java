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

package eionet.webq.xforms;

import configuration.ApplicationTestContextWithMockSession;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.xforms.model.submission.RequestHeader;
import de.betterform.xml.xforms.model.submission.RequestHeaders;
import eionet.webq.dao.orm.KnownHost;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.KnownHostAuthenticationMethod;
import eionet.webq.service.KnownHostsService;
import eionet.webq.service.UserFileService;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Enriko Käsper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class XFormsHTTPRequestAuthHandlerImplTest {

    private static final String BASE_URI = "http://webq2.eionet.europe.eu";
    private static final String BASIC_AUTH_KNOWN_HOST_URL = "http://basicauth.host";
    private static final String REQUEST_PARAM_KNOWN_HOST_URL = "http://requestparam.host";
    private static final String KEY = "key";
    private static final String TICKET = "ticket";
    private static final String oldSessionId = "91F327440B3E82146E7384E54EFB99DD";
    private static final String newSessionId = "B41DDE22E96CC895DD1F0DF328C08527";
    @Mock
    UserFileService userFileService;
    @InjectMocks
    XFormsHTTPRequestAuthHandlerImpl requestAuthHandler = new XFormsHTTPRequestAuthHandlerImpl();
    @Mock
    private KnownHostsService knownHostsService;

    static KnownHost createBasicAuthKnownHost() {
        KnownHost host = new KnownHost();
        host.setHostURL(BASIC_AUTH_KNOWN_HOST_URL);
        host.setTicket(TICKET);
        host.setKey(KEY);
        host.setAuthenticationMethod(KnownHostAuthenticationMethod.BASIC);
        return host;
    }

    static KnownHost createRequestParamKnownHost() {
        KnownHost host = new KnownHost();
        host.setHostURL(REQUEST_PARAM_KNOWN_HOST_URL);
        host.setTicket(TICKET);
        host.setKey(KEY);
        host.setAuthenticationMethod(KnownHostAuthenticationMethod.REQUEST_PARAMETER);
        return host;
    }

    static UserFile createUserFileWithAuth() {
        UserFile userFile = createUserFile();
        userFile.setAuthorization("Basic a2V5OnRpY2tldA==");
        userFile.setAuthorized(true);
        return userFile;
    }

    static UserFile createUserFile() {
        UserFile userFile = new UserFile();
        userFile.setId(1);
        userFile.setUserId("12345");
        return userFile;
    }

    static Map<Object, Object> createBfContextMap() {
        Map<Object, Object> bfContext = new HashMap<Object, Object>();
        bfContext.put("fileId", "1");
        bfContext.put("httpSessionId", newSessionId);
        bfContext.put("requestURL", BASE_URI + "/xform");

        RequestHeaders httpRequestHeaders = new RequestHeaders();
        String initialCookie = "JSESSIONID=" + oldSessionId;
        httpRequestHeaders.addHeader(new RequestHeader("cookie", initialCookie));
        bfContext.put(AbstractHTTPConnector.HTTP_REQUEST_HEADERS, httpRequestHeaders);
        return bfContext;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void ignoreRequestsToBaseUri() {

        HttpRequestBase httpRequest = new HttpGet(BASE_URI + "/resource");

        Map<Object, Object> context = createBfContextMap();
        requestAuthHandler.addAuthToHttpRequest(httpRequest, context);
        assertThatRequestIsNotChanged(httpRequest, BASE_URI + "/resource");
        assertThatRequestHeaderContainsValidSessionId(context);
        verify(userFileService, never()).getById(anyInt());
    }

    @Test
    public void ignoreRequestsWithNoAuthInfoInUserFile() {

        HttpRequestBase httpRequest = new HttpGet(BASIC_AUTH_KNOWN_HOST_URL);
        when(userFileService.getById(anyInt())).thenReturn(createUserFile());
        requestAuthHandler.addAuthToHttpRequest(httpRequest, createBfContextMap());
        assertThatRequestIsNotChanged(httpRequest, BASIC_AUTH_KNOWN_HOST_URL);
        verify(userFileService, atLeastOnce()).getById(anyInt());
    }

    @Test
    public void addBasicAuthIfSameHostAsInstance() {
        HttpRequestBase httpRequest = new HttpGet(BASIC_AUTH_KNOWN_HOST_URL + "/resource.xml");
        Map<Object, Object> context = createBfContextMap();
        context.put("instance", BASIC_AUTH_KNOWN_HOST_URL + "/instance.xml");
        UserFile userFile = createUserFileWithAuth();

        when(userFileService.getById(anyInt())).thenReturn(userFile);
        when(knownHostsService.getKnownHost(anyString())).thenReturn(createBasicAuthKnownHost());
        requestAuthHandler.addAuthToHttpRequest(httpRequest, context);

        assertThat(httpRequest.getURI().toString(), equalTo(BASIC_AUTH_KNOWN_HOST_URL + "/resource.xml"));
        assertThat(httpRequest.getHeaders("Authorization")[0].getValue().trim(), equalTo(userFile.getAuthorization()));

    }

    @Test
    public void addRequestParamAuthIfSameHostAsInstance() {
        HttpRequestBase httpRequest = new HttpGet(REQUEST_PARAM_KNOWN_HOST_URL + "/resource.xml");
        Map<Object, Object> context = createBfContextMap();
        context.put("instance", REQUEST_PARAM_KNOWN_HOST_URL + "/instance.xml");
        UserFile userFile = createUserFileWithAuth();

        when(userFileService.getById(anyInt())).thenReturn(userFile);
        when(knownHostsService.getKnownHost(anyString())).thenReturn(createRequestParamKnownHost());
        requestAuthHandler.addAuthToHttpRequest(httpRequest, context);

        assertThat(httpRequest.getURI().toString(), equalTo(REQUEST_PARAM_KNOWN_HOST_URL + "/resource.xml?key=ticket"));
    }

    @Test
    public void noAuthIfDifferentHostAsInstance() {
        String resourceUrl = "http://resource.xml";
        HttpRequestBase httpRequest = new HttpGet(resourceUrl);
        Map<Object, Object> context = createBfContextMap();
        context.put("instance", REQUEST_PARAM_KNOWN_HOST_URL + "/instance.xml");
        UserFile userFile = createUserFileWithAuth();

        when(userFileService.getById(anyInt())).thenReturn(userFile);
        when(knownHostsService.getKnownHost(anyString())).thenReturn(null);
        requestAuthHandler.addAuthToHttpRequest(httpRequest, context);

        assertThat(httpRequest.getURI().toString(), equalTo(resourceUrl));
        assertThat(httpRequest.getHeaders("Authorization").length, equalTo(0));
    }

    @Test
    public void useAuthInfoFromContextAndNotFromDb() {

        HttpRequestBase httpRequest = new HttpGet(REQUEST_PARAM_KNOWN_HOST_URL + "/resource.xml");
        Map<Object, Object> context = createBfContextMap();
        context.put("instance", REQUEST_PARAM_KNOWN_HOST_URL + "/instance.xml");
        context.put("webqAuth", "Basic auth");

        requestAuthHandler.addAuthToHttpRequest(httpRequest, context);

        assertThat((String) context.get("webqAuth"), equalTo("Basic auth"));
        verify(userFileService, never()).getById(anyInt());
    }

    @Test
    public void testUrlWithAuthParam() {
        String safeUrl =
                XFormsHTTPRequestAuthHandlerImpl.getUrlWithAuthParam(REQUEST_PARAM_KNOWN_HOST_URL, createRequestParamKnownHost());
        assertThat(safeUrl, equalTo(REQUEST_PARAM_KNOWN_HOST_URL + "?key=ticket"));

        String safeUrlWithParams =
                XFormsHTTPRequestAuthHandlerImpl.getUrlWithAuthParam(REQUEST_PARAM_KNOWN_HOST_URL + "?1=1",
                        createRequestParamKnownHost());
        assertThat(safeUrlWithParams, equalTo(REQUEST_PARAM_KNOWN_HOST_URL + "?1=1&key=ticket"));
    }

    @Test
    public void validateRequestHeaderIfInvalidSessionId() {
        Map<Object, Object> bfContext = new HashMap<Object, Object>();
        RequestHeaders httpRequestHeaders = new RequestHeaders();
        String initialCookie = "JSESSIONID=" + oldSessionId + "; DWRSESSIONID=YEG6zYlxfPqHQHZwt3wC0L9Dgek";
        httpRequestHeaders.addHeader(new RequestHeader("cookie", initialCookie));
        bfContext.put(AbstractHTTPConnector.HTTP_REQUEST_HEADERS, httpRequestHeaders);
        XFormsHTTPRequestAuthHandlerImpl.validateSessionIdInRequestHeader(bfContext, newSessionId);
        assertThatRequestHeaderContainsValidSessionId(bfContext);
    }

    @Test
    public void validateRequestHeaderIfInvalidSessionIdInDifferentOrder() {
        Map<Object, Object> bfContext = new HashMap<Object, Object>();
        RequestHeaders httpRequestHeaders = new RequestHeaders();
        String initialCookie = "DWRSESSIONID=YEG6zYlxfPqHQHZwt3wC0L9Dgek;JSESSIONID=" + oldSessionId;
        httpRequestHeaders.addHeader(new RequestHeader("cookie", initialCookie));
        bfContext.put(AbstractHTTPConnector.HTTP_REQUEST_HEADERS, httpRequestHeaders);
        XFormsHTTPRequestAuthHandlerImpl.validateSessionIdInRequestHeader(bfContext, newSessionId);
        assertThatRequestHeaderContainsValidSessionId(bfContext);
    }

    @Test
    public void validateRequestHeaderIfMissingCookie() {
        Map<Object, Object> bfContext = new HashMap<Object, Object>();
        RequestHeaders httpRequestHeaders = new RequestHeaders();
        bfContext.put(AbstractHTTPConnector.HTTP_REQUEST_HEADERS, httpRequestHeaders);
        XFormsHTTPRequestAuthHandlerImpl.validateSessionIdInRequestHeader(bfContext, newSessionId);
        assertThatRequestHeaderContainsValidSessionId(bfContext);
    }

    @Test
    public void validateRequestHeaderIfMissingSessionId() {
        Map<Object, Object> bfContext = new HashMap<Object, Object>();
        RequestHeaders httpRequestHeaders = new RequestHeaders();
        String initialCookie = "DWRSESSIONID=YEG6zYlxfPqHQHZwt3wC0L9Dgek";
        httpRequestHeaders.addHeader(new RequestHeader("cookie", initialCookie));
        bfContext.put(AbstractHTTPConnector.HTTP_REQUEST_HEADERS, httpRequestHeaders);
        XFormsHTTPRequestAuthHandlerImpl.validateSessionIdInRequestHeader(bfContext, newSessionId);
        assertThatRequestHeaderContainsValidSessionId(bfContext);
    }

    private void assertThatRequestHeaderContainsValidSessionId(Map<?, ?> bfContext) {
        RequestHeaders requestHeaders = (RequestHeaders) bfContext.get(AbstractHTTPConnector.HTTP_REQUEST_HEADERS);
        RequestHeader cookieHeader = requestHeaders.getRequestHeader("cookie");
        assertThat(cookieHeader.getValue(), containsString("JSESSIONID=" + newSessionId));
        assertThat(cookieHeader.getValue(), not(containsString("JSESSIONID=" + oldSessionId)));

    }

    private void assertThatRequestIsNotChanged(HttpRequestBase httpRequest, String requestUri) {
        assertThat(httpRequest.getURI().toString(), startsWith(requestUri));
        assertThat(httpRequest.getHeaders("Authorization").length, equalTo(0));
    }
}
