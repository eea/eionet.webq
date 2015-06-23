package eionet.webq.service.impl.project.export;

import eionet.webq.dao.orm.ProjectFile;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

/**
 * Metadata structure that maintains all the information necessary so that a 
 * WebQ project can be fully restored through and export-import work-flow.
 * 
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public class ProjectMetadata {
    
    private ProjectFile[] projectFiles;

    public ProjectMetadata() { }
    
    public ProjectMetadata(Collection<ProjectFile> projectFiles) {
        this.projectFiles = new ProjectFile[projectFiles.size()];
        projectFiles.toArray(this.projectFiles);
    }
    
    public ProjectFile[] getProjectFiles() {
        return projectFiles;
    }

    public void setProjectFiles(ProjectFile[] projectFiles) {
        this.projectFiles = projectFiles;
    }
    
    /**
     * Checks whether the metadata model contains the bare minimun information
     * required, for an import process to be executed successfully.
     * 
     * @return true if the model is valid; false otherwise.
     */
    public boolean isValid() {
        if (this.getProjectFiles() == null) {
            return false;
        }
        
        for (ProjectFile projectFile : this.getProjectFiles()) {
            if (!this.isValid(projectFile)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isValid(ProjectFile projectFile) {
        if (StringUtils.isBlank(projectFile.getFileName())) {
            return false;
        }
        
        return true;
    }
}
