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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 */
@Service
public class KnownHostsServiceImpl implements KnownHostsService {
    /**
     * Known hosts storage.
     */
    @Autowired
    private KnownHosts knownHosts;

    @Override
    public void save(KnownHost host) {
        knownHosts.save(host);
    }

    @Override
    public KnownHost findById(int id) {
        return knownHosts.findById(id);
    }

    @Override
    public Collection<KnownHost> findAll() {
        return knownHosts.findAll();
    }

    @Override
    public void update(KnownHost host) {
        knownHosts.update(host);
    }

    @Override
    public void remove(int id) {
        knownHosts.remove(id);
    }

    @Override
    public KnownHost getKnownHost(String uri) {

        KnownHost knownHost = null;
        Collection<KnownHost> hosts = findAll();
        for (KnownHost host : hosts) {
            if (uri.startsWith(host.getHostURL())) {
                knownHost = host;
                break;
            }
        }
        return knownHost;
    }

}
