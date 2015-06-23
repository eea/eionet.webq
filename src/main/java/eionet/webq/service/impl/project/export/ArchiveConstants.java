package eionet.webq.service.impl.project.export;

import java.nio.charset.Charset;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public final class ArchiveConstants {

    public static final Charset CHARSET;
    
    static {
        CHARSET = Charset.forName("UTF-8");
    }
    
    private ArchiveConstants() { }
}
