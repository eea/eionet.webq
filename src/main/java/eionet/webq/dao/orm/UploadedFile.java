/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Questionnaires 2
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Anton Dmitrijev
 */
package eionet.webq.dao.orm;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Class represents uploaded file.
 */
@Embeddable
public class UploadedFile {
    /**
     * File name.
     */
    @Column(name = "file_name", updatable = false)
    private String name;
    /**
     * File content bytes.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "file_content_id")
    private FileContent fileContent;
    /**
     * File size in bytes.
     */
    @Column(name = "file_size_in_bytes")
    private long sizeInBytes;

    /**
     * Shorthand for uploaded file creation.
     * File size in bytes is taken from content length.
     *
     * @param name file name
     * @param content content
     */
    public UploadedFile(String name, byte[] content) {
        this.name = name;
        this.fileContent = new FileContent(content);
        this.sizeInBytes = content.length;
    }

    /**
     * Empty constructor for instantiation by reflexion.
     */
    public UploadedFile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileContent getContent() {
        return fileContent;
    }

    public void setContent(FileContent content) {
        this.fileContent = content;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    @Override
    public String toString() {
        return "UploadedFile{" + "name='" + name + '\'' + ", sizeInBytes=" + sizeInBytes + '}';
    }

    /**
     * FileContent entity.
     */
    @Entity
    @Table(name = "file_content")
    public static class FileContent {
        /**
         * Id.
         */
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private int id;
        /**
         * File content.
         */
        @Lob
        @Column(nullable = false)
        private byte[] fileContent;

        /**
         * Creates file content with content.
         *
         * @param fileContent file content bytes
         */
        public FileContent(byte[] fileContent) {
            this.fileContent = fileContent;
        }

        /**
         * Empty constructor for reflexion.
         */
        public FileContent() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public byte[] getFileContent() {
            return fileContent;
        }

        public void setFileContent(byte[] fileContent) {
            this.fileContent = fileContent;
        }
    }
}
