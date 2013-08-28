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
import eionet.webq.dto.ProjectEntry;
import eionet.webq.dto.ProjectFile;
import eionet.webq.dto.ProjectFileType;
import eionet.webq.dto.UploadedFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class WebFormStorageTest {
    @Autowired
    private WebFormStorage webFormStorage;
    @Autowired
    @Qualifier("project-files")
    private FileStorage<ProjectEntry, ProjectFile> projectFileStorage;
    @Autowired
    private JdbcTemplate template;
    private ProjectFile webform = createProjectFile(ProjectFileType.WEBFORM); //will be reset after each test
    private ProjectEntry project = testProject();

    @Before
    public void setUp() throws Exception {
        template.execute("DELETE FROM project_file");
    }

    @Test
    public void allowsToFetchAllWebformsInRepository() throws Exception {
        save(webform);
        save(webform);
        save(createProjectFile(ProjectFileType.FILE));

        Collection<ProjectFile> activeWebForms = webFormStorage.getAllActiveWebForms();

        assertThat(activeWebForms.size(), equalTo(2));
        Iterator<ProjectFile> iterator = activeWebForms.iterator();
        assertThat(iterator.next().getFileType(), equalTo(ProjectFileType.WEBFORM));
        assertThat(iterator.next().getFileType(), equalTo(ProjectFileType.WEBFORM));
    }

    @Test
    public void webformsMustBeActive() throws Exception {
        save(webform);

        webform.setActive(false);
        save(webform);

        Collection<ProjectFile> allActiveWebForms = webFormStorage.getAllActiveWebForms();
        assertThat(allActiveWebForms.size(), equalTo(1));
        assertTrue(allActiveWebForms.iterator().next().isActive());
    }

    @Test
    public void xmlSchemaMustBeSet() throws Exception {
        save(webform);

        webform.setXmlSchema(null);
        save(webform);

        Collection<ProjectFile> allActiveWebForms = webFormStorage.getAllActiveWebForms();
        assertThat(allActiveWebForms.size(), equalTo(1));
        assertNotNull(allActiveWebForms.iterator().next().getXmlSchema());
    }

    @Test
    public void webformMustBeMarkedAsMainForm() throws Exception {
        save(webform);

        webform.setMainForm(false);
        save(webform);

        Collection<ProjectFile> allActiveWebForms = webFormStorage.getAllActiveWebForms();
        assertThat(allActiveWebForms.size(), equalTo(1));
        assertTrue(allActiveWebForms.iterator().next().isMainForm());
    }

    @Test
    public void getAllReturnsRequiredFieldsAndNotUploadedFile() throws Exception {
        save(webFormWithAllFieldsSet());

        Collection<ProjectFile> allActiveWebForms = webFormStorage.getAllActiveWebForms();
        ProjectFile foundWebform = allActiveWebForms.iterator().next();
        assertNotNull(foundWebform.getId());
        assertNotNull(foundWebform.getTitle());

        assertNull(foundWebform.getFileContent());
    }

    private void save(ProjectFile projectFile) {
        projectFileStorage.save(projectFile, project);
    }

    private ProjectFile createProjectFile(ProjectFileType type) {
        ProjectFile file = new ProjectFile();
        file.setFileType(type);
        file.setActive(true);
        file.setMainForm(true);
        file.setXmlSchema("test-schema");
        return file;
    }

    private ProjectFile webFormWithAllFieldsSet() {
        ProjectFile projectFile = createProjectFile(ProjectFileType.WEBFORM);
        projectFile.setFile(new UploadedFile("test.xml", "test-content".getBytes()));
        projectFile.setEmptyInstanceUrl("emptyInstanceUrl");
        projectFile.setNewXmlFileName("newXmlFileName");
        projectFile.setDescription("description");
        projectFile.setTitle("title");
        projectFile.setUserName("userName");
        projectFile.setMainForm(true);
        return projectFile;
    }

    private ProjectEntry testProject() {
        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setId(1);
        projectEntry.setProjectId("test");
        return projectEntry;
    }
}
