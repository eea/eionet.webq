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

import eionet.webq.dao.orm.MergeModule;
import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.dao.orm.UserFile;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class UserFileMergeServiceImplTest {
    private UserFileMergeService service = new UserFileMergeServiceImpl();
    private static UserFile file1;
    private static UserFile file2;
    private static byte[] expectedResult;
    private static MergeModule testMergeModule;

    @BeforeClass
    public static void setUp() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        file1 = createUserFileFromFile("file1.xml");
        file2 = createUserFileFromFile("file2.xml");
        expectedResult = readBytesFromFile("2_files_merge_result.xml");
        testMergeModule = new MergeModule();
        testMergeModule.setXslFile(new UploadedFile("merge_file", readBytesFromFile("test_merge.xsl")));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        XMLUnit.setIgnoreWhitespace(false);
    }

    @Test
    public void shouldMerge2FilesUsingMergeModule() throws Exception {
        byte[] mergeResult = service.mergeFiles(Arrays.asList(file1, file2), testMergeModule);
        assertXMLEqual(new InputSource(new ByteArrayInputStream(expectedResult)),
                new InputSource(new ByteArrayInputStream(mergeResult)));
    }


    private static UserFile createUserFileFromFile(String fileName) throws IOException {
        return new UserFile(new UploadedFile(fileName, readBytesFromFile(fileName)), "http://xmlSchema");
    }

    private static byte[] readBytesFromFile(String fileName) throws IOException {
        FileInputStream input = null;
        try {
            input = new FileInputStream("src/test/resources/merge/" + fileName);
            return IOUtils.toByteArray(input);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }
}
