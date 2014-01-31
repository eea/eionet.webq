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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dao.orm.ProjectEntry;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class ProjectFoldersTest {
    @Autowired
    private ProjectFolders folders;

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

    @Test
    public void filesAreSortedByProjectIdAscending() throws Exception {
        String firstProjectName = "1";
        String secondProjectName = "A";
        String lastFileName = "z";
        folders.save(projectEntry(lastFileName));
        folders.save(projectEntry(firstProjectName));
        folders.save(projectEntry(secondProjectName));

        assertThatAllFoldersSizeIs(3);
        Collection<ProjectEntry> allFolders = folders.getAllFolders();

        Iterator<ProjectEntry> iterator = allFolders.iterator();
        assertThat(iterator.next().getProjectId(), equalTo(firstProjectName));
        assertThat(iterator.next().getProjectId(), equalTo(secondProjectName));
        assertThat(iterator.next().getProjectId(), equalTo(lastFileName));
    }

    @Test(expected = ConstraintViolationException.class)
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

    @Test
    public void allowToFetchProjectEntryByProjectId() throws Exception {
        String projectId = "projectId";
        folders.save(projectEntry(projectId));
        ProjectEntry entry = folders.getByProjectId(projectId);

        assertThat(entry.getProjectId(), equalTo(projectId));
        assertNotNull(entry.getCreated());
        assertNotNull(entry.getId());
    }

    @Test
    public void allowToFetchProjectEntryById() throws Exception {
        String projectId = "projectId";
        folders.save(projectEntry(projectId));
        ProjectEntry entry1 = folders.getByProjectId(projectId);

        ProjectEntry entry2 = folders.getById(entry1.getId());
        assertThat(entry1.getProjectId(), equalTo(entry2.getProjectId()));
    }

    @Test
    public void allowsToEditProject() throws Exception {
        String projectId = "project";
        folders.save(projectEntry(projectId));
        ProjectEntry savedProject = folders.getByProjectId(projectId);
        String newProjectId = "updated_project";
        savedProject.setProjectId(newProjectId);
        folders.update(savedProject);

        assertNotNull(folders.getByProjectId(newProjectId));
        assertThatAllFoldersSizeIs(1);
    }

    private void assertThatAllFoldersSizeIs(int count) {
        assertThat(folders.getAllFolders().size(), equalTo(count));
    }

    private ProjectEntry projectEntry(String projectId) {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setProjectId(projectId);
        projectEntry.setDescription("description");
        return projectEntry;
    }
}
