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

import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.dao.orm.UserFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Performs converting from {@link MultipartFile} to {@link eionet.webq.dao.orm.UserFile}.
 *
 * @see Converter
 */
public class MultipartFileConverter implements Converter<MultipartFile, UserFile> {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(MultipartFileConverter.class);
    /**
     * Converter to {@link UploadedFile}.
     */
    @Autowired
    private MultipartFileToUploadedFile toUploadedFileConverter;
    /**
     * Xml schema extractor.
     */
    @Autowired
    private XmlSchemaExtractor xmlSchemaExtractor;

    @Override
    public UserFile convert(MultipartFile multipartFile) {
        UploadedFile uploadedFile = toUploadedFileConverter.convert(multipartFile);
        LOGGER.info("Converting " + uploadedFile);
        return new UserFile(uploadedFile, xmlSchemaExtractor.extractXmlSchema(uploadedFile.getContent().getFileContent()));
    }
}
