package eionet.webq.converter;

import eionet.webq.model.UploadedXmlFile;
import org.apache.commons.io.IOUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFileConverter implements Converter<MultipartFile, UploadedXmlFile> {

    @Override
    public UploadedXmlFile convert(MultipartFile multipartFile) {
        UploadedXmlFile uploadedXmlFile = new UploadedXmlFile();
        InputStream inputStream = toInputStream(multipartFile);
        String fileContent = fileContent(inputStream);
        uploadedXmlFile.setFileContent(fileContent);
        uploadedXmlFile.setName(multipartFile.getOriginalFilename());
        uploadedXmlFile.setXmlSchema(extractXmlSchema(inputStream));
        return uploadedXmlFile;
    }

    private String extractXmlSchema(InputStream stream) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        XPathFactory xPathFactory = XPathFactory.newInstance();
        try {
            stream.reset();
            Document xml = builderFactory.newDocumentBuilder().parse(stream);
            return xPathFactory.newXPath().evaluate("//@xsi:noNamespaceSchemaLocation", xml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String fileContent(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to transform xml file to string", e);
        }
    }

    private InputStream toInputStream(MultipartFile multipartFile) {
        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
