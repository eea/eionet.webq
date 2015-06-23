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

import eionet.webq.dao.orm.ProjectEntry;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.ProjectFileType;
import eionet.webq.service.impl.project.export.ImportProjectResult;
import java.io.IOException;
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
    
    /**
     * Creates an archived bundle containing all the files of the specified project.
     * The bundle must contain the metadata required so that it can be used to replicate
     * the project structure elsewhere.
     * 
     * @param project the project whose files to export
     * @return the archived bundle file content in byte form
     * @throws IOException in case of an I/O error
     * @see #importFromArchive(eionet.webq.dao.orm.ProjectEntry, byte[], java.lang.String) 
     */
    byte[] exportToArchive(ProjectEntry project) throws IOException;
    
    /**
     * Using an archived bundle as an input, this process imports the archived files 
     * into the specified project. Note that this is a clean-install operation; i.e. 
     * any existing files within the project will be deleted, then the new ones will
     * be imported.
     * 
     * @param project the project in which the archived files will be imported.
     * @param archiveContent the archived bundle file content in byte form
     * @param userName the name of the user who triggered the process.
     * @return a complex object of type {@link ImportProjectResult}, describing the 
     * result of the process.
     * @throws IOException in case of an I/O error
     * @see #exportToArchive(eionet.webq.dao.orm.ProjectEntry) 
     */
    ImportProjectResult importFromArchive(ProjectEntry project, byte[] archiveContent, String userName) throws IOException;
}
