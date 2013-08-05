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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import eionet.webq.dto.UploadedXmlFile;
import eionet.webq.exception.WebQuestionnaireException;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileConverterTest {
    private MultipartFileConverter fileConverter = new MultipartFileConverter();
    private final String originalFilename = "file.xml";

    @Test
    public void convertToUploadedFile() throws Exception {
        String schemaLocation = "testSchema";
        String rootAttributesDeclaration = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + noNamespaceSchemaAttribute(schemaLocation);
        byte[] fileContent = xmlWithRootElementAttributes(rootAttributesDeclaration);
        MultipartFile xmlFileUpload = createMultipartFile(fileContent);

        UploadedXmlFile xmlFile = fileConverter.convert(xmlFileUpload);

        assertThat(xmlFile.getName(), equalTo(originalFilename));
        assertThat(xmlFile.getContent(), equalTo(fileContent));
        assertThat(xmlFile.getXmlSchema(), equalTo(schemaLocation));
        assertThat(xmlFile.getSizeInBytes(), equalTo(xmlFileUpload.getSize()));
    }

    @Test(expected = WebQuestionnaireException.class)
    public void throwsExceptionIfNamespaceXsiNotDeclared() {
        fileConverter.convert(createMultipartFile(xmlWithRootElementAttributes(noNamespaceSchemaAttribute("foo"))));
    }

    private String noNamespaceSchemaAttribute(String schemaLocation) {
        return "xsi:noNamespaceSchemaLocation=\"" + schemaLocation + "\"";
    }

    private MultipartFile createMultipartFile(byte[] content) {
        return new MockMultipartFile("xmlFileUpload", originalFilename, MediaType.APPLICATION_XML_VALUE, content);
    }

    private byte[] xmlWithRootElementAttributes(String rootAttributesDeclaration) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<derogations " + rootAttributesDeclaration + " >" +
                "</derogations>";
        return xml.getBytes();
    }
}
