package eionet.webq.service.impl.project.export.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UploadedFile;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
final class ProjectFileFieldExclusionStrategy implements ExclusionStrategy {

    private static final List<String> FIELD_WHITELIST_PROJECT_FILE;
    private static final List<String> FIELD_WHITELIST_UPLOADED_FILE;
    
    static {
        FIELD_WHITELIST_PROJECT_FILE = Arrays.asList(
            "title",
            "remoteFileUrl",
            "newXmlFileName",
            "emptyInstanceUrl",
            "description",
            "xmlSchema",
            "active",
            "localForm",
            "remoteForm",
            "fileType",
            "file"
        );
        
        FIELD_WHITELIST_UPLOADED_FILE = Arrays.asList(
            "name"
        );
    }
    
    @Override
    public boolean shouldSkipField(FieldAttributes fa) {
        Class objectClass = fa.getDeclaringClass();
        List<String> whiteList = null;
        
        if (objectClass.equals(ProjectFile.class)) {
            whiteList = FIELD_WHITELIST_PROJECT_FILE;
        }
        else if (objectClass.equals(UploadedFile.class)) {
            whiteList = FIELD_WHITELIST_UPLOADED_FILE;
        }
        
        if (whiteList != null) {
            return !whiteList.contains(fa.getName());
        }
        
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> type) {
        return false;
    }
    
}
