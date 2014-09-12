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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.EnumUtils;

/**
 * Class represents conversion for xml schema.
 */
@XmlRootElement(name = "conversion")
public class Conversion {
    /**
     * Basic types enum.
     */
    public static enum BASIC_TYPES {
        HTML, EXCEL, ODS, CSV, TXT
    };

    /**
     * Conversion id or xsl name.
     */
    @XmlElement(name = "convert_id")
    private String id;
    /**
     * Description of this conversion.
     */
    @XmlElement(name = "description")
    private String description;

    /**
     * Conversion result type (HTML, TXT, ...).
     */
    @XmlElement(name = "result_type")
    private String resultType;

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getResultType() {
        return resultType;
    }

    public boolean isBasic() {
        return EnumUtils.isValidEnum(BASIC_TYPES.class, this.resultType);
    }
}
