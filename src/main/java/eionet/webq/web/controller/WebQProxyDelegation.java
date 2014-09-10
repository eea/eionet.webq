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
 *        TripleDev
 */
package eionet.webq.web.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * Base controller for WebQ proxy delegations.
 *
 * @author enver
 */
@Controller
@RequestMapping
public class WebQProxyDelegation {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(WebQProxyDelegation.class);

    /**
     * This method delegates given uri to another instance.
     *
     * @param body
     *            body
     * @param method
     *            method
     * @param request
     *            request
     * @param response
     *            response
     * @return delegated
     * @throws URISyntaxException
     */
    @RequestMapping("/delegate")
    @ResponseBody
    public ResponseEntity<String> delegate(@RequestBody String body, HttpMethod method, HttpServletRequest request,
            HttpServletResponse response) throws URISyntaxException {
        // get uri parameter, it should have been escaped
        String uriStr = request.getParameter("uri");
        LOGGER.info("/delegate uri=" + uriStr + ". From: " + request.getRequestURI() + ", with Query: " + request.getQueryString());
        // create uri
        URI uri = new URI(uriStr);
        // call and return
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(uri, method == null ? HttpMethod.GET : method, new HttpEntity<String>(body), String.class);
        return responseEntity;
    } // end of method delegate

} // end of class WebQProxyDelegation
