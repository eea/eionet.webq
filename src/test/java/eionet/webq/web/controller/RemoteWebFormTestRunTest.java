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
package eionet.webq.web.controller;

import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.service.UserFileService;
import eionet.webq.service.WebFormService;
import org.hamcrest.core.StringContains;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class RemoteWebFormTestRunTest {
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    @InjectMocks
    RemoteWebFormTestRun controller;
    @Mock
    UserFileService userFileService;
    @Mock
    WebFormService webFormService;

    @Test
    public void whenStartingRemoteForm_loadsFormByGivenId() throws Exception {
        controller.webFormTestRun(4, null, null, request);

        verify(webFormService).findActiveWebFormById(4);
    }

    @Test
    public void whenStartingRemoteForm_savesUserFileBasedOnWebForm() throws Exception {
        ProjectFile webForm = new ProjectFile();
        when(webFormService.findActiveWebFormById(anyInt())).thenReturn(webForm);

        controller.webFormTestRun(4, null, null, request);

        verify(userFileService).saveBasedOnWebForm(any(UserFile.class), eq(webForm));
    }

    @Test
    public void whenStartingRemoteForm_redirectToXFormEngine() throws Exception {
        String redirect = controller.webFormTestRun(2, null, null, request);

        assertThat(redirect, StringStartsWith.startsWith("redirect:/xform/"));
    }

    @Test
    public void whenStartingRemoteForm_ifInstanceURLSpecified_addItToRedirect() throws Exception {
        String instanceURL = "http://instance.url";
        String redirect = controller.webFormTestRun(1, instanceURL, null, request);
        assertThat(redirect, StringContains.containsString("&instance=" + instanceURL));
    }

    @Test
    public void whenStartingRemoteForm_ifAnyAdditionalParametersSpecified_addItToRedirect() throws Exception {
        String additionalParameters = "&foo=bar&foobar=barfoo";
        String redirect = controller.webFormTestRun(1, null, additionalParameters, request);
        assertThat(redirect, StringContains.containsString(additionalParameters));
    }
}
