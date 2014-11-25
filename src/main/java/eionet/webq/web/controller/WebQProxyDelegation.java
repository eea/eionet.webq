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

import eionet.webq.dao.orm.KnownHost;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.KnownHostAuthenticationMethod;
import eionet.webq.service.CDREnvelopeService;
import eionet.webq.service.FileNotAvailableException;
import eionet.webq.service.KnownHostsService;
import eionet.webq.web.controller.util.UserFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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
     * Helper web layer service to match request parameters and UserFle object in database.
     */
    @Autowired
    UserFileHelper userFileHelper;
    /**
     * Known host service.
     */
    @Autowired
    KnownHostsService knownHostsService;
    /**
     * Cdr envelope service.
     */
    @Autowired
    private CDREnvelopeService envelopeService;
    /**
     * Rest client.
     */
    @Autowired
    private RestOperations restOperations;

    /**
     * This method delegates GET request to remote host. See:
     * http://stackoverflow.com/questions/14595245/rest-service-pass-through-via-spring This method also works when a method is not
     * defined.
     *
     * @param uri the actual uri to make the request
     * @return result request results received from uri
     */
    @RequestMapping(value = "/restProxy", method = RequestMethod.GET)
    public @ResponseBody String restProxyGet(@RequestParam("uri") String uri, @RequestParam(required = false) Integer fileId,
            HttpServletRequest request)
            throws UnsupportedEncodingException, URISyntaxException, FileNotAvailableException {

        if (fileId != null && fileId > 0) {
            return restProxyGetWithAuth(uri, fileId, request);
        }
        LOGGER.info("/restProxy [GET] uri=" + uri);
        return new RestTemplate().getForObject(new URI(uri), String.class);
    } // end of method restProxyGet

    /**
     * This method delegates POST request to remote host.
     *
     * @param uri  the actual uri to make the request
     * @param body body request body to forward to remote host
     * @return result request results received from uri
     */
    @RequestMapping(value = "/restProxy", method = RequestMethod.POST)
    public @ResponseBody String restProxyPost(@RequestParam("uri") String uri, @RequestBody String body,
            @RequestParam(required = false) Integer fileId, HttpServletRequest request)
            throws URISyntaxException, FileNotAvailableException {
        if (fileId != null && fileId > 0) {
            return restProxyPostWithAuth(uri, body, fileId, request);
        }
        LOGGER.info("/restProxy [POST] uri=" + uri);
        return new RestTemplate().postForObject(new URI(uri), body, String.class);
    } // end of method restProxyPost

    /**
     * This method delegates GET request to remote host using authorisation stored in UserFile.
     *
     * @param uri the actual uri to make the request
     * @return result request results received from uri
     */
    @RequestMapping(value = "/restProxyWithAuth", method = RequestMethod.GET)
    public @ResponseBody String restProxyGetWithAuth(@RequestParam("uri") String uri, @RequestParam int fileId,
            HttpServletRequest request) throws URISyntaxException, FileNotAvailableException {

        UserFile file = userFileHelper.getUserFile(fileId, request);

        if (file != null) {
            if (uri.startsWith(file.getEnvelope())) {
                return new String(envelopeService.fetchFileFromCdr(file, uri).getBody());
            } else if (file.isAuthorized()) {
                // check if we have known host in db
                KnownHost knownHost = knownHostsService.getKnownHost(uri);
                if (knownHost != null) {
                    if (knownHost.getAuthenticationMethod() == KnownHostAuthenticationMethod.REQUEST_PARAMETER) {
                        // add ticket parameter to request URI if needed
                        LOGGER.info("Add ticket parameter from known hosts to URL: " + uri);
                        uri += (uri.contains("?")) ? "&" : "?";
                        uri += knownHost.getKey() + "=" + knownHost.getTicket();
                    } else if (knownHost.getAuthenticationMethod() == KnownHostAuthenticationMethod.BASIC) {
                        // Add basic authorisation if needed
                        HttpHeaders authorization = getHttpHeaderWithBasicAuthentication(knownHost);
                        LOGGER.info("Add basic auth from known hosts to URL: " + uri);
                        String response = (new RestTemplate()
                                .exchange(new URI(uri), HttpMethod.GET, new HttpEntity<Object>(authorization), String.class))
                                .getBody();
                        return response;
                    }
                }
            }
        }

        LOGGER.info("/restProxy [GET] uri=" + uri);
        return new RestTemplate().getForObject(new URI(uri), String.class);
    }

    /**
     * This method delegates multipart POST request to remote host using authorisation stored in UserFile.
     *
     * @param uri  the actual uri to make the request
     * @param body body request body to forward to remote host
     * @return result request results received from uri
     */
    @RequestMapping(value = "/restProxyWithAuth", method = RequestMethod.POST)
    public @ResponseBody String restProxyPostWithAuth(@RequestParam("uri") String uri, @RequestBody String body,
            @RequestParam int fileId, HttpServletRequest request) throws URISyntaxException, FileNotAvailableException {

        UserFile file = userFileHelper.getUserFile(fileId, request);

        if (file != null) {
            if (uri.startsWith(file.getEnvelope())) {
                return envelopeService.submitRequest(file, uri, body);
            } else if (file.isAuthorized()) {
                // check if we have known host in db
                KnownHost knownHost = knownHostsService.getKnownHost(uri);
                if (knownHost != null) {
                    if (knownHost.getAuthenticationMethod() == KnownHostAuthenticationMethod.REQUEST_PARAMETER) {
                        // add ticket parameter to request URI if needed
                        LOGGER.info("Add ticket parameter from known hosts to URL: " + uri);
                        uri += (uri.contains("?")) ? "&" : "?";
                        uri += knownHost.getKey() + "=" + knownHost.getTicket();
                    } else if (knownHost.getAuthenticationMethod() == KnownHostAuthenticationMethod.BASIC) {
                        // Add basic authorisation if needed
                        HttpHeaders authorization = getHttpHeaderWithBasicAuthentication(knownHost);
                        LOGGER.info("Add basic auth from known hosts to URL: " + uri);
                        HttpEntity<String> httpEntity = new HttpEntity<String>(body, authorization);
                        return new RestTemplate().postForObject(new URI(uri), httpEntity, String.class);
                    }
                }
            }
        }

        LOGGER.info("/restProxy [POST] uri=" + uri);
        return new RestTemplate().postForObject(new URI(uri), body, String.class);
    }

    /**
     * The method proxies multipart POST requests. If the request target is CDR envelope, then USerFile authorization info is used.
     *
     * @param uri              the address to forward the request
     * @param fileId           UserFile id stored in session
     * @param multipartRequest file part in multipart request
     * @return response from remote host
     * @throws URISyntaxException provide URI is incorrect
     * @throws IOException        could not read file from request
     */
    @RequestMapping(value = "/restProxyFileUpload", method = RequestMethod.POST)
    public @ResponseBody String restProxyFileUpload(@RequestParam("uri") String uri,
            @RequestParam int fileId, MultipartHttpServletRequest multipartRequest)
            throws URISyntaxException, IOException {

        UserFile file = userFileHelper.getUserFile(fileId, multipartRequest);

        if (!multipartRequest.getFileNames().hasNext()) {
            throw new IllegalArgumentException("File not found in multipart request.");
        }
        Map<String, String[]> parameters = multipartRequest.getParameterMap();
        // limit request to one file
        String fileName = multipartRequest.getFileNames().next();
        MultipartFile multipartFile = multipartRequest.getFile(fileName);

        HttpHeaders authorization = new HttpHeaders();
        if (file != null) {
            if (uri.startsWith(file.getEnvelope())) {
                authorization = envelopeService.getAuthorizationHeader(file);
            }
        }

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentDispositionFormData("file", multipartFile.getOriginalFilename());
        fileHeaders.setContentType(MediaType.valueOf(multipartFile.getContentType()));

        MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
        byte[] content = multipartFile.getBytes();
        request.add("file", new HttpEntity<byte[]>(content, fileHeaders));
        for (Map.Entry<String, String[]> parameter : parameters.entrySet()) {
            if (!parameter.getKey().equals("uri") && !parameter.getKey().equals("fileId") && !parameter.getKey()
                    .equals("sessionid") && !parameter.getKey()
                    .equals("restricted")) {
                request.add(parameter.getKey(), new HttpEntity<String>(StringUtils.defaultString(parameter.getValue()[0])));
            }
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>
                (request, authorization);

        LOGGER.info("/restProxy [POST] uri=" + uri);
        return new RestTemplate().postForObject(uri, requestEntity, String.class);
    }

    /**
     * Create HttpHeader with basic authentication info.
     *
     * @param knownHost KnownHost object
     * @return HttpHeader with authorization attribute
     */
    private HttpHeaders getHttpHeaderWithBasicAuthentication(KnownHost knownHost) {
        HttpHeaders authorization = new HttpHeaders();
        try {
            authorization.add("Authorization", "Basic " + Base64.encodeBase64String((knownHost.getKey() + ":"
                    + knownHost.getTicket()).getBytes("utf-8")).replaceAll("\n", ""));
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("UnsupportedEncodingException: utf-8");
        }
        return authorization;
    }
} // end of class WebQProxyDelegation
