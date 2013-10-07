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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 */
@Component
public class CdrAuthorizationInterceptor extends HandlerInterceptorAdapter {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(CdrAuthorizationInterceptor.class);
    /**
     * Flag signals that request handling could proceed.
     */
    private static final boolean PROCEED = true;
    /**
     * Flag signals that response already written and no more handling required.
     */
    private static final boolean STOP_REQUEST_PROPAGATION = false;
    /**
     * Rest operations.
     */
    @Autowired
    private RestOperations restOperations;
    /**
     *  Save xml files to cdr method name.
     */
    @Value("#{ws['cdr.login']}")
    private String cdrLoginMethod;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(authorization)) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", authorization);
            try {
                ResponseEntity<String> loginResponse
                        = restOperations.postForEntity(extractCdrUrl(request) + "/" + cdrLoginMethod,
                        new HttpEntity<Object>(headers), String.class);
                if (loginResponse.getStatusCode() == HttpStatus.FOUND) {
                    return PROCEED;
                }
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() != HttpStatus.UNAUTHORIZED) {
                    LOGGER.warn("Authorization against CDR failed with unexpected HTTP status code", e);
                }
            }
        }

        new BasicAuthenticationEntryPoint().commence(request, response,
                new BadCredentialsException("credentials are empty or wrong!"));
        return STOP_REQUEST_PROPAGATION;
    }

    /**
     * Extracts CDR host URL from request.
     *
     * @param request http request
     * @return host URL
     */
    private String extractCdrUrl(HttpServletRequest request) {
        return defaultString(getUrlHost(request.getParameter("base_uri")),
                defaultString(getUrlHost(request.getParameter("envelope")),
                        getUrlHost(request.getParameter("instance"))));
    }

    /**
     * Gets host from url string.
     * Result will be in format protocol://host:port.
     *
     * @param url url to be checked
     * @return host or null if provided string is malformed URL
     */
    private String getUrlHost(String url) {
        try {
            URL parsedUrl = new URL(url);
            return parsedUrl.getProtocol() + "://" + parsedUrl.getAuthority();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public void setCdrLoginMethod(String cdrLoginMethod) {
        this.cdrLoginMethod = cdrLoginMethod;
    }
}
