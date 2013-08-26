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
package eionet.webq.dto;

/**
 * Class represents uploaded file.
 */
public class UploadedFile {
    /**
     * File name.
     */
    private String name;
    /**
     * File content bytes.
     */
    private byte[] content;
    /**
     * File size in bytes.
     */
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
        this.content = content;
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    @Override
    public String toString() {
        return "UploadedFile{" + "name='" + name + '\'' + ", content="
                + (content != null ? content.length : null) + ", sizeInBytes=" + sizeInBytes + '}';
    }
}
