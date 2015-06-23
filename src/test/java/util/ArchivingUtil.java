package util;

import eionet.webq.service.impl.project.export.ArchiveFile;
import eionet.webq.service.impl.project.export.ArchiveReadAdapter;
import eionet.webq.service.impl.project.export.ArchiveWriteAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public class ArchivingUtil {

    public static final Comparator<ArchiveFile> SORT_COMPARATOR;
    public static final Comparator<ArchiveFile> EQUALITY_COMPARATOR;
    
    static {
        SORT_COMPARATOR = new Comparator<ArchiveFile>() {

            @Override
            public int compare(ArchiveFile o1, ArchiveFile o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        
        EQUALITY_COMPARATOR = new Comparator<ArchiveFile>() {

            @Override
            public int compare(ArchiveFile o1, ArchiveFile o2) {
                int cmp = o1.getName().compareTo(o2.getName());
                
                if (cmp != 0) {
                    return cmp;
                }
                
                cmp = o1.getContent().length - o2.getContent().length;
                
                if (cmp != 0) {
                    return cmp;
                }
                
                for (int i = 0; i < o1.getContent().length; ++i) {
                    cmp = o1.getContent()[i] - o2.getContent()[i];
                    
                    if (cmp != 0) {
                        return cmp;
                    }
                }
                
                return 0;
            }
        };
    }
    
    public static byte[] createArchive(Collection<ArchiveFile> files) throws IOException {
        ArchiveWriteAdapter writer = new ArchiveWriteAdapter();
        
        try {
            for (ArchiveFile file : files) {
                writer.addEntry(file);
            }
        }
        finally {
            writer.close();
        }
        
        return writer.getArchiveContent();
    }
    
    public static List<ArchiveFile> extractArchive(byte[] archiveContent) throws IOException {
        ArchiveReadAdapter reader = new ArchiveReadAdapter(archiveContent);
        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        
        try {
            ArchiveFile file;
            
            while ((file = reader.next()) != null) {
                files.add(file);
            }
        }
        finally {
            reader.close();
        }
        
        return files;
    }
}
