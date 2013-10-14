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

import org.hibernate.annotations.Generated;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 */
@Entity
public class MergeModule {
    /**
     * Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    /**
     * Short unique name.
     */
    private String name;
    /**
     * Display title.
     */
    private String title;
    /**
     * Xml schemas this merge module can handle.
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "merge_module_id")
    private List<MergeModuleXmlSchema> xmlSchemas;
    /**
     * Xsl file to perform merging.
     */
    @Embedded
    private UploadedFile xslFile;
    /**
     * User who uploads xml file.
     */
    private String userName;
    /**
     * Timestamp when created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(insertable = false, updatable = false)
    @Generated(org.hibernate.annotations.GenerationTime.INSERT)
    private Date created;
    /**
     * Timestamp when last time updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MergeModuleXmlSchema> getXmlSchemas() {
        return xmlSchemas;
    }

    public void setXmlSchemas(List<MergeModuleXmlSchema> xmlSchemas) {
        this.xmlSchemas = xmlSchemas;
    }

    public UploadedFile getXslFile() {
        return xslFile;
    }

    public void setXslFile(UploadedFile xslFile) {
        this.xslFile = xslFile;
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
}
