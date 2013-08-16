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
package eionet.webq.dao;

import java.util.Collection;

/**
 * Common interface for file upload.
 *
 * @param <KEY> classifier for file.
 * @param <FILE> operates with this file type.
 */
public interface FileStorage<KEY, FILE> {

    /**
     * Saves file to storage.
     *
     * @param file file to save
     * @param key key to which this file will belong
     */
    void save(FILE file, KEY key);

    /**
     * Retrieve file data by id in storage without file content.
     * To get content consider usage of {@link FileStorage#fileContentBy(int, KEY)}
     *
     * @param id file id in storage
     * @return file data without file content
     */
    FILE fileById(int id);

    /**
     * Performs required updates to file data/content.
     * Data to be updated are implementation specific.
     *
     * @param file file to update
     * @param key key to which this file will belong
     */
    void update(FILE file, KEY key);

    /**
     * Retrieves all files for the key.
     *
     * @param key key for files classification
     * @return collection of files linked to this key
     */
    Collection<FILE> allFilesFor(KEY key);

    /**
     * Retrieve file name and content.
     *
     * @param key key to which this file belong
     * @param id file id
     * @return file
     */
    FILE fileContentBy(int id, KEY key);

    /**
     * Remove file by id and classifier.
     * @param id file id
     * @param key classifier
     */
    void remove(int id, KEY key);
}
