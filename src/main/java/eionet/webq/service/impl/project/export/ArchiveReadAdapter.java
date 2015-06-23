package eionet.webq.service.impl.project.export;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;

/**
 * Delegate object type that handles file extraction from archived bundles.
 * The extraction takes place in an iteratively manner. After the iteration is
 * complete, the read adapter must be closed.
 * 
 * @see ArchiveWriteAdapter
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public final class ArchiveReadAdapter {
    
    private final ZipInputStream zin;
    
    /**
     * Creates a new {@link ArchiveReadAdapter} using the provided content as the archived bundle.
     * 
     * @param archiveContent the byte content of the archived bundle.
     */
    public ArchiveReadAdapter(byte[] archiveContent) {
        this.zin = new ZipInputStream(new ByteArrayInputStream(archiveContent));
    }
    
    /**
     * Returns the next file within the archived bundle.
     * 
     * @return an {@link ArchiveFile} instance representing the next existing file; null if no other file is left.
     * @throws IOException in case of an I/O error.
     */
    public ArchiveFile next() throws IOException {
        ZipEntry entry = this.zin.getNextEntry();
        
        if (entry == null) {
            return null;
        }
        
        byte[] content = IOUtils.toByteArray(this.zin);
        this.zin.closeEntry();
                
        return new ArchiveFile(entry.getName(), content);
    }
    
    /**
     * Closes the adapter and releases internal resources.
     * 
     * @throws IOException in case of an I/O error.
     */
    public void close() throws IOException {
        this.zin.close();
    }
    
}
