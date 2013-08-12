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
package eionet.webq.converter;

import eionet.webq.dto.UploadedXmlFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Performs converting from {@link MultipartFile} to {@link UploadedXmlFile}.
 * 
 * @see Converter
 */
public class MultipartFileConverter implements Converter<MultipartFile, UploadedXmlFile> {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(MultipartFileConverter.class);
    /**
     * Xsi namespace URI.
     */
    private static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";

    @Override
    public UploadedXmlFile convert(MultipartFile multipartFile) {
        byte[] bytes = toByteArray(multipartFile);
        return new UploadedXmlFile().setContent(bytes).setName(multipartFile.getOriginalFilename())
                .setSizeInBytes(multipartFile.getSize()).setXmlSchema(extractXmlSchema(bytes));
    }

    /**
     * Extracts {@code @xsi:noNamespaceSchemaLocation} or {@code @xsi:schemaLocation} attribute value from xml root element.
     *
     * @param bytes uploaded file bytes
     * @return {@code @xsi:noNamespaceSchemaLocation} or {@code @xsi:schemaLocation} attribute value
     */
    private String extractXmlSchema(byte[] bytes) {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(bytes));
            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.next() == XMLStreamConstants.START_ELEMENT) {
                    return StringUtils.defaultString(
                            xmlStreamReader.getAttributeValue(XSI_NAMESPACE_URI, "noNamespaceSchemaLocation"),
                            xmlStreamReader.getAttributeValue(XSI_NAMESPACE_URI, "schemaLocation"));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("exception thrown during extracting xml schema", e);
        }
        return null;
    }

    /**
     * Calls {@link org.springframework.web.multipart.MultipartFile#getBytes()} wrapping {@link IOException}.
     *
     * @param multipartFile uploaded file to be converted
     * @return uploaded file bytes.
     */
    private byte[] toByteArray(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            LOGGER.warn("unable to transform uploaded file to bytes", e);
            return new byte[0];
        }
    }
}
