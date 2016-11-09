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

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

/**
 */
@Service
public class EncryptedSessionBasedUserIdProvider implements UserIdProvider {
    
    @Autowired(required = false)
    private HttpServletRequest request;
    
    /**
     * Session.
     */
    @Autowired
    private HttpSession session;
    
    @Autowired
    private RequestBasedUserIdProvider requestBasedUserIdProvider;

    @Override
    public String getUserId() {        
        String userId = this.tryGetCookieBasedId();
        
        if (StringUtils.isBlank(userId)) {
            userId = this.requestBasedUserIdProvider.getUserId(session);
        }
        
        return userId;
    }
    
    protected String tryGetCookieBasedId() {
        if (this.request == null) {
            return null;
        }
        
        return this.requestBasedUserIdProvider.getUserId(this.request);
    }
    
    
}
