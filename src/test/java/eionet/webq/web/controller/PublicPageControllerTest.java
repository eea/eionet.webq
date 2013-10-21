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
import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.UploadForm;
import eionet.webq.service.CDREnvelopeService;
import eionet.webq.service.RemoteFileService;
import eionet.webq.service.UserFileService;
import eionet.webq.service.WebFormService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class PublicPageControllerTest {

    public static final int WEB_FORM_ID = 1;
    @Mock
    private WebFormService webFormService;
    @Mock
    private UserFileService userFileService;
    @InjectMocks
    private PublicPageController publicPageController = new PublicPageController();
    @Mock
    private RemoteFileService remoteFileService;
    @Mock
    private CDREnvelopeService envelopeService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private Model model;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void savesNewUserFileToStorageAndRedirectsToWebForm() throws Exception {
        ProjectFile projectFile = new ProjectFile();
        when(webFormService.findActiveWebFormById(WEB_FORM_ID)).thenReturn(projectFile);

        publicPageController.startWebFormSaveFile(WEB_FORM_ID, new MockHttpServletRequest());

        verify(userFileService).saveBasedOnWebForm(any(UserFile.class), eq(projectFile));
    }

    @Test
    public void ifFileIsFromCdrSaveItToEnvelope() throws Exception {
        UserFile userFile = userFileServiceWillReturnUserFileFromCdr();

        publicPageController.saveXml(userFile.getId(), requestWillHaveContent());

        verify(envelopeService).pushXmlFile(any(UserFile.class));
    }

    @Test
    public void onXmlSave_IfFileIsFromCdrAndRequestHasRestrictedParameterSetToTrue_SetUserFileRestrictionParametersToTrue() throws Exception {
        UserFile userFile = userFileServiceWillReturnUserFileFromCdr();
        MockHttpServletRequest request = requestWillHaveContent();
        request.setParameter("restricted", "true");

        publicPageController.saveXml(userFile.getId(), request);

        assertThat(userFile.isApplyRestriction(), equalTo(true));
        assertThat(userFile.isRestricted(), equalTo(true));
    }

    @Test
    public void onXmlSave_IfFileIsFromCdrAndRestrictedAttributeNotSet_UserFileRestrictionParametersWillBeSetToFalse() throws Exception {
        UserFile userFile = userFileServiceWillReturnUserFileFromCdr();
        publicPageController.saveXml(userFile.getId(), requestWillHaveContent());

        assertThat(userFile.isApplyRestriction(), equalTo(false));
        assertThat(userFile.isRestricted(), equalTo(false));
    }

    @Test
    public void onXmlSave_IfFileIsFromCdrAndRestrictedAttributeSetToFalse_UserFileApplyRestrictionWillBeTrueButRestrictedWillBeSetToFalse() throws Exception {
        UserFile userFile = userFileServiceWillReturnUserFileFromCdr();
        MockHttpServletRequest request = requestWillHaveContent();
        request.setParameter("restricted", "false");
        publicPageController.saveXml(userFile.getId(), request);

        assertThat(userFile.isApplyRestriction(), equalTo(true));
        assertThat(userFile.isRestricted(), equalTo(false));
    }

    @Test
    public void writesWebFormContentForResponseWithRightHeaders() throws Exception {
        ProjectFile projectFile = new ProjectFile();
        byte[] testContent = "test-content".getBytes();
        projectFile.setFileContent(testContent);
        when(webFormService.findActiveWebFormById(WEB_FORM_ID)).thenReturn(projectFile);

        MockHttpServletResponse response = new MockHttpServletResponse();
        publicPageController.startWebFormWriteFormToResponse(WEB_FORM_ID, response);

        assertThat(response.getContentType(), equalTo("application/xhtml+html"));
        assertThat(response.getContentLength(), equalTo(testContent.length));
        assertThat(response.getContentAsByteArray(), equalTo(testContent));
    }

    @Test
    public void whenUploadingFile_ifMultipleFilesGiven_saveThemToStorage() throws Exception {
        UploadForm uploadForm = new UploadForm();
        UserFile file1 = new UserFile(new UploadedFile("file1", "file1-content".getBytes()), "xmlSchema");
        UserFile file2 = new UserFile(new UploadedFile("file2", "file2-content".getBytes()), "xmlSchema");
        uploadForm.setUserFile(Arrays.asList(file1, file2));

        publicPageController.upload(uploadForm, bindingResult, model, new MockHttpServletRequest());

        verify(userFileService).save(file1);
        verify(userFileService).save(file2);
    }

    private UserFile userFileServiceWillReturnUserFileFromCdr() {
        UserFile userFile = new UserFile();
        userFile.setId(1);
        userFile.setFromCdr(true);
        when(userFileService.getById(userFile.getId())).thenReturn(userFile);
        return userFile;
    }

    private MockHttpServletRequest requestWillHaveContent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("request-content".getBytes());
        return request;
    }
}
