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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 */
public class XmlSchemaExtractorTest {

    @Test
    public void extractNoNamespaceSchemaLocation() throws Exception {
        String inlineXml =
                "<MMRArticle17Questionnaire \n"
                        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n"
                        + "xsi:noNamespaceSchemaLocation=\"http://dd.eionet.europa.eu/schemas/mmr-article17/MMRArticle17Questionnaire.xsd\" "
                        + " year=\"2013\"> \n"
                        + "</MMRArticle17Questionnaire>\n";
        XmlSchemaExtractor schemaExtractor = new XmlSchemaExtractor();
        String schema = schemaExtractor.extractXmlSchema(inlineXml.getBytes("UTF-8"));
        String expected = "http://dd.eionet.europa.eu/schemas/mmr-article17/MMRArticle17Questionnaire.xsd";
        assertThat(expected, equalTo(schema));
    }

    @Test
    public void extractSingleSchemaLocationWithoutNamespace() throws Exception {
        String inlineXml =
                "<rdf:RDF \n"
                        + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" \n"
                        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n"
                        + "xsi:schemaLocation=\"http://www.w3.org/1999/02/22-rdf-syntax-ns# "
                        + "http://dd.eionet.europa.eu/schemas/Sense3-webtool.workingCopy/rdf.xsd\"> \n"
                        + "</rdf:RDF>\n";
        XmlSchemaExtractor schemaExtractor = new XmlSchemaExtractor();
        String schema = schemaExtractor.extractXmlSchema(inlineXml.getBytes("UTF-8"));
        String expected = "http://dd.eionet.europa.eu/schemas/Sense3-webtool.workingCopy/rdf.xsd";
        assertThat(expected, equalTo(schema));
    }

    @Test
    public void extractMultipleSchemaLocationsWithoutNamespace() throws Exception {
        String inlineXml =
                "<gml:FeatureCollection " + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xmlns:gml=\"http://www.opengis.net/gml/3.2\" " + "xmlns:aqd=\"http://aqd.ec.europa.eu/aqd/0.3.6b\" "
                        + "xmlns:swe=\"http://www.opengis.net/swe/2.0\" "
                        + "xsi:schemaLocation=\"http://aqd.ec.europa.eu/aqd/0.3.6b "
                        + "http://dd.eionet.europa.eu/schemas/id2011850eu/AirQualityReporting_0.3.6b.xsd \n\n"
                        + "http://www.opengis.net/swe/2.0  http://schemas.opengis.net/sweCommon/2.0/swe.xsd\">\n"
                        + "</gml:FeatureCollection>\n";
        XmlSchemaExtractor schemaExtractor = new XmlSchemaExtractor();
        String schema = schemaExtractor.extractXmlSchema(inlineXml.getBytes("UTF-8"));
        String expected =
                "http://dd.eionet.europa.eu/schemas/id2011850eu/AirQualityReporting_0.3.6b.xsd "
                        + "http://schemas.opengis.net/sweCommon/2.0/swe.xsd";
        assertThat(expected, equalTo(schema));
    }

    @Test
    public void noSchema() throws Exception {
        String inlineXml =
                "<MMRArticle17Questionnaire \n"
                        + " year=\"2013\"> \n"
                        + "</MMRArticle17Questionnaire>\n";
        XmlSchemaExtractor schemaExtractor = new XmlSchemaExtractor();
        String schema = schemaExtractor.extractXmlSchema(inlineXml.getBytes("UTF-8"));
        assertThat(schema, nullValue());
    }
}

