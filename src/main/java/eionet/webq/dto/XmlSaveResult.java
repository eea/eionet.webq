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

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * Object for transferring XML save result messages to XForms. The object is marshaled into XML in HTTP response.
 *
 * @author Enriko Käsper
 */
@XmlRootElement(name = "saveresult")
public class XmlSaveResult {

    /** Result code: 0 - failed; 1 - successful save. */
    private int code;
    /** Save result message. */
    private String message;
    /** Timestamp of action took place. */
    private Date timestamp;

    /** Message to be returned in case of successful save action. */
    private static final String SUCCESS_MESSAGE = "Content saved successfully.";
    /** Message to be returned in case of error. */
    private static final String ERROR_MESSAGE = "Error on saving data! ";
    /**
     * Code to message mapping.
     */
    private static final Map<Integer, String> MESSAGES_BY_CODE = new HashMap<Integer, String>() {
        {
            put(0, ERROR_MESSAGE);
            put(1, SUCCESS_MESSAGE);
        }
    };

    /**
     * No-arg default constructor required by {@link org.springframework.oxm.jaxb.Jaxb2Marshaller}.
     */
    public XmlSaveResult() {
        super();
    }

    /**
     * Private constructor for generating object with correct data in fields.
     *
     * @param code save result code
     * @param message result message
     */
    private XmlSaveResult(int code, String message) {
        super();
        this.code = code;
        this.message = message;
        this.timestamp = new Date();
    }

    /**
     * Generates save result object with error code.
     *
     * @return the save result object
     */
    public static XmlSaveResult valueOfSuccess() {
        return new XmlSaveResult(1, SUCCESS_MESSAGE);
    }

    /**
     * Generates save result object with success code.
     *
     * @param message error reason
     * @return the save result object
     */
    public static XmlSaveResult valueOfError(String message) {
        return new XmlSaveResult(0, ERROR_MESSAGE.concat(" ").concat(message));
    }

    /**
     * Creates {@link eionet.webq.dto.XmlSaveResult} from encoded response.
     * Encoding format is code(first character, must be an integer) and message(all other characters).
     *
     * @param encodedResponse encoded response
     * @return XmlSaveResult
     */
    public static XmlSaveResult valueOf(String encodedResponse) {
        if (StringUtils.isEmpty(encodedResponse)) {
            return new XmlSaveResult(0, ERROR_MESSAGE + " No response from server.");
        }
        int responseCode = Character.getNumericValue(encodedResponse.charAt(0));
        return new XmlSaveResult(responseCode,
                defaultString(MESSAGES_BY_CODE.get(responseCode), ERROR_MESSAGE) + encodedResponse.substring(1));
    }

    /**
     * @return the code
     */
    @XmlElement(name = "code")
    public int getCode() {
        return code;
    }

    /**
     * @return the message
     */
    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    /**
     * @return the timestamp
     */
    @XmlElement(name = "timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

}
