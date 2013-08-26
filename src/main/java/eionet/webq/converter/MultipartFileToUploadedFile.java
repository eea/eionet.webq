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

import eionet.webq.dto.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Convert {@link MultipartFile} to {@link UploadedFile}.
 */
@Component
public class MultipartFileToUploadedFile implements Converter<MultipartFile, UploadedFile> {
    /**
     * Converter from {@link MultipartFile} to byte[].
     */
    @Autowired
    private MultipartToByteArray toByteArrayConverter;

    @Override
    public UploadedFile convert(MultipartFile source) {
        return new UploadedFile(source.getOriginalFilename(), toByteArrayConverter.convert(source));
    }
}
