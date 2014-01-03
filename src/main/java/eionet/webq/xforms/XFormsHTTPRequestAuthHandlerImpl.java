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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    /** User XML file service. */
    @Autowired
    UserFileService userFileService;

    /** Known host service. */
    @Autowired
    KnownHostsService knownHostsService;

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(XFormsHTTPRequestAuthHandlerImpl.class);

    @Override
    public void addAuthToHttpRequest(HttpRequestBase httpRequestBase, Map<?, ?> context) {

        String uri = httpRequestBase.getURI().toString();

        String instance = null;
        String envelope = null;
        String requestURLHost = null;
        Integer fileId = null;
        String basicAuth = null;
        String sessionId = null;

        if (context.get("instance") != null) {
            instance = (String) context.get("instance");
        }
        if (context.get("envelope") != null) {
            instance = (String) context.get("envelope");
        }
        if (context.get("requestURL") != null) {
            try {
                URI requestURI = new URI((String) context.get("requestURL"));
                requestURLHost = StringUtils.substringBefore(requestURI.toString(), requestURI.getHost()) + requestURI.getHost();
            } catch (URISyntaxException e) {
                LOGGER.warn("requestURL is not valid URL: " + context.get("requestURL"));
            }
        }
        if (context.get("fileId") != null) {
            fileId = Integer.valueOf((String) context.get("fileId"));
        }
        if (context.get("jsessionid") != null) {
            sessionId = (String) context.get("jsessionid");
        }
        // add auth info only for URIs that are not on the same host.
        if (!uri.startsWith(requestURLHost)) {
            if (fileId != null && sessionId != null) {
                // check if user is logged in
                UserFile userFile = userFileService.getByIdAndUser(fileId, sessionId);
                if (userFile != null) {
                    basicAuth = userFile.getAuthorization();
                }
                // add auth info only if user is logged in
                if (StringUtils.isNotEmpty(basicAuth)) {

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
                                if (!uri.equals(httpRequestBase.getURI())) {
                                    try {
                                        httpRequestBase.setURI(new URI(uri));
                                    } catch (URISyntaxException e) {
                                        LOGGER.error("Unable to add known host ticket parameter for URI:" + uri);
                                        e.printStackTrace();
                                    }
                                }
                            } else if (knownHost.getAuthenticationMethod() == KnownHostAuthenticationMethod.BASIC) {
                                // Add basic authorisation if needed
                                httpRequestBase.addHeader(
                                        "Authorization",
                                        "Basic "
                                                +
                                                Base64.encodeBase64String((new String(knownHost.getKey() + ":"
                                                        + knownHost.getTicket())).getBytes()));
                                LOGGER.debug("Add basic auth from known hosts to URL: " + uri);
                            }
                        }
                    }
                }
            }
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
