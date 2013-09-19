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

import eionet.webq.dto.WebQMenuParameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Converts {@link javax.servlet.http.HttpServletRequest} to {@link eionet.webq.dto.WebQMenuParameters}.
 *
 * @see Converter
 */
@Component
public class RequestToWebQMenuParameters implements Converter<HttpServletRequest, WebQMenuParameters> {
    /**
     * Basic authorization prefix.
     */
    private static final String BASIC_AUTHORIZATION_PREFIX = "Basic ";

    @Override
    public WebQMenuParameters convert(HttpServletRequest request) {
        WebQMenuParameters parameters = new WebQMenuParameters();
        parameters.setEnvelopeUrl(request.getParameter("envelope"));
        parameters.setSchema(request.getParameter("schema"));
        parameters.setLanguage(request.getParameter("language"));
        parameters.setJavascriptEnabled(Boolean.valueOf(request.getParameter("JavaScript")));
        parameters.setNewFormCreationAllowed(Boolean.valueOf(request.getParameter("add")));
        parameters.setNewFileName(request.getParameter("file_id"));

        String authorizationHeader = request.getHeader("Authorization");
        if (hasBasicAuthorization(authorizationHeader)) {
            setAuthorizationDetails(parameters, extractCredentialsFromBasicAuthorization(authorizationHeader));
        }
        return parameters;
    }

    /**
     * Set authorization details.
     *
     * @param parameters parameters.
     * @param credentials credentials.
     */
    private void setAuthorizationDetails(WebQMenuParameters parameters, String[] credentials) {
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
}
