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

import eionet.webq.model.UploadedXmlFile;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

@Repository
public class FileStorageImpl implements FileStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HttpSession session;
    @Autowired
    private LobHandler lobHandler;

    @Override
    public void save(final UploadedXmlFile file) {
        jdbcTemplate.execute("INSERT INTO user_xml(session_id, filename, xml_schema, xml) VALUES(?, ?, ?, ?)",
                new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                    @Override
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                        ps.setString(1, sessionId());
                        ps.setString(2, file.getName());
                        ps.setString(3, file.getXmlSchema());
                        lobCreator.setBlobAsBytes(ps, 4, file.getFileContent());
                    }
                });
    }

    @Override
    public byte[] getByFilename(final String fileName) {
        Object[] params = {sessionId(), fileName};
        return jdbcTemplate.queryForObject("SELECT xml FROM user_xml WHERE session_id = ? AND filename = ?", params,
                new RowMapper<byte[]>() {
                    @Override
                    public byte[] mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return lobHandler.getBlobAsBytes(rs, 1);
                    }
                });
    }

    @Override
    public Collection<String> allUploadedFiles() {
        return jdbcTemplate.queryForList("SELECT filename FROM user_xml WHERE session_id = ?", new Object[] {sessionId()},
                String.class);
    }

    private String sessionId() {
        return session.getId();
    }
}
