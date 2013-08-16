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

import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.WebFormUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * ProjectFileStorage implementation.
 */
@Repository
public class ProjectFileStorageImpl implements ProjectFileStorage {
    /**
     * Jdbc template for accessing data storage.
     */
    @Autowired
    private JdbcTemplate template;
    /**
     * Large objects handler. Used for storing and retrieving {@link java.sql.Blob} object from database.
     */
    @Autowired
    private LobHandler lobHandler;

    @Override
    public void save(final ProjectEntry project, final WebFormUpload webFormUpload) {
        template.execute(
                "INSERT INTO project_file(project_id, title, file, xml_schema, description, user_name, active, main_form)"
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)", new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                    @Override
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                        ps.setInt(1, project.getId());
                        ps.setString(2, webFormUpload.getTitle());
                        lobCreator.setBlobAsBytes(ps, 3, webFormUpload.getFile());
                        ps.setString(4, webFormUpload.getXmlSchema());
                        ps.setString(5, webFormUpload.getDescription());
                        ps.setString(6, webFormUpload.getUserName());
                        ps.setBoolean(7, webFormUpload.isActive());
                        ps.setBoolean(8, webFormUpload.isMainForm());
                    }
                });
    }

    @Override
    public void update(WebFormUpload webFormUpload) {

    }

    @Override
    public void remove(int fileId) {
        template.update("DELETE FROM project_file WHERE id=?", fileId);
    }

    //TODO file content not needed
    @Override
    public Collection<WebFormUpload> allFilesFor(ProjectEntry project) {
        return template.query("SELECT * FROM project_file WHERE project_id=?",
                BeanPropertyRowMapper.newInstance(WebFormUpload.class), project.getId());
    }
}
