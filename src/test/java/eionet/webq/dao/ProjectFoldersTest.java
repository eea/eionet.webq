package eionet.webq.dao;

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dto.ProjectEntry;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import util.ProjectFoldersCleaner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class ProjectFoldersTest {
    @Autowired
    private ProjectFolders folders;
    @Autowired
    private ProjectFoldersCleaner cleaner;

    @After
    public void removeAllProjectEntries() {
        cleaner.removeAllProjects();
    }

    @Test
    public void allowToSaveProjectDataWithoutException() throws Exception {
        folders.save(projectEntry("id"));
    }

    @Test
    public void returnsEmptyCollectionIfNoFoldersInStorage() throws Exception {
        assertThatAllFoldersSizeIs(0);
    }

    @Test
    public void allowToRetrieveSavedProjectDataFromStorage() throws Exception {
        folders.save(projectEntry("myId"));

        assertThatAllFoldersSizeIs(1);
    }

    @Test(expected = DuplicateKeyException.class)
    public void saveWithSameIdCausesException() throws Exception {
        folders.save(projectEntry("myId"));
        folders.save(projectEntry("myId"));
    }

    @Test
    public void multipleProjectEntriesCanBeSavedToDb() throws Exception {
        folders.save(projectEntry("1"));
        folders.save(projectEntry("2"));
        folders.save(projectEntry("3"));

        assertThatAllFoldersSizeIs(3);
    }

    @Test
    public void allowsToRemoveProjectEntryByProjectId() throws Exception {
        String projectId = "projectToRemove";
        folders.save(projectEntry(projectId));
        assertThatAllFoldersSizeIs(1);

        folders.remove(projectId);
        assertThatAllFoldersSizeIs(0);
    }

    private void assertThatAllFoldersSizeIs(int count) {
        assertThat(folders.getAllFolders().size(), equalTo(count));
    }

    private ProjectEntry projectEntry(String id) {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setProjectId(id);
        projectEntry.setDescription("description");
        return projectEntry;
    }
}
