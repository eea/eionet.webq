package eionet.webq.service.impl.project.export;

/**
 * Special exception type that indicates failure of parsing project metadata.
 * 
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public class MetadataSerializerException extends Exception {

    public MetadataSerializerException() {
    }

    public MetadataSerializerException(String message) {
        super(message);
    }

    public MetadataSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetadataSerializerException(Throwable cause) {
        super(cause);
    }
    
}
