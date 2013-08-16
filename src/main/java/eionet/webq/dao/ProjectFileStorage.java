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

import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.WebFormUpload;

import java.util.Collection;

/**
 * Interface for storing uploaded files.
 *
 * @see eionet.webq.dto.WebFormUpload
 */
//TODO common interface with UserFileStorage
public interface ProjectFileStorage {
    /**
     * Save uploaded file to storage.
     *
     * @param project project with which webform is associated
     * @param webFormUpload webform to be saved
     */
    void save(ProjectEntry project, WebFormUpload webFormUpload);

    /**
     * Updates webform.
     *
     * @param webFormUpload webform to update
     */
    void update(WebFormUpload webFormUpload);

    /**
     * Removes uploaded webform.
     *
     * @param fileId file id to remove
     */
    void remove(int fileId);

    /**
     * Return files for project.
     *
     * @param project project for which files would be retrieved.
     * @return all files for specified project
     */
    Collection<WebFormUpload> allFilesFor(ProjectEntry project);
}
