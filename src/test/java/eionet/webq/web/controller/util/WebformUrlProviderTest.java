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
 *        Enriko Käsper
 */
package eionet.webq.web.controller.util;

import eionet.webq.dao.orm.ProjectEntry;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.service.ProjectService;
import eionet.webq.web.controller.AbstractProjectsControllerTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebformUrlProviderTest extends AbstractProjectsControllerTests {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private WebformUrlProviderImpl webformUrlProvider;

    @Test
    public void getXFormUrl() {
        ProjectFile webformFile = new ProjectFile();
        webformFile.setId(1);
        webformFile.setFileName("xform.xhtml");

        assertThat(webformUrlProvider.getWebformPath(webformFile), equalTo("/xform/?formId=1&"));
    }

    @Test
    public void getHtmlFormWithProjectUrl() {
        ProjectFile webformFile = new ProjectFile();
        webformFile.setId(1);
        webformFile.setFileName("webform.html");
        webformFile.setProjectIdentifier("projectId");
        assertThat(webformUrlProvider.getWebformPath(webformFile), equalTo("/webform/project/projectId/file/webform.html?"));

        webformFile.setFileName("webform.htm");
        assertThat(webformUrlProvider.getWebformPath(webformFile), equalTo("/webform/project/projectId/file/webform.htm?"));
    }

    @Test
    public void getHtmlFormWithoutProjectUrl() {
        ProjectFile webformFile = new ProjectFile();
        webformFile.setId(1);
        webformFile.setFileName("webform.html");
        webformFile.setProjectId(1);

        ProjectEntry projectEntry = new ProjectEntry();
        projectEntry.setId(1);
        projectEntry.setProjectId("projectId");

        when(projectService.getById(1)).thenReturn(projectEntry);

        assertThat(webformUrlProvider.getWebformPath(webformFile), equalTo("/webform/project/projectId/file/webform.html?"));

    }
}
