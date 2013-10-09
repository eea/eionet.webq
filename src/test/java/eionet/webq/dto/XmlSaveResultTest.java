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
package eionet.webq.dto;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 */
public class XmlSaveResultTest {

    @Test
    public void returnErrorForEmptyResult() throws Exception {
        assertThat(XmlSaveResult.valueOf("").getCode(), equalTo(0));
        assertThat(XmlSaveResult.valueOf(null).getCode(), equalTo(0));
    }

    @Test
    public void firstCharacterIsCodeInteger() throws Exception {
        assertThat(XmlSaveResult.valueOf("0response").getCode(), equalTo(0));
        assertThat(XmlSaveResult.valueOf("1response").getCode(), equalTo(1));
    }

    @Test
    public void returnsSuccessMessageForCode1() throws Exception {
        assertTrue(XmlSaveResult.valueOf("1file.xml").getMessage().contains("success"));
    }

    @Test
    public void returnsErrorMessageForCode0() throws Exception {
        assertTrue(XmlSaveResult.valueOf("0file.xml").getMessage().contains("Error"));
    }

    @Test
    public void extractsMessageFromResponse() throws Exception {
        assertTrue(XmlSaveResult.valueOf("1file.xml").getMessage().contains("file.xml"));
    }

    @Test
    public void ifResponseCodeNotInDefaultOnesResultWillBeError() throws Exception {
        assertTrue(XmlSaveResult.valueOf("5file.xml").getMessage().contains("Error"));
    }
}
