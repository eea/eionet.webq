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
 *        Raptis Dimos
 */
package eionet.webq.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;


public class UserManagementServiceImpl extends JdbcUserDetailsManager implements UserManagementService, UserDetailsManager, GroupManager {
    
    public static final String DEF_GET_ALL_USERS_SQL = "select * from users"; 
    
    @Override
    public List<UserDetails> getAllUsers(){
        return getJdbcTemplate().query(DEF_GET_ALL_USERS_SQL, new String[] {},
                    new RowMapper<UserDetails>() {
                        public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                            String username = rs.getString(1);
                            String password = rs.getString(2);
                            boolean enabled = rs.getBoolean(3);
                            return new User(username, password, enabled, true, true, true, loadUserAuthorities(username));
                        }

                    });
    }
    
}
