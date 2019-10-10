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
 *        Enriko Käsper
 */

package eionet.webq.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.FileInfo;
import eionet.webq.service.ConversionService;

/**
 * Convert {@link UserFile} to {@link FileInfo}.
 *
 * @author Enriko Käsper
 */
@Component
public class UserFileToFileInfoConverter implements Converter<UserFile, FileInfo> {

    /** Injected service for asking conversions for given XML Schema. */
    @Autowired
    ConversionService conversionService;

    @Override
    public FileInfo convert(UserFile userFile) {

        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(userFile.getId());
        fileInfo.setSizeInBytes(userFile.getSizeInBytes());
        fileInfo.setCreatedDate(userFile.getCreated());
        fileInfo.setUpdatedDate(userFile.getUpdated());
        fileInfo.setDownloadedDate(userFile.getDownloaded());
        fileInfo.setLocalFile(!userFile.isFromCdr());

        if (userFile.getAvailableConversions() == null) {
            fileInfo.setConversions(conversionService.conversionsFor(userFile.getXmlSchema()));
        } else {
            fileInfo.setConversions(userFile.getAvailableConversions());
        }

        return fileInfo;
    }
}
