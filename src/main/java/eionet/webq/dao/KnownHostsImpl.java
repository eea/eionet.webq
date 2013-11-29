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
package eionet.webq.dao;

import eionet.webq.dao.orm.KnownHost;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 */
@Repository
public class KnownHostsImpl extends AbstractDao<KnownHost> implements KnownHosts {

    @Override
    public int save(KnownHost host) {
        getCurrentSession().save(host);
        return host.getId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<KnownHost> findAll() {
        return getCriteria().list();
    }

    @Override
    public void update(KnownHost host) {
        getCurrentSession().update(host);
    }

    @Override
    public void remove(int id) {
        removeByCriterion(Restrictions.eq("id", id));
    }

    @Override
    Class<KnownHost> getEntityClass() {
        return KnownHost.class;
    }
}
