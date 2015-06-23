package eionet.webq.service.impl.project.export;

/**
 * Object type that provides thorough information about whether a project import 
 * process executed successfully, and if not, about what went wrong.
 * 
 * @see ErrorType
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public final class ImportProjectResult {

    /**
     * Represents the different outcomes of a project import process execution.
     */
    public static enum ErrorType {

        /**
         * Successful execution.
         */
        NONE,
        /**
         * Archive bundle did not contain project file metadata.
         */
        ARCHIVE_METADATA_NOT_FOUND,
        /**
         * Project file metadata could not be parsed correctly.
         */
        MALFORMED_ARCHIVE_METADATA,
        /**
         * Project file metadata where parsed, but failed validation.
         * @see ProjectMetadata#isValid() 
         */
        INVALID_ARCHIVE_METADATA,
        /**
         * Archive file structure did not conform with the project metadata.
         */
        INVALID_ARCHIVE_STRUCTURE
    }
    
    private ErrorType errorType;
    
    public ImportProjectResult() {
        this(ErrorType.NONE);
    }
    
    public ImportProjectResult(ErrorType errorType) {
        this.setErrorType(errorType);
    }
    
    public boolean isSuccess() {
        return this.errorType == null || this.errorType == ErrorType.NONE;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        if (errorType == null) {
            throw new IllegalArgumentException();
        }
        
        this.errorType = errorType;
    }
    
}
