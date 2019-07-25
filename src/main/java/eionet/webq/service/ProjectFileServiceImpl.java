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
import eionet.webq.dao.orm.ProjectEntry;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.ProjectFileType;
import eionet.webq.dao.orm.util.WebQFileInfo;
import eionet.webq.service.impl.project.export.ArchiveConstants;
import eionet.webq.service.impl.project.export.ArchiveFile;
import eionet.webq.service.impl.project.export.ArchiveReadAdapter;
import eionet.webq.service.impl.project.export.ArchiveWriteAdapter;
import eionet.webq.service.impl.project.export.ImportProjectResult;
import eionet.webq.service.impl.project.export.MetadataSerializerException;
import eionet.webq.service.impl.project.export.ProjectMetadata;
import eionet.webq.service.impl.project.export.ProjectMetadataSerializer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 */
@Service
public class ProjectFileServiceImpl implements ProjectFileService {
    
    static final String PROJECT_EXPORT_METADATA_FILE = "webform-project-export.metadata";
    
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

    @Autowired
    ProjectMetadataSerializer projectMedatadataSerializer;
    
    @Override
    public void saveOrUpdate(ProjectFile file, ProjectEntry project) {
        if (!WebQFileInfo.isNew(file)) {
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

    @Transactional
    @Override
    public byte[] exportToArchive(ProjectEntry project) throws IOException {
        Collection<ProjectFile> projectFiles = this.allFilesFor(project);
        ArchiveWriteAdapter writer = new ArchiveWriteAdapter();
        
        try {
            for (ProjectFile projectFile : projectFiles) {
                writer.addEntry(new ArchiveFile(projectFile.getFileName(), projectFile.getFileContent()));
            }
            
            ProjectMetadata projectMetadata = new ProjectMetadata(projectFiles);
            byte[] metadataContent = this.generateExportMetadataContent(projectMetadata);
            writer.addEntry(new ArchiveFile(PROJECT_EXPORT_METADATA_FILE, metadataContent));
        }
        finally {
            writer.close();
        }
        
        return writer.getArchiveContent();
    }

    @Transactional
    @Override
    public ImportProjectResult importFromArchive(ProjectEntry project, byte[] archiveContent, String userName) throws IOException {
        ProjectArchiveContents archiveContents = this.extractArchive(archiveContent);
        
        if (archiveContents.metadataFile == null) {
            return new ImportProjectResult(ImportProjectResult.ErrorType.ARCHIVE_METADATA_NOT_FOUND);
        }
        
        ProjectMetadata projectMetadata = this.deserializeProjectMetadata(archiveContents.metadataFile);
        
        if (projectMetadata == null) {
            return new ImportProjectResult(ImportProjectResult.ErrorType.MALFORMED_ARCHIVE_METADATA);
        }
        
        if (!projectMetadata.isValid()) {
            return new ImportProjectResult(ImportProjectResult.ErrorType.INVALID_ARCHIVE_METADATA);
        }
        
        try {
            this.attachFileContent(projectMetadata, archiveContents.archiveFiles);
        }
        catch (FileNotFoundException ex) {
            return new ImportProjectResult(ImportProjectResult.ErrorType.INVALID_ARCHIVE_STRUCTURE);
        }
        
        Collection<ProjectFile> projectFiles = Arrays.asList(projectMetadata.getProjectFiles());
        
        for (ProjectFile projectFile : projectFiles) {
            projectFile.setUserName(userName);
        }
        
        this.projectFileStorage.cleanInsert(project, projectFiles);
        
        return new ImportProjectResult();
    }
    
    private byte[] generateExportMetadataContent(ProjectMetadata projectMetadata) {
        String metadata = this.projectMedatadataSerializer.serialize(projectMetadata);
        
        return metadata.getBytes(ArchiveConstants.CHARSET);
    }

    private ProjectArchiveContents extractArchive(byte[] archiveContent) throws IOException {
        ProjectArchiveContents result = new ProjectArchiveContents();
        ArchiveReadAdapter reader = new ArchiveReadAdapter(archiveContent);
        
        try {
            ArchiveFile archiveFile;
            
            while ((archiveFile = reader.next()) != null) {
                if (PROJECT_EXPORT_METADATA_FILE.equalsIgnoreCase(archiveFile.getName())) {
                    result.metadataFile = archiveFile;
                }
                else {
                    result.archiveFiles.add(archiveFile);
                }
            }
        }
        finally {
            reader.close();
        }
        
        return result;
    }
    
    private ProjectMetadata deserializeProjectMetadata(ArchiveFile metadataFile) {
        String metadataText = new String(metadataFile.getContent(), ArchiveConstants.CHARSET);
        
        try {
            return this.projectMedatadataSerializer.deserialize(metadataText);
        }
        catch (MetadataSerializerException ex) {
            return null;
        }
    }
    
    private void attachFileContent(ProjectMetadata metadata, Collection<ArchiveFile> archiveFiles) throws FileNotFoundException {
        for (ProjectFile projectFile : metadata.getProjectFiles()) {
            ArchiveFile archiveFile = this.findByName(archiveFiles, projectFile.getFileName());
            
            if (archiveFile == null) {
                throw new FileNotFoundException(projectFile.getFileName());
            }
            
            projectFile.setFileContent(archiveFile.getContent());
        }
    }
    
    private ArchiveFile findByName(Collection<ArchiveFile> archiveFiles, String name) {
        for (ArchiveFile archiveFile : archiveFiles) {
            if (archiveFile.getName().equalsIgnoreCase(name)) {
                return archiveFile;
            }
        }
        
        return null;
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
    
    private static final class ProjectArchiveContents {
        
        public ArchiveFile metadataFile;
        public Collection<ArchiveFile> archiveFiles = new ArrayList<ArchiveFile>();
        
    }
}
