package eionet.webq.web.controller;

import eionet.webq.dao.ProjectFolders;
import eionet.webq.dto.ProjectEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
public class ProjectsControllerTest extends AbstractProjectsControllerTest {
    @Autowired
    private ProjectFolders projectFolders;

    @Test
    public void returnsAllProjectsViewName() throws Exception {
        mvc().perform(get("/projects/")).andExpect(view().name("projects"));
    }

    @Test
    public void modelCollectionIsEmptyIfNoProjects() throws Exception {
        Collection<ProjectEntry> allProjects = getAllProjectEntries();
        assertThat(allProjects.size(), equalTo(0));
    }

    @Test
    public void allProjectsStoredInDataStorageArePresentInModel() throws Exception {
        projectFolders.save(projectEntryWith("1"));
        projectFolders.save(projectEntryWith("2"));

        assertThat(getAllProjectEntries().size(), equalTo(2));
    }

    @Test
    public void allowToAddNewProject() throws Exception {
        addNewProject("1", "short description");
        assertThat(getAllProjectEntries().size(), equalTo(1));
    }

    private ProjectEntry projectEntryWith(String id) {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setId(id);
        return projectEntry;
    }

    @SuppressWarnings("unchecked")
    private Collection<ProjectEntry> getAllProjectEntries() throws Exception {
        MvcResult mvcResult = mvc().perform(get("/projects/")).andReturn();
        return (Collection<ProjectEntry>) mvcResult.getModelAndView().getModelMap().get("allProjects");
    }
}
