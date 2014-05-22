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

import eionet.webq.dao.MergeModules;
import eionet.webq.dao.orm.MergeModule;
import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.service.UserFileMergeService;
import eionet.webq.service.UserFileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static eionet.webq.web.controller.FileDownloadController.MergeModuleChoiceRequiredException;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class FileDownloadControllerTest {
    @Mock
    private UserFileService userFileService;
    @Mock
    private MergeModules mergeModules;
    @Mock
    private UserFileMergeService userFileMergeService;
    @InjectMocks
    private FileDownloadController controller;

    @Test(expected = IllegalArgumentException.class)
    public void whenMergingUserFiles_IfFileIdsListIsNull_throwsException() throws Exception {
        controller.mergeFiles(null, null, new MockHttpServletRequest(), new MockHttpServletResponse());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenMergingUserFiles_IfFileIdsListIsEmpty_throwsException() throws Exception {
        controller.mergeFiles(Collections.<Integer>emptyList(), null, new MockHttpServletRequest(), new MockHttpServletResponse());
    }

    @Test
    public void whenMergingFiles_ifThereIsOnlyOneMergeModule_performMergeAndReturnFileToUser() throws Exception {
        UserFile userFile = new UserFile(new UploadedFile(), "schema");
        MergeModule mergeModule = new MergeModule();
        byte[] mergeResult = "merge-result".getBytes();

        when(userFileService.getById(anyInt())).thenReturn(userFile, userFile);
        when(mergeModules.findByXmlSchemas(anyCollectionOf(String.class))).thenReturn(Arrays.asList(mergeModule));
        when(userFileMergeService.mergeFiles(anyCollectionOf(UserFile.class), eq(mergeModule)))
                .thenReturn(mergeResult);
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.mergeFiles(Arrays.asList(1,2), null, new MockHttpServletRequest(), response);

        verify(userFileMergeService).mergeFiles(anyCollectionOf(UserFile.class), eq(mergeModule));
        assertThat(response.getContentAsByteArray(), equalTo(mergeResult));
    }

    @Test
    public void whenMergingFiles_ifFilesAmountIsOnlyOne_ReturnThisFileContentToUser() throws Exception {
        int fileId = 1;
        controller = spy(controller);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        doNothing().when(controller).downloadUserFile(fileId, request, response);

        controller.mergeFiles(Arrays.asList(fileId), null, request, response);

        verify(controller).downloadUserFile(fileId, request, response);
        verifyNoMoreInteractions(userFileMergeService, mergeModules, userFileService);
    }

    @Test(expected = MergeModuleChoiceRequiredException.class)
    public void whenMergingFiles_ifMoreThanOneXmlSchemaFound_throwsException() throws Exception {
        UserFile userFile1 = new UserFile();
        UserFile userFile2 = new UserFile();

        userFile1.setXmlSchema("schema1");
        userFile2.setXmlSchema("schema2");

        when(userFileService.getById(anyInt())).thenReturn(userFile1, userFile2);
        controller.mergeFiles(Arrays.asList(1, 2), null, new MockHttpServletRequest(), new MockHttpServletResponse());
    }

    @Test
    public void whenMergingFiles_ifMergeModuleSpecified_useItToMergeFiles() throws Exception {
        int mergeModuleId = 5;
        MergeModule mergeModule = new MergeModule();

        when(userFileService.getById(anyInt())).thenReturn(new UserFile(), new UserFile());
        when(mergeModules.findById(mergeModuleId)).thenReturn(mergeModule);
        when(userFileMergeService.mergeFiles(anyCollectionOf(UserFile.class), eq(mergeModule)))
                .thenReturn("merge-result".getBytes());

        controller.mergeFiles(Arrays.asList(1, 2), mergeModuleId, new MockHttpServletRequest(), new MockHttpServletResponse());

        verify(mergeModules).findById(mergeModuleId);
        verify(userFileMergeService).mergeFiles(anyCollectionOf(UserFile.class), eq(mergeModule));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void whenHandlingMergeModuleChoiceRequiredException_ifNoModulesFound_showFullListOfModules() throws Exception {
        List<MergeModule> modules = Arrays.asList(new MergeModule());
        when(mergeModules.findAll()).thenReturn(modules);

        MergeModuleChoiceRequiredException mergeModuleChoiceRequired =
                new MergeModuleChoiceRequiredException(Arrays.asList(new UserFile()), Collections.<MergeModule>emptyList());
        ModelAndView modelAndView = controller.mergeSelect(mergeModuleChoiceRequired);

        assertThat((List<MergeModule>) modelAndView.getModel().get("mergeModules"), equalTo(modules));
        verify(mergeModules).findAll();
    }
}
