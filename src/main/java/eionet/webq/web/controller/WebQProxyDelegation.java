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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
     * This method also delegates but with a different approach. See:
     * http://stackoverflow.com/questions/14595245/rest-service-pass-through-via-spring This method also works when a method is not
     * defined.
     *
     * @param uri
     *            uri
     * @return result
     */
    @RequestMapping(value = "/restProxy", method = RequestMethod.GET)
    public @ResponseBody String restProxyGet(@RequestParam("uri") String uri) {
        LOGGER.info("/restProxy [GET] uri=" + uri);
        return new RestTemplate().getForObject(uri, String.class);
    } // end of method restProxyGet

    /**
     * This method also delegates but with a different approach.
     *
     * @param uri
     *            uri
     * @param body
     *            body
     * @return result
     */
    @RequestMapping(value = "/restProxy", method = RequestMethod.POST)
    public @ResponseBody String restProxyPost(@RequestParam("uri") String uri, @RequestBody String body) {
        LOGGER.info("/restProxy [POST] uri=" + uri);
        return new RestTemplate().postForObject(uri, body, String.class);
    } // end of method restProxyPost

} // end of class WebQProxyDelegation
