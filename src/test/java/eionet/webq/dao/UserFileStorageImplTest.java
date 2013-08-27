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
import eionet.webq.dto.UploadedFile;
import eionet.webq.dto.UserFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Iterator;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class UserFileStorageImplTest {
    @Autowired
    @Qualifier("user-files")
    private FileStorage<String, UserFile> storage;
    private String userId = userId();
    private String otherUserId = "other" + userId;

    @Autowired
    JdbcTemplate template;

    @Test
    public void saveUploadedFileToStorageWithoutException() {
        uploadSingleFileFor(userId);
    }

    @Test
    public void savesRequiredFields() throws Exception {
        UserFile userFile =
                new UserFile(new UploadedFile("name", "test_content".getBytes()), "xmlSchema");

        storage.save(userFile, userId);

        UserFile fileFromDb = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        UserFile fileContentFromDb = storage.fileContentBy(fileFromDb.getId(), userId);

        assertThat(fileFromDb.getName(), equalTo(userFile.getName()));
        assertThat(fileContentFromDb.getContent(), equalTo(userFile.getContent()));
        assertThat(fileFromDb.getSizeInBytes(), equalTo(userFile.getSizeInBytes()));
        assertThat(fileFromDb.getXmlSchema(), equalTo(userFile.getXmlSchema()));
        assertNotNull(fileFromDb.getCreated());
        assertNotNull(fileFromDb.getUpdated());
    }

    @Test(expected = DataAccessException.class)
    public void saveIgnoresId() throws Exception {
        UserFile userFile = new UserFile();
        userFile.setId(15);

        uploadFileForUser(userId, userFile);

        storage.fileContentBy(15, userId);
    }

    @Test(expected = DataAccessException.class)
    public void userCannotGetOtherUserFiles() throws Exception {
        uploadSingleFileFor(userId);
        uploadSingleFileFor(otherUserId);

        UserFile fileUploadedByAnotherUser = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(otherUserId);

        storage.fileContentBy(fileUploadedByAnotherUser.getId(), userId);
    }

    @Test
    public void savedFileCanBeRetrieved() throws Exception {
        String savedFileName = "file_to_retrieve.xml";
        UserFile fileToUpload = new UserFile();
        fileToUpload.setName(savedFileName);
        storage.save(fileToUpload, userId);

        UserFile userFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        assertThat(userFile.getName(), equalTo(savedFileName));
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
        UserFile fileWithContent = fileWithContent("File content".getBytes());

        uploadFilesFor(userId, 10, fileWithContent);
        Collection<UserFile> uploadedFiles = getAllFilesForUserAndAssertThatResultSetSizeIsAsExpected(userId, 10);
        for (UserFile file : uploadedFiles) {
            assertNull(file.getContent());
        }
    }

    @Test
    public void saveFilesContentCouldBeRetrievedByFileId() throws Exception {
        byte[] contentBytes = "Hello world!".getBytes();
        UserFile fileToUpload =
                new UserFile(new UploadedFile("my_file.xml", contentBytes), "my_schema.xsd");

        storage.save(fileToUpload, userId);

        UserFile uploadedFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        UserFile fileContent = storage.fileContentBy(uploadedFile.getId(), userId);

        assertThat(fileContent.getContent(), equalTo(contentBytes));
    }

    @Test
    public void fileContentCouldBeChanged() {
        uploadFileForUser(userId, fileWithContent("initial content".getBytes()));
        byte[] newContentBytes = "new content".getBytes();
        UserFile uploadedFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);

        storage.update(fileWithContentAndId(newContentBytes, uploadedFile.getId()), userId);

        assertThat(storage.fileContentBy(uploadedFile.getId(), userId).getContent(), equalTo(newContentBytes));
    }

    @Test
    public void userCannotChangeOtherUserContent() throws Exception {
        byte[] originalContent = (userId + " content").getBytes();
        uploadFileForUser(userId, fileWithContent(originalContent));
        UserFile uploadedFileByOtherUser = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);

        UserFile contentChangeRequestFile =
                fileWithContentAndId((otherUserId + " content").getBytes(), uploadedFileByOtherUser.getId());

        storage.update(contentChangeRequestFile, otherUserId);

        assertThat(storage.fileContentBy(uploadedFileByOtherUser.getId(), userId).getContent(), equalTo(originalContent));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getByIdNotImplemented() throws Exception {
        storage.fileById(1);
    }

    @Test
    public void removesUserFileById() throws Exception {
        uploadSingleFileFor(userId);
        UserFile file = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);

        storage.remove(userId, file.getId());

        assertThat(storage.allFilesFor(userId).size(), equalTo(0));
    }

    @Test
    public void allowsBulkRemoval() throws Exception {
        uploadFilesFor(userId, 2);
        Iterator<UserFile> it = storage.allFilesFor(userId).iterator();

        storage.remove(userId, it.next().getId(), it.next().getId());

        assertThat(storage.allFilesFor(userId).size(), equalTo(0));
    }

    @Test
    public void getIdAfterSave() throws Exception {
        UserFile userFile =
                new UserFile(new UploadedFile("name", "test_content".getBytes()), "xmlSchema");
        int fileId = storage.save(userFile, userId);
        int maxId = template.queryForInt("SELECT MAX(id) from user_xml");

        assertThat(fileId, equalTo(maxId));
    }

    private void uploadSingleFileFor(String userId) {
        uploadFilesFor(userId, 1);
    }

    private UserFile fileWithContent(byte[] content) {
        UserFile userFile = new UserFile();
        userFile.setContent(content);
        return userFile;
    }

    private UserFile fileWithContentAndId(byte[] content, int id) {
        UserFile userFile = fileWithContent(content);
        userFile.setId(id);
        return userFile;
    }

    private void uploadFilesFor(String userId, int count) {
        uploadFilesFor(userId, count, new UserFile());
    }

    private void uploadFilesFor(String userId, int count, UserFile file) {
        for (int i = 0; i < count; i++) {
            uploadFileForUser(userId, file);
        }
    }

    private void uploadFileForUser(String userId, UserFile file) {
        storage.save(file, userId);
    }

    private UserFile getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(String userId) {
        return getAllFilesForUserAndAssertThatResultSetSizeIsAsExpected(userId, 1).iterator().next();
    }

    private Collection<UserFile> getAllFilesForUserAndAssertThatResultSetSizeIsAsExpected(String userId, int resultSetSize) {
        Collection<UserFile> userFiles = storage.allFilesFor(userId);
        assertThat(userFiles.size(), equalTo(resultSetSize));
        return userFiles;
    }

    private String userId() {
        return Long.toString(System.currentTimeMillis());
    }
}
