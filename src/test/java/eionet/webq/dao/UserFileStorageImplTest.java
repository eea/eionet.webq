package eionet.webq.dao;

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dto.UploadedXmlFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class UserFileStorageImplTest {
    @Autowired
    @Qualifier("user-files")
    private FileStorage<String, UploadedXmlFile> storage;
    private String userId = userId();
    private String otherUserId = "other" + userId;

    @Test
    public void saveUploadedFileToStorageWithoutException() {
        uploadSingleFileFor(userId);
    }

    @Test
    public void savesRequiredFields() throws Exception {
        UploadedXmlFile uploadedXmlFile =
                new UploadedXmlFile().setName("name").setXmlSchema("xmlSchema").setSizeInBytes(1000)
                        .setContent("test_content".getBytes());

        storage.save(uploadedXmlFile, userId);

        UploadedXmlFile fileFromDb = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        UploadedXmlFile fileContentFromDb = storage.fileContentBy(fileFromDb.getId(), userId);

        assertThat(fileFromDb.getName(), equalTo(uploadedXmlFile.getName()));
        assertThat(fileContentFromDb.getContent(), equalTo(uploadedXmlFile.getContent()));
        assertThat(fileFromDb.getSizeInBytes(), equalTo(uploadedXmlFile.getSizeInBytes()));
        assertThat(fileFromDb.getXmlSchema(), equalTo(uploadedXmlFile.getXmlSchema()));
        assertNotNull(fileFromDb.getCreated());
        assertNotNull(fileFromDb.getUpdated());
    }

    @Test(expected = DataAccessException.class)
    public void saveIgnoresId() throws Exception {
        UploadedXmlFile uploadedXmlFile = new UploadedXmlFile().setId(15);

        uploadFileForUser(userId, uploadedXmlFile);

        storage.fileContentBy(15, userId);
    }

    @Test(expected = DataAccessException.class)
    public void userCannotGetOtherUserFiles() throws Exception {
        uploadSingleFileFor(userId);
        uploadSingleFileFor(otherUserId);

        UploadedXmlFile fileUploadedByAnotherUser = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(otherUserId);

        storage.fileContentBy(fileUploadedByAnotherUser.getId(), userId);
    }

    @Test
    public void savedFileCanBeRetrieved() throws Exception {
        String savedFileName = "file_to_retrieve.xml";
        UploadedXmlFile fileToUpload = new UploadedXmlFile().setName(savedFileName);
        storage.save(fileToUpload, userId);

        UploadedXmlFile uploadedXmlFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        assertThat(uploadedXmlFile.getName(), equalTo(savedFileName));
    }

    @Test
    public void allFilesSavedForOneUserCanBeRetrieved() throws Exception {
        uploadFilesFor(userId, 3);

        assertThat(storage.allFilesFor(userId).size(), equalTo(3));
    }

    @Test
    public void filesRetrievedOnlyForSpecifiedUser() throws Exception {
        uploadFilesFor(userId, 3);
        uploadFilesFor(otherUserId, 2);

        assertThat(storage.allFilesFor(userId).size(), equalTo(3));
    }

    @Test
    public void allFilesWillNotFetchFilesContent() throws Exception {
        UploadedXmlFile fileWithContent = new UploadedXmlFile().setContent("File content".getBytes());
        uploadFilesFor(userId, 10, fileWithContent);
        Collection<UploadedXmlFile> uploadedFiles = getAllFilesForUserAndAssertThatResultSetSizeIsAsExpected(userId, 10);
        for (UploadedXmlFile file : uploadedFiles) {
            assertNull(file.getContent());
        }
    }

    @Test
    public void saveFilesContentCouldBeRetrievedByFileId() throws Exception {
        byte[] contentBytes = "Hello world!".getBytes();
        UploadedXmlFile fileToUpload =
                new UploadedXmlFile().setName("my_file.xml").setContent(contentBytes).setXmlSchema("my_schema.xsd")
                        .setSizeInBytes(contentBytes.length);

        storage.save(fileToUpload, userId);

        UploadedXmlFile uploadedFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        UploadedXmlFile fileContent = storage.fileContentBy(uploadedFile.getId(), userId);

        assertThat(fileContent.getContent(), equalTo(contentBytes));
    }

    @Test
    public void fileContentCouldBeChanged() {
        uploadFileForUser(userId, new UploadedXmlFile().setContent("initial content".getBytes()));
        byte[] newContentBytes = "new content".getBytes();
        UploadedXmlFile uploadedFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);

        storage.update(new UploadedXmlFile().setId(uploadedFile.getId()).setContent(newContentBytes), userId);

        assertThat(storage.fileContentBy(uploadedFile.getId(), userId).getContent(), equalTo(newContentBytes));
    }

    @Test
    public void userCannotChangeOtherUserContent() throws Exception {
        byte[] originalContent = (userId + " content").getBytes();
        uploadFileForUser(userId, new UploadedXmlFile().setContent(originalContent));
        UploadedXmlFile uploadedFileByOtherUser = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);

        UploadedXmlFile contentChangeRequestFile =
                new UploadedXmlFile().setContent((otherUserId + " content").getBytes()).setId(uploadedFileByOtherUser.getId());

        storage.update(contentChangeRequestFile, otherUserId);

        assertThat(storage.fileContentBy(uploadedFileByOtherUser.getId(), userId).getContent(), equalTo(originalContent));
    }

    private void uploadSingleFileFor(String userId) {
        uploadFilesFor(userId, 1);
    }

    private void uploadFilesFor(String userId, int count) {
        uploadFilesFor(userId, count, new UploadedXmlFile());
    }

    private void uploadFilesFor(String userId, int count, UploadedXmlFile file) {
        for (int i = 0; i < count; i++) {
            uploadFileForUser(userId, file);
        }
    }

    private void uploadFileForUser(String userId, UploadedXmlFile file) {
        storage.save(file, userId);
    }

    private UploadedXmlFile getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(String userId) {
        return getAllFilesForUserAndAssertThatResultSetSizeIsAsExpected(userId, 1).iterator().next();
    }

    private Collection<UploadedXmlFile> getAllFilesForUserAndAssertThatResultSetSizeIsAsExpected(String userId, int resultSetSize) {
        Collection<UploadedXmlFile> uploadedXmlFiles = storage.allFilesFor(userId);
        assertThat(uploadedXmlFiles.size(), equalTo(resultSetSize));
        return uploadedXmlFiles;
    }

    private String userId() {
        return Long.toString(System.currentTimeMillis());
    }
}
