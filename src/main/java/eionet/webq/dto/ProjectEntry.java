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

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * Data transfer object, represents project entry.
 */
public class ProjectEntry {
    /**
     * project id in storage.
     */
    private int id;
    /**
     * project entry id.
     * must be unique.
     */
    @Length(min = 1, max = 255)
    @Pattern(regexp = "[A-Za-z0-9-\\._~]*")
    private String projectId;
    /**
     * project entry description.
     */
    @Length(max = 2000)
    private String description;
    /**
     * project entry creation date.
     */
    private Date created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
