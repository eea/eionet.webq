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
import eionet.webq.dao.orm.UploadedFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationTestContextWithMockSession.class})
public class MergeModulesTest {

    @Autowired
    private MergeModules mergeModules;

    @Test
    public void savesMergeModuleToStorage() throws Exception {
        MergeModule mergeModule = new MergeModule();
        int id = mergeModules.save(mergeModule);

        assertNotNull(mergeModules.findById(id));
    }

    @Test
    public void writesAndReadsDataFromStorage() throws Exception {
        MergeModule moduleToSave = new MergeModule();
        moduleToSave.setName("uniqueShortName");
        moduleToSave.setTitle("this will be displayed to user");
        moduleToSave.setUserName("developer");
        moduleToSave.setXslFile(new UploadedFile("file.xsl", "xsl-content".getBytes()));

        int id = mergeModules.save(moduleToSave);

        MergeModule moduleFromStorage = mergeModules.findById(id);

        assertThat(moduleFromStorage.getName(), equalTo(moduleToSave.getName()));
        assertThat(moduleFromStorage.getTitle(), equalTo(moduleToSave.getTitle()));
        assertThat(moduleFromStorage.getUserName(), equalTo(moduleToSave.getUserName()));
        assertThat(moduleFromStorage.getXslFile().getName(), equalTo(moduleToSave.getXslFile().getName()));
        assertNotNull(moduleFromStorage.getCreated());
    }
}
