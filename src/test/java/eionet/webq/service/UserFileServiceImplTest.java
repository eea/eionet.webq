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

package eionet.webq.service;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import eionet.webq.dao.UserFileDownload;
import eionet.webq.dao.UserFileStorage;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.UserFileIdUpdate;

public class UserFileServiceImplTest {
    @InjectMocks
    private UserFileServiceImpl service;
    @Mock
    private UserFileStorage storage;
    @Mock
    private UserFileDownload userFileDownload;
    @Mock
    private RemoteFileService remoteFileService;
    @Mock
    private UserIdProvider userIdProvider;
    @Mock
    private HttpServletRequest request;

    private final String userId = "userId";
    private static final int FILE_ID = 1;
    private final String userAgentHeaderName = "user-agent";
    private final String expectedUserAgent = "IE 11";

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(userIdProvider.getUserId()).thenReturn(userId);
        when(request.getHeader(userAgentHeaderName)).thenReturn(expectedUserAgent);
    }

    @After
    public void verifyGeneralMockCalls() {
        verify(userIdProvider, atLeastOnce()).getUserId();
        verifyNoMoreInteractions(userIdProvider, storage, userFileDownload);
    }

    @Test
    public void testSave() throws Exception {
        UserFile fileToSave = new UserFile();
        when(storage.save(fileToSave, userId)).thenReturn(FILE_ID);

        service.save(fileToSave);

        verify(storage).save(fileToSave, userId);
    }

    @Test
    public void fetchFileContentAlsoUpdatesDownloadTime() throws Exception {
        UserFile fileInStorage = new UserFile();
        fileInStorage.setContent("test-file-content".getBytes());
        fileInStorage.setName("file.name");
        when(storage.findFile(FILE_ID, userId)).thenReturn(fileInStorage);

        assertThat(service.download(FILE_ID), equalTo(fileInStorage));
        verify(storage).findFile(FILE_ID, userId);
        verify(userFileDownload).updateDownloadTime(FILE_ID);
    }

    @Test
    public void testAllUploadedFiles() throws Exception {
        Collection<UserFile> filesInStorage = Arrays.asList(new UserFile());
        when(storage.findAllUserFiles(userId)).thenReturn(filesInStorage);

        Collection<UserFile> uploadedFiles = service.allUploadedFiles();

        assertThat(uploadedFiles, equalTo(filesInStorage));
        verify(storage).findAllUserFiles(userId);
    }

    @Test
    public void testUpdateContent() throws Exception {
        UserFile fileToUpdate = new UserFile();
        doNothing().when(storage).update(fileToUpdate, userId);

        Date lastDate = fileToUpdate.getUpdated();
        service.updateContent(fileToUpdate);

        verify(storage).update(fileToUpdate, userId);
        assertNotEquals(lastDate, fileToUpdate.getUpdated());
    }

    @Test
    public void testUpdate() throws Exception {
        UserFile fileToUpdate = new UserFile();
        doNothing().when(storage).update(fileToUpdate, userId);

        Date lastDate = fileToUpdate.getUpdated();
        service.update(fileToUpdate);

        verify(storage).update(fileToUpdate, userId);
        assertEquals(lastDate, fileToUpdate.getUpdated());
    }

    @Test
    public void removesFiles() throws Exception {
        int[] fileIds = {1, 2, 3};
        service.removeFilesById(fileIds);

        verify(storage).remove(userId, fileIds);
    }

    @Test
    public void allowToSaveFileBasedOnWebForm() throws Exception {
        String url = "external-file.url";
        ProjectFile webForm = new ProjectFile();
        webForm.setEmptyInstanceUrl(url);
        byte[] fileContent = "remote-file-content".getBytes();
        when(remoteFileService.fileContent(url)).thenReturn(fileContent);
        when(storage.getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'))).thenReturn(
                new Integer(0));
        service.saveBasedOnWebForm(new UserFile(), webForm);

        ArgumentCaptor<UserFile> userFileArgument = ArgumentCaptor.forClass(UserFile.class);
        verify(storage).getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'));
        verify(storage).save(userFileArgument.capture(), anyString());
        assertThat(userFileArgument.getValue().getContent(), equalTo(fileContent));
    }

    @Test
     public void whenSavingBasedOnWebFormSetFileNameFromWebFormIfItIsNotSet() throws Exception {
        String fileName = "new file name";
        ProjectFile webForm = new ProjectFile();
        webForm.setNewXmlFileName(fileName);
        UserFile userFile = new UserFile();
        when(storage.getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'))).thenReturn(
                new Integer(0));
        service.saveBasedOnWebForm(userFile, webForm);

        verify(storage).getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'));
        verify(storage).save(eq(userFile), anyString());
        assertThat(userFile.getName(), equalTo(fileName + "_" + 1));
    }

    @Test
    public void whenSavingMultipleWebFormInASessionWithoutFileExtension() throws Exception {
        String fileName = "multiple web form file name";
        ProjectFile webForm = new ProjectFile();
        webForm.setNewXmlFileName(fileName);
        when(storage.getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'))).thenReturn(
                new Integer(0));
        UserFile userFile = new UserFile();
        service.saveBasedOnWebForm(userFile, webForm);
        //check first file
        assertThat(userFile.getName(), equalTo(fileName + "_" + 1));
        verify(storage).save(eq(userFile), anyString());

        when(storage.getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'))).thenReturn(
                new Integer(1));
        userFile = new UserFile();
        service.saveBasedOnWebForm(userFile, webForm);
        //check second file
        assertThat(userFile.getName(), equalTo(fileName + "_" + 2));

        verify(storage, times(2)).getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'));
        verify(storage).save(eq(userFile), anyString());
    }

    @Test
    public void whenSavingMultipleWebFormInASessionWithFileExtension() throws Exception {
        String fileName = "multiple web form file name";
        String fileExtension = ".xml";
        ProjectFile webForm = new ProjectFile();
        webForm.setNewXmlFileName(fileName+ fileExtension);
        when(storage.getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'))).thenReturn(
                new Integer(0));
        UserFile userFile = new UserFile();
        service.saveBasedOnWebForm(userFile, webForm);
        //check first file
        assertThat(userFile.getName(), equalTo(fileName + "_" + 1 + fileExtension));
        verify(storage).save(eq(userFile), anyString());

        when(storage.getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'))).thenReturn(
                new Integer(1));
        userFile = new UserFile();
        service.saveBasedOnWebForm(userFile, webForm);
        //check second file
        assertThat(userFile.getName(), equalTo(fileName + "_" + 2 + fileExtension));

        verify(storage, times(2)).getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'));
        verify(storage).save(eq(userFile), anyString());
    }

    @Test
    public void whenSavingWebFormInASessionWithFileExtensionWhichDoesNotExistBefore() throws Exception {
        String fileName = "multiple web form file name";
        String fileExtension = ".xml";
        ProjectFile webForm = new ProjectFile();
        webForm.setNewXmlFileName(fileName+ fileExtension);
        when(storage.getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'))).thenReturn(
                null);
        UserFile userFile = new UserFile();
        service.saveBasedOnWebForm(userFile, webForm);
        //check file
        assertThat(userFile.getName(), equalTo(fileName + "_" + 1 + fileExtension));
        verify(storage).save(eq(userFile), anyString());
        verify(storage).getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'));
    }

    @Test
    public void whenSavingBasedOnWebFormSetFileContentFromRemoteLocationIfPresent() throws Exception {
        byte[] fileContent = "file-content".getBytes();

        ProjectFile webForm = new ProjectFile();
        webForm.setEmptyInstanceUrl("empty.instance");
        UserFile userFile = new UserFile();
        when(remoteFileService.fileContent(anyString())).thenReturn(fileContent);
        when(storage.getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'))).thenReturn(
                new Integer(0));
        service.saveBasedOnWebForm(userFile, webForm);

        assertThat(userFile.getContent(), equalTo(fileContent));
        verify(remoteFileService).fileContent(webForm.getEmptyInstanceUrl());
        verify(storage).getUserWebFormFileMaxNum(eq(userId), eq(webForm.getXmlSchema()), anyString(), eq('_'), eq('.'));
        verify(storage).save(eq(userFile), anyString());
    }

    @Test
    public void allowToUpdateFilesUserIdIfNewUserIsCurrentOne() throws Exception {
        String oldUserId = "old";
        service.updateUserId(oldUserId, userId);

        ArgumentCaptor<UserFileIdUpdate> updateDataCaptor = ArgumentCaptor.forClass(UserFileIdUpdate.class);
        verify(storage).updateUserId(updateDataCaptor.capture());
        UserFileIdUpdate updateData = updateDataCaptor.getValue();

        assertThat(updateData.getOldUserId(), equalTo(oldUserId));
        assertThat(updateData.getNewUserId(), equalTo(userId));
    }

    @Test
    public void doNotUpdateFilesUserIdIfUserIsNotCurrentOne() throws Exception {
        String oldUserId = "old";
        String newUserId = "new";
        service.updateUserId(oldUserId, newUserId);
    }

    @Test
    public void userAgentIsSetForFileOnSave() throws Exception {
        UserFile file = new UserFile();
        service.save(file);

        assertThat(file.getUserAgent(), equalTo(expectedUserAgent));
        verify(request).getHeader(userAgentHeaderName);
        verify(storage).save(eq(file), anyString());
    }

    @Test
    public void whenUpdatingUserId_sendUserAgentInUpdateData() throws Exception {
        service.updateUserId("oldUserId", userId);
        ArgumentCaptor<UserFileIdUpdate> updateDataCaptor = ArgumentCaptor.forClass(UserFileIdUpdate.class);
        verify(storage).updateUserId(updateDataCaptor.capture());

        UserFileIdUpdate updateData = updateDataCaptor.getValue();
        assertThat(updateData.getUserAgent(), equalTo(expectedUserAgent));
    }
}
