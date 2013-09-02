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

import eionet.webq.dao.FileStorage;
import eionet.webq.dao.UserFileDownload;
import eionet.webq.dto.UserFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UserFileServiceImplTest {
    @InjectMocks
    private UserFileServiceImpl service;
    @Mock
    private FileStorage<String, UserFile> storage;
    @Mock
    HttpSession mockSession;
    @Mock
    UserFileDownload userFileDownload;
    private final String userId = "userId";
    private static final int FILE_ID = 1;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockSession.getId()).thenReturn(userId);
    }

    @After
    public void verifyGeneralMockCalls() {
        verify(mockSession).getId();
        verifyNoMoreInteractions(mockSession, storage, userFileDownload);
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
        fileInStorage.setName("file.name");
        when(storage.fileContentBy(FILE_ID, userId)).thenReturn(fileInStorage);

        assertThat(service.getById(FILE_ID), equalTo(fileInStorage));
        verify(storage).fileContentBy(FILE_ID, userId);
        verify(userFileDownload).updateDownloadTime(FILE_ID);
    }

    @Test
    public void testAllUploadedFiles() throws Exception {
        Collection<UserFile> filesInStorage = Arrays.asList(new UserFile());
        when(storage.allFilesFor(userId)).thenReturn(filesInStorage);

        Collection<UserFile> uploadedFiles = service.allUploadedFiles();

        assertThat(uploadedFiles, equalTo(filesInStorage));
        verify(storage).allFilesFor(userId);
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
}
