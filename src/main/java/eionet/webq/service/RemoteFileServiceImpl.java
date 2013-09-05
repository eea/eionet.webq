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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import java.util.zip.CRC32;

/**
 * {@link eionet.webq.service.RemoteFileService} implementation.
 */
@Service
public class RemoteFileServiceImpl implements RemoteFileService {
    /**
     * Used for file download.
     */
    @Autowired
    RestOperations downloader;

    @Override
    public byte[] fileContent(String remoteFileUrl) throws FileNotAvailableException {
        ResponseEntity<byte[]> download = downloader.getForEntity(remoteFileUrl, byte[].class);
        if (download.getStatusCode() != HttpStatus.OK || !download.hasBody()) {
            throw new FileNotAvailableException("Response is not OK or body not attached for " + remoteFileUrl);
        }
        return download.getBody();
    }

    @Override
    public boolean isChecksumMatches(byte[] localFile, String remoteFileUrl) throws FileNotAvailableException {
        byte[] remoteFile = fileContent(remoteFileUrl);
        return crc32Checksum(localFile) == crc32Checksum(remoteFile);
    }

    /**
     * Calculates crc32 checksum.
     * @see java.util.zip.CRC32
     *
     * @param bytes bytes to calculate checksum.
     * @return checksum
     */
    private long crc32Checksum(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }
}
