package eionet.webq.dao;

import java.io.File;
import java.io.IOException;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class InMemoryFileStorage implements FileStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(MultipartFile file) {
        Object[] params = {Long.toString(System.currentTimeMillis()), file.getOriginalFilename(), fileContent(file), new Date()};
        jdbcTemplate.update("INSERT INTO user_xml(id, filename, xml, created) VALUES(?, ?, ?, ?)", params);
    }

    @Override
    public File getByFilename(final String fileName) {
        return jdbcTemplate.queryForObject("SELECT xml FROM user_xml WHERE filename = ?", new Object[]{fileName}, new RowMapper<File>() {
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
        });
    }

    @Override
    public Collection<String> allUploadedFiles() {
        return jdbcTemplate.query("SELECT filename FROM user_xml", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });
    }
    
    private String fileContent(MultipartFile file) {
        try {
            return IOUtils.toString(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to transform xml file to string");
        }
    }
}
