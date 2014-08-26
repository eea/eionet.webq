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

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dao.orm.util.UserFileInfo;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import static eionet.webq.converter.MultipartFileToUserFileConverter.ZIP_ATTACHMENT_MEDIA_TYPE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class MultipartFileToUserFileConverterTest {
    @Autowired
    private MultipartFileToUserFileConverter fileConverter;
    private final String originalFilename = "file.xml";
    private static final String TEST_XML_FILES_ZIP = "src/test/resources/merge/xml_files.zip";
    private static final String BROKEN_TEST_XML_FILE_ZIP = "src/test/resources/merge/broken_xml.zip";

    @Test
    public void convertToUploadedFile() throws Exception {
        String schemaLocation = "testSchema";
        String rootAttributesDeclaration = rootAttributesDeclaration(noNamespaceSchemaAttribute(schemaLocation));
        byte[] fileContent = xmlWithRootElementAttributes(rootAttributesDeclaration);
        MultipartFile xmlFileUpload = createMultipartFile(fileContent);

        UserFile xmlFile = fileConverter.convert(xmlFileUpload).iterator().next();

        assertThat(xmlFile.getName(), equalTo(originalFilename));
        assertThat(xmlFile.getContent(), equalTo(fileContent));
        assertThat(xmlFile.getXmlSchema(), equalTo(schemaLocation));
        assertThat(xmlFile.getSizeInBytes(), equalTo(xmlFileUpload.getSize()));
    }

    @Test
    public void setXmlSchemaToNullIfUnableToRead() {
        UserFile result =
                fileConverter.convert(createMultipartFile(xmlWithRootElementAttributes(noNamespaceSchemaAttribute("foo"))))
                .iterator().next();
        assertNull(result.getXmlSchema());
    }

    @Test
    public void setXmlSchemaWithNamespace() throws Exception {
        String namespace = "namespace";
        String schemaLocation = "testSchema";
        UserFile result =
                fileConverter.convert(createMultipartFile(xmlWithRootElementAttributes(rootAttributesDeclaration(schemaAttribute(
                        namespace, schemaLocation))))).iterator().next();

        assertThat(result.getXmlSchema(), equalTo(schemaLocation));
    }

    @Test
    public void setsContentTypeFromMultipartFile() throws Exception {
        String expectedContentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        UserFile result = fileConverter.convert(createMultipartFile(expectedContentType, "attachment-content".getBytes()))
                .iterator().next();
        assertThat(result.getContentType(), equalTo(expectedContentType));
    }

    @Test
    public void whenConvertingMultipartFile_ifAttachmentTypeIsZipFile_unpackAllFiles() throws Exception {
        Collection<UserFile> files =
                fileConverter.convert(createMultipartFile(ZIP_ATTACHMENT_MEDIA_TYPE,
                        FileUtils.readFileToByteArray(new File(TEST_XML_FILES_ZIP))));

        verifyContentExtractedFromTestZipFile(files);
    }

    @Test
    public void whenConvertingMultipartFile_ifAttachmentFileNameExtensionIsZip_unpackAllFiles() throws Exception {
        Collection<UserFile> files =
                fileConverter.convert(new MockMultipartFile("xmlFileUpload", "file.zip",
                        "some-other-zip-attachment-content-type",
                        FileUtils.readFileToByteArray(new File(TEST_XML_FILES_ZIP))));

        verifyContentExtractedFromTestZipFile(files);
    }

    @Test
    public void whenConvertingMultipartFile_ifAttachmentTypeIsZipFileAndFilesAreCorrupt_setDummyXmlSchema() throws Exception {
        Collection<UserFile> files =
                fileConverter.convert(createMultipartFile(ZIP_ATTACHMENT_MEDIA_TYPE,
                        FileUtils.readFileToByteArray(new File(BROKEN_TEST_XML_FILE_ZIP))));

        assertThat(files.size(), equalTo(1));
        Iterator<UserFile> it = files.iterator();
        assertThat(it.next().getXmlSchema(), equalTo(UserFileInfo.DUMMY_XML_SCHEMA));
    }

    private void verifyContentExtractedFromTestZipFile(Collection<UserFile> files) {
        String expectedXmlSchema = "http://biodiversity.eionet.europa.eu/schemas/bernconvention/derogations.xsd";

        assertThat(files.size(), equalTo(2));
        Iterator<UserFile> it = files.iterator();
        assertThat(it.next().getXmlSchema(), equalTo(expectedXmlSchema));
        assertThat(it.next().getXmlSchema(), equalTo(expectedXmlSchema));
    }

    private String noNamespaceSchemaAttribute(String schemaLocation) {
        return "xsi:noNamespaceSchemaLocation=\"" + schemaLocation + "\"";
    }

    private String schemaAttribute(String namespace, String schemaLocation) {
        return "xsi:schemaLocation=\"" + namespace + " " + schemaLocation + "\"";
    }

    private String rootAttributesDeclaration(String schemaAttribute) {
        return "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + schemaAttribute;
    }

    private MultipartFile createMultipartFile(byte[] content) {
        return createMultipartFile(MediaType.APPLICATION_XML_VALUE, content) ;
    }
    
    private MultipartFile createMultipartFile(String contentType, byte[] content) {
        return new MockMultipartFile("xmlFileUpload", originalFilename, contentType, content);
    }

    private byte[] xmlWithRootElementAttributes(String rootAttributesDeclaration) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<derogations " + rootAttributesDeclaration + " >" +
                "</derogations>";
        return xml.getBytes();
    }
}
