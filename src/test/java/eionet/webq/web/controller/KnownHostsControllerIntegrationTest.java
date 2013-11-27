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
import eionet.webq.service.KnownHostsService;
import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static eionet.webq.dto.KnownHostAuthenticationMethod.BASIC;
import static eionet.webq.dto.KnownHostAuthenticationMethod.REQUEST_PARAMETER;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Controller for {@link eionet.webq.dao.orm.KnownHost}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KnownHostsControllerIntegrationTest extends AbstractContextControllerTests {
    @Autowired
    private KnownHostsService knownHostsService;
    @Test
    public void returnsListOfAllKnownHostsPageAsDefaultPage() throws Exception {
        request(get("/known_hosts")).andExpect(view().name("known_hosts_list"));
    }

    @Test
    public void whenComingListOfKnownHostsPage_listIsAddedToModel() throws Exception {
        KnownHost host = saveKnownHost();
        knownHostsService.save(host);
        request(get("/known_hosts"))
                .andExpect(model().attributeExists("allKnownHosts"))
                .andExpect(model().attribute("allKnownHosts", hasItem(host)));
    }

    @Test
    public void showsNewKnowHostPageOnGetRequest() throws Exception {
        request(get("/known_hosts/add"))
                .andExpect(view().name("add_edit_known_host"));
    }

    @Test
    public void allowsToAddNewHost() throws Exception {
        assertThat(knownHostsService.findAll().size(), equalTo(0));

        request(post("/known_hosts/add")
                .param("hostURL", "http://host.url")
                .param("hostName", "Host name")
                .param("authenticationMethod", REQUEST_PARAMETER.name())
                .param("key", "api-key")
                .param("ticket", "api-ticket"))
                .andExpect(model().attribute("message", "Known host saved"))
                .andExpect(view().name("known_hosts_list"));

        assertThat(knownHostsService.findAll().size(), equalTo(1));
    }

    @Test
    public void whenRequestForEdit_showEditPageAndLoadRequiredHostData() throws Exception {
        KnownHost host = saveKnownHost();

        request(get("/known_hosts/update/" + host.getId()))
                .andExpect(view().name("add_edit_known_host"))
                .andExpect(model().attribute("host", equalTo(host)));

    }

    private KnownHost saveKnownHost() {
        KnownHost host = new KnownHost();
        host.setHostURL("http://host.url");
        host.setTicket("ticket");
        host.setKey("key");
        host.setAuthenticationMethod(BASIC);

        knownHostsService.save(host);

        return host;
    }
}
