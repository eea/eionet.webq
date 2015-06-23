package eionet.webq.web.io;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public final class HttpResponseZipWriter extends HttpResponseFileWriterBase {

    @Override
    protected MediaType getMediaType() {
        return MediaType.APPLICATION_ZIP;
    }

    @Override
    protected Disposition getDisposition() {
        return Disposition.ATTACHMENT;
    }

    @Override
    protected boolean isCachingEnabled() {
        return false;
    }

    @Override
    protected String getCharacterSetName() {
        return "UTF-8";
    }
    
}
