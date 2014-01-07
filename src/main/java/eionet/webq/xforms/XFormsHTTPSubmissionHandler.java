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

import de.betterform.connector.SubmissionHandler;
import de.betterform.connector.http.HTTPSubmissionHandler;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.w3c.dom.Node;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Submission handler to proxy http GET, POST, DELETE and PUT request executed through XForm submission element.
 *
 * @author Enriko Käsper
 */
@Configurable
public class XFormsHTTPSubmissionHandler extends HTTPSubmissionHandler implements SubmissionHandler {
    /**
     * Http request auth handler.
     */
    @Autowired
    HTTPRequestAuthHandler httpRequestAuthHandler;

    @SuppressWarnings("rawtypes")
    @Override
    public Map submit(Submission submission, Node instance) throws XFormsException {
        return super.submit(submission, instance);
    }

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

    @Override
    protected void post(String uri, String body, String type, String encoding) throws XFormsException {
        HttpEntityEnclosingRequestBase httpMethod = new HttpPost(uri);
        httpRequestAuthHandler.addAuthToHttpRequest(httpMethod, getContext());

        try {
            configureRequest(httpMethod, body, type, encoding);

            execute(httpMethod);
        } catch (XFormsException e) {
            throw e;
        } catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    @Override
    protected void put(String uri, String body, String type, String encoding) throws XFormsException {
        HttpEntityEnclosingRequestBase httpMethod = new HttpPut(uri);
        httpRequestAuthHandler.addAuthToHttpRequest(httpMethod, getContext());

        try {
            configureRequest(httpMethod, body, type, encoding);

            execute(httpMethod);
        } catch (XFormsException e) {
            throw e;
        } catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    @Override
    protected void delete(String uri) throws XFormsException {
        HttpRequestBase httpMethod = new HttpDelete(uri);
        httpRequestAuthHandler.addAuthToHttpRequest(httpMethod, getContext());

        try {
            execute(httpMethod);
        } catch (XFormsException e) {
            throw e;
        } catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    /**
     * Create encoded HttpEntity with request body.
     *
     * @param httpMethod Http request
     * @param body String to be written into request body
     * @param type request body type
     * @param encoding Request encoding
     * @throws UnsupportedEncodingException when encoding is not supported
     */
    private void configureRequest(HttpEntityEnclosingRequestBase httpMethod, String body, String type, String encoding)
            throws UnsupportedEncodingException {
        HttpEntity entity = new StringEntity(body, type, encoding);
        httpMethod.setEntity(entity);
    }
}
