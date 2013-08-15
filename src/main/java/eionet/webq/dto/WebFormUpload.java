package eionet.webq.dto;

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

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * DTO for web form upload.
 */
public class WebFormUpload extends UploadForm {
    /**
     * Auto generated id.
     */
    private int id;
    /**
     * Form title.
     */
    private String title;
    /**
     * Form content.
     */
    @Size(min = 1)
    private byte[] file;
    /**
     * details about the webform
     */
    private String description;
    /**
     * form status.
     */
    private boolean active;
    /**
     * in case XML Schema has several webforms attached, then it is possible to edit XML file only with main webforms.
     */
    private boolean mainForm;
    /**
     * User name of user who uploaded web form.
     */
    private String userName;
    /**
     * timestamp of first upload of the file
     */
    private Date created;
    /**
     * timestamp of last update of the file
     */
    private Date updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
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

    public boolean isMainForm() {
        return mainForm;
    }

    public void setMainForm(boolean mainForm) {
        this.mainForm = mainForm;
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
