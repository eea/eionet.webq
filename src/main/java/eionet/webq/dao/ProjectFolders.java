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

import java.util.Collection;

import eionet.webq.dao.orm.ProjectEntry;

/**
 * Interface for database interaction on project folders.
 */
public interface ProjectFolders {

    /**
     * Method allows to persist project entry to database.
     *
     * @param projectEntry project entry to persist
     */
    void save(ProjectEntry projectEntry);

    /**
     * Retrieves all persisted project entries from data storage.
     *
     * @return collection of project entries.
     */
    Collection<ProjectEntry> getAllFolders();

    /**
     * Removes project folder by project id.
     *
     * @param projectId project id
     */
    void remove(String projectId);

    /**
     * Updates project folder.
     *
     * @param project project with storage id set and new data
     */
    void update(ProjectEntry project);

    /**
     * Returns project stored with specified project id.
     *
     * @param projectId project id
     * @return project from storage
     */
    ProjectEntry getByProjectId(String projectId);

    /**
     * Returns project stored with specified project numeric id.
     *
     * @param id project numeric id
     * @return project from storage
     */
    ProjectEntry getById(int id);
}
