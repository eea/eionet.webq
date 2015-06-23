package eionet.webq.web.io;

/**
 * Content disposition types.
 * 
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public enum Disposition {

    INLINE("inline"),
    ATTACHMENT("attachment");
    
    private final String value;
    
    private Disposition(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
    
}
