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
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.hibernate.criterion.CriteriaSpecification.DISTINCT_ROOT_ENTITY;

/**
 * {@link eionet.webq.dao.MergeModules} implementation.
 */
@Repository
@Transactional
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
        return (MergeModule) getCurrentSession().byId(getEntityClass()).load(id);
    }

    @Override
    public void remove(int... ids) {
        Query query = getCurrentSession().createQuery("DELETE FROM MergeModule WHERE id = :id");
        for (int id : ids) {
            query.setInteger("id", id).executeUpdate();
        }
    }

    @Override
    public MergeModule findByName(String moduleName) {
        return (MergeModule) getCriteria().add(Restrictions.eq("name", moduleName)).uniqueResult();
    }

    @Override
    public void update(MergeModule module) {
        if (module.getXslFile() == null || module.getXslFile().getSizeInBytes() == 0) {
            MergeModule moduleFromStorage = findById(module.getId());
            moduleFromStorage.setName(module.getName());
            moduleFromStorage.setXmlSchemas(module.getXmlSchemas());
            moduleFromStorage.setTitle(module.getTitle());
            setUpdatedAndUpdate(moduleFromStorage);
        } else {
            setUpdatedAndUpdate(module);
        }
    }

    /**
     * Sets updated date and performs update to storage.
     *
     * @param module module to update
     */
    private void setUpdatedAndUpdate(MergeModule module) {
        module.setUpdated(new Date());
        getCurrentSession().update(module);
    }

    @Override
    Class<MergeModule> getEntityClass() {
        return MergeModule.class;
    }
}
