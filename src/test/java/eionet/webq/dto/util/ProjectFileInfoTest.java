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
package eionet.webq.dto.util;

import eionet.webq.dto.ProjectFile;
import eionet.webq.dto.UploadedFile;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectFileInfoTest {

    @Test
    public void fileIsNewIfIdIsZero() throws Exception {
        assertTrue(ProjectFileInfo.isNew(new ProjectFile()));
    }

    @Test
    public void fileExistIfIdIsGreaterThanZero() throws Exception {
        ProjectFile file = new ProjectFile();
        file.setId(1);
        assertFalse(ProjectFileInfo.isNew(file));
    }

    @Test
    public void fileIsEmptyIfItsSizeIsZero() throws Exception {
        ProjectFile file = new ProjectFile();
        file.setFile(new UploadedFile(null, new byte[0]));
        assertTrue(ProjectFileInfo.fileIsEmpty(file));
    }

    @Test
    public void fileIsNotEmpty() throws Exception {
        ProjectFile file = new ProjectFile();
        file.setFile(new UploadedFile(null, new byte[1]));
        assertFalse(ProjectFileInfo.fileIsEmpty(file));
    }
}
