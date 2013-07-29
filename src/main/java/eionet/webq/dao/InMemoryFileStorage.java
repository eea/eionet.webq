package eionet.webq.dao;

import java.io.File;
import java.io.IOException;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import javax.servlet.http.HttpSession;

import eionet.webq.model.UploadedXmlFile;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryFileStorage implements FileStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HttpSession session;

    @Override
    public void save(UploadedXmlFile file) {
        Object[] params = {sessionId(), file.getName(), file.getXmlSchema(), file.getFileContent(), new Date()};
        jdbcTemplate.update("INSERT INTO user_xml(session_id, filename, xml_schema, xml, created) VALUES(?, ?, ?, ?, ?)", params);
    }

    @Override
    public File getByFilename(final String fileName) {
        Object[] params = {sessionId(), fileName};
        return jdbcTemplate.queryForObject("SELECT xml FROM user_xml WHERE session_id = ? AND filename = ?", params, fileFromDb(fileName));
    }

    @Override
    public Collection<String> allUploadedFiles() {
        return jdbcTemplate.queryForList("SELECT filename FROM user_xml WHERE session_id = ?", new Object[] {sessionId()}, String.class);
    }

    private RowMapper<File> fileFromDb(final String fileName) {
        return new RowMapper<File>() {
            @Override
            public File mapRow(ResultSet rs, int rowNum) throws SQLException {
                NClob nClob = rs.getNClob(1);
                File file = new File(fileName);
                try {
                    FileUtils.write(file, nClob.getSubString(1, (int) nClob.length()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return file;
            }
        };
    }

    private String sessionId() {
        return session.getId();
    }
}
