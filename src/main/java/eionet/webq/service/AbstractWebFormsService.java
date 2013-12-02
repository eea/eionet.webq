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

import eionet.webq.dao.orm.ProjectFile;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 */
public abstract class AbstractWebFormsService implements WebFormService {

    @Override
    public Collection<ProjectFile> findWebFormsForSchemas(Collection<String> xmlSchemas) {
        if (CollectionUtils.isEmpty(xmlSchemas)) {
            return getAllActiveWebForms();
        }
        return findWebFormsForNotEmptyXmlSchemas(xmlSchemas);
    }

    /**
     * Performs search for project files when provided collection of xml schemas is not empty.
     *
     * @param xmlSchemas xml schemas
     * @return web forms belonging to provided schemas
     */
    protected abstract Collection<ProjectFile> findWebFormsForNotEmptyXmlSchemas(Collection<String> xmlSchemas);
}
