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
package eionet.webq;

import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class PlaceholderTest extends AbstractContextControllerTests {

    /**
     * Not overridden in pom.xml.
     */
    @Value("${cdr.save.xml}")
    private String cdrSaveXml;

    @Test
    public void testSaveXML() {
        assertEquals("saveXML", cdrSaveXml);
    }

    /**
     * Defined in the pom as a system property.
     */
    @Value("${db.url}")
    private String dbUrl;

    @Test
    public void testDbUrl() {
        assertEquals("jdbc:h2:mem:xmlStorage;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=AUTHORIZATION", dbUrl);
        assertEquals(System.getProperty("db.url"), dbUrl);
    }

    /**
     * Defined in the pom as a system property.
     */
    @Value("${user.file.expiration.hours}")
    private String fileExpiration;

    @Test
    public void testFileExpiration() {
        assertEquals("9", fileExpiration);
        assertEquals(System.getProperty("user.file.expiration.hours"), fileExpiration);
    }

    /**
     * Defined in the pom as a system property.
     */
    @Value("${converters.api.url}")
    private String convertersUrl;

    @Test
    public void testConvertersUrl() {
        assertEquals("http://converters-test", convertersUrl);
    }

    /**
     * Defined in the pom as a system property.
     */
    @Value("${cas.service}")
    private String casService;

    @Test
    public void testCasService() {
        assertEquals("http://localhost:8080", casService);
    }

}
