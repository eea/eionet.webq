package eionet.webq.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.repeat;

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
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ProjectValidationTest extends AbstractProjectsControllerTests {

    public static final String VALIDATION_CODE_BODY = ".projectEntry.";
    public static final String LENGTH_VALIDATION_CODE_PREFIX = "Length" + VALIDATION_CODE_BODY;
    public static final String PATTERN_VALIDATION_CODE_PREFIX = "Pattern" + VALIDATION_CODE_BODY;

    @Test
    public void projectIdIsEmpty() throws Exception {
        assertLengthFieldError(getFirstAndOnlyFieldError(addNewProjectWithId(EMPTY)), "projectId");
    }

    @Test
    public void projectIdSizeLowerBound() throws Exception {
        assertNoFieldErrors(addNewProjectWithId("1"));
    }

    @Test
    public void projectIdSizeUpperBound() throws Exception {
        assertNoFieldErrors(addNewProjectWithId(stringOfLength(255)));
    }

    @Test
    public void projectIdLengthIsExceeded() throws Exception {
        assertLengthFieldError(getFirstAndOnlyFieldError(addNewProjectWithId(stringOfLength(256))), "projectId");
    }

    @Test
    public void projectDescriptionLengthUpperBound() throws Exception {
        assertNoFieldErrors(addNewProjectWithDescription(stringOfLength(2000)));
    }

    @Test
    public void projectDescriptionLengthIsExceeded() throws Exception {
        assertLengthFieldError(getFirstAndOnlyFieldError(addNewProjectWithDescription(stringOfLength(2001))), "description");
    }

    @Test
    public void projectIdMustBeUnique() throws Exception {
        String duplicateId = "1";
        assertNoFieldErrors(addNewProjectWithId(duplicateId));

        assertFieldError(getFirstAndOnlyFieldError(addNewProjectWithId(duplicateId)), "projectId", "duplicate.project.id");
    }

    @Test
    public void allowedCharactersInProjectId() throws Exception {
        String allowed = "abcdefghiklmnopqrstvxyz";
        allowed += allowed.toUpperCase();
        allowed += "0123456789-._~";
        for (char allowedSymbol : allowed.toCharArray()) {
            assertNoFieldErrors(addNewProjectWithId(repeat(allowedSymbol, 3)));
        }
    }

    @Test
    public void notAllowedCharactersInProjectId() throws Exception {
        String notAllowed = "öäüõœ∑´®†¥¨ˆøπ“‘åß∂ƒ©˙∆˚¬…æ«Ω≈ç√∫˜µªº=`\"\\/";
        notAllowed += " ";
        for (char c : notAllowed.toCharArray()) {
            assertFieldError(getFirstAndOnlyFieldError(addNewProjectWithId(repeat(c, 3))), "projectId",
                    PATTERN_VALIDATION_CODE_PREFIX + "projectId");
        }
    }

    private String stringOfLength(int length) {
        return repeat("1", length);
    }

    private void assertLengthFieldError(FieldError fieldError, String field) {
        assertFieldError(fieldError, field, LENGTH_VALIDATION_CODE_PREFIX + field);
    }

    private MvcResult addNewProjectWithId(String id) throws Exception {
        return addNewProject(id, "description");
    }

    private MvcResult addNewProjectWithDescription(String description) throws Exception {
        return addNewProject("1", description);
    }

    @Override
    protected String bindingResultPropertyNameInModel() {
        return "org.springframework.validation.BindingResult.projectEntry";
    }
}
