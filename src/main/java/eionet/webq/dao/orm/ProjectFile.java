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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Generated;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * DTO for web form upload.
 */
@Entity
@Table(name = "project_file")
public class ProjectFile {
    /**
     * Auto generated id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * {@link eionet.webq.dao.orm.ProjectEntry#id} connected with this file.
     */
    @Column(name = "project_id", updatable = false)
    private int projectId;
    /**
     * Form title.
     */
    @NotEmpty
    private String title;
    /**
     * Uploaded file.
     */
    @Embedded
    private UploadedFile file = new UploadedFile();
    /**
     * Remote file location(typically VCS link).
     */
    @Column(name = "remote_file_url")
    private String remoteFileUrl;
    /**
     * New xml file name for web form.
     */
    @Column(name = "new_xml_file_name")
    private String newXmlFileName;
    /**
     * The location of empty instance XML file.
     */
    @Column(name = "empty_instance_url")
    private String emptyInstanceUrl;
    /**
     * details about the webform.
     */
    private String description;
    /**
     * xml schema connected with this file.
     */
    @Column(name = "xml_schema")
    private String xmlSchema;
    /**
     * form status.
     */
    private boolean active;
    /**
     * form is available for webq users.
     */
    @Column(name = "local_form")
    private boolean localForm;
    /**
     * form is available for remote usage(e.g. CDR).
     */
    @Column(name = "remote_form")
    private boolean remoteForm;

    /**
     * Project file type.
     */
    @Enumerated(value = EnumType.STRING)
    @Column(name = "file_type", updatable = false)
    private ProjectFileType fileType;
    /**
     * User name of user who uploaded web form.
     */
    @Column(name = "user_name")
    private String userName;
    /**
     * timestamp of first upload of the file.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Generated(org.hibernate.annotations.GenerationTime.INSERT)
    private Date created;
    /**
     * timestamp of last update of the file.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    /**
     * Link to open webform from UI.
     */
    @Transient
    private String webformLink;

    /**
     * Related project textual identifier.
     */
    @Transient
    private String projectIdentifier;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return file.getName();
    }

    /**
     * Set file name for embedded {@link UploadedFile}.
     *
     * @param fileName file name
     */
    public void setFileName(String fileName) {
        file.setName(fileName);
    }

    public long getFileSizeInBytes() {
        return file.getSizeInBytes();
    }

    public String getNewXmlFileName() {
        return newXmlFileName;
    }

    public void setNewXmlFileName(String newXmlFileName) {
        this.newXmlFileName = newXmlFileName;
    }

    public String getEmptyInstanceUrl() {
        return emptyInstanceUrl;
    }

    public void setEmptyInstanceUrl(String emptyInstanceUrl) {
        this.emptyInstanceUrl = emptyInstanceUrl;
    }

    /**
     * Gets file content.
     *
     * @return file content or null.
     */
    public byte[] getFileContent() {
        UploadedFile.FileContent content = file.getContent();
        if (content == null) {
            return null;
        } else {
            return content.getFileContent();
        }
    }

    /**
     * Set file content for embedded {@link UploadedFile}.
     *
     * @param fileContent file content.
     */
    public void setFileContent(byte[] fileContent) {
        UploadedFile.FileContent content = file.getContent();
        if (content == null) {
            file.setContent(new UploadedFile.FileContent(fileContent));
        } else {
            content.setFileContent(fileContent);
        }
        file.setSizeInBytes(fileContent.length);
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getRemoteFileUrl() {
        return remoteFileUrl;
    }

    public void setRemoteFileUrl(String remoteFileUrl) {
        this.remoteFileUrl = remoteFileUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isLocalForm() {
        return localForm;
    }

    public void setLocalForm(boolean localForm) {
        this.localForm = localForm;
    }

    public boolean isRemoteForm() {
        return remoteForm;
    }

    public void setRemoteForm(boolean remoteForm) {
        this.remoteForm = remoteForm;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getXmlSchema() {
        return xmlSchema;
    }

    public void setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    public ProjectFileType getFileType() {
        return fileType;
    }

    public void setFileType(ProjectFileType fileType) {
        this.fileType = fileType;
    }

    /**
     * @return the webformLink
     */
    public String getWebformLink() {
        return webformLink;
    }

    /**
     * @param webformLink the webformLink to set
     */
    public void setWebformLink(String webformLink) {
        this.webformLink = webformLink;
    }

    /**
     * @return the projectIdentifier
     */
    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    /**
     * @param projectIdentifier the projectIdentifier to set
     */
    public void setProjectIdentifier(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProjectFile{");
        sb.append("id=").append(id);
        sb.append(", projectId=").append(projectId);
        sb.append(", title='").append(title).append('\'');
        sb.append(", file=").append(file);
        sb.append(", newXmlFileName='").append(newXmlFileName).append('\'');
        sb.append(", emptyInstanceUrl='").append(emptyInstanceUrl).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", xmlSchema='").append(xmlSchema).append('\'');
        sb.append(", active=").append(active);
        sb.append(", localForm=").append(localForm);
        sb.append(", remoteForm=").append(remoteForm);
        sb.append(", fileType=").append(fileType);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", created=").append(created);
        sb.append(", updated=").append(updated);
        sb.append('}');
        return sb.toString();
    }
}
