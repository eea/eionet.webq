package eionet.webq.service;

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

import eionet.webq.dto.ProjectEntry;

import java.util.Collection;

/**
 * Service for operations on projects.
 */
public interface ProjectService {
    /**
     * Fetches all entries from storage.
     *
     * @return collection of project entries.
     */
    Collection<ProjectEntry> getAllFolders();

    /**
     * Removes entry from storage by project id.
     *
     * @param projectId project id
     */
    void remove(String projectId);

    /**
     * Retrieves storage data by project id.
     *
     * @param projectId project id
     * @return project from storage
     */
    ProjectEntry getByProjectId(String projectId);

    /**
     * Saves or updates project depending whether {@link ProjectEntry#id} is set.
     *
     * @param projectEntry project entry to save or update
     */
    void saveOrUpdate(ProjectEntry projectEntry);
}
