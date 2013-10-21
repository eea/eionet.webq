package configuration;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestOperations;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import util.CacheCleaner;

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
@Configuration
@ImportResource({"file:src/main/webapp/WEB-INF/spring/application-context.xml",
        "file:src/test/resources/test-datasource-context.xml",
        "file:src/main/webapp/WEB-INF/spring/properties-context.xml"})
public class ApplicationTestContext {

    @Bean
    public RestOperations mockRestOperations() {
        return Mockito.mock(RestOperations.class);
    }

    @Bean
    public XmlRpcClient xmlRpcClient() {
        return Mockito.mock(XmlRpcClient.class);
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Bean
    @Lazy
    public CacheCleaner cacheCleaner() {
        return new CacheCleaner();
    }
}
