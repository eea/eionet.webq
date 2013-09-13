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
package eionet.webq.task;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

/**
 * Removes expired user files periodically, based on cron statement.
 */
@Component
public class RemoveExpiredUserFilesTask {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(RemoveExpiredUserFilesTask.class);
    /**
     * Jdbc template.
     */
    @Autowired
    JdbcTemplate jdbcTemplate;
    /**
     * Task properties.
     */
    @Autowired
    @Qualifier("task")
    Properties properties;

    /**
     * Perform removal task.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void removeExpiredUserFiles() {
        Integer hoursAgo = getExpirationHours();
        Date date = DateUtils.addHours(new Date(), -hoursAgo);
        LOGGER.info("Removing user files created before " + date + "(in storage more than " + hoursAgo + " hours). ");

        int rowsAffected = jdbcTemplate.update("DELETE FROM user_xml WHERE created < ?", new Timestamp(date.getTime()));

        LOGGER.info("Removal successful. Removed " + rowsAffected + " files.");
    }

    int getExpirationHours() {
        return Integer.valueOf(properties.getProperty("user.file.expiration.hours"));
    }
}
