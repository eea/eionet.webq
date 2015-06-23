package eionet.webq.web.io;

/**
 * Media types.
 * 
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public enum MediaType {

    APPLICATION_ZIP("application/zip");
    
    private final String value;
    
    private MediaType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
    
}
