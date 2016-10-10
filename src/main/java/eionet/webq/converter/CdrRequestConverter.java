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
package eionet.webq.converter;

import eionet.webq.dto.CdrRequest;
import eionet.webq.web.interceptor.CdrAuthorizationInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Converts {@link javax.servlet.http.HttpServletRequest} to {@link eionet.webq.dto.CdrRequest}.
 *
 * @see Converter
 */
@Component
public class CdrRequestConverter implements Converter<HttpServletRequest, CdrRequest> {
    /**
     * This class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CdrRequestConverter.class);
    /**
     * Basic authorization prefix.
     */
    private static final String BASIC_AUTHORIZATION_PREFIX = "Basic ";

    /**
     * Convert cookie objects to string and vice versa.
     */
    @Autowired
    CookiesToStringBidirectionalConverter cookiesConverter;

    @Override
    public CdrRequest convert(HttpServletRequest httpRequest) {
        QueriedParametersTracker parametersTracker = new QueriedParametersTracker(httpRequest);

        CdrRequest parameters = new CdrRequest();
        parameters.setContextPath(httpRequest.getContextPath());
        parameters.setEnvelopeUrl(parametersTracker.getParameter("envelope"));
        parameters.setSchema(parametersTracker.getParameter("schema"));
        parameters.setNewFormCreationAllowed(Boolean.valueOf(parametersTracker.getParameter("add")));
        parameters.setNewFileName(parametersTracker.getParameter("file_id"));
        String instanceUrl = parametersTracker.getParameter("instance");
        parameters.setInstanceUrl(instanceUrl);
        if (isNotEmpty(instanceUrl)) {
            int fileNameSeparatorIndex = instanceUrl.lastIndexOf("/");
            parameters.setInstanceName(instanceUrl.substring(fileNameSeparatorIndex + 1));
            if (isEmpty(parameters.getEnvelopeUrl())) {
                parameters.setEnvelopeUrl(instanceUrl.substring(0, fileNameSeparatorIndex));
            }
        }
        parameters.setInstanceTitle(parametersTracker.getParameter("instance_title"));

        if (StringUtils.isEmpty(parameters.getEnvelopeUrl()) && StringUtils.isNotEmpty(parameters.getInstanceUrl())) {
            parameters.setEnvelopeUrl(StringUtils.substringBeforeLast(parameters.getInstanceUrl(), "/"));
        }
        
        parameters.setSessionId(parameters.getEnvelopeUrl());

        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (authorizationAgainstCdrSucceed(httpRequest) && hasBasicAuthorization(authorizationHeader)) {
            try {
                setAuthorizationDetails(parameters, authorizationHeader);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Unable to parse authorisation details" + e.toString());
            }
        }
        if (authorizationAgainstCdrSucceed(httpRequest) && !parameters.isAuthorizationSet()
                && httpRequest.getAttribute(CdrAuthorizationInterceptor.PARSED_COOKIES_ATTRIBUTE) != null) {
            parameters.setAuthorizationSet(true);
            parameters.setCookies((String) httpRequest.getAttribute(CdrAuthorizationInterceptor.PARSED_COOKIES_ATTRIBUTE));
        }
        parameters.setAdditionalParametersAsQueryString(createQueryStringFromParametersNotRead(parametersTracker));
        return parameters;
    }

    /**
     * Set authorization details.
     *
     * @param parameters          parameters.
     * @param authorizationHeader authorization header.
     * @throws UnsupportedEncodingException Cannot convert the auth info to UTF-8
     */
    private void setAuthorizationDetails(CdrRequest parameters, String authorizationHeader) throws UnsupportedEncodingException {
        String[] credentials = extractCredentialsFromBasicAuthorization(authorizationHeader);
        if (credentials.length != 2) {
            return;
        }
        parameters.setAuthorizationSet(true);
        parameters.setBasicAuthorization(authorizationHeader);
        parameters.setUserName(credentials[0]);
        parameters.setPassword(credentials[1]);
    }

    /**
     * Decodes and parses basic authentication header to username and password.
     *
     * @param authHeader authentication header
     * @return username and password or empty array in case of error.
     */
    private String[] extractCredentialsFromBasicAuthorization(String authHeader) throws UnsupportedEncodingException {
        String encodedCredentials = authHeader.substring(BASIC_AUTHORIZATION_PREFIX.length());
        String credentials = new String(Base64.decodeBase64(encodedCredentials), "UTF-8");
        return credentials.split(":");
    }

    /**
     * Check whether authorization header contains basic authorization data.
     *
     * @param authorizationHeader authorization header value
     * @return is basic authorization is present
     */
    private boolean hasBasicAuthorization(String authorizationHeader) {
        return isNotEmpty(authorizationHeader) && authorizationHeader.startsWith(BASIC_AUTHORIZATION_PREFIX);
    }

    /**
     * Produces http request compatible query string part.
     * E.g. &foo=bar&number=42
     *
     * @param parameters holder of all parameters with queried parameters tracking
     * @return query string part
     */
    private String createQueryStringFromParametersNotRead(QueriedParametersTracker parameters) {
        StringBuilder builder = new StringBuilder();
        for (String entry : parameters.getAllParameterNames()) {
            if (parameters.isNotReadParameter(entry)) {
                builder.append('&').append(entry).append('=').append(parameters.getParameter(entry));
            }
        }
        return builder.toString();
    }

    /**
     * Check whether authorization against CDR succeed.
     *
     * @param request http request
     * @return is authorization succeed
     */
    private boolean authorizationAgainstCdrSucceed(HttpServletRequest request) {
        Object attribute = request.getAttribute(CdrAuthorizationInterceptor.AUTHORIZATION_FAILED_ATTRIBUTE);
        return attribute == null;
    }

    /**
     * Class wraps {@link javax.servlet.http.HttpServletRequest} to keep track of parameters queried.
     */
    private final class QueriedParametersTracker {
        /**
         * Http request.
         */
        private HttpServletRequest request;
        /**
         * Parameters that were queried.
         */
        private Collection<String> parametersRead = new ArrayList<String>();

        /**
         * Constructs new instance.
         *
         * @param request http request to be queried for parameters.
         */
        private QueriedParametersTracker(HttpServletRequest request) {
            this.request = request;
        }

        /**
         * Returns parameter value from wrapped {@link javax.servlet.http.HttpServletRequest}.
         * Remembers parameter name that was queried.
         *
         * @param parameterName parameter name
         * @return parameter value
         */
        public String getParameter(String parameterName) {
            parametersRead.add(parameterName);
            return request.getParameter(parameterName);
        }

        /**
         * Check whether parameter with such name were read.
         *
         * @param parameterName parameter name
         * @return is not parameter read
         */
        public boolean isNotReadParameter(String parameterName) {
            return !parametersRead.contains(parameterName);
        }

        public Collection<String> getAllParameterNames() {
            return request.getParameterMap().keySet();
        }
    }
}
