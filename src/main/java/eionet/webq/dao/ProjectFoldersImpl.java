package eionet.webq.dao;

import eionet.webq.dto.ProjectEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
public class ProjectFoldersImpl implements ProjectFolders {
    /**
     * Jdbc template for accessing data storage.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Collection<ProjectEntry> getAllFolders() {
        return jdbcTemplate.query("SELECT * FROM project_folder", BeanPropertyRowMapper.newInstance(ProjectEntry.class));
    }

    @Override
    public void save(ProjectEntry projectEntry) {
        jdbcTemplate.update("INSERT INTO project_folder(project_id, description) VALUES(?, ?)", projectEntry.getId(),
                projectEntry.getDescription());
    }
}
