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
package eionet.webq.service;

import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.CdrRequest;
import eionet.webq.dto.XmlSaveResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.net.URISyntaxException;

/**
 */
public interface CDREnvelopeService {
    /**
     * Gets xml files data for given parameters.
     *
     * @param parameters parameters
     * @return xml files grouped by xml schema
     */
    MultiValueMap<String, XmlFile> getXmlFiles(CdrRequest parameters);

    /**
     * Allows to push xml file content to cdr.
     *
     * @param file user file
     * @return save result
     */
    XmlSaveResult pushXmlFile(UserFile file);

    /**
     * Download file from CDR using authorisation info stored in UserFile object.
     *
     * @param file          UserFile holding auth info
     * @param remoteFileUrl remote file to download
     * @return remote file as byte array
     * @throws FileNotAvailableException file not found
     */
    public ResponseEntity<byte[]> fetchFileFromCdr(UserFile file, String remoteFileUrl)
            throws FileNotAvailableException, URISyntaxException;

    public String submitRequest(UserFile file, String uri, String body) throws URISyntaxException;

    /**
     * Create HTTP request header with auth attributes for CDR requests.
     *
     * @param file user file containing authorization properties
     * @return HttpHeaders with auth info
     */
    HttpHeaders getAuthorizationHeader(UserFile file);

    /**
     * Envelope service xml file data.
     */
    public final class XmlFile {
        /**
         * File full name.
         */
        private String fullName;
        /**
         * File title.
         */
        private String title;

        /**
         * Constructs file.
         *
         * @param fullName file full name.
         * @param title    file title.
         */
        public XmlFile(String fullName, String title) {
            this.fullName = fullName;
            this.title = title;
        }

        public String getFullName() {
            return fullName;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return "XmlFile{" + "fullName='" + fullName + '\'' + ", title='" + title + '\'' + '}';
        }
    }
}
