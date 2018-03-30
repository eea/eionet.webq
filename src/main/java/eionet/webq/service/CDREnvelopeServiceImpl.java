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
package eionet.webq.service;

import eionet.webq.converter.CookiesToStringBidirectionalConverter;
import eionet.webq.dao.orm.KnownHost;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.CdrRequest;
import eionet.webq.dto.XmlSaveResult;
import eionet.webq.web.CustomURI;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfig;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import static java.util.Collections.emptyList;
import org.springframework.web.client.RestOperations;

/**
 */
@Service
public class CDREnvelopeServiceImpl implements CDREnvelopeService {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(CDREnvelopeServiceImpl.class);
    /**
     * Save xml files to cdr method name.
     */
    @Value("${cdr.save.xml}")
    String saveXmlFilesMethod;
    /**
     * Convert cookie objects to string and vice versa.
     */
    @Autowired
    CookiesToStringBidirectionalConverter cookiesConverter;
    /**
     * XML-RPC client.
     */
    @Autowired
    private XmlRpcClient xmlRpcClient;
    /**
     * Rest client.
     */
    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestOperations restOperations;
    /**
     * Get envelope xml files remote method name.
     */
    @Value("${cdr.envelope.get.xml.files}")
    private String getEnvelopeXmlFilesMethod;
    /**
     * Conversion service.
     */
    @Autowired
    private ConversionService conversionService;
    /**
     * Known hosts service.
     */
    @Autowired
    private KnownHostsService knownHostsService;

    @Override
    public MultiValueMap<String, XmlFile> getXmlFiles(CdrRequest parameters) {
        try {
            Object xmlFilesMappedBySchema = xmlRpcClient.execute(buildConfig(parameters), getEnvelopeXmlFilesMethod, emptyList());
            return transformGetXmlFilesResponse(xmlFilesMappedBySchema);
        } catch (XmlRpcException e) {
            throw new CDREnvelopeException("Unable to call envelope XML-RPC service", e);
        }
    }

    @Override
    @Transactional
    public XmlSaveResult pushXmlFile(UserFile file) {
        if (!file.isFromCdr()) {
            LOGGER.error("File is not belong to cdr. " + file);
            throw new IllegalArgumentException("Provided file is not belong to CDR!");
        }
        String saveXmlUrl = file.getEnvelope() + '/' + saveXmlFilesMethod;
        HttpEntity<MultiValueMap<String, Object>> requestParameters = prepareXmlSaveRequestParameters(file);

        ResponseEntity<String> entity = restOperations.postForEntity(saveXmlUrl, requestParameters, String.class);

        String responseBody = entity.getBody();
        if (entity.getStatusCode() != HttpStatus.OK) {
            LOGGER.info("Response headers from saveXml=" + entity.getHeaders());
            return XmlSaveResult.valueOfError("Service unavailable.");
        }
        LOGGER.info("Response headers from saveXml=" + entity.getHeaders());
        LOGGER.info("Response from saveXml=" + responseBody);
        return XmlSaveResult.valueOf(responseBody);
    }

    @Override
    public ResponseEntity<byte[]> fetchFileFromCdr(UserFile file, String remoteFileUrl)
            throws FileNotAvailableException, URISyntaxException {

        HttpHeaders authorization = new HttpHeaders();
        // use user provided auth info only when the remote file is in the same host as UserFile.
        if (URIUtils.extractHost(new URI(remoteFileUrl)).equals(URIUtils.extractHost(new URI(file.getEnvelope())))) {
            authorization = getAuthorizationHeader(file);
        }
        ResponseEntity<byte[]> download = null;
        try {
            download = restOperations
                    .exchange(new URI(remoteFileUrl), HttpMethod.GET, new HttpEntity<Object>(authorization), byte[].class);
        } catch (RestClientException e) {
            LOGGER.error("Unable to download remote file.", e);
        }
        if (download == null || !download.hasBody()) {
            throw new FileNotAvailableException("Response is not OK or body not attached for " + remoteFileUrl);
        }
        return download;
    }

    @Override
    public String submitRequest(UserFile file, String uri, String body) throws URISyntaxException {
        HttpHeaders authorization = new HttpHeaders();
        // use user provided auth info only when the remote file is in the same host as UserFile.
        LOGGER.info(URIUtils.extractHost(new URI(uri)));
        if (URIUtils.extractHost(new URI(uri)).equals(URIUtils.extractHost(new URI(file.getEnvelope())))) {
            authorization = getAuthorizationHeader(file);
        }
        HttpEntity<String> httpEntity = new HttpEntity<String>(body, authorization);
        return restOperations.postForObject(new URI(uri), httpEntity, String.class);
    }

    /**
     * Prepares parameters for saveXml remote method.
     *
     * @param file file to be saved.
     * @return http entity representing request
     */
    HttpEntity<MultiValueMap<String, Object>> prepareXmlSaveRequestParameters(UserFile file) {

        HttpHeaders authorization = getAuthorizationHeader(file);

        HttpHeaders fileHeaders = new HttpHeaders();
        LOGGER.info("PARAMETERS: " + file.toString());
        fileHeaders.setContentDispositionFormData("file", file.getName());
        fileHeaders.setContentType(MediaType.TEXT_XML);

        MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
        byte[] content = file.getContent();
        if (StringUtils.isNotEmpty(file.getConversionId())) {
            content = conversionService.convert(file, file.getConversionId()).getBody();
        }
        request.add("file", new HttpEntity<byte[]>(content, fileHeaders));
        request.add("file_id", new HttpEntity<String>(file.getName()));
        request.add("title", new HttpEntity<String>(StringUtils.defaultString(file.getTitle())));
        if (file.isApplyRestriction()) {
            request.add("applyRestriction", new HttpEntity<String>("1"));
            request.add("restricted", new HttpEntity<String>(file.isRestricted() ? "1" : "0"));
        }

        return new HttpEntity<MultiValueMap<String, Object>>(request, authorization);
    }

    @Override
    public HttpHeaders getAuthorizationHeader(UserFile file) {
        HttpHeaders authorization = new HttpHeaders();
        if (file.isAuthorized()) {
            String authorizationInfo = file.getAuthorization();
            if (StringUtils.isNotEmpty(authorizationInfo)) {
                authorization.add("Authorization", authorizationInfo);
                LOGGER.info("Use basic auth for file: " + file.getId());
            }
            String cookiesInfo = file.getCookies();
            if (StringUtils.isNotEmpty(cookiesInfo)) {
                Cookie[] cookies = cookiesConverter.convertStringToCookies(cookiesInfo);
                for (Cookie cookie : cookies) {
                    authorization.add("Cookie", cookie.getName() + "=" + cookie.getValue());
                    LOGGER.info("User cookie auth for file: " + file.getId());
                }
            }
        }
        return authorization;
    }

    /**
     * Transform raw envelope service response to usable form.
     *
     * @param response service response
     * @return {@link XmlFile} grouped by xml schema.
     */
    @SuppressWarnings("unchecked")
    private MultiValueMap<String, XmlFile> transformGetXmlFilesResponse(Object response) {
        LinkedMultiValueMap<String, XmlFile> result = new LinkedMultiValueMap<String, XmlFile>();
        if (response != null) {
            try {
                for (Map.Entry<String, Object[]> entry : ((Map<String, Object[]>) response).entrySet()) {
                    String xmlSchema = entry.getKey();
                    for (Object values : entry.getValue()) {
                        Object[] xmlFileData = (Object[]) values;
                        result.add(xmlSchema, new XmlFile(xmlFileData[0].toString(), xmlFileData[1].toString()));
                    }
                }
            } catch (ClassCastException e) {
                LOGGER.error("received response=" + response);
                throw new CDREnvelopeException("unexpected response format from CDR envelope service.", e);
            }
        } else {
            LOGGER.warn("expected not null response from envelope service");
        }
        LOGGER.info("Xml files received=" + result);
        return result;
    }

    /**
     * Builds XmlRpcClientConfig from {@link eionet.webq.dto.CdrRequest}.
     *
     * @param parameters parameters
     * @return config
     */
    private XmlRpcClientConfig buildConfig(CdrRequest parameters) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(createUrlFromString(parameters.getEnvelopeUrl()));
        if (parameters.isAuthorizationSet()) {
            if (StringUtils.isNotEmpty(parameters.getUserName())) {
                config.setBasicUserName(parameters.getUserName());
                config.setBasicPassword(parameters.getPassword());
            } else {
                KnownHost knownHost = knownHostsService.getKnownHost(parameters.getEnvelopeUrl());
                if (knownHost != null) {
                    config.setBasicUserName(knownHost.getKey());
                    config.setBasicPassword(knownHost.getTicket());
                }
            }
        }
        return config;
    }

    /**
     * Creates {@link URL} instance wrapping {@link java.net.MalformedURLException}.
     *
     * @param url string
     * @return url object
     */
    private URL createUrlFromString(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new CDREnvelopeException("Envelope URL is malformed", e);
        }
    }
}
