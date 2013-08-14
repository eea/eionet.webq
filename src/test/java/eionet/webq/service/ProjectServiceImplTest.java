package eionet.webq.service;

import eionet.webq.dao.ProjectFolders;
import eionet.webq.dto.ProjectEntry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
public class ProjectServiceImplTest {
    private ProjectService service;
    private ProjectFolders folders;


    @Before
    public void createService() {
        ProjectServiceImpl projectService = new ProjectServiceImpl();
        service = projectService;
        folders = Mockito.mock(ProjectFolders.class);
        projectService.folders = folders;
    }

    @Test
    public void allowToRemoveProject() throws Exception {
        String projectId = "projectId";
        service.remove(projectId);

        verifyOnly().remove(projectId);
    }

    @Test
    public void allowToFetchAllProject() throws Exception {
        when(folders.getAllFolders()).thenReturn(Collections.<ProjectEntry>emptyList());
        service.getAllFolders();

        verifyOnly().getAllFolders();
    }

    @Test
    public void allowToGetById() throws Exception {
        String projectId = "projectId";
        when(folders.getByProjectId(projectId)).thenReturn(new ProjectEntry());

        service.getByProjectId(projectId);

        verifyOnly().getByProjectId(projectId);
    }

    @Test
    public void updateProjectIfStorageIdIsSet() throws Exception {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setId(1);

        service.saveOrUpdate(projectEntry);

        verifyOnly().update(projectEntry);
    }

    @Test
    public void addNewProjectIfStorageIdIsNotSet() throws Exception {
        ProjectEntry projectEntry = new ProjectEntry();
        service.saveOrUpdate(projectEntry);

        verifyOnly().save(projectEntry);
    }

    private ProjectFolders verifyOnly() {
        return verify(folders, only());
    }
}
