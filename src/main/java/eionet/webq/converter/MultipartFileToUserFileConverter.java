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
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.ZipEntryCallback;
import org.zeroturnaround.zip.ZipUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;

import static eionet.webq.dao.orm.util.UserFileInfo.DUMMY_XML_SCHEMA;
import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * Performs converting from {@link MultipartFile} to {@link eionet.webq.dao.orm.UserFile}.
 *
 * @see Converter
 */
@Component
public class MultipartFileToUserFileConverter implements Converter<MultipartFile, Collection<UserFile>> {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(MultipartFileToUserFileConverter.class);
    /**
     * Zip file media type.
     */
    public static final String ZIP_ATTACHMENT_MEDIA_TYPE = "application/zip";
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
    public Collection<UserFile> convert(MultipartFile multipartFile) {
        UploadedFile uploadedFile = toUploadedFileConverter.convert(multipartFile);
        //TODO zip extension check?
        if (ZIP_ATTACHMENT_MEDIA_TYPE.equals(uploadedFile.getContentType())) {
            return extractFromZip(uploadedFile);
        }
        LOGGER.info("Converting " + uploadedFile);
        return Arrays.asList(new UserFile(uploadedFile, xmlSchemaExtractor.extractXmlSchema(uploadedFile.getContent().getFileContent())));
    }

    /**
     * Extract files from zip archive, not recursive.
     *
     * @param uploadedFile uploaded zip file
     * @return collection of zip files.
     */
    private Collection<UserFile> extractFromZip(UploadedFile uploadedFile) {
        final List<UserFile> userFiles = new ArrayList<UserFile>();
        ZipUtil.iterate(new ByteArrayInputStream(uploadedFile.getContent().getFileContent()), new ZipEntryCallback() {
            @Override
            public void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
                if (!zipEntry.isDirectory()) {
                    byte[] content = IOUtils.toByteArray(inputStream);
                    String xmlSchema = defaultString(xmlSchemaExtractor.extractXmlSchema(content), DUMMY_XML_SCHEMA);
                    userFiles.add(new UserFile(new UploadedFile(zipEntry.getName(), content), xmlSchema));
                }
            }
        });
        return userFiles;
    }
}
