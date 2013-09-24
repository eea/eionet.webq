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
package eionet.webq.web.controller.cdr;

import eionet.webq.converter.CdrRequestConverter;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dto.CdrRequest;
import eionet.webq.service.CDREnvelopeService;
import eionet.webq.service.FileNotAvailableException;
import eionet.webq.service.UserFileService;
import eionet.webq.service.WebFormService;
import org.hamcrest.core.StringStartsWith;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static eionet.webq.service.CDREnvelopeService.XmlFile;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class IntegrationWithCDRControllerTest {
    private static final String XML_SCHEMA = "schema";
    @InjectMocks
    IntegrationWithCDRController controller;
    @Mock
    CDREnvelopeService envelopeService;
    @Mock
    WebFormService webFormService;
    @Mock
    UserFileService userFileService;
    @Mock
    CdrRequestConverter converter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(converter.convert(any(HttpServletRequest.class))).thenReturn(new CdrRequest());
    }

    @Test
    public void noRedirectFromMenuIfFilesAmountIsZero() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(0);

        assertNoRedirectOnMenuCall();
    }

    @Test
    public void noRedirectIfWebFormsAmountIsZero() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(1);
        when(webFormService.findWebFormsForSchemas(anyCollectionOf(String.class))).thenReturn(Collections.<ProjectFile>emptyList());

        assertNoRedirectOnMenuCall();
    }

    @Test
    public void noRedirectIfFilesAmountMoreThan1() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(2);
        thereWillBeWebFormsAmountOf(1);

        assertNoRedirectOnMenuCall();
    }

    @Test
    public void noRedirectIfWebFormsSizeIsMoreThanOne() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(1);
        thereWillBeWebFormsAmountOf(2);

        assertNoRedirectOnMenuCall();
    }

    @Test
    public void noRedirectIfWebFormAndFileIsForDifferentSchemas() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(1);

        ProjectFile webFormForAnotherSchema = new ProjectFile();
        webFormForAnotherSchema.setXmlSchema(XML_SCHEMA + "-another-schema");
        when(webFormService.findWebFormsForSchemas(anyCollectionOf(String.class))).thenReturn(Arrays.asList(webFormForAnotherSchema));

        assertNoRedirectOnMenuCall();
    }

    @Test
    public void noRedirectIf1WebFormAnd1FileForSameSchemaButAddParameterSet() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(1);
        thereWillBeWebFormsAmountOf(1);

        CdrRequest menuParameters = new CdrRequest();
        menuParameters.setNewFormCreationAllowed(true);
        when(converter.convert(any(HttpServletRequest.class))).thenReturn(menuParameters);

        assertNoRedirectOnMenuCall();
    }

    @Test
    public void redirectsToWebFormIfThereAreOnlyOneWebFormAndOneFileAvailableForTheSameSchema() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(1);
        thereWillBeWebFormsAmountOf(1);
        when(webFormService.findActiveWebFormById(anyInt())).thenReturn(new ProjectFile());

        assertThat(controller.menu(new MockHttpServletRequest(), mock(Model.class)), StringStartsWith.startsWith("redirect:/xform/"));
    }

    @Test
    public void redirectsToWebFormIfThereIsOneWebFormNoFilesAndNewFilesCreationIsAllowed() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(0);
        thereWillBeWebFormsAmountOf(1);
        when(webFormService.findActiveWebFormById(anyInt())).thenReturn(new ProjectFile());
        CdrRequest menuParameters = new CdrRequest();
        menuParameters.setNewFormCreationAllowed(true);
        when(converter.convert(any(HttpServletRequest.class))).thenReturn(menuParameters);

        assertThat(controller.menu(new MockHttpServletRequest(), mock(Model.class)), StringStartsWith.startsWith("redirect:/startWebform"));
    }

    private void thereWillBeWebFormsAmountOf(int amount) {
        ProjectFile file = new ProjectFile();
        file.setXmlSchema(XML_SCHEMA);
        ArrayList<ProjectFile> projectFiles = new ArrayList<ProjectFile>();
        for (int i = 0; i < amount; i++) {
            projectFiles.add(file);
        }
        when(webFormService.findWebFormsForSchemas(anyCollectionOf(String.class))).thenReturn(projectFiles);
    }

    private void assertNoRedirectOnMenuCall() throws FileNotAvailableException {
        assertThat(controller.menu(new MockHttpServletRequest(), mock(Model.class)), equalTo("deliver_menu"));
    }

    private void getXmlFilesWillReturnFilesAmountOf(int amount) {
        LinkedMultiValueMap<String, XmlFile> files = new LinkedMultiValueMap<String, XmlFile>();
        when(envelopeService.getXmlFiles(any(CdrRequest.class))).thenReturn(files);

        for (int i = 0; i < amount; i++) {
            files.add(XML_SCHEMA, new XmlFile(null, null, XML_SCHEMA));
        }
    }
}
