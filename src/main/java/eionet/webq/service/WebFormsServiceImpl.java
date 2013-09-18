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

import eionet.webq.dao.WebFormStorage;
import eionet.webq.dao.orm.ProjectFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link eionet.webq.service.WebFormService} implementation.
 */
@Service
public class WebFormsServiceImpl implements WebFormService {
    /**
     * Web forms storage.
     */
    @Autowired
    WebFormStorage storage;

    @Override
    public Collection<ProjectFile> getAllActiveWebForms() {
        return storage.getAllActiveWebForms();
    }

    @Override
    public Collection<ProjectFile> findWebFormsForSchemas(Collection<String> xmlSchemas) {
        Collection<ProjectFile> allActiveWebForms = getAllActiveWebForms();
        if (CollectionUtils.isEmpty(xmlSchemas)) {
            return allActiveWebForms;
        }
        return filter(allActiveWebForms, xmlSchemas);
    }

    /**
     * Filters specified project files by required xml schemas.
     *
     * @param projectFiles project files
     * @param xmlSchemas xml schemas
     * @return collection of project files which belong to specified schemas.
     */
    private Collection<ProjectFile> filter(Collection<ProjectFile> projectFiles, Collection<String> xmlSchemas) {
        List<ProjectFile> filtered = new ArrayList<ProjectFile>();
        for (ProjectFile projectFile : projectFiles) {
            if (xmlSchemas.contains(projectFile.getXmlSchema())) {
                filtered.add(projectFile);
            }
        }
        return filtered;
    }
}
