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
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;

public class UserFileMergeServiceImplTest {
    private UserFileMergeService service = new UserFileMergeServiceImpl();
    private static UserFile file1;
    private static UserFile file2;
    private static byte[] expectedResult;
    private static MergeModule testMergeModule;

    @BeforeClass
    public static void setUp() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        file1 = createUserFileFromFile(1, "file1.xml");
        file2 = createUserFileFromFile(2, "file2.xml");
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
        assertXMLEqual(createSource(expectedResult),
                createSource(mergeResult));
    }

    @Test
    public void shouldMerge4FilesUsingMergeModule() throws Exception {
        List<UserFile> fourUserFiles = Arrays.asList(file1,
                file2,
                createUserFileFromFile(3, "file3.xml", file1.getContent()),
                createUserFileFromFile(4, "file4.xml", file2.getContent()));

        byte[] mergeResult = service.mergeFiles(fourUserFiles, testMergeModule);

        assertXpathEvaluatesTo("2", "count(beans/bean[@name='file1Bean1'])", createSource(mergeResult));
        assertXpathEvaluatesTo("2", "count(beans/bean[@name='file1Bean2'])", createSource(mergeResult));
        assertXpathEvaluatesTo("2", "count(beans/bean[@name='file2Bean1'])", createSource(mergeResult));
        assertXpathEvaluatesTo("2", "count(beans/bean[@name='file2Bean2'])", createSource(mergeResult));
    }

    @Test
    public void ifMerging1File_returnFileContent() throws Exception {
        byte[] mergeResult = service.mergeFiles(Arrays.asList(file1), testMergeModule);
        assertXMLEqual(createSource(file1.getContent()),
                createSource(mergeResult));
    }

    private InputSource createSource(byte[] bytes) {
        return new InputSource(new ByteArrayInputStream(bytes));
    }

    private static UserFile createUserFileFromFile(int id, String fileName) throws IOException {
        return createUserFileFromFile(id, fileName, readBytesFromFile(fileName));
    }

    private static UserFile createUserFileFromFile(int id, String fileName, byte[] content) throws IOException {
        UserFile file = new UserFile(new UploadedFile(fileName, content), "http://xmlSchema");
        file.setId(id);
        return file;
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
