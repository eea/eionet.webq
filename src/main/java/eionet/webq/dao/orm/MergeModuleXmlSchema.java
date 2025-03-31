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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 */
@Entity
@Table(name = "merge_module_xml_schema")
public class MergeModuleXmlSchema {
    /**
     * Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * Xml schema.
     */
    @Pattern(regexp = "^((http://)|(https://)).*")
    @NotNull
    @Column(name = "xml_schema")
    private String xmlSchema;

    /**
     * Empty constructor for reflection.
     */
    public MergeModuleXmlSchema() {
    }

    /**
     * Shorthand for xml schema creation.
     *
     * @param xmlSchema xml schema.
     */
    public MergeModuleXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXmlSchema() {
        return xmlSchema;
    }

    public void setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
    }
}
