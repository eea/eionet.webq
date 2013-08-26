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
 *        Enriko Käsper
 */

package eionet.webq.dao.util;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PreparedStatementCreator for inserting blob and returning generated key with Spring JdbcTemplate.
 *
 * @see http://stackoverflow.com/questions/2770877/spring-jdbctemplate-insert-blob-and-return-generated-key
 *
 * @author Enriko Käsper
 *
 */
public abstract class AbstractLobPreparedStatementCreator implements
                PreparedStatementCreator {
    /**
     * Lob handler.
     *
     * @see LobHandler
     * @see LobCreator
     */
    private final LobHandler lobHandler;
    /**
     * Sql to be executed.
     */
    private final String sql;
    /**
     * Generated id column.
     */
    private final String keyColumn;

    /**
     * Constructs AbstractLobPreparedStatementCreator.
     *
     * @param lobHandler {@link #lobHandler}
     * @param sql {@link #sql}
     * @param keyColumn {@link #keyColumn}
     */
    public AbstractLobPreparedStatementCreator(LobHandler lobHandler,
                        String sql, String keyColumn) {
        this.lobHandler = lobHandler;
        this.sql = sql;
        this.keyColumn = keyColumn;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con)
                        throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql,
                                new String[] {keyColumn});
        LobCreator lobCreator = this.lobHandler.getLobCreator();
        setValues(ps, lobCreator);
        return ps;
    }

    /**
     * Abstract method, which allows to set parameters to prepared statement.
     *
     * @param ps prepared statement
     * @param lobCreator {@link LobCreator}
     * @throws SQLException could be thrown from this method
     */
    protected abstract void setValues(PreparedStatement ps,
                        LobCreator lobCreator) throws SQLException;
}
