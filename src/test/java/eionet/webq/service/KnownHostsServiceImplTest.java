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

import eionet.webq.dao.KnownHosts;
import eionet.webq.dao.orm.KnownHost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class KnownHostsServiceImplTest {
    @Mock
    private KnownHosts knownHosts;
    @InjectMocks
    private KnownHostsServiceImpl service;

    @Test
    public void whenSavingKnownHost_callsDaoMethod() throws Exception {
        KnownHost host = new KnownHost();
        service.save(host);

        verify(knownHosts).save(host);
    }

    @Test
    public void whenFindById_callDaoMethod() throws Exception {
        service.findById(1);

        verify(knownHosts).findById(1);
    }

    @Test
    public void whenFindAll_callDaoMethod() throws Exception {
        service.findAll();

        verify(knownHosts).findAll();
    }

    @Test
    public void whenUpdateIsCalled_callDaoMethod() throws Exception {
        KnownHost host = new KnownHost();
        service.update(host);

        verify(knownHosts).update(host);
    }
}
