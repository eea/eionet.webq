package eionet.webq.web.controller;

import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import util.ProjectFoldersCleaner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
public class AbstractProjectsControllerTest extends AbstractContextControllerTests {
    @Autowired
    private ProjectFoldersCleaner cleaner;

    @Before
    public void removeAllProjectFolders() {
        cleaner.removeAllProjects();
    }

    MvcResult addNewProject(String id, String description) throws Exception {
        return mvc().perform(post("/projects/add").param("id", id).param("description", description)).andReturn();
    }
}
