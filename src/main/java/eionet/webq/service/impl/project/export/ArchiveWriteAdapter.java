package eionet.webq.service.impl.project.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Delegate object type that handles creation of archive bundle files.
 * 
 * @see ArchiveReadAdapter
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public final class ArchiveWriteAdapter {

    private final ByteArrayOutputStream bout;
    private final ZipOutputStream zout;
    
    public ArchiveWriteAdapter() {
        this.bout = new ByteArrayOutputStream();
        this.zout = new ZipOutputStream(bout, this.getCharset());
    }
    
    /**
     * Adds a new file within the open archive.
     * 
     * @param file the file whose content will be added to this archive.
     * @throws IOException in case of an I/O error.
     */
    public void addEntry(ArchiveFile file) throws IOException {
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zout.putNextEntry(zipEntry);
        zout.write(file.getContent());
        zout.closeEntry();
    }
    
    /**
     * Finalizes the archiving process by closing the archive.
     * 
     * @throws IOException in case of an I/O error.
     */
    public void close() throws IOException {
        this.zout.close();
    }
    
    public byte[] getArchiveContent() {
        return this.bout.toByteArray();
    }
    
    public Charset getCharset() {
        return ArchiveConstants.CHARSET;
    }
}
