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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.CRC32;

/**
 * {@link eionet.webq.service.RemoteFileService} implementation.
 */
@Service
public class RemoteFileServiceImpl implements RemoteFileService {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(RemoteFileServiceImpl.class);
    /**
     * Used for file download.
     */
    @Autowired
    RestOperations downloader;

    @Override
    public byte[] fileContent(String remoteFileUrl) throws FileNotAvailableException {

        if (remoteFileUrl == null) {
            throw new FileNotAvailableException("Remote file URL is not defined.");
        }
        else if (remoteFileUrl != null && remoteFileUrl.startsWith("file://")) {
            return readFilesystemFile(remoteFileUrl);
        } else {
            ResponseEntity<byte[]> download = null;
            try {
                download = downloader.getForEntity(new URI(remoteFileUrl), byte[].class);
            } catch (RestClientException e) {
                LOGGER.error("Unable to download remote file.", e);
            } catch (URISyntaxException e) {
                LOGGER.error("Remote file URI is invalid: " + remoteFileUrl, e);
            }
            if (download == null || !download.hasBody()) {
                throw new FileNotAvailableException("Response is not OK or body not attached for " + remoteFileUrl);
            }
            return download.getBody();
        }
    }

    @Override
    public boolean isChecksumMatches(byte[] localFile, String remoteFileUrl) throws FileNotAvailableException {
        byte[] remoteFile = fileContent(remoteFileUrl);
        return crc32Checksum(localFile) == crc32Checksum(remoteFile);
    }

    /**
     * Calculates crc32 checksum.
     *
     * @param bytes bytes to calculate checksum.
     * @return checksum
     * @see java.util.zip.CRC32
     */
    private long crc32Checksum(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }

    /**
     * Read file content from file system. The fileUri has to start with "file://" prefix.
     *
     * @param fileUri file location in file system in form of URI.
     * @return file content as bytes.
     * @throws FileNotAvailableException if file is not available as local resource or fileUri is not valid URI.
     */
    private byte[] readFilesystemFile(String fileUri) throws FileNotAvailableException {
        try {
            return FileUtils.readFileToByteArray(new File(new URI(fileUri)));
        } catch (IOException e) {
            throw new FileNotAvailableException("The file is not available at: " + fileUri + "." + e.toString());
        } catch (URISyntaxException e) {
            throw new FileNotAvailableException("Incorrect file URI: " + fileUri + "." + e.toString());
        } catch (Exception e) {
            throw new FileNotAvailableException("Illegal file URI argument: " + fileUri + "." + e.toString());
        }
    }
}
