package eionet.webq.service;

import eionet.webq.dto.Conversion;
import eionet.webq.dto.ListConversionResponse;

import java.util.Collection;
import java.util.List;

import eionet.webq.dto.UploadedXmlFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

/**
 * Conversion service implementation.
 */
@Component
public class ConversionServiceImpl implements ConversionService {
    /**
     * Static logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ConversionServiceImpl.class);
    /**
     * Template for calling rest services.
     */
    @Autowired
    RestTemplate restTemplate;
    /**
     * Url to converters api.
     */
    @Value("#{application_properties['converters.api.url']}")
    private String converterApiUrl;
    /**
     * Template for conversions list method call.
     */
    @Value("#{application_properties['conversions.list.call.template']}")
    private String conversionListCallTemplate;

    @Override
    public void setAvailableConversionsFor(Collection<UploadedXmlFile> uploadedXmlFiles) {
        for (UploadedXmlFile uploadedXmlFile : uploadedXmlFiles) {
            uploadedXmlFile.setAvailableConversions(conversionsFor(uploadedXmlFile.getXmlSchema()));
        }
    }

    @Override
    public byte[] convert(UploadedXmlFile fileContent, int conversionId) {
        HttpHeaders fileHttpHeaders = new HttpHeaders();
        fileHttpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        fileHttpHeaders.setContentDispositionFormData("convert_file", fileContent.getName());
        HttpEntity<byte[]> file = new HttpEntity<byte[]>(fileContent.getContent(), fileHttpHeaders);

        HttpEntity<String> id = new HttpEntity<String>(Integer.toString(conversionId));

        MultiValueMap<String, Object> mvm = new LinkedMultiValueMap<String, Object>();
        mvm.add("convert_file", file);
        mvm.add("convert_id", id);
        String conversionResult = restTemplate.postForObject(converterApiUrl + "convertPush", mvm, String.class);
        LOGGER.info("Response from conversion service for file=" + fileContent.getName()
                + ", conversionId=" + conversionId + "\n" + conversionResult);
        return conversionResult.getBytes();
    }

    /**
     * List all available conversions.
     *
     * @param schema xml schema
     * @return collection of available conversions
     */
    @Cacheable(value = "conversions")
    private List<Conversion> conversionsFor(String schema) {
        String apiCallUrl = converterApiUrl + conversionListCallTemplate;
        return restTemplate.getForObject(apiCallUrl, ListConversionResponse.class, schema).getConversions();
    }
}
