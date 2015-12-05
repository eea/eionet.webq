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

import eionet.webq.dao.orm.UserFile;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
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
    SessionFactory factory;
    /**
     * Task properties.
     */
    //@Autowired
    //@Qualifier("task")
    //Properties properties;

    @Value("${user.file.expiration.hours}")
    private String expirationHours;

    // For testing
    void setExpirationHours(String property) {
        this.expirationHours = property;
    }

    /**
     * Perform removal task.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void removeExpiredUserFiles() {
        Integer hoursAgo = getExpirationHours();
        Date allowedDate = DateUtils.addHours(new Date(), -hoursAgo);
        LOGGER.info("Removing user files last modified before " + allowedDate + "(in storage more than " + hoursAgo + " hours). ");

        Session currentSession = factory.getCurrentSession();
        List rowsAffected = currentSession.createCriteria(UserFile.class)
                .add(Restrictions.le("updated", new Timestamp(allowedDate.getTime())))
                .list();
        for (Object row : rowsAffected) {
            currentSession.delete(row);
        }

        LOGGER.info("Removal successful. Removed " + rowsAffected + " files.");
    }

    int getExpirationHours() {
        return Integer.valueOf(expirationHours);
        //return Integer.valueOf(properties.getProperty("user.file.expiration.hours"));
    }
}
