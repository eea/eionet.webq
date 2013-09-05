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
package eionet.webq.service;

import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.ProjectFile;
import eionet.webq.dto.ProjectFileType;
import org.springframework.util.MultiValueMap;

import java.util.Collection;

/**
 * Project file storage service.
 */
public interface ProjectFileService {
    /**
     * Saves or updates file in storage.
     *
     * @param file file to save
     * @param project project where this files will belong
     */
    void saveOrUpdate(ProjectFile file, ProjectEntry project);

    /**
     * Updates file content.
     *
     * @param id file id in storage
     * @param content new file content
     * @param project project this file belongs
     */
    void updateContent(int id, byte[] content, ProjectEntry project);

    /**
     * Retrieve file data by id in storage without file content.
     * To get content consider usage of {@link ProjectFileService#fileContentBy(String, ProjectEntry)}
     *
     * @param id file id in storage
     * @return file data without file content
     */
    ProjectFile getById(int id);

    /**
     * Retrieves all files for the project.
     *
     * @param project associated project
     * @return collection of files linked to this key
     */
    MultiValueMap<ProjectFileType, ProjectFile> filesDividedByTypeFor(ProjectEntry project);

    /**
     * Retrieve file name and content.
     *
     * @param project associated project
     * @param name file name
     * @return file
     */
    ProjectFile fileContentBy(String name, ProjectEntry project);

    /**
     * Remove file by id and classifier.
     *
     * @param project associated project
     * @param id file ids
     */
    void remove(ProjectEntry project, int... id);

    /**
     * Returns all files for a project.
     *
     * @param project project
     * @return project files collection
     */
    Collection<ProjectFile> allFilesFor(ProjectEntry project);
}
