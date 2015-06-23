package eionet.webq.service.impl.project.export;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import util.ArchivingUtil;
import util.CollectionUtil;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public class ArchiveAdaptersTest {

    @Test
    public void testArchivingZeroFiles() throws IOException {
        this.testArchiving(new ArchiveFile[] { });
    }
    
    @Test
    public void testArchivingMultipleFiles() throws IOException {
        ArchiveFile file1 = new ArchiveFile("file1", new byte[] { 1, 2, 3, 4 });
        ArchiveFile file2 = new ArchiveFile("file2", new byte[] { 5, 6, 7, 8 });
        this.testArchiving(new ArchiveFile[] { file1, file2 });
    }
    
    private void testArchiving(ArchiveFile[] files) throws IOException {
        Collection<ArchiveFile> originalFiles = Arrays.asList(files);
        
        byte[] archiveContent = ArchivingUtil.createArchive(originalFiles);
        Collection<ArchiveFile> extractedFiles = ArchivingUtil.extractArchive(archiveContent);
        
        Assert.assertTrue(CollectionUtil.equals(originalFiles, extractedFiles, ArchivingUtil.SORT_COMPARATOR, ArchivingUtil.EQUALITY_COMPARATOR));
    }
    
}
