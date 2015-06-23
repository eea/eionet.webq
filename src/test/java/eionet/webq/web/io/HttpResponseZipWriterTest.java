package eionet.webq.web.io;

import eionet.webq.service.impl.project.export.ArchiveFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import util.ArchivingUtil;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public class HttpResponseZipWriterTest {

    @Test
    public void testDownloadZip() throws IOException {
        ArchiveFile file = new ArchiveFile("file.dummy", new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 });
        byte[] archiveContent = ArchivingUtil.createArchive(Arrays.asList(file));
        HttpFileInfo httpFile = new HttpFileInfo("archive.zip", archiveContent);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpResponseZipWriter writer = new HttpResponseZipWriter();
        writer.writeFile(response, httpFile);
        
        this.assertHttpHeader(response, "Content-Disposition", "attachment;filename=" + httpFile.getName());
        this.assertHttpHeader(response, "Cache-control", "no-cache");
        this.assertHttpHeader(response, "Content-Type", "application/zip;charset=UTF-8");
        
        List<ArchiveFile> downloadedFileContainer = ArchivingUtil.extractArchive(response.getContentAsByteArray());
        
        Assert.assertEquals(1, downloadedFileContainer.size());
        Assert.assertEquals(0, ArchivingUtil.EQUALITY_COMPARATOR.compare(file, downloadedFileContainer.get(0)));
    }
    
    private void assertHttpHeader(MockHttpServletResponse response, String headerName, String expectedValue) {
        Assert.assertEquals(expectedValue, response.getHeader(headerName));
    }
}
