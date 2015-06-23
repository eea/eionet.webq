package eionet.webq.service.impl.project.export;

import configuration.ApplicationTestContextWithMockSession;
import eionet.webq.dao.orm.ProjectFile;
import java.util.Arrays;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Nikolaos Nakas <nn@eworx.gr>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class ProjectMetadataSerializerTest {
    
    @Autowired
    private ProjectMetadataSerializer serializer;
    
    @Test
    public void testSerializedFields() throws MetadataSerializerException {
        ProjectFile originalFile = new ProjectFile();
        originalFile.setActive(true);
        originalFile.setCreated(new Date());
        originalFile.setDescription("description");
        originalFile.setEmptyInstanceUrl("http://some.instance.url");
        originalFile.setFileContent(new byte[] {0, 1, 0, 3, 4});
        originalFile.setFileName("file.txt");
        originalFile.setId(4);
        originalFile.setLocalForm(true);
        originalFile.setNewXmlFileName("newxml.xml");
        originalFile.setProjectId(10);
        originalFile.setProjectIdentifier("proj-id");
        originalFile.setRemoteFileUrl("http://some.remote.file");
        originalFile.setRemoteForm(true);
        originalFile.setTitle("title");
        originalFile.setUpdated(new Date());
        originalFile.setUserName("johndoe");
        originalFile.setWebformLink("http://some.webform.link");
        originalFile.setXmlSchema("http://some.schema.url");
        
        ProjectMetadata originalMetadata = new ProjectMetadata(Arrays.asList(originalFile));
        ProjectMetadata deserializedMetadata = this.serializer.deserialize(this.serializer.serialize(originalMetadata));
        
        Assert.assertEquals(1, deserializedMetadata.getProjectFiles().length);
        
        ProjectFile deserializedFile = deserializedMetadata.getProjectFiles()[0];
        
        // Fields that must be serialized.
        Assert.assertEquals(originalFile.isActive(), deserializedFile.isActive());
        Assert.assertEquals(originalFile.getDescription(), deserializedFile.getDescription());
        Assert.assertEquals(originalFile.getEmptyInstanceUrl(), deserializedFile.getEmptyInstanceUrl());
        Assert.assertEquals(originalFile.getFileName(), deserializedFile.getFileName());
        Assert.assertEquals(originalFile.isLocalForm(), deserializedFile.isLocalForm());
        Assert.assertEquals(originalFile.getNewXmlFileName(), deserializedFile.getNewXmlFileName());
        Assert.assertEquals(originalFile.getRemoteFileUrl(), deserializedFile.getRemoteFileUrl());
        Assert.assertEquals(originalFile.isRemoteForm(), deserializedFile.isRemoteForm());
        Assert.assertEquals(originalFile.getTitle(), deserializedFile.getTitle());
        Assert.assertEquals(originalFile.getXmlSchema(), deserializedFile.getXmlSchema());
        
        // Fields that must be ignored at serialization time.
        Assert.assertNotEquals(originalFile.getCreated(), deserializedFile.getCreated());
        Assert.assertNotEquals(originalFile.getFileContent(), deserializedFile.getFileContent());
        Assert.assertNotEquals(originalFile.getId(), deserializedFile.getId());
        Assert.assertNotEquals(originalFile.getProjectIdentifier(), deserializedFile.getProjectIdentifier());
        Assert.assertNotEquals(originalFile.getUpdated(), deserializedFile.getUpdated());
        Assert.assertNotEquals(originalFile.getUserName(), deserializedFile.getUserName());
        Assert.assertNotEquals(originalFile.getWebformLink(), deserializedFile.getWebformLink());
    }
    
}
