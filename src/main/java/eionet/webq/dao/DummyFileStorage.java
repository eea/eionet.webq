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
package eionet.webq.dao;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository("dummy")
public class DummyFileStorage implements FileStorage {

    private static final Map<String, File> FILES = new HashMap<String, File>();

    @Override
    public void save(MultipartFile file) {
        try {
            File tempFile = File.createTempFile(file.getOriginalFilename(), "");
            tempFile.deleteOnExit();
            file.transferTo(tempFile);
            FILES.put(file.getOriginalFilename(), tempFile);
        } catch (IOException e) {
            System.out.println("unable to create temporary file\n" + e);
        }
    }

    @Override
    public File getByFilename(String fileName) {
        return FILES.get(fileName);
    }

    @Override
    public Collection<String> allUploadedFiles() {
        return FILES.keySet();
    }
}
