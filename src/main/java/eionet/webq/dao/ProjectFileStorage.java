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

import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.ProjectFile;

import java.util.Collection;

/**
 */
public interface ProjectFileStorage {
    /**
     * Saves project file.
     *
     * @param projectFile project file
     * @param project project where this file belongs
     * @return id in storage
     */
    int save(ProjectFile projectFile, ProjectEntry project);

    /**
     * Retrieves file by it's id.
     *
     * @param id id
     * @return file from storage
     */
    ProjectFile fileById(int id);

    /**
     * Updates file in storage, does not update empty file content and size if file content is empty.
     *
     * @param projectFile project file
     * @param projectEntry project where this file belongs
     */
    void update(ProjectFile projectFile, ProjectEntry projectEntry);

    /**
     * Lists all files for project.
     *
     * @param project project where this file belongs
     * @return files collection
     */
    @SuppressWarnings("unchecked")
    Collection<ProjectFile> allFilesFor(ProjectEntry project);

    /**
     * Removes file(-s) by id(-s).
     *
     * @param projectEntry project where this file belongs
     * @param fileIds file ids
     */
    void remove(ProjectEntry projectEntry, int... fileIds);

    /**
     * File content by file name and project tuple.
     *
     * @param name file name
     * @param projectEntry project where this file belongs
     * @return project
     */
    ProjectFile fileContentBy(String name, ProjectEntry projectEntry);
}
