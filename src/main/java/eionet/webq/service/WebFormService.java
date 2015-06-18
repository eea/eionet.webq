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
 *        Raptis Dimos
 */
package eionet.webq.service;

import java.util.Collection;

import eionet.webq.dao.orm.ProjectFile;
import java.util.List;

/**
 * Operations with webForms.
 */
public interface WebFormService {
    /**
     * Returns all active web forms from storage.
     *
     * @return collection of web forms.
     */
    Collection<ProjectFile> getAllActiveWebForms();

    /**
     * Find active web form by id.
     *
     * @param id form id
     * @return web form
     */
    ProjectFile findActiveWebFormById(int id);

    /**
     * Finds all web forms for schemas.
     * All returned web forms are active.
     *
     * @param xmlSchemas xml schemas.
     * @return collection of web forms found.
     */
    Collection<ProjectFile> findWebFormsForSchemas(Collection<String> xmlSchemas);

    /**
     * Find web form by id.
     *
     * @param id form id
     * @return web form
     */
    ProjectFile findWebFormById(int id);
    
    /**
     * Sorts a set of webforms by their title
     * @param webforms
     * @return list of sorted webforms
     */
    List<ProjectFile> sortWebformsAlphabetically(Collection<ProjectFile> webforms);
    
}
