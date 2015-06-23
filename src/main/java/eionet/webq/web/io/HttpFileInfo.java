package eionet.webq.web.io;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public final class HttpFileInfo {

    private String name;
    private byte[] content;
    private long lastModifiedMillis;

    public HttpFileInfo() { }
    
    public HttpFileInfo(String name, byte[] content) {
        this();
        this.setName(name);
        this.setContent(content);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getLastModifiedMillis() {
        return lastModifiedMillis;
    }

    public void setLastModifiedMillis(long lastModifiedMillis) {
        this.lastModifiedMillis = lastModifiedMillis;
    }

}
