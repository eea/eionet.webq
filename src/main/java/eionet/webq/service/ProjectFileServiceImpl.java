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

import eionet.webq.converter.XmlSchemaExtractor;
import eionet.webq.dao.FileStorage;
import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.ProjectFile;
import eionet.webq.dto.ProjectFileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 */
@Service
public class ProjectFileServiceImpl implements ProjectFileService {
    /**
     * Project files storage.
     */
    @Autowired
    @Qualifier("project-files")
    FileStorage<ProjectEntry, ProjectFile> projectFileStorage;
    /**
     * Extracts xml schema from project xml files.
     */
    @Autowired
    XmlSchemaExtractor xmlSchemaExtractor;

    @Override
    public void saveOrUpdate(ProjectFile file, ProjectEntry project) {
        boolean update = file.getId() > 0;
        if (update) {
            projectFileStorage.update(file, project);
            return;
        }
        if (file.getFileType() == null) {
            throw new RuntimeException("File type not set. File=" + file);
        }
        extractAndSetXmlSchemaIfRequired(file);
        projectFileStorage.save(file, project);
    }

    @Override
    public ProjectFile getById(int id) {
        return projectFileStorage.fileById(id);
    }

    @Override
    public MultiValueMap<ProjectFileType, ProjectFile> filesDividedByTypeFor(ProjectEntry project) {
        MultiValueMap<ProjectFileType, ProjectFile> result = new LinkedMultiValueMap<ProjectFileType, ProjectFile>();
        for (ProjectFile projectFile : projectFileStorage.allFilesFor(project)) {
            result.add(projectFile.getFileType(), projectFile);
        }
        return result;
    }

    @Override
    public ProjectFile fileContentBy(int id, ProjectEntry project) {
        return projectFileStorage.fileContentBy(id, project);
    }

    @Override
    public void remove(int id, ProjectEntry project) {
        projectFileStorage.remove(project, id);
    }

    /**
     * Try to extract xml schema from file content if required.
     *
     * @param file file.
     */
    private void extractAndSetXmlSchemaIfRequired(ProjectFile file) {
        if (file.getFileType() == ProjectFileType.FILE) {
            file.setXmlSchema(xmlSchemaExtractor.extractXmlSchema(file.getFileContent()));
        }
    }
}
