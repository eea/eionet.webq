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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.xforms.model.submission.RequestHeader;
import de.betterform.xml.xforms.model.submission.RequestHeaders;
import eionet.webq.dao.orm.KnownHost;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.KnownHostAuthenticationMethod;
import eionet.webq.service.KnownHostsService;
import eionet.webq.service.UserFileService;

/**
 * Service handles HTTP requests done through XForms engine and adds auth info to request header if needed.
 *
 * @author Enriko Käsper
 */
@Component
public class XFormsHTTPRequestAuthHandlerImpl implements HTTPRequestAuthHandler {

    /** Session id attribute name stored in HTTP request header. */
    public static final String HTTP_JSESSIONID_ATTRIBUTE = "JSESSIONID=";

    /** Cookie attribute name stored in HTTP request header. */
    public static final String HTTP_COOKIE_ATTRIBUTE = "cookie";

    /** Request base URL attribute in bf context. */
    public static final String BF_REQUEST_URL_ATTRIBUTE = "requestURL";

    /** HTTP session id attribute in bf context. */
    public static final String BF_HTTP_SESSION_ATTRIBUTE = "httpSessionId";

    /** WebQ authorisation attribute in bf context. */
    public static final String WEBQ_AUTH_ATTRIBUTE = "webqAuth";

    /** User XML file service. */
    @Autowired
    UserFileService userFileService;

    /** Known host service. */
    @Autowired
    KnownHostsService knownHostsService;

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(XFormsHTTPRequestAuthHandlerImpl.class);

    @Override
    public void addAuthToHttpRequest(HttpRequestBase httpRequestBase, Map<Object, Object> context) {

        String uri = httpRequestBase.getURI().toString();

        String instance = null;
        String envelope = null;
        String requestURLHost = null;
        Integer fileId = null;
        String basicAuth = null;
        String sessionId = null;
        String sessionIdHash = null;

        if (uri == null) {
            return;
        }

        // load bf context attributes
        if (context.get("instance") != null) {
            instance = (String) context.get("instance");
        }
        if (context.get("envelope") != null) {
            envelope = (String) context.get("envelope");
        }
        if (context.get(BF_REQUEST_URL_ATTRIBUTE) != null) {
            try {
                URI requestURI = new URI((String) context.get(BF_REQUEST_URL_ATTRIBUTE));
                requestURLHost =
                            StringUtils.substringBefore(requestURI.toString(), requestURI.getHost()) + requestURI.getHost();
            } catch (URISyntaxException e) {
                LOGGER.warn("requestURL is not valid URL: " + context.get(BF_REQUEST_URL_ATTRIBUTE));
            }
        }
        if (context.get("fileId") != null) {
            fileId = Integer.valueOf((String) context.get("fileId"));
        }
        // http session attribute stored in betterform context
        if (context.get(BF_HTTP_SESSION_ATTRIBUTE) != null) {
            sessionId = (String) context.get(BF_HTTP_SESSION_ATTRIBUTE);
            sessionIdHash = DigestUtils.md5Hex(sessionId);
        }

        String logSessionId = (sessionIdHash != null) ? "no session id found" : "sessionIdHash=" + sessionIdHash;
        LOGGER.info("Get resource from XForm (" + logSessionId + "): " + uri);

        if (uri.startsWith(requestURLHost)) {
            // check if the request on the same (webq) host is done in the same session. Fix session id if required.
            if (sessionId != null) {
                validateSessionIdInRequestHeader(context, sessionId);
            }
        } else {
            // add auth info only for URIs that are not on the same host.
            if (fileId != null && sessionId != null) {
                LOGGER.debug("Check if user is logged in to get resource for fileId=" + fileId);
                if (!context.containsKey(WEBQ_AUTH_ATTRIBUTE)) {
                    // check if user is logged in - ask auth info from user_xml file table
                    UserFile userFile = userFileService.getByIdAndUser(fileId, sessionIdHash);
                    if (userFile != null) {
                        basicAuth = userFile.getAuthorization();
                    }
                    context.put(WEBQ_AUTH_ATTRIBUTE, basicAuth);
                    LOGGER.debug("Store basic auth info in context for fileId=" + fileId);
                } else {
                    // auth info stored in context
                    basicAuth = context.get(WEBQ_AUTH_ATTRIBUTE) != null ? (String) context.get(WEBQ_AUTH_ATTRIBUTE) : null;
                }
                // add auth info only if user is logged in
                if (StringUtils.isNotEmpty(basicAuth)) {
                    LOGGER.debug("User is logged in to get resource for fileId=" + fileId);

                    // if the URI starts with instance or envelope URI, then we can use the basic auth retrieved from CDR.
                    if (((StringUtils.isNotBlank(instance) && uri.startsWith(instance)) || (StringUtils.isNotBlank(envelope) && uri
                            .startsWith(envelope)))) {
                        httpRequestBase.addHeader("Authorization", basicAuth);
                        LOGGER.debug("Add basic auth from session to URL: " + uri);
                    } else {
                        // check if we have known host in db
                        KnownHost knownHost = getKnownHost(uri);
                        if (knownHost != null) {
                            // add ticket parameter to request URI if needed
                            if (knownHost.getAuthenticationMethod() == KnownHostAuthenticationMethod.REQUEST_PARAMETER) {
                                LOGGER.debug("Add ticket parameter from known hosts to URL: " + uri);
                                uri = getUrlWithAuthParam(uri, knownHost);
                                if (!uri.equals(httpRequestBase.getURI().toString())) {
                                    try {
                                        httpRequestBase.setURI(new URI(uri));
                                    } catch (URISyntaxException e) {
                                        LOGGER.error("Unable to add known host ticket parameter for URI:" + uri);
                                        e.printStackTrace();
                                    }
                                }
                            } else if (knownHost.getAuthenticationMethod() == KnownHostAuthenticationMethod.BASIC) {
                                // Add basic authorisation if needed
                                try {
                                    httpRequestBase.addHeader(
                                            "Authorization", "Basic " + Base64.encodeBase64String((knownHost.getKey() + ":"
                                                    + knownHost.getTicket()).getBytes("utf-8")));
                                } catch (UnsupportedEncodingException e) {
                                    LOGGER.warn("UnsupportedEncodingException: utf-8");
                                }
                                LOGGER.debug("Add basic auth from known hosts to URL: " + uri);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Betterform engine forwards the cookie attribute found from initial request header (sent by browser) to the destination uri.
     * The jsessionid in cookie could be invalid if Tomcat has created a new session. This methods validates the cookie header and
     * replaces or adds the correct jsessionid if needed. Otherwise saveXml will not work if the session id is invalid.
     *
     * @param context Map of context parameters
     * @param sessionId jsessionid stored in request header.
     */
    public static void validateSessionIdInRequestHeader(Map<?, ?> context, String sessionId) {
        RequestHeaders httpRequestHeaders = (RequestHeaders) context.get(AbstractHTTPConnector.HTTP_REQUEST_HEADERS);
        RequestHeader cookieHeader = httpRequestHeaders.getRequestHeader(HTTP_COOKIE_ATTRIBUTE);
        String newCookieHeaderValue = null;

        if (cookieHeader == null || cookieHeader.getValue() == null) {
            newCookieHeaderValue = HTTP_JSESSIONID_ATTRIBUTE + sessionId;
            LOGGER.info("Add new cookie: " + newCookieHeaderValue);
        } else if (!cookieHeader.getValue().contains(HTTP_JSESSIONID_ATTRIBUTE)) {
            newCookieHeaderValue = cookieHeader.getValue() + "; " + HTTP_JSESSIONID_ATTRIBUTE + sessionId;
            LOGGER.info("Append jsessionid to cookie: " + newCookieHeaderValue);
        } else if (cookieHeader.getValue().contains(HTTP_JSESSIONID_ATTRIBUTE)
                && !cookieHeader.getValue().contains(HTTP_JSESSIONID_ATTRIBUTE + sessionId)) {
            newCookieHeaderValue =
                    cookieHeader.getValue().replaceAll(HTTP_JSESSIONID_ATTRIBUTE + "([^;]*)",
                            HTTP_JSESSIONID_ATTRIBUTE + sessionId);
            LOGGER.info("Found wrong JSESSIONID from request header: " + cookieHeader.getValue());
            LOGGER.info("Change jsessionid in cookie: " + newCookieHeaderValue);
        }
        if (newCookieHeaderValue != null) {
            httpRequestHeaders.removeHeader(HTTP_COOKIE_ATTRIBUTE);
            httpRequestHeaders.addHeader(new RequestHeader(HTTP_COOKIE_ATTRIBUTE, newCookieHeaderValue));
        }
    }

    /**
     * Get authorization info for known hosts.
     *
     * @param uri URL to look for authorisation.
     * @return KnownHost object with auth info
     */
    private KnownHost getKnownHost(String uri) {

        KnownHost knownHost = null;
        Collection<KnownHost> hosts = knownHostsService.findAll();
        for (KnownHost host : hosts) {
            if (uri.startsWith(host.getHostURL())) {
                knownHost = host;
                break;
            }
        }
        return knownHost;
    }

    /**
     * Adds authorization info as request parameter to given URI, if it is known host.
     *
     * @param url URL to add authorization info.
     * @param knownHost Host object with authorization info.
     * @return parsed URL.
     */
    public static String getUrlWithAuthParam(String url, KnownHost knownHost) {

        String authUrl = url;
        if (knownHost != null && knownHost.getAuthenticationMethod() == KnownHostAuthenticationMethod.REQUEST_PARAMETER) {
            authUrl += (url.contains("?")) ? "&" : "?";
            authUrl += knownHost.getKey() + "=" + knownHost.getTicket();
        }
        return authUrl;
    }
}
