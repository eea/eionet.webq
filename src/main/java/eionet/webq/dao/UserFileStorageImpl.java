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

import eionet.webq.dto.UserFile;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.eq;

/**
 * {@link FileStorage} implementation for user files.
 */
@Repository
@Qualifier("user-files")
public class UserFileStorageImpl extends AbstractDao<UserFile> implements FileStorage<String, UserFile>, UserFileDownload {

    @Override
    public int save(final UserFile file, final String userId) {
        file.setUserId(userId);
        getCurrentSession().save(file);
        return file.getId();
    }

    @Override
    public UserFile fileContentBy(int id, String userId) {
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
    public Collection<UserFile> allFilesFor(String userId) {
        return getCriteria().add(eq("userId", userId)).list();
    }

    @Override
    public void remove(final String userId, final int... ids) {
        for (int id : ids) {
            getCurrentSession().createQuery("delete from UserFile where id in :fileIds")
                    .setParameter("fileIds", id).executeUpdate();
        }
    }

    @Override
    public UserFile fileContentBy(String name, String userId) {
        throw new UnsupportedOperationException("File name and userId pair is not unique in storage");
    }

    @Override
    public void updateDownloadTime(int userFileId) {
        getCurrentSession().createQuery("update UserFile set downloaded=:downloaded")
                .setTimestamp("downloaded", new Date()).executeUpdate();
    }

    @Override
    public UserFile fileById(int id) {
        return (UserFile) getCurrentSession().byId(getDtoClass()).load(id);
    }

    @Override
    Class<UserFile> getDtoClass() {
        return UserFile.class;
    }
}
