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
package eionet.webq.dao.orm;

import eionet.webq.dto.KnownHostAuthenticationMethod;

import javax.persistence.Entity;

/**
 * Known host data.
 */
@Entity
public class KnownHost {
    /**
     * Host URL.
     */
    private String hostURL;
    /**
     * Host name.
     */
    private String hostName;
    /**
     * Authentication method.
     */
    private KnownHostAuthenticationMethod authenticationMethod;
    /**
     * Authentication key.
     */
    private String key;
    /**
     * Authentication ticket.
     */
    private String ticket;

    public String getHostURL() {
        return hostURL;
    }

    public void setHostURL(String hostURL) {
        this.hostURL = hostURL;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public KnownHostAuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(KnownHostAuthenticationMethod authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
