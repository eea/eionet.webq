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
import eionet.webq.dto.WebFormType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    public static final String ANOTHER_TEST_SCHEMA = "another-test-schema";
    public static final String TEST_SCHEMA = "test-schema";
    @Autowired
    private WebFormStorage webFormStorage;
    @Autowired
    private ProjectFileStorage projectFileStorage;
    private ProjectEntry project = testProject();
    private int localFormId;
    private int remoteFormId;

    @Before
    public void setUp() {
        ProjectFile localWebForm1 = createLocalWebform();
        ProjectFile remoteWebform = createRemoteWebform();
        save(localWebForm1);
        save(createLocalWebform());
        save(remoteWebform);
        save(createProjectFile(ProjectFileType.FILE));
        localFormId = localWebForm1.getId();
        remoteFormId = remoteWebform.getId();
    }

    @Test
    public void allowsToFetchAllLocalWebformsFromRepository() throws Exception {
        Collection<ProjectFile> activeWebForms = webFormStorage.getAllActiveWebForms(WebFormType.LOCAL);

        assertThat(activeWebForms.size(), equalTo(2));
        Iterator<ProjectFile> iterator = activeWebForms.iterator();
        assertIsLocalWebForm(iterator.next());
        assertIsLocalWebForm(iterator.next());
    }

    @Test
    public void allowsToFetchAllRemoteWebformsFromRepository() throws Exception {
        Collection<ProjectFile> remoteWebForms = webFormStorage.getAllActiveWebForms(WebFormType.REMOTE);
        assertThat(remoteWebForms.size(), equalTo(1));

        ProjectFile form = remoteWebForms.iterator().next();
        assertIsRemoteWebForm(form);
    }

    @Test
    public void localAndRemoteWebFormsMustBeActiveToBeFetched() throws Exception {
        saveAsInactive(getFirstActiveFormOfType(WebFormType.LOCAL));
        saveAsInactive(getFirstActiveFormOfType(WebFormType.REMOTE));

        Collection<ProjectFile> activeLocalForms = webFormStorage.getAllActiveWebForms(WebFormType.LOCAL);

        assertThat(activeLocalForms.size(), equalTo(1));
        assertTrue(activeLocalForms.iterator().next().isActive());

        assertThat(webFormStorage.getAllActiveWebForms(WebFormType.REMOTE).size(), equalTo(0));
    }

    @Test
    public void xmlSchemaMustBeSet() throws Exception {
        saveWithoutXmlSchema(getFirstActiveFormOfType(WebFormType.LOCAL));
        saveWithoutXmlSchema(getFirstActiveFormOfType(WebFormType.REMOTE));

        Collection<ProjectFile> activeLocalForms = webFormStorage.getAllActiveWebForms(WebFormType.LOCAL);

        assertThat(activeLocalForms.size(), equalTo(1));
        assertNotNull(activeLocalForms.iterator().next().getXmlSchema());

        assertThat(webFormStorage.getAllActiveWebForms(WebFormType.REMOTE).size(), equalTo(0));
    }

    @Test
    public void getLocalWebFormById() throws Exception {
        ProjectFile webForm = webFormStorage.getActiveWebFormById(WebFormType.LOCAL, localFormId);
        assertIsLocalWebForm(webForm);
        assertThat(webForm.getId(), equalTo(localFormId));
    }

    @Test
    public void getRemoteWebFormById() throws Exception {
        ProjectFile webForm = webFormStorage.getActiveWebFormById(WebFormType.REMOTE, remoteFormId);
        assertIsRemoteWebForm(webForm);
        assertThat(webForm.getId(), equalTo(remoteFormId));
    }

    @Test
    public void fetchWebFormsByXmlSchemaAndType() throws Exception {
        String xmlSchema = ANOTHER_TEST_SCHEMA;
        saveWithXmlSchema(getFirstActiveFormOfType(WebFormType.LOCAL), xmlSchema);
        saveWithXmlSchema(createRemoteWebform(), xmlSchema);

        Collection<ProjectFile> localWebForms =
                webFormStorage.findWebFormsForSchemas(WebFormType.LOCAL, Arrays.asList(xmlSchema));
        assertOnlyOneWebFormWithSchema(localWebForms, xmlSchema);

        Collection<ProjectFile> remoteWebForms =
                webFormStorage.findWebFormsForSchemas(WebFormType.LOCAL, Arrays.asList(xmlSchema));
        assertOnlyOneWebFormWithSchema(remoteWebForms, xmlSchema);
    }

    @Test
    public void allowToSpecifyMoreThanOneXmlSchema() throws Exception {
        saveWithXmlSchema(getFirstActiveFormOfType(WebFormType.LOCAL), ANOTHER_TEST_SCHEMA);
        Collection<ProjectFile> xForms = webFormStorage.findWebFormsForSchemas(WebFormType.LOCAL,
                Arrays.asList(TEST_SCHEMA, ANOTHER_TEST_SCHEMA));

        assertThat(xForms.size(), equalTo(2));
    }

    private ProjectFile getFirstActiveFormOfType(WebFormType type) {
        return webFormStorage.getAllActiveWebForms(type).iterator().next();
    }

    private int save(ProjectFile projectFile) {
        return projectFileStorage.save(projectFile, project);
    }

    private ProjectFile createLocalWebform() {
        ProjectFile projectFile = createProjectFile(ProjectFileType.WEBFORM);
        projectFile.setLocalForm(true);
        return projectFile;
    }

    private ProjectFile createRemoteWebform() {
        ProjectFile projectFile = createProjectFile(ProjectFileType.WEBFORM);
        projectFile.setRemoteForm(true);
        return projectFile;
    }

    private ProjectFile createProjectFile(ProjectFileType type) {
        ProjectFile file = new ProjectFile();
        file.setFileType(type);
        file.setActive(true);
        file.setXmlSchema(TEST_SCHEMA);
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

    private void saveWithXmlSchema(ProjectFile webForm, String xmlSchema) {
        webForm.setXmlSchema(xmlSchema);
        save(webForm);
    }

    private void saveWithoutXmlSchema(ProjectFile webForm) {
        webForm.setXmlSchema(null);
        save(webForm);
    }

    private void saveAsInactive(ProjectFile webForm) {
        webForm.setActive(false);
        save(webForm);
    }

    private void assertOnlyOneWebFormWithSchema(Collection<ProjectFile> projectFiles, String xmlSchema) {
        assertThat(projectFiles.size(), equalTo(1));
        ProjectFile webForm = projectFiles.iterator().next();
        assertThatFileIsWebForm(webForm);
        assertThat(webForm.getXmlSchema(), equalTo(xmlSchema));
    }

    private void assertIsLocalWebForm(ProjectFile file) {
        assertThatFileIsWebForm(file);
        assertTrue(file.isLocalForm());
    }

    private void assertIsRemoteWebForm(ProjectFile form) {
        assertTrue(form.isRemoteForm());
        assertThat(form.getFileType(), equalTo(ProjectFileType.WEBFORM));
    }

    private void assertThatFileIsWebForm(ProjectFile file) {
        assertThat(file.getFileType(), equalTo(ProjectFileType.WEBFORM));
    }
}
