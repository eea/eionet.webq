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

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import eionet.webq.dao.WebFormStorage;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dto.WebFormType;

/**
 */
public abstract class AbstractWebFormsService implements WebFormService {
    /**
     * Web form storage.
     */
    @Autowired
    private WebFormStorage webFormStorage;

    @Override
    public Collection<ProjectFile> findWebFormsForSchemas(Collection<String> xmlSchemas) {
        if (CollectionUtils.isEmpty(xmlSchemas)) {
            return getAllActiveWebForms();
        }
        return webFormStorage.findWebFormsForSchemas(webFormsForType(), xmlSchemas);
    }

    @Override
    public Collection<ProjectFile> getAllActiveWebForms() {
        return webFormStorage.getAllActiveWebForms(webFormsForType());
    }

    @Override
    public ProjectFile findActiveWebFormById(int id) {
        return webFormStorage.getActiveWebFormById(webFormsForType(), id);
    }

    @Override
    public ProjectFile findWebFormById(int id) {
        return webFormStorage.getWebFormById(id);
    }

    /**
     * Specifies type for web forms to be queried.
     *
     * @return web form type.
     */
    protected abstract WebFormType webFormsForType();
}
