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
 *        Enriko Käsper
 */

package eionet.webq.xforms;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eionet.webq.dao.orm.UserFile;
import eionet.webq.service.KnownHostsService;
import eionet.webq.service.UserFileService;

/**
 * Service handles HTTP requests done through XForms engine and adds auth info to request header if needed.
 *
 * @author Enriko Käsper
 */
@Component
public class XFormsHTTPRequestAuthHandlerImpl implements HTTPRequestAuthHandler {

    @Autowired
    UserFileService userFileService;

    @Autowired
    KnownHostsService knownHostsService;

    @Override
    public void addAuthToHttpRequest(HttpRequestBase httpRequestBase, Map<?, ?> context) {

        String uri = httpRequestBase.getURI().toString();

        String instance = null;
        String requestURL = null;
        Integer fileId = null;
        String basicAuth = null;
        String sessionId = null;

        if (context.get("instance") != null) {
            instance = (String) context.get("instance");
        }
        if (context.get("requestURL") != null) {
            requestURL = (String) context.get("requestURL");
        }
        if (context.get("fileId") != null) {
            fileId = Integer.valueOf((String) context.get("fileId"));
        }
        if (context.get("jsessionid") != null) {
            sessionId = (String) context.get("jsessionid");
        }

        // add auth info only for URIs that are not on the same host.
        if (!uri.startsWith(requestURL)) {
            // if the URI starts with instance URI, then we can use the basic auth retrieved from CDR.
            if (uri.startsWith(instance) && fileId != null && sessionId != null) {
                UserFile userFile = userFileService.getByIdAndUser(fileId, sessionId);
                if (userFile != null) {
                    basicAuth = userFile.getAuthorization();
                }
            }
            if (StringUtils.isNotEmpty(basicAuth)) {
                httpRequestBase.addHeader("Authorization", basicAuth);
            }
        }
        // TODO add auth info from known hosts
    }
}
