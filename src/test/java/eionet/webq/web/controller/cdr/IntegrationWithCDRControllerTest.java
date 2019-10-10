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
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.CdrRequest;
import eionet.webq.service.CDREnvelopeService;
import eionet.webq.service.CDREnvelopeService.XmlFile;
import eionet.webq.service.FileNotAvailableException;
import eionet.webq.service.UserFileService;
import eionet.webq.service.WebFormService;
import eionet.webq.web.controller.util.WebformUrlProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
public class IntegrationWithCDRControllerTest {
    private static final String XML_SCHEMA = "schema";
    private final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
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
    @Mock
    Model model;
    @Mock
    WebformUrlProvider webformUrlProvider;
    CdrRequest cdrRequest = new CdrRequest();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(converter.convert(any(HttpServletRequest.class))).thenReturn(cdrRequest);
        cdrRequest.setSessionId("sessionId");
        when(webformUrlProvider.getWebformPath(any(ProjectFile.class))).thenReturn("/xform/");
    }

    @Test
    public void noRedirectFromMenuIfFilesAmountIsZero() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(0);
        thereWillBeWebFormsAmountOf(1);

        assertNoRedirectOnMenuCall();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfWebFormsAmountIsZero() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(1);
        when(webFormService.findWebFormsForSchemas(anyCollectionOf(String.class))).thenReturn(
                Collections.<ProjectFile>emptyList());

        controller.webQMenu(mockRequest, model);
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
        when(webFormService.findWebFormsForSchemas(anyCollectionOf(String.class))).thenReturn(
                Arrays.asList(webFormForAnotherSchema));

        assertNoRedirectOnMenuCall();
    }

    @Test
    public void noRedirectIf1WebFormAnd1FileForSameSchemaButAddParameterSet() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(1);
        thereWillBeWebFormsAmountOf(1);

        cdrRequest.setNewFormCreationAllowed(true);

        assertNoRedirectOnMenuCall();
    }

    @Test
    public void redirectsToWebFormIfThereAreOnlyOneWebFormAndOneFileAvailableForTheSameSchema() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(1);
        thereWillBeWebFormsAmountOf(1);
        when(webFormService.findActiveWebFormById(anyInt())).thenReturn(new ProjectFile());

        assertThat(controller.webQMenu(mockRequest, model), startsWith("redirect:/xform/"));
    }

    @Test
    public void redirectsToWebFormIfThereIsOneWebFormNoFilesAndNewFilesCreationIsAllowed() throws Exception {
        getXmlFilesWillReturnFilesAmountOf(0);
        thereWillBeWebFormsAmountOf(1);
        when(webFormService.findActiveWebFormById(anyInt())).thenReturn(new ProjectFile());
        cdrRequest.setNewFormCreationAllowed(true);

        assertThat(controller.webQMenu(mockRequest, model), startsWith("redirect:/xform"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void webQEditThrowsExceptionIfNoXmlSchemaSpecified() throws Exception {
        controller.webQEdit(mockRequest, model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void webQEditThrowsExceptionIfNoWebFormsFoundForXmlSchemaRequested() throws Exception {
        thereWillBeWebFormsAmountOf(0);
        cdrRequest.setSchema(XML_SCHEMA);

        controller.webQEdit(mockRequest, model);
    }

    @Test
    public void webQEditRedirectsToWebForm() throws Exception {
        thereWillBeWebFormsAmountOf(1);
        cdrRequestWillContainXmlSchemaAndInstanceUrl();

        assertThat(controller.webQEdit(mockRequest, model), startsWith("redirect:/xform/"));
    }

    @Test
    public void webQEditRedirectsToWebQMenuIfFormsAmountMoreThan1() throws Exception {
        thereWillBeWebFormsAmountOf(2);
        cdrRequestWillContainXmlSchemaAndInstanceUrl();

        assertThat(controller.webQEdit(mockRequest, model), equalTo("deliver_menu"));
    }

    @Test
    public void savesFileWithCdrParametersBeforeRedirectToWebForm() throws Exception {
        cdrRequest.setNewFileName("newName");
        cdrRequest.setInstanceTitle("title");
        cdrRequest.setEnvelopeUrl("envelope.url");
        cdrRequest.setBasicAuthorization("basic.auth");

        prepareRedirectToNewWebFormCase();

        controller.webQMenu(mockRequest, model);

        ArgumentCaptor<UserFile> userFileArgument = ArgumentCaptor.forClass(UserFile.class);
        verify(userFileService).saveBasedOnWebForm(userFileArgument.capture(), any(ProjectFile.class));

        UserFile userFile = userFileArgument.getValue();
        assertThat(userFile.getName(), equalTo(cdrRequest.getNewFileName()));
        assertThat(userFile.getTitle(), equalTo(cdrRequest.getInstanceTitle()));
        assertThat(userFile.getEnvelope(), equalTo(cdrRequest.getEnvelopeUrl()));
        assertThat(userFile.getAuthorization(), equalTo(cdrRequest.getBasicAuthorization()));
        assertTrue(userFile.isFromCdr());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void webQEditWillAddXmlFilesToModelIfThereAreMultipleForms() throws Exception {
        thereWillBeWebFormsAmountOf(2);
        cdrRequestWillContainXmlSchemaAndInstanceUrl();

        controller.webQEdit(mockRequest, model);
        ArgumentCaptor<MultiValueMap> xmlFilesArgument = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(model).addAttribute(eq("xmlFiles"), xmlFilesArgument.capture());
        MultiValueMap<String, XmlFile> xmlFiles = xmlFilesArgument.getValue();

        assertTrue(xmlFiles.containsKey(XML_SCHEMA));
        List<XmlFile> xmlFilesForSchema = xmlFiles.get(XML_SCHEMA);
        assertThat(xmlFilesForSchema.size(), equalTo(1));
        assertThat(xmlFilesForSchema.get(0).getFullName(), equalTo(cdrRequest.getInstanceUrl()));
    }

    @Test
    // TODO Fix or remove
    public void exceptionHandlerFormUnavailable() throws Exception {
        String queryString = "param1=value1&param2=value2";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setQueryString(queryString);

        MockHttpServletResponse response = new MockHttpServletResponse();
       // controller.formUnavailable(request, response);

//        assertThat(response.getHeader("Location"), equalTo(controller.webQFallBackUrl + "?" + queryString));
    }

    private void prepareRedirectToNewWebFormCase() {
        cdrRequest.setNewFormCreationAllowed(true);
        thereWillBeWebFormsAmountOf(1);
        getXmlFilesWillReturnFilesAmountOf(0);
    }

    private void cdrRequestWillContainXmlSchemaAndInstanceUrl() {
        cdrRequest.setSchema(XML_SCHEMA);
        cdrRequest.setInstanceUrl("http://instance.url");
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
        assertThat(controller.webQMenu(mockRequest, model), equalTo("deliver_menu"));
    }

    private void getXmlFilesWillReturnFilesAmountOf(int amount) {
        LinkedMultiValueMap<String, XmlFile> files = new LinkedMultiValueMap<String, XmlFile>();
        when(envelopeService.getXmlFiles(any(CdrRequest.class))).thenReturn(files);

        for (int i = 0; i < amount; i++) {
            files.add(XML_SCHEMA, new XmlFile(null, null));
        }
    }
}
