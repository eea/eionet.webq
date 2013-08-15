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

import org.apache.log4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Performs converting from {@link MultipartFile} to byte array.
 *
 * @see Converter
 */
public class MultipartToByteArray implements Converter<MultipartFile, byte[]> {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(MultipartToByteArray.class);

    /**
     * Calls {@link org.springframework.web.multipart.MultipartFile#getBytes()} wrapping {@link IOException}.
     *
     * @param multipartFile uploaded file to be converted
     * @return uploaded file bytes.
     */
    @Override
    public byte[] convert(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            LOGGER.warn("unable to transform uploaded file to bytes", e);
            return new byte[0];
        }
    }
}
