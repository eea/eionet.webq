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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;

/**
 * Converter for performing bi-directional conversion between XML and json.
 */
@Component
public class JsonXMLBidirectionalConverter {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(JsonXMLBidirectionalConverter.class);

    /**
     * Converts xml to json.
     *
     * @param xml xml as byte array.
     * @return json as byte array.
     */
    public byte[] convertXmlToJson(byte[] xml) {
        JsonXMLConfig config = new JsonXMLConfigBuilder()
                .prettyPrint(true)
                .autoArray(true)
                .autoPrimitive(true)
                .build();
        XMLInputFactory reader = XMLInputFactory.newInstance();
        JsonXMLOutputFactory writer = new JsonXMLOutputFactory(config);
        return convert(reader, writer, xml);
    }

    /**
     * Converts json to xml.
     *
     * @param json json as byte array.
     * @return xml as byte array.
     */
    public byte[] convertJsonToXml(byte[] json) {
        JsonXMLConfig config = new JsonXMLConfigBuilder()
            .prettyPrint(true)
            .multiplePI(false)
            .build();
        XMLInputFactory reader = new JsonXMLInputFactory(config);
        XMLOutputFactory writer = XMLOutputFactory.newInstance();
        return convert(reader, writer, json);
    }

    /**
     * Template for conversion.
     *
     * @param inputFactory input factory.
     * @param outputFactory output factory.
     * @param source source to convert.
     * @return conversion result as byte array.
     */
    private byte[] convert(XMLInputFactory inputFactory, XMLOutputFactory outputFactory,
                           byte[] source) {
        InputStream input = new ByteArrayInputStream(source);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            XMLEventReader reader = inputFactory.createXMLEventReader(input);
            XMLEventWriter writer = outputFactory.createXMLEventWriter(output, "utf-8");
            writer = new PrettyXMLEventWriter(writer);
            writer.add(reader);
            closeQuietly(reader, writer);
            return output.toByteArray();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * Closes quietly reader and writer.
     * @param reader xml event reader
     * @param writer xml event writer
     */
    private void closeQuietly(XMLEventReader reader, XMLEventWriter writer) {
        try {
            reader.close();
        } catch (XMLStreamException e) {
            LOGGER.warn("Unable to close XMLEventReader", e);
        }
        try {
            writer.close();
        } catch (XMLStreamException e) {
            LOGGER.warn("Unable to close XMLEventWriter", e);
        }
    }
}
