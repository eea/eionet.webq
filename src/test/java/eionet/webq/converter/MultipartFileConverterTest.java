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
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class MultipartFileConverterTest {

    @Test
    public void convertToUploadedFile() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<derogations xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xml:lang=\"en\" xsi:noNamespaceSchemaLocation=\"http://biodiversity.eionet.europa.eu/schemas/bernconvention/derogations.xsd\" country=\"\">" +
                "</derogations>";
        String originalFilename = "file.xml";
        MockMultipartFile xmlFileUpload = new MockMultipartFile("xmlFileUpload", originalFilename, MediaType.APPLICATION_XML_VALUE, xml.getBytes());

        MultipartFileConverter multipartFileConverter = new MultipartFileConverter();
        UploadedXmlFile xmlFile = multipartFileConverter.convert(xmlFileUpload);
        assertThat(xmlFile.getName(), equalTo(originalFilename));
        assertThat(xmlFile.getFileContent(), equalTo(xml.getBytes()));
        assertThat(xmlFile.getXmlSchema(), equalTo("http://biodiversity.eionet.europa.eu/schemas/bernconvention/derogations.xsd"));
    }
}
