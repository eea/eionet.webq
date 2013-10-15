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

import eionet.webq.dao.orm.MergeModule;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static org.hibernate.criterion.CriteriaSpecification.DISTINCT_ROOT_ENTITY;

/**
 * {@link eionet.webq.dao.MergeModules} implementation.
 */
@Repository
public class MergeModulesImpl extends AbstractDao<MergeModule> implements MergeModules {

    @SuppressWarnings("unchecked")
    @Override
    public Collection<MergeModule> findAll() {
        return (List<MergeModule>) getCriteria().setResultTransformer(DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    public int save(MergeModule module) {
        getCurrentSession().save(module);
        return module.getId();
    }

    @Override
    public MergeModule findById(int id) {
        return (MergeModule) getCurrentSession().byId(getDtoClass()).load(id);
    }

    @Override
    public void remove(int... ids) {
        Query query = getCurrentSession().createQuery("DELETE FROM MergeModule WHERE id = :id");
        for (int id : ids) {
            query.setInteger("id", id).executeUpdate();
        }
    }

    @Override
    Class<MergeModule> getDtoClass() {
        return MergeModule.class;
    }
}
