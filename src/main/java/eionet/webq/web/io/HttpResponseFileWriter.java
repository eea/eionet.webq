package eionet.webq.web.io;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public interface HttpResponseFileWriter {
    
    void writeFile(HttpServletResponse response, HttpFileInfo fileInfo);
}
