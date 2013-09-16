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
import eionet.webq.dao.ProjectFileStorage;
import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.ProjectFile;
import eionet.webq.dto.ProjectFileType;
import eionet.webq.dto.util.ProjectFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collection;

/**
 */
@Service
public class ProjectFileServiceImpl implements ProjectFileService {
    /**
     * Project files storage.
     */
    @Autowired
    ProjectFileStorage projectFileStorage;
    /**
     * Extracts xml schema from project xml files.
     */
    @Autowired
    XmlSchemaExtractor xmlSchemaExtractor;

    @Override
    public void saveOrUpdate(ProjectFile file, ProjectEntry project) {
        if (!ProjectFileInfo.isNew(file)) {
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
        return projectFileStorage.findById(id);
    }

    @Override
    public MultiValueMap<ProjectFileType, ProjectFile> filesDividedByTypeFor(ProjectEntry project) {
        MultiValueMap<ProjectFileType, ProjectFile> result = new LinkedMultiValueMap<ProjectFileType, ProjectFile>();
        for (ProjectFile projectFile : projectFileStorage.findAllFilesFor(project)) {
            result.add(projectFile.getFileType(), projectFile);
        }
        return result;
    }

    @Override
    public ProjectFile fileContentBy(String name, ProjectEntry project) {
        return projectFileStorage.findByNameAndProject(name, project);
    }

    @Override
    public void remove(ProjectEntry project, int... id) {
        projectFileStorage.remove(project, id);
    }

    @Override
    public Collection<ProjectFile> allFilesFor(ProjectEntry project) {
        return projectFileStorage.findAllFilesFor(project);
    }

    @Override
    public void updateContent(int id, byte[] content, ProjectEntry project) {
        ProjectFile file = projectFileStorage.findById(id);
        file.setFileContent(content);
        projectFileStorage.update(file, project);
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
