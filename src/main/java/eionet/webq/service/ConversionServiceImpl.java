package eionet.webq.service;

import eionet.webq.dto.Conversion;
import eionet.webq.dto.ListConversionResponse;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
     * Template for calling rest services.
     */
    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<Conversion> conversionsFor(String schema) {
        ListConversionResponse availableConversions =
                restTemplate.getForObject("http://converters.eionet.europa.eu/api/listConversions?schema={xml_schema}",
                        ListConversionResponse.class, schema);
        return availableConversions.getConversions();
    }
}
