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
import org.springframework.beans.factory.annotation.Qualifier;
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
 * Key id is {@link eionet.webq.dto.ProjectEntry#getId()}
 */
@Repository
@Qualifier("project-files")
public class ProjectFileStorageImpl extends AbstractDao<WebFormUpload> implements FileStorage<ProjectEntry, WebFormUpload> {
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
    public void save(final WebFormUpload webFormUpload, final ProjectEntry project) {
        template.execute(sqlProperties.getProperty("insert.project.file"), new AbstractLobCreatingPreparedStatementCallback(
                lobHandler) {
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
    public WebFormUpload fileById(int id) {
        return template.queryForObject(sqlProperties.getProperty("select.file.by.id"), rowMapper(), id);
    }

    @Override
    public void update(final WebFormUpload webFormUpload, ProjectEntry projectEntry) {
        final boolean updateFile = webFormUpload.getFile() != null;
        String updateStatement =
                updateFile ? sqlProperties.getProperty("update.project.file") : sqlProperties
                        .getProperty("update.project.file.without.file");
        template.execute(updateStatement, new AbstractLobCreatingPreparedStatementCallback(
                lobHandler) {
            @Override
            protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                //TODO same pattern to all AbstractLobCreatingPreparedStatementCallback
                int index = 1;
                ps.setString(index++, webFormUpload.getTitle());
                ps.setString(index++, webFormUpload.getXmlSchema());
                ps.setString(index++, webFormUpload.getDescription());
                ps.setString(index++, webFormUpload.getUserName());
                ps.setBoolean(index++, webFormUpload.isActive());
                ps.setBoolean(index++, webFormUpload.isMainForm());
                if (updateFile) {
                    lobCreator.setBlobAsBytes(ps, index++, webFormUpload.getFile());
                }
                ps.setInt(index, webFormUpload.getId());
            }
        });
    }

    @Override
    public Collection<WebFormUpload> allFilesFor(ProjectEntry project) {
        return template.query(sqlProperties.getProperty("select.all.project.files"), rowMapper(), project.getId());
    }

    @Override
    public void remove(int fileId, ProjectEntry projectEntry) {
        template.update(sqlProperties.getProperty("delete.project.file"), fileId, projectEntry.getId());
    }

    @Override
    public WebFormUpload fileContentBy(int id, ProjectEntry projectEntry) {
        return template.queryForObject(sqlProperties.getProperty("select.project.file.content"), rowMapper(), id,
                projectEntry.getId());
    }

    @Override
    Class<WebFormUpload> getDtoClass() {
        return WebFormUpload.class;
    }
}
