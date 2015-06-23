package eionet.webq.web.io;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public abstract class HttpResponseFileWriterBase implements HttpResponseFileWriter {

    @Override
    public final void writeFile(HttpServletResponse response, HttpFileInfo fileInfo) {
        this.setContentType(response);
        this.setContentDisposition(response, fileInfo);
        this.setLastModified(response, fileInfo);
        this.setCaching(response);
        this.performWrite(response, fileInfo);
    }
    
    protected abstract MediaType getMediaType();
    
    protected abstract Disposition getDisposition();
    
    protected abstract boolean isCachingEnabled();
    
    protected abstract String getCharacterSetName();
    
    private void setContentType(HttpServletResponse response) {
        String value = this.getMediaType().toString();
        
        if (this.getCharacterSetName() != null) {
            value += ";charset=" + this.getCharacterSetName();
        }
        
        response.setContentType(value);
    }
    
    private void setContentDisposition(HttpServletResponse response, HttpFileInfo fileInfo) {
        String value = String.format("%s;filename=%s", this.getDisposition().toString(), fileInfo.getName());
        response.setHeader("Content-Disposition", value);
    }
    
    private void setCaching(HttpServletResponse response) {
        if (!this.isCachingEnabled()) {
            response.addHeader("Cache-control", "no-cache");
        }
    }
    
    private void setLastModified(HttpServletResponse response, HttpFileInfo fileInfo) {
        if (fileInfo.getLastModifiedMillis() > 0) {
            response.setDateHeader("Last-Modified", fileInfo.getLastModifiedMillis());
        }
    }
    
    private void performWrite(HttpServletResponse response, HttpFileInfo fileInfo) {
        ServletOutputStream out = null;
        
        try {
            out = response.getOutputStream();
            out.write(fileInfo.getContent());
        }
        catch (IOException ex) {
            throw new RuntimeException("Unable to write response", ex);
        }
        finally {
            IOUtils.closeQuietly(out);
        }
    }
}
