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
package eionet.webq.web.controller;

import eionet.webq.dao.MergeModules;
import eionet.webq.dao.orm.MergeModule;
import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.web.AbstractContextControllerTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class FileDownloadControllerIntegrationTest extends AbstractContextControllerTests {
    @Autowired
    private MergeModules modules;
    @Test
    public void allowsToDownloadMergeFiles() throws Exception {
        MergeModule module = new MergeModule();
        UploadedFile xslFile = new UploadedFile("merge.xsl", "merge-file-content".getBytes());
        module.setXslFile(xslFile);
        module.setName("mergeModuleName");
        modules.save(module);

        request(MockMvcRequestBuilders.get("/download/merge/file/" + module.getName()))
                .andExpect(MockMvcResultMatchers.content().bytes(xslFile.getContent().getFileContent()));
    }
}
