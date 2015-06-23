package eionet.webq.service.impl.project.export.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import eionet.webq.service.impl.project.export.MetadataSerializerException;
import eionet.webq.service.impl.project.export.ProjectMetadata;
import eionet.webq.service.impl.project.export.ProjectMetadataSerializer;
import org.springframework.stereotype.Component;

/**
 * Project metadata serializer that uses the JSON format.
 * 
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
@Component
public class ProjectMetadataJsonSerializer implements ProjectMetadataSerializer {
    
    @Override
    public String serialize(ProjectMetadata projectMetadata) {
        Gson serializer = new GsonBuilder().setExclusionStrategies(new ProjectFileFieldExclusionStrategy()).create();
        
        return serializer.toJson(projectMetadata);
    }

    @Override
    public ProjectMetadata deserialize(String text) throws MetadataSerializerException {
        Gson serializer = new GsonBuilder().create();
        
        try {
            return serializer.fromJson(text, ProjectMetadata.class);
        }
        catch (JsonSyntaxException ex) {
            throw new MetadataSerializerException(ex);
        }
    }
    
}
