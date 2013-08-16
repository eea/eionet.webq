package eionet.webq.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Properties;

/**
 * Common logic for DAO classes.
 */
public abstract class AbstractDao {
    /**
     * Sql statements properties.
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired
    @Qualifier("sql_statements")
    Properties sqlProperties;
}
