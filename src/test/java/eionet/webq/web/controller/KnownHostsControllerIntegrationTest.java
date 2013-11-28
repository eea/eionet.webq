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
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static eionet.webq.dto.KnownHostAuthenticationMethod.BASIC;
import static eionet.webq.web.controller.KnownHostsController.HOST_REMOVED_MESSAGE;
import static eionet.webq.web.controller.KnownHostsController.KNOWN_HOST_SAVED_MESSAGE;
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
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void returnsListOfAllKnownHostsPageAsDefaultPage() throws Exception {
        request(get("/known_hosts")).andExpect(view().name("known_hosts_list"));
    }

    @Test
    public void whenListOfKnownHostsPageRequested_listIsAddedToModel() throws Exception {
        KnownHost host = saveKnownHost();

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
        KnownHost knownHost = createKnownHost();

        request(setHostPropertiesToRequest(post("/known_hosts/save"), knownHost))
                .andExpect(model().attribute("message", KNOWN_HOST_SAVED_MESSAGE))
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

    @Test
    public void whenHostIsUpdated_hostIsUpdatedAndMessageIsAddedToModel() throws Exception {
        KnownHost host = saveKnownHost();
        host.setHostURL(host.getHostURL() + "/new_url");

        request(setHostPropertiesToRequest(post("/known_hosts/save"), host))
                .andExpect(view().name("known_hosts_list"))
                .andExpect(model().attribute("message", KNOWN_HOST_SAVED_MESSAGE));

        KnownHost hostFromStorage = knownHostsService.findById(host.getId());
        assertThat(hostFromStorage, equalTo(host));
    }

    @Test
    public void whenRemovingHost_hostIsRemovedAndMessageIsAdded() throws Exception {
        KnownHost host = saveKnownHost();
        assertThat(knownHostsService.findAll().size(), equalTo(1));

        request(get("/known_hosts/remove/" + host.getId()))
                .andExpect(model().attribute("message", HOST_REMOVED_MESSAGE));

        assertThat(knownHostsService.findAll().size(), equalTo(0));
    }

    private MockHttpServletRequestBuilder setHostPropertiesToRequest(MockHttpServletRequestBuilder request, KnownHost knownHost) {
        return request
                .param("id", Integer.toString(knownHost.getId()))
                .param("hostURL", knownHost.getHostURL())
                .param("hostName", knownHost.getHostName())
                .param("authenticationMethod", knownHost.getAuthenticationMethod().name())
                .param("key", knownHost.getKey())
                .param("ticket", knownHost.getTicket());
    }

    private KnownHost saveKnownHost() {
        KnownHost host = createKnownHost();
        knownHostsService.save(host);
        sessionFactory.getCurrentSession().evict(host);
        return host;
    }

    private KnownHost createKnownHost() {
        KnownHost host = new KnownHost();
        host.setHostURL("http://host.url");
        host.setTicket("ticket");
        host.setKey("key");
        host.setAuthenticationMethod(BASIC);
        host.setHostName("host name");
        return host;
    }
}
