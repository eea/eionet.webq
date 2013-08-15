package eionet.webq.service;

import eionet.webq.dao.UserFileStorage;
import eionet.webq.dto.UploadedXmlFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

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
public class UploadedXmlFileServiceImplTest {
    private UploadedXmlFileServiceImpl service;
    private UserFileStorage storage;
    HttpSession mockSession;
    private final String userId = "userId";

    @Before
    public void prepare() {
        service = new UploadedXmlFileServiceImpl();
        storage = Mockito.mock(UserFileStorage.class);
        mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockSession.getId()).thenReturn(userId);
        service.storage = storage;
        service.session = mockSession;
    }

    @After
    public void verifyGeneralMockCalls() {
        verify(mockSession, only()).getId();
        verifyNoMoreInteractions(mockSession, storage);
    }

    @Test
    public void testSave() throws Exception {
        UploadedXmlFile fileToSave = new UploadedXmlFile();
        doNothing().when(storage).save(fileToSave, userId);

        service.save(fileToSave);

        verify(storage, only()).save(fileToSave, userId);
    }

    @Test
    public void testGetById() throws Exception {
        int fileId = 1;
        UploadedXmlFile fileInStorage = new UploadedXmlFile().setName("file.name");
        when(storage.fileContentById(fileId, userId)).thenReturn(fileInStorage);

        assertThat(service.getById(fileId), equalTo(fileInStorage));
        verify(storage, only()).fileContentById(fileId, userId);
    }

    @Test
    public void testAllUploadedFiles() throws Exception {
        Collection<UploadedXmlFile> filesInStorage = Arrays.asList(new UploadedXmlFile());
        when(storage.allUploadedFiles(userId)).thenReturn(filesInStorage);

        Collection<UploadedXmlFile> uploadedFiles = service.allUploadedFiles();

        assertThat(uploadedFiles, equalTo(filesInStorage));
        verify(storage, only()).allUploadedFiles(userId);
    }

    @Test
    public void testUpdateContent() throws Exception {
        UploadedXmlFile fileToUpdate = new UploadedXmlFile();
        doNothing().when(storage).updateContent(fileToUpdate, userId);

        service.updateContent(fileToUpdate);

        verify(storage, only()).updateContent(fileToUpdate, userId);
    }
}
