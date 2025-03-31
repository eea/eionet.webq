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
package eionet.webq.dao.orm;

import eionet.webq.dto.Conversion;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

/**
 * Data transfer object to pass uploaded file data across application.
 */
@Entity
@Table(name = "user_xml")
public class UserFile {
    /**
     * File id in data storage.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * User id.
     */
    @Column(name = "user_id", updatable = false)
    private String userId;
    /**
     * Uploaded file.
     */
    @Embedded
    private UploadedFile file = new UploadedFile();
    /**
     * Xml schema name extracted during conversion.
     *
     * @see eionet.webq.converter.MultipartFileToUserFileConverter
     */
    @NotEmpty
    @Column(name = "xml_schema")
    private String xmlSchema;
    /**
     * Is this file origin is CDR?
     */
    @Column(name = "cdr_file")
    private boolean fromCdr;
    /**
     * Cdr envelope URL.
     */
    @Column
    private String envelope;
    /**
     * Cdr authorization.
     */
    @Column
    private String authorization;
    /**
     * Is request authorized in CDR.
     */
    @Column
    private boolean authorized;
    /**
     * Cdr cookies.
     */
    @Column
    private String cookies;
    /**
     * Whether restriction should be applied on this file.
     */
    @Transient
    private boolean applyRestriction;
    /**
     * Whether file is with restricted access.
     */
    @Transient
    private boolean restricted;
    /**
     * Xsl transformation required for this file.
     */
    @Transient
    private String conversionId;
    /**
     * File title.
     */
    @Column(name = "instance_title")
    private String title;

    /**
     * File upload date.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    private Date created;
    /**
     * Last change date.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated = new Date();
    /**
     * User agent on upload time.
     */
    @Column(name = "user_agent")
    private String userAgent;
    /**
     * Last download date.
     */
    private Date downloaded;

    /**
     * Available conversions for this file.
     */
    @Transient
    private Collection<Conversion> availableConversions;

    /**
     * Shorthand for file uploaded by user creation.
     *
     * @param file      uploaded file
     * @param xmlSchema xml schema extracted from file
     */
    public UserFile(UploadedFile file, String xmlSchema) {
        this.file = file;
        this.xmlSchema = xmlSchema;
    }

    /**
     * Empty constructor for instantiation by reflection.
     */
    public UserFile() {
    }

    /**
     * Get user file ID.
     *
     * @return user file unique ID
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return file.getName();
    }

    /**
     * Set file name for embedded {@link UploadedFile}.
     *
     * @param name file name
     */
    public void setName(String name) {
        file.setName(name);
    }

    /**
     * Returns content or null.
     *
     * @return content or null.
     */
    public byte[] getContent() {
        UploadedFile.FileContent content = file.getContent();
        if (content == null) {
            return null;
        }
        return content.getFileContent();
    }

    /**
     * Set file content for embedded {@link UploadedFile}.
     *
     * @param content file content.
     */
    public void setContent(byte[] content) {
        UploadedFile.FileContent fileContent = file.getContent();
        if (fileContent != null) {
            fileContent.setFileContent(content);
        } else {
            file.setContent(new UploadedFile.FileContent(content));
        }
        file.setSizeInBytes(content.length);
    }

    public long getSizeInBytes() {
        return file.getSizeInBytes();
    }

    public String getContentType() {
        return file.getContentType();
    }

    public String getXmlSchema() {
        return xmlSchema;
    }

    public void setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    public boolean isFromCdr() {
        return fromCdr;
    }

    public void setFromCdr(boolean fromCdr) {
        this.fromCdr = fromCdr;
    }

    public String getEnvelope() {
        return envelope;
    }

    public void setEnvelope(String envelope) {
        this.envelope = envelope;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public boolean isApplyRestriction() {
        return applyRestriction;
    }

    public void setApplyRestriction(boolean applyRestriction) {
        this.applyRestriction = applyRestriction;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(Date downloaded) {
        this.downloaded = downloaded;
    }

    public Collection<Conversion> getAvailableConversions() {
        return availableConversions;
    }

    public void setAvailableConversions(Collection<Conversion> availableConversions) {
        this.availableConversions = availableConversions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    @Override
    public String toString() {
        return "UserFile{" + "id=" + id + ", userId='" + userId + '\'' + ", file=" + file + ", xmlSchema='" + xmlSchema + '\''
                + ", fromCdr=" + fromCdr + ", envelope='" + envelope + '\'' + ", title='" + title + '\'' + ", created=" + created
                + ", updated=" + updated + ", downloaded=" + downloaded + ", availableConversions=" + availableConversions
                + ", authorized=" + authorized + '}';
    }
}
