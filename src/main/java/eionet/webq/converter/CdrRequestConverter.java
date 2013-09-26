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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Converts {@link javax.servlet.http.HttpServletRequest} to {@link eionet.webq.dto.CdrRequest}.
 *
 * @see Converter
 */
@Component
public class CdrRequestConverter implements Converter<HttpServletRequest, CdrRequest> {
    /**
     * Basic authorization prefix.
     */
    private static final String BASIC_AUTHORIZATION_PREFIX = "Basic ";

    @Override
    public CdrRequest convert(HttpServletRequest httpRequest) {
        QueriedParametersTracker request = new QueriedParametersTracker(httpRequest);
        CdrRequest parameters = new CdrRequest();
        parameters.setEnvelopeUrl(request.getParameter("envelope"));
        parameters.setSchema(request.getParameter("schema"));
        parameters.setLanguage(request.getParameter("language"));
        parameters.setJavascriptEnabled(Boolean.valueOf(request.getParameter("JavaScript")));
        parameters.setNewFormCreationAllowed(Boolean.valueOf(request.getParameter("add")));
        parameters.setNewFileName(request.getParameter("file_id"));
        parameters.setInstanceUrl(request.getParameter("instance"));
        parameters.setInstanceTitle(request.getParameter("instance_title"));

        String authorizationHeader = httpRequest.getHeader("Authorization");
        if (hasBasicAuthorization(authorizationHeader)) {
            setAuthorizationDetails(parameters, extractCredentialsFromBasicAuthorization(authorizationHeader));
        }
        parameters.setAdditionalParametersAsQueryString(createQueryString(request.getNotReadParametersWithValues()));
        return parameters;
    }

    /**
     * Set authorization details.
     *
     * @param parameters parameters.
     * @param credentials credentials.
     */
    private void setAuthorizationDetails(CdrRequest parameters, String[] credentials) {
        if (credentials.length != 2) {
            return;
        }
        parameters.setAuthorizationSet(true);
        parameters.setUserName(credentials[0]);
        parameters.setPassword(credentials[1]);
    }

    /**
     * Decodes and parses basic authentication header to username and password.
     *
     * @param authHeader authentication header
     * @return username and password or empty array in case of error.
     */
    private String[] extractCredentialsFromBasicAuthorization(String authHeader) {
        String encodedCredentials = authHeader.substring(BASIC_AUTHORIZATION_PREFIX.length());
        String credentials = new String(Base64.decodeBase64(encodedCredentials));
        return credentials.split(":");
    }

    /**
     * Check whether authorization header contains basic authorization data.
     *
     * @param authorizationHeader authorization header value
     * @return is basic authorization is present
     */
    private boolean hasBasicAuthorization(String authorizationHeader) {
        return StringUtils.isNotEmpty(authorizationHeader) && authorizationHeader.startsWith(BASIC_AUTHORIZATION_PREFIX);
    }

    /**
     * Produces http request compatible query string part.
     * E.g. &foo=bar&number=42
     *
     * @param notReadParametersWithValues parameters with values to be transformed to such string
     * @return query string part
     */
    private String createQueryString(Map<String, String> notReadParametersWithValues) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : notReadParametersWithValues.entrySet()) {
            builder.append('&').append(entry.getKey()).append('=').append(entry.getValue());
        }
        return builder.toString();
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
         * Returns map of parameters and values that was not queried before.
         *
         * @return map of parameters with values, not queried previously.
         */
        public Map<String, String> getNotReadParametersWithValues() {
            Map<String, String> notReadParametersWithValues = new TreeMap<String, String>();
            for (String parameterName : request.getParameterMap().keySet()) {
                if (!parametersRead.contains(parameterName)) {
                    notReadParametersWithValues.put(parameterName, request.getParameter(parameterName));
                }
            }
            return notReadParametersWithValues;
        }
    }
}
