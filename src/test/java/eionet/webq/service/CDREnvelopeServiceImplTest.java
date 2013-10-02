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

import eionet.webq.dao.orm.UploadedFile;
import eionet.webq.dao.orm.UserFile;
import eionet.webq.dto.CdrRequest;
import eionet.webq.dto.XmlSaveResult;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfig;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static eionet.webq.service.CDREnvelopeService.XmlFile;
import static java.util.Collections.singletonMap;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 */
public class CDREnvelopeServiceImplTest {

    private CdrRequest parametersWithUrl = createWebQMenuParameters("http://cdr-envelope-service.eu");
    @InjectMocks
    private CDREnvelopeServiceImpl cdrEnvelopeService;
    @Mock
    private XmlRpcClient xmlRpcClient;
    @Mock
    private RestOperations restOperations;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void setCorrectUrlToClientConfig() throws Exception {
        cdrEnvelopeService.getXmlFiles(parametersWithUrl);

        ArgumentCaptor<XmlRpcClientConfigImpl> configCaptor = ArgumentCaptor.forClass(XmlRpcClientConfigImpl.class);
        verify(xmlRpcClient).execute(configCaptor.capture(), anyString(), anyList());
        XmlRpcClientConfigImpl config = configCaptor.getValue();

        assertThat(config.getServerURL().toString(), equalTo(parametersWithUrl.getEnvelopeUrl()));
    }

    @Test
    public void setAuthorizationInfoIfPresent() throws Exception {
        CdrRequest cdrRequest =
                createWebQMenuParametersWithAuthorization("http://cdr-envelope-service.eu", "username", "password");
        cdrEnvelopeService.getXmlFiles(cdrRequest);

        ArgumentCaptor<XmlRpcClientConfigImpl> configCaptor = ArgumentCaptor.forClass(XmlRpcClientConfigImpl.class);
        verify(xmlRpcClient).execute(configCaptor.capture(), anyString(), anyList());
        XmlRpcClientConfigImpl config = configCaptor.getValue();

        assertThat(config.getBasicUserName(), equalTo(cdrRequest.getUserName()));
        assertThat(config.getBasicPassword(), equalTo(cdrRequest.getPassword()));
    }

    @Test
    public void convertsGetXmlFilesServiceResponse() throws Exception {
        Object[] firstFileData = {"file name", "file title"};
        Object[] secondFileData = {"another name", "another title"};
        String xmlSchema = "xml-schema";
        Map<String,Object[]> expectedFormat = singletonMap(xmlSchema, new Object[]{firstFileData, secondFileData});
        whenGetXmlFilesRequest().thenReturn(expectedFormat);

        MultiValueMap<String, XmlFile> xmlFiles = cdrEnvelopeService.getXmlFiles(parametersWithUrl);
        assertThat(xmlFiles.size(), equalTo(1));
        assertTrue(xmlFiles.containsKey(xmlSchema));

        List<XmlFile> filesData = xmlFiles.get(xmlSchema);
        Iterator<XmlFile> it = filesData.iterator();

        assertThat(filesData.size(), equalTo(2));
        assertThat(it.next().getFullName(), equalTo(firstFileData[0]));
        assertThat(it.next().getFullName(), equalTo(secondFileData[0]));
    }

    @Test(expected = CDREnvelopeException.class)
    public void wrapsRpcExceptionIntoRuntimeException() throws Exception {
        whenGetXmlFilesRequest().thenThrow(new XmlRpcException("error"));

        cdrEnvelopeService.getXmlFiles(parametersWithUrl);
    }

    @Test(expected = CDREnvelopeException.class)
    public void throwsExceptionWhenUnexpectedResponseFormat() throws Exception {
        whenGetXmlFilesRequest().thenReturn(singletonMap("schema", new Object[]{Collections.emptyList()}));

        cdrEnvelopeService.getXmlFiles(parametersWithUrl);
    }

    @Test
    public void onSaveXmlUseEnvelopeUrlWithRemoteMethodName() throws Exception {
        restOperationWillReturnResponseStringAndStatus(HttpStatus.OK);
        cdrEnvelopeService.saveXmlFilesMethod = "save";

        UserFile file = fileFromCdr();
        file.setEnvelope(parametersWithUrl.getEnvelopeUrl());
        cdrEnvelopeService.pushXmlFile(file);

        verify(restOperations).postForEntity(eq(file.getEnvelope() + "/save"), anyObject(), any(Class.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void setRequiredParametersAndAuthorizationHeaderOnSaveXml() throws Exception {
        restOperationWillReturnResponseStringAndStatus(HttpStatus.OK);
        byte[] content = "file-content".getBytes();
        String fileName = "file.name";

        UserFile file = new UserFile(new UploadedFile(fileName, content), "");
        file.setFromCdr(true);
        file.setAuthorization("Basic 123");

        cdrEnvelopeService.pushXmlFile(file);

        ArgumentCaptor<Object> requestCaptor = ArgumentCaptor.forClass(Object.class);
        verify(restOperations).postForEntity(anyString(), requestCaptor.capture(), any(Class.class));

        HashSet<String> requestParameters = new HashSet<String>();
        requestParameters.add("file");
        requestParameters.add("file_id");
        requestParameters.add("title");

        HttpEntity<MultiValueMap<String, Object>> request = (HttpEntity<MultiValueMap<String, Object>>) requestCaptor.getValue();
        assertTrue(request.getHeaders().containsKey("Authorization"));
        assertThat(request.getBody().keySet(), IsEqual.<Set<String>>equalTo(requestParameters));
    }

    @Test
    public void saveXmlWillReturnXmlSaveResult() throws Exception {
        restOperationWillReturnResponseStringAndStatus(HttpStatus.OK);

        XmlSaveResult xmlSaveResult = cdrEnvelopeService.pushXmlFile(fileFromCdr());

        assertNotNull(xmlSaveResult);
        assertThat(xmlSaveResult.getCode(), equalTo(1));
    }

    @Test
    public void ifResponseStatusIsNotHttpOkReturnResultWithError() throws Exception {
        restOperationWillReturnResponseStringAndStatus(HttpStatus.BAD_REQUEST);

        XmlSaveResult xmlSaveResult = cdrEnvelopeService.pushXmlFile(fileFromCdr());

        assertThat(xmlSaveResult.getCode(), equalTo(0));
    }

    @Test(expected = CDREnvelopeException.class)
    public void throwsExceptionWhenUrlIsMalformed() throws Exception {
        cdrEnvelopeService.getXmlFiles(createWebQMenuParameters("malformed-url"));
    }

    private UserFile fileFromCdr() {
        UserFile userFile = new UserFile();
        userFile.setFromCdr(true);
        return userFile;
    }

    private void restOperationWillReturnResponseStringAndStatus(HttpStatus status) {
        when(restOperations.postForEntity(anyString(), anyObject(), any(Class.class)))
                .thenReturn(new ResponseEntity<String>("1file.xml", status));
    }

    private OngoingStubbing<Object> whenGetXmlFilesRequest() throws XmlRpcException {
        return when(xmlRpcClient.execute(any(XmlRpcClientConfig.class), anyString(), anyList()));
    }

    private CdrRequest createWebQMenuParameters(String url) {
        CdrRequest cdrRequest = new CdrRequest();
        cdrRequest.setEnvelopeUrl(url);
        return cdrRequest;
    }

    private CdrRequest createWebQMenuParametersWithAuthorization(String url, String userName, String password) {
        CdrRequest cdrRequest = new CdrRequest();
        cdrRequest.setEnvelopeUrl(url);
        cdrRequest.setAuthorizationSet(true);
        cdrRequest.setUserName(userName);
        cdrRequest.setPassword(password);
        return cdrRequest;
    }
    
}
