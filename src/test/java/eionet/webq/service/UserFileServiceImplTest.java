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

import eionet.webq.dao.UserFileDownload;
import eionet.webq.dao.UserFileStorage;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UserFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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

    private final String userId = "userId";
    private static final int FILE_ID = 1;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(userIdProvider.getUserId()).thenReturn(userId);
    }

    @After
    public void verifyGeneralMockCalls() {
        verify(userIdProvider).getUserId();
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

        service.updateContent(fileToUpdate);

        verify(storage).update(fileToUpdate, userId);
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
        service.saveBasedOnWebForm(new UserFile(), webForm);

        ArgumentCaptor<UserFile> userFileArgument = ArgumentCaptor.forClass(UserFile.class);
        verify(storage).save(userFileArgument.capture(), anyString());
        assertThat(userFileArgument.getValue().getContent(), equalTo(fileContent));
    }

    @Test
    public void whenSavingBasedOnWebFormSetFileNameFromWebFormIfItIsNotSet() throws Exception {
        ProjectFile webForm = new ProjectFile();
        webForm.setNewXmlFileName("new file name");
        UserFile userFile = new UserFile();
        service.saveBasedOnWebForm(userFile, webForm);

        verify(storage).save(eq(userFile), anyString());
        assertThat(userFile.getName(), equalTo(webForm.getNewXmlFileName()));
    }

    @Test
    public void whenSavingBasedOnWebFormSetFileContentFromRemoteLocationIfPresent() throws Exception {
        byte[] fileContent = "file-content".getBytes();

        ProjectFile webForm = new ProjectFile();
        webForm.setEmptyInstanceUrl("empty.instance");
        UserFile userFile = new UserFile();
        when(remoteFileService.fileContent(anyString())).thenReturn(fileContent);
        service.saveBasedOnWebForm(userFile, webForm);

        assertThat(userFile.getContent(), equalTo(fileContent));
        verify(remoteFileService).fileContent(webForm.getEmptyInstanceUrl());
        verify(storage).save(eq(userFile), anyString());
    }

    @Test
    public void allowToUpdateFilesUserIdIfNewUserIsCurrentOne() throws Exception {
        String oldUserId = "old";
        service.updateUserId(oldUserId, userId);

        verify(storage).updateUserId(oldUserId, userId);
    }

    @Test
    public void doNotUpdateFilesUserIdIfUserIsNotCurrentOne() throws Exception {
        String oldUserId = "old";
        String newUserId = "new";
        service.updateUserId(oldUserId, newUserId);
    }
}
