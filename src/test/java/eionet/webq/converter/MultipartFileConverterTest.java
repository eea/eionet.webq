package eionet.webq.converter;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import eionet.webq.model.UploadedXmlFile;
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
        assertThat(xmlFile.getFileContent(), equalTo(xml));
        assertThat(xmlFile.getXmlSchema(), equalTo("http://biodiversity.eionet.europa.eu/schemas/bernconvention/derogations.xsd"));
    }
}
