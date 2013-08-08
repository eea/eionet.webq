package eionet.webq.service;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import eionet.webq.dto.Conversion;
import eionet.webq.dto.ListConversionResponse;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/application-context.xml",
        "file:src/test/resources/test-datasource-context.xml", "file:src/test/resources/test-servlet-context.xml",
        "file:src/main/webapp/WEB-INF/spring/properties-context.xml"})
@EnableCaching
public class ConversionServiceImplTest {
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private CacheManager cacheManager;
    private ConversionServiceImpl conversionServiceImpl;

    private RestTemplate restTemplate;
    private final String xmlSchema = "schema.xsd";

    @PostConstruct
    public void getImplementation() throws Exception {
        conversionServiceImpl = (ConversionServiceImpl) ((Advised) conversionService).getTargetSource().getTarget();
    }

    @Before
    public void prepare() {
        restTemplate = Mockito.mock(RestTemplate.class);
        conversionServiceImpl.restTemplate = restTemplate;
        cacheManager.getCache("conversions").clear();
    }

    @Test
    public void getsConversionForFileUsingXmlSchema() throws Exception {
        restTemplateWillReturnConversionsForSchema(createResponse(new Conversion()), xmlSchema);

        List<Conversion> conversions = conversionService.conversionsFor(xmlSchema);

        assertThat(conversions.size(), equalTo(1));
    }

    @Test
    public void forTheSameSchemaConversionsAreTakenFromCache() throws Exception {
        restTemplateWillReturnConversionsForSchema(createResponse(new Conversion()), xmlSchema);
        conversionService.conversionsFor(xmlSchema);
        conversionService.conversionsFor(xmlSchema);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ListConversionResponse.class), eq(xmlSchema));
    }

    private ListConversionResponse createResponse(Conversion... conversions) {
        ListConversionResponse response = new ListConversionResponse();
        response.setConversions(Arrays.asList(conversions));
        return response;
    }

    private void restTemplateWillReturnConversionsForSchema(ListConversionResponse conversionResponse, String xmlSchema) {
        when(restTemplate.getForObject(anyString(), eq(ListConversionResponse.class), eq(xmlSchema))).thenReturn(
                conversionResponse);
    }
}
