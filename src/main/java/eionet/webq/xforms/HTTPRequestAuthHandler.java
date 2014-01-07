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

import org.apache.http.client.methods.HttpRequestBase;

import java.util.Map;

/**
 * Service handles HTTP requests done through XForms engine and adds authorisation info to request header if needed.
 *
 * @author Enriko Käsper
 */
public interface HTTPRequestAuthHandler {

    /**
     * The method compares the request URI and adds authorisation info to request
     * header if it is instance URL or URL from known hosts table.
     *
     * @param httpRequestBase Get, post, delete or put HTTP request method.
     * @param context Map of context parameters.
     */
    public void addAuthToHttpRequest(HttpRequestBase httpRequestBase, Map<?, ?> context);

}
