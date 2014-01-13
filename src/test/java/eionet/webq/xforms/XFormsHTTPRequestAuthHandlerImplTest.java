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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dao.orm.KnownHost;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.KnownHostAuthenticationMethod;
import eionet.webq.service.KnownHostsService;
import eionet.webq.service.UserFileService;

/**
 * @author Enriko Käsper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class XFormsHTTPRequestAuthHandlerImplTest {

    @Mock
    UserFileService userFileService;
    @Mock
    private KnownHostsService knownHostsService;
    @InjectMocks
    XFormsHTTPRequestAuthHandlerImpl requestAuthHandler = new XFormsHTTPRequestAuthHandlerImpl();

    private static final String BASE_URI = "http://webq2.eionet.europe.eu";
    private static final String BASIC_AUTH_KNOWN_HOST_URL = "http://basicauth.host";
    private static final String REQUEST_PARAM_KNOWN_HOST_URL = "http://requestparam.host";
    private static final String KEY = "key";
    private static final String TICKET = "ticket";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void ignoreRequestsToBaseUri() {

        HttpRequestBase httpRequest = new HttpGet(BASE_URI + "/resource");

        requestAuthHandler.addAuthToHttpRequest(httpRequest, createParamsMap());
        assertThatRequestIsNotChanged(httpRequest, BASE_URI + "/resource");
        verify(userFileService, never()).getByIdAndUser(anyInt(), anyString());
    }

    @Test
    public void ignoreRequestsWithNoAuthInfoInUserFile() {

        HttpRequestBase httpRequest = new HttpGet(BASIC_AUTH_KNOWN_HOST_URL);
        when(userFileService.getByIdAndUser(anyInt(), anyString())).thenReturn(createUserFile());
        requestAuthHandler.addAuthToHttpRequest(httpRequest, createParamsMap());
        assertThatRequestIsNotChanged(httpRequest, BASIC_AUTH_KNOWN_HOST_URL);
        verify(userFileService, atLeastOnce()).getByIdAndUser(anyInt(), anyString());
    }

    @Test
    public void addBasicAuthIfSameHostAsInstance() {
        HttpRequestBase httpRequest = new HttpGet(BASIC_AUTH_KNOWN_HOST_URL + "/resource.xml");
        Map<String, String> params = createParamsMap();
        params.put("instance", BASIC_AUTH_KNOWN_HOST_URL + "/instance.xml");
        UserFile userFile = createUserFileWithAuth();

        when(userFileService.getByIdAndUser(anyInt(), anyString())).thenReturn(userFile);
        when(knownHostsService.findAll()).thenReturn(Arrays.asList(createBasicAuthKnownHost()));
        requestAuthHandler.addAuthToHttpRequest(httpRequest, params);

        assertThat(httpRequest.getURI().toString(), equalTo(BASIC_AUTH_KNOWN_HOST_URL + "/resource.xml"));
        assertThat(httpRequest.getHeaders("Authorization")[0].getValue().trim(), equalTo(userFile.getAuthorization()));

    }

    @Test
    public void addRequestParamAuthIfSameHostAsInstance() {
        HttpRequestBase httpRequest = new HttpGet(REQUEST_PARAM_KNOWN_HOST_URL + "/resource.xml");
        Map<String, String> params = createParamsMap();
        params.put("instance", REQUEST_PARAM_KNOWN_HOST_URL + "/instance.xml");
        UserFile userFile = createUserFileWithAuth();

        when(userFileService.getByIdAndUser(anyInt(), anyString())).thenReturn(userFile);
        when(knownHostsService.findAll()).thenReturn(Arrays.asList(createRequestParamKnownHost()));
        requestAuthHandler.addAuthToHttpRequest(httpRequest, params);

        assertThat(httpRequest.getURI().toString(), equalTo(REQUEST_PARAM_KNOWN_HOST_URL + "/resource.xml?key=ticket"));
    }

    @Test
    public void noAuthIfDifferentHostAsInstance() {
        String resourceUrl = "http://resource.xml";
        HttpRequestBase httpRequest = new HttpGet(resourceUrl);
        Map<String, String> params = createParamsMap();
        params.put("instance", REQUEST_PARAM_KNOWN_HOST_URL + "/instance.xml");
        UserFile userFile = createUserFileWithAuth();

        when(userFileService.getByIdAndUser(anyInt(), anyString())).thenReturn(userFile);
        when(knownHostsService.findAll()).thenReturn(Arrays.asList(createRequestParamKnownHost()));
        requestAuthHandler.addAuthToHttpRequest(httpRequest, params);

        assertThat(httpRequest.getURI().toString(), equalTo(resourceUrl));
        assertThat(httpRequest.getHeaders("Authorization").length, equalTo(0));

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

    private void assertThatRequestIsNotChanged(HttpRequestBase httpRequest, String requestUri) {
        assertThat(httpRequest.getURI().toString(), equalTo(requestUri));
        assertThat(httpRequest.getHeaders("Authorization").length, equalTo(0));
    }

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
        return userFile;
    }

    static UserFile createUserFile() {
        UserFile userFile = new UserFile();
        userFile.setId(1);
        userFile.setUserId("12345");
        return userFile;
    }

    static Map<String, String> createParamsMap() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fileId", "1");
        params.put("sessionid", "12345");
        params.put("requestURL", BASE_URI + "/xform");
        return params;
    }
}
