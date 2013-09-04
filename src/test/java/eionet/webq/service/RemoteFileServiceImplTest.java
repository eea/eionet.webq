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

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 */
public class RemoteFileServiceImplTest {
    private final byte[] FILE_CONTENT_IN_RESPONSE = "test file content".getBytes();
    private RemoteFileService remoteFileService = new RemoteFileServiceImpl();
    private Connection connection;
    private URL url;

    @Before
    public void setUp() throws Exception {
        Server server = new ContainerServer(new FileResponse());
        ServerSocket serverSocket = new ServerSocket(0);
        InetSocketAddress address = new InetSocketAddress(serverSocket.getLocalPort());
        serverSocket.close();
        url = new URI("http://" + address.getHostName() + ":" + address.getPort()).toURL();
        connection = new SocketConnection(server);
        connection.connect(address);
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void fetchesFileContentFromUrl() throws Exception {
        byte[] bytes = remoteFileService.fileContent(url);
        assertThat(bytes, equalTo(FILE_CONTENT_IN_RESPONSE));
    }

    @Test
    public void checksumForFilesMustBeEqual() throws Exception {
        assertTrue(remoteFileService.isChecksumMatches(FILE_CONTENT_IN_RESPONSE, url));
    }

    @Test
    public void filesMustMismatch() throws Exception {
        byte[] otherContent = "some other test file content".getBytes();
        assertFalse(remoteFileService.isChecksumMatches(otherContent, url));
    }

    private final class FileResponse implements Container {
        @Override
        public void handle(Request request, Response response) {
            OutputStream outputStream = null;
            try {
                response.setContentType(MediaType.TEXT_XML_VALUE);
                response.setContentLength(FILE_CONTENT_IN_RESPONSE.length);
                outputStream = response.getOutputStream();
                IOUtils.write(FILE_CONTENT_IN_RESPONSE, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
                fail("Unable to write response.");
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }
}
