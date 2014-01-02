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
import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.UserFileIdUpdate;
import org.hibernate.FlushMode;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Iterator;

import static eionet.webq.dao.FileContentUtil.getFileContentRowsCount;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
@Transactional
public class UserFileStorageImplTest {
    private final String defaultUserAgent = "Mozilla";
    @Autowired
    private UserFileStorage storage;
    @Autowired
    private UserFileDownload userFileDownload;
    @Autowired
    SessionFactory sessionFactory;
    private String userId = userId();
    private String otherUserId = "other" + userId;

    @Before
    public void setUp() throws Exception {
        sessionFactory.getCurrentSession().setFlushMode(FlushMode.ALWAYS);
    }

    @Test
    public void saveUploadedFileToStorageWithoutException() {
        uploadSingleFileFor(userId);
    }

    @Test
    public void savesRequiredFields() throws Exception {
        UserFile userFile =
                new UserFile(new UploadedFile("name", "test_content".getBytes()), "xmlSchema");
        userFile.setFromCdr(true);
        userFile.setUserAgent("Mozilla");

        storage.save(userFile, userId);

        UserFile fileFromDb = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        UserFile fileContentFromDb = storage.findFile(fileFromDb.getId(), userId);

        assertThat(fileFromDb.getName(), equalTo(userFile.getName()));
        assertThat(fileContentFromDb.getContent(), equalTo(userFile.getContent()));
        assertThat(fileFromDb.getSizeInBytes(), equalTo(userFile.getSizeInBytes()));
        assertThat(fileFromDb.getXmlSchema(), equalTo(userFile.getXmlSchema()));
        assertThat(fileFromDb.isFromCdr(), equalTo(userFile.isFromCdr()));
        assertThat(fileFromDb.getUserAgent(), equalTo(userFile.getUserAgent()));
        assertNotNull(fileFromDb.getCreated());
        assertNotNull(fileFromDb.getUpdated());
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveIgnoresId() throws Exception {
        UserFile userFile = new UserFile();
        userFile.setId(15);

        saveFileForUser(userId, userFile);

        storage.findFile(15, userId);
    }

    @Test
    public void userCannotGetOtherUserFiles() throws Exception {
        uploadSingleFileFor(userId);
        uploadSingleFileFor(otherUserId);

        UserFile fileUploadedByAnotherUser = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(otherUserId);

        Assert.assertNull(storage.findFile(fileUploadedByAnotherUser.getId(), userId));
    }

    @Test
    public void savedFileCanBeRetrieved() throws Exception {
        String savedFileName = "file_to_retrieve.xml";
        UserFile fileToUpload = new UserFile();
        fileToUpload.setName(savedFileName);
        fileToUpload.setXmlSchema("test-schema");
        storage.save(fileToUpload, userId);

        UserFile userFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        assertThat(userFile.getName(), equalTo(savedFileName));
    }

    @Test
    public void allFilesSavedForOneUserCanBeRetrieved() throws Exception {
        saveFilesFor(userId, 3);

        assertThat(storage.findAllUserFiles(userId).size(), equalTo(3));
    }

    @Test
    public void filesRetrievedOnlyForSpecifiedUser() throws Exception {
        saveFilesFor(userId, 3);
        saveFilesFor(otherUserId, 2);

        assertThat(storage.findAllUserFiles(userId).size(), equalTo(3));
    }

    @Test(expected = LazyInitializationException.class)
    public void filesContentIsFetchedLazily() throws Exception {
        storage.save(fileWithContentAndXmlSchema("test-content".getBytes()), userId);
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.clear();

        UserFile theOnlyFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        currentSession.evict(theOnlyFile);

        theOnlyFile.getContent();// content must be not initialized
    }

    @Test
    public void fileContentWillBeLoadedOnDemand() throws Exception {
        byte[] testContent = "aaaaa".getBytes();
        storage.save(fileWithContentAndXmlSchema(testContent), userId);
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.clear();

        UserFile theOnlyFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);

        assertThat(theOnlyFile.getContent(), equalTo(testContent));
    }

    @Test
    public void saveFilesContentCouldBeRetrievedByFileId() throws Exception {
        byte[] contentBytes = "Hello world!".getBytes();
        UserFile fileToUpload =
                new UserFile(new UploadedFile("my_file.xml", contentBytes), "my_schema.xsd");

        storage.save(fileToUpload, userId);

        UserFile uploadedFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
        UserFile fileContent = storage.findFile(uploadedFile.getId(), userId);

        assertThat(fileContent.getContent(), equalTo(contentBytes));
    }

    @Test
    public void fileContentCouldBeChanged() {
        saveFileForUser(userId, fileWithContentAndXmlSchema("initial content".getBytes()));
        byte[] newContentBytes = "new content".getBytes();
        UserFile uploadedFile = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);

        storage.update(fileWithContentAndId(newContentBytes, uploadedFile.getId()), userId);

        assertThat(storage.findFile(uploadedFile.getId(), userId).getContent(), equalTo(newContentBytes));
    }

    @Test
    public void userCannotChangeOtherUserContent() throws Exception {
        byte[] originalContent = (userId + " content").getBytes();
        saveFileForUser(userId, fileWithContentAndXmlSchema(originalContent));
        UserFile uploadedFileByOtherUser = getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);

        UserFile contentChangeRequestFile =
                fileWithContentAndId((otherUserId + " content").getBytes(), uploadedFileByOtherUser.getId());

        storage.update(contentChangeRequestFile, otherUserId);

        assertThat(storage.findFile(uploadedFileByOtherUser.getId(), userId).getContent(), equalTo(originalContent));
    }

    public void getByIdNotImplemented() throws Exception {
        UserFile file = fileWithContentAndXmlSchema(userId.getBytes());
        saveFileForUser(userId, file);
        UserFile userFile = storage.findFile(file.getId(), userId);
        org.junit.Assert.assertNotNull(userFile);
    }

    @Test
    public void removesUserFileById() throws Exception {
        UserFile file = saveAndGetBackSavedFileForDefaultUser();

        storage.remove(userId, file.getId());

        assertThat(storage.findAllUserFiles(userId).size(), equalTo(0));
    }

    @Test
    public void allowsBulkRemoval() throws Exception {
        saveFilesFor(userId, 2);
        Iterator<UserFile> it = storage.findAllUserFiles(userId).iterator();

        storage.remove(userId, it.next().getId(), it.next().getId());

        assertThat(storage.findAllUserFiles(userId).size(), equalTo(0));
    }

    @Test
    public void getIdAfterSave() throws Exception {
        UserFile userFile =
                new UserFile(new UploadedFile("name", "test_content".getBytes()), "xmlSchema");
        int fileId = storage.save(userFile, userId);
        int maxId = (Integer) sessionFactory.getCurrentSession().createQuery("SELECT MAX(id) from UserFile").uniqueResult();

        assertThat(fileId, equalTo(maxId));
    }

    @Test
    public void lastDownloadTimeForSavedFileIsNull() throws Exception {
        UserFile file = saveAndGetBackSavedFileForDefaultUser();
        assertThat(file.getDownloaded(), equalTo(null));
    }

    @Test
    public void allowsToUpdateDownloadTime() throws Exception {
        UserFile userFile = saveAndGetBackSavedFileForDefaultUser();
        userFileDownload.updateDownloadTime(userFile.getId());

        sessionFactory.getCurrentSession().clear();

        UserFile updatedFile = storage.findFile(userFile.getId(), userId);
        assertNotNull(updatedFile.getDownloaded());
    }

    @Test
    public void updatedTimeIsChangedAfterRecordUpdateInStorage() throws Exception {
        UserFile userFile = saveAndGetBackSavedFileForDefaultUser();
        userFile.setUpdated(null);

        storage.update(userFile, userFile.getUserId());

        assertNotNull(storage.findFile(userFile.getId(), userId).getUpdated());
    }

    @Test
    public void uploadedFileRemovedWithUserFileRemoval() throws Exception {
        UserFile userFile = saveAndGetBackSavedFileForDefaultUser();
        assertThat(getFileContentRowsCount(sessionFactory), equalTo(1));

        storage.remove(userId, userFile.getId());

        assertThat(getFileContentRowsCount(sessionFactory), equalTo(0));
    }

    @Test
    public void allowToUpdateUserIdForFiles() throws Exception {
        saveAndGetBackSavedFileForDefaultUser();

        assertThat(storage.findAllUserFiles(userId).size(), equalTo(1));
        assertThat(storage.findAllUserFiles(otherUserId).size(), equalTo(0));

        UserFileIdUpdate updateData = createUserFileIdUpdateData(userId, otherUserId);

        storage.updateUserId(updateData);

        assertThat(storage.findAllUserFiles(userId).size(), equalTo(0));
        assertThat(storage.findAllUserFiles(otherUserId).size(), equalTo(1));
    }

    @Test
    public void whenUserAgentNotSet_doNotUpdateUserFileId() throws Exception {
        saveAndGetBackSavedFileForDefaultUser();

        UserFileIdUpdate userFileIdUpdateData = createUserFileIdUpdateData(userId, otherUserId);
        userFileIdUpdateData.setUserAgent(null);
        storage.updateUserId(userFileIdUpdateData);

        assertThat(storage.findAllUserFiles(userId).size(), equalTo(1));
        assertThat(storage.findAllUserFiles(otherUserId).size(), equalTo(0));
    }

    @Test
    public void whenUpdatingFileId_andUserAgentIsDifferent_doNotUpdateUserFileId() throws Exception {
        UserFile userFile = saveAndGetBackSavedFileForDefaultUser();

        UserFileIdUpdate userFileIdUpdateData = createUserFileIdUpdateData(userId, otherUserId);
        userFileIdUpdateData.setUserAgent("IE 11");
        assertNotEquals(userFileIdUpdateData.getUserAgent(), userFile.getUserAgent());

        storage.updateUserId(userFileIdUpdateData);

        assertThat(storage.findAllUserFiles(userId).size(), equalTo(1));
        assertThat(storage.findAllUserFiles(otherUserId).size(), equalTo(0));
    }

    private UserFileIdUpdate createUserFileIdUpdateData(String oldUserId, String newUserId) {
        UserFileIdUpdate updateData = new UserFileIdUpdate();
        updateData.setOldUserId(oldUserId);
        updateData.setNewUserId(newUserId);
        updateData.setUserAgent(defaultUserAgent);
        return updateData;
    }

    private UserFile saveAndGetBackSavedFileForDefaultUser() {
        uploadSingleFileFor(userId);
        return getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(userId);
    }

    private void uploadSingleFileFor(String userId) {
        saveFilesFor(userId, 1);
    }

    private UserFile fileWithContentAndXmlSchema(byte[] content) {
        UserFile userFile = new UserFile();
        userFile.setXmlSchema("xml-schema");
        userFile.setContent(content);
        return userFile;
    }

    private UserFile fileWithContentAndId(byte[] content, int id) {
        UserFile userFile = fileWithContentAndXmlSchema(content);
        userFile.setId(id);
        return userFile;
    }

    private void saveFilesFor(String userId, int count) {
        for (int i = 0; i < count; i++) {
            saveFileForUser(userId, fileWithContentAndXmlSchema("test-content".getBytes()));
        }
    }

    private void saveFileForUser(String userId, UserFile file) {
        file.setUserAgent(defaultUserAgent);
        storage.save(file, userId);
    }

    private UserFile getFirstUploadedFileAndAssertThatItIsTheOnlyOneAvailableFor(String userId) {
        return getAllFilesForUserAndAssertThatResultSetSizeIsAsExpected(userId, 1).iterator().next();
    }

    private Collection<UserFile> getAllFilesForUserAndAssertThatResultSetSizeIsAsExpected(String userId, int resultSetSize) {
        Collection<UserFile> userFiles = storage.findAllUserFiles(userId);
        assertThat(userFiles.size(), equalTo(resultSetSize));
        return userFiles;
    }

    private String userId() {
        return Long.toString(System.currentTimeMillis());
    }
}
