package eionet.webq.service;

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

import eionet.webq.dao.ProjectFolders;
import eionet.webq.dto.ProjectEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Project service implementation.
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    /**
     * Project folders access in storage.
     */
    @Autowired
    ProjectFolders folders;

    @Override
    public Collection<ProjectEntry> getAllFolders() {
        return folders.getAllFolders();
    }

    @Override
    public void remove(String projectId) {
        folders.remove(projectId);
    }

    @Override
    public ProjectEntry getByProjectId(String projectId) {
        return folders.getByProjectId(projectId);
    }

    @Override
    public void saveOrUpdate(ProjectEntry projectEntry) {
        if (projectEntry.getId() > 0) {
            folders.update(projectEntry);
        } else {
            folders.save(projectEntry);
        }
    }
}
