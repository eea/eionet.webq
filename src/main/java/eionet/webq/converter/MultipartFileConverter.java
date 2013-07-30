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

import eionet.webq.model.UploadedXmlFile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
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
        try {
            uploadedXmlFile.setFileContent(multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        uploadedXmlFile.setName(multipartFile.getOriginalFilename());
        uploadedXmlFile.setXmlSchema(extractXmlSchema(toInputStream(multipartFile)));
        return uploadedXmlFile;
    }

    private String extractXmlSchema(InputStream stream) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        XPathFactory xPathFactory = XPathFactory.newInstance();
        try {
            Document xml = builderFactory.newDocumentBuilder().parse(stream);
            return xPathFactory.newXPath().evaluate("//@xsi:noNamespaceSchemaLocation", xml);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
