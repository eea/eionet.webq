package eionet.webq.service.impl.project.export;

/**
 * Descriptor of archived entries.
 * 
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
public final class ArchiveFile {

    private String name;
    private byte[] content;

    public ArchiveFile() { }
    
    public ArchiveFile(String name, byte[] content) {
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
    
}
