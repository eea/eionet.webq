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

import eionet.webq.dto.CdrRequest;
import eionet.webq.web.controller.cdr.IntegrationWithCDRController;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 */
@Service
public class EncryptedSessionBasedUserIdProvider implements UserIdProvider {
    
    private static final String[] CDR_PATHS;
    
    static {
        CDR_PATHS = new String[] { "/WebQMenu", "/WebQEdit", "/cdr/edit/file", "/cdr/add/file" };
    }
    
    @Autowired(required = false)
    private HttpServletRequest request;
    
    /**
     * Session.
     */
    @Autowired
    private HttpSession session;

    @Override
    public String getUserId() {
        if (this.isCdrOrientedRequest()) {
            CdrRequest cdrRequest = (CdrRequest) this.session.getAttribute(IntegrationWithCDRController.LATEST_CDR_REQUEST);
            
            return DigestUtils.md5Hex(cdrRequest.getSessionId());
        }
        
        if (this.isCustomSessionOrientedRequest()) {
            return request.getParameter("sessionid");
        }
        
        return DigestUtils.md5Hex(session.getId());
    }
    
    protected boolean isCdrOrientedRequest() {
        if (this.request == null) {
            return false;
        }
        
        String requestUri = this.request.getRequestURI();
        
        for (String cdrCtxPath : CDR_PATHS) {
            if (requestUri.contains(cdrCtxPath)) {
                return true;
            }
        }
        
        return false;
    }
    
    protected boolean isCustomSessionOrientedRequest() {
        if (this.request == null) {
            return false;
        }
        
        return request.getParameter("sessionid") != null;
    }
    
}
