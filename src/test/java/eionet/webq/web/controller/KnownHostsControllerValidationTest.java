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
package eionet.webq.web.controller;

import eionet.webq.dao.orm.KnownHost;
import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static eionet.webq.web.controller.KnownHostsControllerIntegrationTest.createValidKnownHost;
import static eionet.webq.web.controller.KnownHostsControllerIntegrationTest.setHostPropertiesToRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static util.TestUtil.stringOfLength;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KnownHostsControllerValidationTest extends AbstractContextControllerTests {

    @Test
    public void saveValidHost_producesNoErrors() throws Exception {
        KnownHost validKnownHost = createValidKnownHost();
        assertNoFieldErrorsOnSave(validKnownHost);
    }

    @Test
    public void saveWithEmptyHostUrl() throws Exception {
        KnownHost host = createValidKnownHost();
        host.setHostURL("");

        assertFieldErrorOnSave(host, "hostURL");
    }

    @Test
    public void saveWithEmptyKey() throws Exception {
        KnownHost host = createValidKnownHost();
        host.setKey("");

        assertFieldErrorOnSave(host, "key");
    }

    @Test
    public void saveWithEmptyTicket() throws Exception {
        KnownHost host = createValidKnownHost();
        host.setTicket("");

        assertFieldErrorOnSave(host, "ticket");
    }

    @Test
    public void tooLongHostUrl() throws Exception {
        KnownHost host = createValidKnownHost();
        host.setHostURL(stringOfLength(256));

        assertFieldErrorOnSave(host, "hostURL");
    }

    @Test
    public void tooLongHostName() throws Exception {
        KnownHost host = createValidKnownHost();
        host.setHostName(stringOfLength(256));

        assertFieldErrorOnSave(host, "hostName");
    }

    @Test
    public void tooLongKey() throws Exception {
        KnownHost host = createValidKnownHost();
        host.setKey(stringOfLength(256));

        assertFieldErrorOnSave(host, "key");
    }

    @Test
    public void tooLongTicket() throws Exception {
        KnownHost host = createValidKnownHost();
        host.setTicket(stringOfLength(256));

        assertFieldErrorOnSave(host, "ticket");
    }

    @Test
    public void textFieldsWithinAllowedLength() throws Exception {
        KnownHost host = createValidKnownHost();
        host.setHostURL(stringOfLength(255));
        host.setHostName(stringOfLength(255));
        host.setKey(stringOfLength(255));
        host.setTicket(stringOfLength(255));

        assertNoFieldErrorsOnSave(host);
    }

    private void assertNoFieldErrorsOnSave(KnownHost validKnownHost) throws Exception {
        saveHost(validKnownHost).andExpect(model().attributeErrorCount("knownHost", 0));
    }

    private void assertFieldErrorOnSave(KnownHost validKnownHost, String field) throws Exception {
        saveHost(validKnownHost).andExpect(model().attributeHasFieldErrors("knownHost", field));
    }

    private ResultActions saveHost(KnownHost knownHost) throws Exception {
        return request(setHostPropertiesToRequest(post("/known_hosts/save"), knownHost));
    }
}
