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

import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collection;
import java.util.Date;

/**
 * Data transfer object to pass uploaded file data across application.
 */
public class UploadedXmlFile {
    /**
     * File id in data storage.
     */
    private int id;
    /**
     * Uploaded file.
     */
    //todo file content @Size(min = 1)
    private UploadedFile file = new UploadedFile();
    /**
     * Xml schema name extracted during conversion.
     * @see eionet.webq.converter.MultipartFileConverter
     */
    @NotEmpty
    private String xmlSchema;
    /**
     * File upload date.
     */
    private Date created;
    /**
     * Last change date.
     */
    private Date updated;
    /**
     * Available conversions for this file.
     */
    private Collection<Conversion> availableConversions;

    public UploadedXmlFile(UploadedFile file, String xmlSchema) {
        this.file = file;
        this.xmlSchema = xmlSchema;
    }

    public UploadedXmlFile() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return file.getName();
    }

    public void setName(String name) {
        file.setName(name);
    }

    public byte[] getContent() {
        return file.getContent();
    }

    public void setContent(byte[] content) {
        file.setContent(content);
    }

    public long getSizeInBytes() {
        return file.getSizeInBytes();
    }

    public void setSizeInBytes(long sizeInBytes) {
        file.setSizeInBytes(sizeInBytes);
    }

    public String getXmlSchema() {
        return xmlSchema;
    }

    public void setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Collection<Conversion> getAvailableConversions() {
        return availableConversions;
    }

    public void setAvailableConversions(Collection<Conversion> availableConversions) {
        this.availableConversions = availableConversions;
    }

    @Override
    public String toString() {
        return "UploadedXmlFile{" + "id=" + id + ", file=" + file + ", xmlSchema='" + xmlSchema + '\''
                + ", created=" + created + ", updated=" + updated + '}';
    }
}
