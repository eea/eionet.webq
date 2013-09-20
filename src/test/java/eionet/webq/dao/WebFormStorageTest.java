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

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dao.orm.ProjectEntry;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.ProjectFileType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
@Transactional
public class WebFormStorageTest {
    @Autowired
    private WebFormStorage webFormStorage;
    @Autowired
    private ProjectFileStorage projectFileStorage;
    private ProjectEntry project = testProject();

    @Test
    public void allowsToFetchAllWebformsInRepository() throws Exception {
        save(createWebform());
        save(createWebform());
        save(createProjectFile(ProjectFileType.FILE));

        Collection<ProjectFile> activeWebForms = webFormStorage.getAllActiveWebForms();

        assertThat(activeWebForms.size(), equalTo(2));
        Iterator<ProjectFile> iterator = activeWebForms.iterator();
        assertThat(iterator.next().getFileType(), equalTo(ProjectFileType.WEBFORM));
        assertThat(iterator.next().getFileType(), equalTo(ProjectFileType.WEBFORM));
    }

    @Test
    public void webformsMustBeActive() throws Exception {
        save(createWebform());

        ProjectFile otherWebform = createWebform();
        otherWebform.setActive(false);
        save(otherWebform);

        Collection<ProjectFile> allActiveWebForms = webFormStorage.getAllActiveWebForms();
        assertThat(allActiveWebForms.size(), equalTo(1));
        assertTrue(allActiveWebForms.iterator().next().isActive());
    }

    @Test
    public void xmlSchemaMustBeSet() throws Exception {
        save(createWebform());

        ProjectFile otherWebform = createWebform();
        otherWebform.setXmlSchema(null);
        save(otherWebform);

        Collection<ProjectFile> allActiveWebForms = webFormStorage.getAllActiveWebForms();
        assertThat(allActiveWebForms.size(), equalTo(1));
        assertNotNull(allActiveWebForms.iterator().next().getXmlSchema());
    }

    @Test
    public void webformMustBeMarkedAsMainForm() throws Exception {
        save(createWebform());

        ProjectFile otherWebform = createWebform();
        otherWebform.setMainForm(false);
        save(otherWebform);

        Collection<ProjectFile> allActiveWebForms = webFormStorage.getAllActiveWebForms();
        assertThat(allActiveWebForms.size(), equalTo(1));
        assertTrue(allActiveWebForms.iterator().next().isMainForm());
    }

    @Test
    public void getActiveWebFormById() throws Exception {
        ProjectFile webform = createWebform();
        int id = save(webform);
        ProjectFile file = webFormStorage.getActiveWebFormById(id);

        assertThat(file.getTitle(), equalTo(webform.getTitle()));
    }

    private int save(ProjectFile projectFile) {
        return projectFileStorage.save(projectFile, project);
    }

    private ProjectFile createWebform() {
        return createProjectFile(ProjectFileType.WEBFORM);
    }

    private ProjectFile createProjectFile(ProjectFileType type) {
        ProjectFile file = new ProjectFile();
        file.setFileType(type);
        file.setActive(true);
        file.setMainForm(true);
        file.setXmlSchema("test-schema");
        file.setTitle("test-title");
        file.setUserName("test-username");
        return file;
    }

    private ProjectEntry testProject() {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setId(1);
        projectEntry.setProjectId("test");
        return projectEntry;
    }
}
