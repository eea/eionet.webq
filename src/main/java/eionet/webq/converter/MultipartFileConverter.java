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

import eionet.webq.dto.UploadedFile;
import eionet.webq.dto.UserFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

/**
 * Performs converting from {@link MultipartFile} to {@link eionet.webq.dto.UserFile}.
 *
 * @see Converter
 */
public class MultipartFileConverter implements Converter<MultipartFile, UserFile> {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(MultipartFileConverter.class);
    /**
     * Xsi namespace URI.
     */
    private static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";
    @Autowired
    private MultipartFileToUploadedFile toUploadedFileConverter;

    @Override
    public UserFile convert(MultipartFile multipartFile) {
        UploadedFile uploadedFile = toUploadedFileConverter.convert(multipartFile);
        LOGGER.info("Converting " + uploadedFile);
        return new UserFile(uploadedFile, extractXmlSchema(uploadedFile.getContent()));
    }

    /**
     * Extracts {@code @xsi:noNamespaceSchemaLocation} or {@code @xsi:schemaLocation} attribute value from xml root element.
     *
     * @param bytes uploaded file bytes
     * @return {@code @xsi:noNamespaceSchemaLocation} or {@code @xsi:schemaLocation} attribute value
     */
    private String extractXmlSchema(byte[] bytes) {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            xmlStreamReader = xmlInputFactory.createXMLStreamReader(bais);
            while (xmlStreamReader.hasNext()) {
                if (xmlStreamReader.next() == START_ELEMENT) {
                    return StringUtils.defaultString(
                            xmlStreamReader.getAttributeValue(XSI_NAMESPACE_URI, "noNamespaceSchemaLocation"),
                            xmlStreamReader.getAttributeValue(XSI_NAMESPACE_URI, "schemaLocation"));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("exception thrown during extracting xml schema", e);
        } finally {
            IOUtils.closeQuietly(bais);
            if (xmlStreamReader != null) {
                try {
                    xmlStreamReader.close();
                } catch (XMLStreamException e) {
                    LOGGER.warn("unable to close xml stream", e);
                }
            }
        }
        return null;
    }
}
