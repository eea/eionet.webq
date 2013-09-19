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
import org.apache.commons.net.util.Base64;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 */
public class RequestToWebQMenuParametersTest {
    private final RequestToWebQMenuParameters requestToWebQMenuParameters = new RequestToWebQMenuParameters();
    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    public void buildsWebQRequestParameters() throws Exception {
        request.addParameter("envelope", "cdr-envelope.url");
        request.addParameter("schema", "requested-schema");
        request.addParameter("language", "EN");
        request.addParameter("JavaScript", "true");
        request.addParameter("add", "true");
        request.addParameter("file_id", "new-file-name");

        WebQMenuParameters convert = requestToWebQMenuParameters.convert(request);

        assertThat(convert.getEnvelopeUrl(), equalTo(request.getParameter("envelope")));
        assertThat(convert.getSchema(), equalTo(request.getParameter("schema")));
        assertThat(convert.getLanguage(), equalTo(request.getParameter("language")));
        assertThat(convert.isJavascriptEnabled(), equalTo(true));
        assertThat(convert.isNewFormCreationAllowed(), equalTo(true));
        assertThat(convert.getNewFileName(), equalTo(request.getParameter("file_id")));
    }

    @Test
    public void doesNotSetAuthorizationInfoIfNoAuthenticationHeaderInRequest() throws Exception {
        WebQMenuParameters parameters = requestToWebQMenuParameters.convert(request);

        assertFalse(parameters.isAuthorizationSet());
        assertNull(parameters.getUserName());
        assertNull(parameters.getPassword());
    }

    @Test
    public void doesNotSetAuthorizationInfoIfAuthorizationHeaderValueIsNotBASIC() throws Exception {
        request.setParameter("Authorization", "FORM 123");

        WebQMenuParameters parameters = requestToWebQMenuParameters.convert(request);

        assertFalse(parameters.isAuthorizationSet());
        assertNull(parameters.getUserName());
        assertNull(parameters.getPassword());
    }

    @Test
    public void setUserNameAndPasswordIfAuthorizationHeaderIsBASIC() throws Exception {
        request.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64("username:password".getBytes())));

        WebQMenuParameters parameters = requestToWebQMenuParameters.convert(request);

        assertTrue(parameters.isAuthorizationSet());
        assertThat(parameters.getUserName(), equalTo("username"));
        assertThat(parameters.getPassword(), equalTo("password"));
    }
}

