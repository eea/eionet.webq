package eionet.webq.service.impl.project.export;

/**
 * 
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public interface ProjectMetadataSerializer {

    /**
     * Converts the project metadata to a string equivalent representation.
     * 
     * @param projectMetadata the project metadata model.
     * @return the project metadata in string form.
     */
    public String serialize(ProjectMetadata projectMetadata);
    
    /**
     * Parses the string form of a project's metada to produce the corresponding
     * object model.
     * 
     * @param text the project metadata in string form.
     * @return the project metadata model.
     * @throws MetadataSerializerException in case of a parsing error. 
     */
    public ProjectMetadata deserialize(String text) throws MetadataSerializerException;
    
}
