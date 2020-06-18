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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.defaultString;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.web.client.RestOperations;

/**
 * Intercepts calls to pages for CDR integration.
 */
@Component
public class CdrAuthorizationInterceptor extends HandlerInterceptorAdapter {


    public static final String BYPASS_AUTH_HEADER="ByPassCDRInterceptorAuth";

    public static final String EIONET_DOMAIN="eionet.europa.eu";

    /**
     * Authorization failed attribute.
     */
    public static final String AUTHORIZATION_FAILED_ATTRIBUTE = CdrAuthorizationInterceptor.class.getName();
    /**
     * Forward parsed cookies to controller via request attribute.
     */
    public static final String PARSED_COOKIES_ATTRIBUTE = "request.parsed.cookies";
    /**
     * Session attribute name for counting unsuccessful authorization counts.
     */
    static final String AUTHORIZATION_TRY_COUNT = "authorization-try-count";
    /**
     * Session attribute name for counting unsuccessful authorization counts.
     */
    static final Integer ALLOWED_AUTHORIZATION_FAILURES_COUNT = 3;
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
     * Authorization header name.
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    /**
     * Convert cookie objects to string and vice versa.
     */
    @Autowired
    CookiesToStringBidirectionalConverter cookiesConverter;
    /**
     * Rest operations.
     */
    @Autowired
    private RestOperations restOperations;
    /**
     * CDR login method name.
     */
    @Value("${cdr.login}")
    private String cdrLoginMethod;
    /**
     * CDR envelope properties page name.
     */
    @Value("${cdr.envelope.properties}")
    private String cdrEnvelopePropertiesMethod;
    /**
     * Current session.
     */
    @Autowired
    private HttpSession session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
//        if (true) return PROCEED;
        if (StringUtils.isNotEmpty(authorization) || request.getParameter("auth") != null) {
            // if Basic auth is present in the request, then try to log in to CDR to test if it is valid token for given domain.
            // "auth" parameter is just meant for testing the CDR API in development environment - WebQ asks to authenticate.
            HttpHeaders headers = new HttpHeaders();
            headers.add(AUTHORIZATION_HEADER, authorization);
//            return PROCEED;
            try {
                ResponseEntity<String> loginResponse
                        = restOperations.postForEntity(extractCdrUrl(request) + "/" + cdrLoginMethod,
                        new HttpEntity<Object>(headers), String.class);
                LOGGER.info("Response code received from CDR basic authorization request " + loginResponse.getStatusCode());
                return PROCEED;
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() != HttpStatus.UNAUTHORIZED) {
                    LOGGER.warn("Authorization against CDR failed with unexpected HTTP status code", e);
                }
            }
        } else {
            // if Basic auth is not present, then test if user is already authorised in this domain
            // by using provided cookies to fetch CDR envelope properties page.
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                HttpHeaders headers = new HttpHeaders();
                 String cookieHeader ="";
                for (Cookie cookie : cookies) {

                    // put ZopeId parameter to request header. It works only when the value is surrounded with quotes.
                    cookieHeader = cookieHeader.concat(cookiesConverter.convertCookieToString(cookie)+" ");
                }
                headers.add("Cookie",cookieHeader);

                String instanceUrl = this.extractCdrEnvelopeUrl(request);
                if(!this.isInstanceURLWhiteListed(instanceUrl)){
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return STOP_REQUEST_PROPAGATION;
                }

                String urlToFetch = extractCdrEnvelopeUrl(request) + "/" + cdrEnvelopePropertiesMethod;
                    //ResponseEntity<String> loginResponse = restOperations.exchange(urlToFetch, HttpMethod.GET,
                    //        new HttpEntity<Object>(headers), String.class);

                 HttpResponse responseFromCdr = fetchUrlWithoutRedirection(urlToFetch, headers);
                try {
                    int statusCode = responseFromCdr.getStatusLine().getStatusCode();

                    LOGGER.info(
                            "Response code received from CDR envelope request using cookies " + statusCode);
                    if (statusCode == HttpStatus.OK.value()) {
                        request.setAttribute(PARSED_COOKIES_ATTRIBUTE, cookiesConverter.convertCookiesToString(cookies));
                        return PROCEED;
                    } else if ((statusCode == HttpStatus.MOVED_PERMANENTLY.value()
                            || statusCode == HttpStatus.MOVED_TEMPORARILY.value())
                            && responseFromCdr.getFirstHeader("Location") != null) {
                        // redirect to CDR login page
                        String redirectUrl = extractCdrUrl(request) + responseFromCdr.getFirstHeader("Location").getValue();
                        LOGGER.info("Redirect to " + redirectUrl);
                        response.sendRedirect(redirectUrl);
                    }
                } catch (HttpStatusCodeException e) {
                    if (e.getStatusCode() != HttpStatus.UNAUTHORIZED) {
                        LOGGER.warn("Fetching CDR envelope page failed with unexpected HTTP status code", e);
                    }
                }
            }
        }

        if (isFailureCountsEqualsToAllowedFailuresCount()) {
            request.setAttribute(AUTHORIZATION_FAILED_ATTRIBUTE, AUTHORIZATION_FAILED_ATTRIBUTE);
            session.removeAttribute(AUTHORIZATION_TRY_COUNT);
            return PROCEED;
        }

        increaseFailedAuthorizationsCount();
        response.addHeader("WWW-Authenticate", "Basic realm=\"Please login to use webforms.\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return STOP_REQUEST_PROPAGATION;
    }

    public void setCdrLoginMethod(String cdrLoginMethod) {
        this.cdrLoginMethod = cdrLoginMethod;
    }

    private boolean isFailureCountsEqualsToAllowedFailuresCount() {
        return ALLOWED_AUTHORIZATION_FAILURES_COUNT.equals(session.getAttribute(AUTHORIZATION_TRY_COUNT));
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
     * Extracts CDR envelope URL from request. If envelope parameter is not defined, then extract envelope from instance parameter.
     *
     * @param request http request
     * @return envelope URL
     */
    private String extractCdrEnvelopeUrl(HttpServletRequest request) {

        if (StringUtils.isNotEmpty(request.getParameter("envelope"))) {
            return request.getParameter("envelope");
        } else if (StringUtils.isNotEmpty(request.getParameter("instance"))) {
          return this.extractCdrInstanceUrlWithoutFileName(request);
        } else {
            return null;
        }
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

    /**
     * Increase failed authorizations count.
     */
    private void increaseFailedAuthorizationsCount() {
        Integer failedAttempts = (Integer) session.getAttribute(AUTHORIZATION_TRY_COUNT);
        session.setAttribute(AUTHORIZATION_TRY_COUNT, failedAttempts != null ? failedAttempts + 1 : 1);
    }

    /**
     * Calls a resource in CDR with redirect disabled. Then it is possible to catch if the user is redirected to login page.
     *
     * @param url CDR url to fetch.
     * @param headers HTTP headers to send.
     * @return HTTP response object
     * @throws IOException if network error occurs
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyManagementException
     */

    protected CloseableHttpResponse fetchUrlWithoutRedirection(String url, HttpHeaders headers) throws IOException, NoSuchAlgorithmException, KeyManagementException {
         HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
         httpClientBuilder.setSSLContext(SSLContexts.custom().useProtocol("TLSv1.2").build()).setRedirectStrategy(
                 new RedirectStrategy() {
             @Override
             public boolean isRedirected(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext)
                     throws ProtocolException {
                 return false;
             }

             @Override
             public HttpUriRequest getRedirect(HttpRequest httpRequest, HttpResponse httpResponse,
                     HttpContext httpContext) throws ProtocolException {
                 return null;
             }
         });
         HttpGet httpget = new HttpGet(url);

         for (Map.Entry<String, List<String>> header : headers.entrySet()) {
             for (String value : header.getValue()) {
                 httpget.addHeader(header.getKey(), value);
             }
         }
         CloseableHttpClient client = httpClientBuilder.build();
         CloseableHttpResponse httpResponse = client.execute(httpget);
         return httpResponse;
     }

     protected boolean isInstanceURLWhiteListed(String instanceUrl) throws URISyntaxException {
         URI uri = new URI(instanceUrl);
         String domain = uri.getHost();
         if(domain.endsWith(EIONET_DOMAIN)){
           return true;
       }else{
           return false;
       }
     }

     private String extractCdrInstanceUrlWithoutFileName(HttpServletRequest request){
         int fileNameSeparatorIndex = request.getParameter("instance").lastIndexOf("/");
         return request.getParameter("instance").substring(0, fileNameSeparatorIndex);
     }

}


