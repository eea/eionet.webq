package eionet.webq.dao;

import eionet.webq.dto.ProjectEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
@Repository
public class ProjectFoldersImpl extends AbstractDao<ProjectEntry> implements ProjectFolders {
    /**
     * Jdbc template for accessing data storage.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Collection<ProjectEntry> getAllFolders() {
        return jdbcTemplate.query(sqlProperties.getProperty("select.all.projects"), rowMapper());
    }

    @Override
    public void remove(String projectId) {
        jdbcTemplate.update(sqlProperties.getProperty("delete.project.by.project.id"), projectId);
    }

    @Override
    public void update(ProjectEntry project) {
        if (project.getId() < 1) {
            throw new RuntimeException("Unable to update project, since it is not present in database.");
        }
        jdbcTemplate.update(sqlProperties.getProperty("update.project"), project.getProjectId(),
                project.getDescription(), project.getId());
    }

    @Override
    public void save(ProjectEntry projectEntry) {
        jdbcTemplate.update(sqlProperties.getProperty("insert.project"), projectEntry.getProjectId(),
                projectEntry.getDescription());
    }

    @Override
    public ProjectEntry getByProjectId(String projectId) {
        return jdbcTemplate.queryForObject(sqlProperties.getProperty("select.project.by.project.id"), rowMapper(), projectId);
    }

    @Override
    Class<ProjectEntry> getDtoClass() {
        return ProjectEntry.class;
    }
}
