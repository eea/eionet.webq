package eionet.webq.dao;

import eionet.webq.dao.orm.ProjectEntry;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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

/**
 * Project folders interface implementation.
 */
@Transactional
@Repository
public class ProjectFoldersImpl extends AbstractDao<ProjectEntry> implements ProjectFolders {
    /**
     * Session factory.
     */
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ProjectEntry> getAllFolders() {
        return getCriteria().addOrder(Order.asc("projectId")).list();
    }

    @Override
    public void remove(String projectId) {
        String hql = "DELETE from ProjectEntry where projectId=:projectId";
        getCurrentSession().createQuery(hql).setString("projectId", projectId).executeUpdate();
    }

    @Override
    public void update(ProjectEntry project) {
        getCurrentSession().merge(project);
    }

    @Override
    public void save(ProjectEntry projectEntry) {
        getCurrentSession().save(projectEntry);
    }

    @Override
    public ProjectEntry getByProjectId(String projectId) {
        return (ProjectEntry) getCriteria().add(Restrictions.eq("projectId", projectId)).uniqueResult();
    }

    @Override
    Class<ProjectEntry> getDtoClass() {
        return ProjectEntry.class;
    }
}
