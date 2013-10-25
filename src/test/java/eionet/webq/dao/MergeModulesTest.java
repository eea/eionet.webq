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
import eionet.webq.dao.orm.MergeModule;
import eionet.webq.dao.orm.MergeModuleXmlSchema;
import eionet.webq.dao.orm.UploadedFile;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static eionet.webq.dao.FileContentUtil.getFileContentRowsCount;
import static eionet.webq.dao.FileContentUtil.getXmlSchemaRowsCount;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
@Transactional
public class MergeModulesTest {

    @Autowired
    private MergeModules mergeModules;
    @Autowired
    private SessionFactory sessionFactory;
    private MergeModule moduleToSave = validMergeModule();

    @Before
    public void before() throws Exception {
        sessionFactory.getCurrentSession().setFlushMode(FlushMode.ALWAYS);
    }

    @Test
    public void savesMergeModuleToStorage() throws Exception {
        int id = mergeModules.save(moduleToSave);

        assertNotNull(mergeModules.findById(id));
    }

    @Test
    public void writesAndReadsDataFromStorage() throws Exception {
        moduleToSave.setTitle("this will be displayed to user");
        moduleToSave.setUserName("developer");
        moduleToSave.setXslFile(new UploadedFile("file.xsl", "xsl-content".getBytes()));

        int id = mergeModules.save(moduleToSave);

        MergeModule moduleFromStorage = mergeModules.findById(id);

        assertThat(moduleFromStorage.getTitle(), equalTo(moduleToSave.getTitle()));
        assertThat(moduleFromStorage.getUserName(), equalTo(moduleToSave.getUserName()));
        assertThat(moduleFromStorage.getXslFile().getName(), equalTo(moduleToSave.getXslFile().getName()));
        assertNotNull(moduleFromStorage.getCreated());
    }

    @Test
    public void allowToAddMultipleXmlSchemas() throws Exception {
        MergeModuleXmlSchema xmlSchema1 = new MergeModuleXmlSchema("http://xml-schema-1");
        MergeModuleXmlSchema xmlSchema2 = new MergeModuleXmlSchema("http://xml-schema-2");
        moduleToSave.setXmlSchemas(Arrays.asList(xmlSchema1, xmlSchema2));

        int id = mergeModules.save(moduleToSave);

        List<MergeModuleXmlSchema> xmlSchemas = mergeModules.findById(id).getXmlSchemas();
        assertThat(xmlSchemas.size(), equalTo(2));
        Iterator<MergeModuleXmlSchema> it = xmlSchemas.iterator();
        assertThat(it.next().getXmlSchema(), equalTo(xmlSchema1.getXmlSchema()));
        assertThat(it.next().getXmlSchema(), equalTo(xmlSchema2.getXmlSchema()));
    }

    @Test
    public void xslSchemaMustStartWithHttpOrHttps() throws Exception {
        MergeModule module = new MergeModule();
        module.setXslFile(new UploadedFile("file.xsl", "xsl-file-content".getBytes()));
        MergeModuleXmlSchema schema1 = new MergeModuleXmlSchema();
        MergeModuleXmlSchema schema2 = new MergeModuleXmlSchema();
        schema1.setXmlSchema("http://some.schema");
        schema2.setXmlSchema("https://another.schema");
        module.setXmlSchemas(Arrays.asList(schema1, schema2));

        mergeModules.save(module);
    }

    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void xslSchemaPrefixMisspelled() throws Exception {
        MergeModule module = new MergeModule();
        module.setXslFile(new UploadedFile("file.xsl", "xsl-file-content".getBytes()));
        MergeModuleXmlSchema schema1 = new MergeModuleXmlSchema();
        schema1.setXmlSchema("http:/only.one.forward.slash");
        module.setXmlSchemas(Arrays.asList(schema1));

        mergeModules.save(module);
    }

    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void xmlSchemaDoesNotHaveRequiredPrefix() throws Exception {
        MergeModule module = moduleWithFileName("file.xsl");
        MergeModuleXmlSchema mergeModuleXmlSchema = new MergeModuleXmlSchema();
        mergeModuleXmlSchema.setXmlSchema("some.schema.without.http.prefix");
        module.setXmlSchemas(Arrays.asList(mergeModuleXmlSchema));

        mergeModules.save(module);
    }

    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void xmlSchemaShouldNotBeEmpty() throws Exception {
        MergeModule module = moduleWithFileName("file.xsl");
        module.setXmlSchemas(Arrays.asList(new MergeModuleXmlSchema()));

        mergeModules.save(module);
    }

    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void shouldHaveAtLeastOneXmlSchema() throws Exception {
        MergeModule module = new MergeModule();
        module.setXslFile(new UploadedFile("file.xsl", "content".getBytes()));

        mergeModules.save(module);
    }

    @Test
    public void listsAllAvailableModules() throws Exception {
        mergeModules.save(validMergeModule());
        mergeModules.save(validMergeModule());

        assertThat(mergeModules.findAll().size(), equalTo(2));
    }

    @Test
    public void whenLoadingAllModules_distinctRootEntity() throws Exception {
        MergeModule module = new MergeModule();
        module.setXmlSchemas(Arrays.asList(new MergeModuleXmlSchema("http://one"), new MergeModuleXmlSchema("http://two")));
        mergeModules.save(module);

        assertThat(mergeModules.findAll().size(), equalTo(1));
    }

    @Test
    public void removesMergeModulesByIds() throws Exception {
        int id1 = mergeModules.save(validMergeModule());
        int id2 = mergeModules.save(validMergeModule());

        assertThat(mergeModules.findAll().size(), equalTo(2));

        mergeModules.remove(id1, id2);

        assertThat(mergeModules.findAll().size(), equalTo(0));
    }

    @Test(expected = ConstraintViolationException.class)
    public void modelNameMustBeUnique() throws Exception {
        mergeModules.save(moduleWithFileName("uniqueName"));
        mergeModules.save(moduleWithFileName("uniqueName"));
    }

    @Test
    public void findsMergeModuleByItsUniqueName() throws Exception {
        MergeModule module = moduleWithFileName("uniqueModuleName");
        int id = mergeModules.save(module);

        assertThat(mergeModules.findByFileName(module.getXslFile().getName()).getId(), equalTo(id));
    }

    @Test
    public void editAllowsToChangeAllAvailableFields() throws Exception {
        MergeModule module = new MergeModule();
        module.setUserName("username");
        module.setTitle("titleToEdit");
        MergeModuleXmlSchema mergeModuleXmlSchema = new MergeModuleXmlSchema();
        mergeModuleXmlSchema.setXmlSchema("http://schemaToDrop");
        module.setXmlSchemas(Arrays.asList(mergeModuleXmlSchema));
        module.setXslFile(new UploadedFile("file.name", "content-to-update".getBytes()));

        int id = mergeModules.save(module);

        String newTitle = "newTitle";
        String newSchema = "http://newSchema";
        String newXslFileName = "newXslFileName";
        byte[] xslFileContent = "new-xsl-content".getBytes();
        MergeModuleXmlSchema newXmlSchema = new MergeModuleXmlSchema();
        newXmlSchema.setXmlSchema(newSchema);

        module.setTitle(newTitle);
        module.setXmlSchemas(Arrays.asList(newXmlSchema));
        module.setXslFile(new UploadedFile(newXslFileName, xslFileContent));

        mergeModules.update(module);

        MergeModule moduleById = mergeModules.findById(id);
        assertThat(moduleById.getTitle(), equalTo(newTitle));
        assertThat(moduleById.getXmlSchemas().size(), equalTo(1));
        assertThat(moduleById.getXmlSchemas().iterator().next().getXmlSchema(), equalTo(newSchema));
        assertThat(moduleById.getXslFile().getName(), equalTo(newXslFileName));
        assertThat(moduleById.getXslFile().getContent().getFileContent(), equalTo(xslFileContent));
    }

    @Test
    public void whenUpdatingModule_ifContentIsNotSet_DoNotWipeIt() throws Exception {
        byte[] initialContent = "xsl-file-content".getBytes();
        MergeModule module = validMergeModule();
        module.setXslFile(new UploadedFile("file.xsl", initialContent));
        int id = mergeModules.save(module);

        MergeModule updateModule = new MergeModule();
        updateModule.setId(id);
        updateModule.setXslFile(new UploadedFile());
        mergeModules.update(updateModule);

        assertThat(mergeModules.findById(id).getXslFile().getContent().getFileContent(), equalTo(initialContent));
    }

    @Test
    public void afterUpdate_updatedTimestampIsSet() throws Exception {
        int id = mergeModules.save(moduleToSave);
        MergeModule moduleToUpdate = mergeModules.findById(id);
        assertNull(moduleToUpdate.getUpdated());

        mergeModules.update(moduleToUpdate);

        assertNotNull(mergeModules.findById(id).getUpdated());
    }

    @Test
    public void onDeleteAlsoRemovesUploadedFiles() throws Exception {
        moduleToSave.setXslFile(new UploadedFile("file.name", "xsl-file-content".getBytes()));
        int id = mergeModules.save(moduleToSave);

        assertThat(getFileContentRowsCount(sessionFactory), equalTo(1));

        mergeModules.remove(id);
        assertThat(getFileContentRowsCount(sessionFactory), equalTo(0));
    }

    @Test
    public void onDeleteAlsoRemovesXmlSchemas() throws Exception {
        moduleToSave.setXmlSchemas(Arrays.asList(new MergeModuleXmlSchema("http://one"), new MergeModuleXmlSchema("http://two")));
        int id = mergeModules.save(moduleToSave);

        assertThat(getXmlSchemaRowsCount(sessionFactory), equalTo(2));

        mergeModules.remove(id);

        assertThat(getXmlSchemaRowsCount(sessionFactory), equalTo(0));
    }

    @Test
    public void shouldFindMergeModuleBySchema() throws Exception {
        String schemaToFind = "http://schema1";
        moduleToSave.setXmlSchemas(Arrays.asList(new MergeModuleXmlSchema(schemaToFind),
                new MergeModuleXmlSchema("http://schema2")));
        int id = mergeModules.save(moduleToSave);

        Collection<MergeModule> byXmlSchema = mergeModules.findByXmlSchema(schemaToFind);
        assertThat(byXmlSchema.size(), equalTo(1));
        assertThat(byXmlSchema.iterator().next().getId(), equalTo(id));
    }

    private MergeModule moduleWithFileName(String uniqueModuleName) {
        MergeModule module = validMergeModule();
        UploadedFile xslFile = new UploadedFile();
        xslFile.setName(uniqueModuleName);
        module.setXslFile(xslFile);
        return module;
    }

    private MergeModule validMergeModule() {
        MergeModule module = new MergeModule();
        module.setXmlSchemas(Arrays.asList(new MergeModuleXmlSchema("http://xml.schema")));
        return module;
    }
}
