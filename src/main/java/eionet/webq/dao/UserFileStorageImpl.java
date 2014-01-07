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

import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.UserFileIdUpdate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.in;

/**
 * {@link eionet.webq.dao.UserFileStorage} implementation.
 */
@Repository
public class UserFileStorageImpl extends AbstractDao<UserFile> implements UserFileDownload, UserFileStorage {
    /**
     * This class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UserFileStorageImpl.class);

    @Override
    public int save(final UserFile file, final String userId) {
        file.setUserId(userId);
        getCurrentSession().save(file);
        return file.getId();
    }

    @Override
    public UserFile findFile(int id, String userId) {
        return (UserFile) getCriteria().add(and(eq("id", id), eq("userId", userId))).uniqueResult();
    }

    @Override
    public void update(final UserFile file, final String userId) {
        UserFile userFile = (UserFile) getCriteria().add(Restrictions.idEq(file.getId())).uniqueResult();
        getCurrentSession().evict(userFile);
        if (userId.equals(userFile.getUserId())) {
            file.setUpdated(new Timestamp(System.currentTimeMillis()));
            getCurrentSession().update(file);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<UserFile> findAllUserFiles(String userId) {
        return getCriteria().add(eq("userId", userId)).list();
    }

    @Override
    public void remove(final String userId, final int... ids) {
        removeByCriterion(and(eq("userId", userId), in("id", ArrayUtils.toObject(ids))));
    }

    @Override
    public void updateUserId(UserFileIdUpdate updateData) {
        if (updateData.getUserAgent() != null) {
            getCurrentSession().createQuery("UPDATE UserFile SET userId=:newId WHERE userId=:oldId AND userAgent=:userAgent")
                    .setString("newId", updateData.getNewUserId()).setString("oldId", updateData.getOldUserId())
                    .setString("userAgent", updateData.getUserAgent()).executeUpdate();
        } else {
            LOGGER.warn("No user agent set in user file id update data.");
        }
    }

    @Override
    public void updateDownloadTime(int userFileId) {
        getCurrentSession().createQuery("update UserFile set downloaded=:downloaded where id=:id")
                .setTimestamp("downloaded", new Date()).setInteger("id", userFileId).executeUpdate();
    }

    @Override
    Class<UserFile> getEntityClass() {
        return UserFile.class;
    }
}
