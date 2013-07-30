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
 *        Enriko Käsper
 */
package eionet.webq.dto;

import java.util.Date;

public class UploadedXmlFile {
    private int id;
    private String name;
    private byte[] fileContent;
    private String xmlSchema;
    private long fileSizeInBytes;
    private Date created;
    private Date updated;

    public int getId() {
        return id;
    }

    public UploadedXmlFile setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UploadedXmlFile setName(String name) {
        this.name = name;
        return this;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public UploadedXmlFile setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
        return this;
    }

    public long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public UploadedXmlFile setFileSizeInBytes(long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
        return this;
    }

    /**
     * @return the xmlSchema
     */
    public String getXmlSchema() {
        return xmlSchema;
    }

    /**
     * @param xmlSchema the xmlSchema to set
     */
    public UploadedXmlFile setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public UploadedXmlFile setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Date getUpdated() {
        return updated;
    }

    public UploadedXmlFile setUpdated(Date updated) {
        this.updated = updated;
        return this;
    }
}