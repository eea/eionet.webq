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

import de.betterform.connector.URIResolver;
import de.betterform.connector.http.HTTPURIResolver;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Resolve URIs called from XForms. Add authentication info if available in context or in known hosts table.
 *
 * @author Enriko Käsper
 */
@Configurable
public class XFormsHTTPURIResolver extends HTTPURIResolver implements URIResolver {
    /**
     * Http request auth handler.
     */
    @Autowired
    HTTPRequestAuthHandler httpRequestAuthHandler;

    @Override
    protected void get(String uri) throws XFormsException {
        HttpRequestBase httpRequestBase = new HttpGet(uri);
        httpRequestAuthHandler.addAuthToHttpRequest(httpRequestBase, getContext());
        try {
            execute(httpRequestBase);
        } catch (XFormsException e) {
            throw e;
        } catch (Exception e) {
            throw new XFormsException(e);
        }
    }

}
