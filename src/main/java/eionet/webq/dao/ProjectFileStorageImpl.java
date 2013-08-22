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
import eionet.webq.dto.ProjectFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * ProjectFileStorage implementation.
 * Key id is {@link eionet.webq.dto.ProjectEntry#getId()}
 */
@Repository
@Qualifier("project-files")
public class ProjectFileStorageImpl extends AbstractDao<ProjectFile> implements FileStorage<ProjectEntry, ProjectFile> {
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
    public void save(final ProjectFile projectFile, final ProjectEntry project) {
        template.execute(sqlProperties.getProperty("insert.project.file"), new AbstractLobCreatingPreparedStatementCallback(
                lobHandler) {
            @Override
            protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                ps.setInt(1, project.getId());
                ps.setString(2, projectFile.getTitle());
                lobCreator.setBlobAsBytes(ps, 3, projectFile.getFileContent());
                ps.setLong(4, projectFile.getFileSizeInBytes());
                ps.setString(5, projectFile.getXmlSchema());
                ps.setString(6, projectFile.getDescription());
                ps.setString(7, projectFile.getUserName());
                ps.setBoolean(8, projectFile.isActive());
                ps.setBoolean(9, projectFile.isMainForm());
            }
        });
    }

    @Override
    public ProjectFile fileById(int id) {
        return template.queryForObject(sqlProperties.getProperty("select.file.by.id"), rowMapper(), id);
    }

    @Override
    public void update(final ProjectFile projectFile, ProjectEntry projectEntry) {
        final boolean updateFile = projectFile.getFileContent() != null;
        String updateStatement =
                updateFile ? sqlProperties.getProperty("update.project.file") : sqlProperties
                        .getProperty("update.project.file.without.file");
        template.execute(updateStatement, new AbstractLobCreatingPreparedStatementCallback(
                lobHandler) {
            @Override
            protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                int index = 1;
                ps.setString(index++, projectFile.getTitle());
                ps.setString(index++, projectFile.getXmlSchema());
                ps.setString(index++, projectFile.getDescription());
                ps.setString(index++, projectFile.getUserName());
                ps.setBoolean(index++, projectFile.isActive());
                ps.setBoolean(index++, projectFile.isMainForm());
                if (updateFile) {
                    lobCreator.setBlobAsBytes(ps, index++, projectFile.getFileContent());
                }
                ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
                ps.setInt(index, projectFile.getId());
            }
        });
    }

    @Override
    public Collection<ProjectFile> allFilesFor(ProjectEntry project) {
        return template.query(sqlProperties.getProperty("select.all.project.files"), rowMapper(), project.getId());
    }

    @Override
    public void remove(int fileId, ProjectEntry projectEntry) {
        template.update(sqlProperties.getProperty("delete.project.file"), fileId, projectEntry.getId());
    }

    @Override
    public ProjectFile fileContentBy(int id, ProjectEntry projectEntry) {
        return template.queryForObject(sqlProperties.getProperty("select.project.file.content"), rowMapper(), id,
                projectEntry.getId());
    }

    @Override
    Class<ProjectFile> getDtoClass() {
        return ProjectFile.class;
    }
}
