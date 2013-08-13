package eionet.webq.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
public class ProjectValidationTest extends AbstractProjectsControllerTest {
    @Test
    public void projectIdIsEmpty() throws Exception {
        MvcResult mvcResult = addNewProjectWithId(EMPTY);
        List<FieldError> errorList = getFieldErrorsFromMvcResultAndAssertThatFieldErrorCountIs(mvcResult, 1);

        assertFieldError(errorList.get(0), "id", "project.id.length");
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
        MvcResult mvcResult = addNewProjectWithId(stringOfLength(256));
        List<FieldError> fieldErrors = getFieldErrorsFromMvcResultAndAssertThatFieldErrorCountIs(mvcResult, 1);

        assertFieldError(fieldErrors.get(0), "id", "project.id.length");
    }

    @Test
    public void projectDescriptionLengthUpperBound() throws Exception {
        assertNoFieldErrors(addNewProjectWithDescription(stringOfLength(2000)));
    }

    @Test
    public void projectDescriptionLengthIsExceeded() throws Exception {
        MvcResult mvcResult = addNewProjectWithDescription(stringOfLength(2001));
        List<FieldError> fieldErrors = getFieldErrorsFromMvcResultAndAssertThatFieldErrorCountIs(mvcResult, 1);

        assertFieldError(fieldErrors.get(0), "description", "project.description.length");
    }

    private String stringOfLength(int length) {
        return StringUtils.repeat("1", length);
    }

    private void assertFieldError(FieldError fieldError, String field, String defaultMessage) {
        assertThat(fieldError.getField(), equalTo(field));
        assertThat(fieldError.getDefaultMessage(), equalTo(defaultMessage));
    }

    private void assertNoFieldErrors(MvcResult result) {
        List<FieldError> errorList = getFieldErrorsFromMvcResultAndAssertThatFieldErrorCountIs(result, 0);

        assertTrue(errorList.isEmpty());
    }

    private MvcResult addNewProjectWithId(String id) throws Exception {
        return addNewProject(id, "description");
    }

    private MvcResult addNewProjectWithDescription(String description) throws Exception {
        return addNewProject("1", description);
    }

    private List<FieldError> getFieldErrorsFromMvcResultAndAssertThatFieldErrorCountIs(MvcResult result, int size) {
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        BindException bindException = (BindException) result.getResolvedException();
        if (bindException == null) {
            if (size == 0) {
                return Collections.emptyList();
            }
            fail("expected bind exception");
        }

        assertThat(bindException.getFieldErrorCount(), equalTo(1));
        return bindException.getFieldErrors();
    }
}
