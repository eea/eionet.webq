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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Class represents response of conversion service.
 */
@XmlRootElement(name = "response")
public class ListConversionResponse {
    /**
     * Http response code. 200 - OK.
     */
    @XmlAttribute(name = "code")
    private int code;

    /**
     * List of conversions available.
     */
    private List<Conversion> conversions;

    public List<Conversion> getConversions() {
        return conversions;
    }
    @XmlElement(name = "conversion")
    public void setConversions(List<Conversion> conversions) {
        this.conversions = conversions;
    }

    public int getCode() {
        return code;
    }
}
