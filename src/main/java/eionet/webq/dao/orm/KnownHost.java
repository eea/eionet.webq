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
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Known host data.
 */
@Entity
@Table(name = "known_host")
public class KnownHost {
    /**
     * Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * Host URL.
     */
    @Length(max = 255)
    @Column(name = "host_url")
    @NotEmpty
    private String hostURL;
    /**
     * Host name.
     */
    @Length(max = 255)
    @Column(name = "host_name")
    private String hostName;
    /**
     * Authentication method.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_method")
    @NotNull
    private KnownHostAuthenticationMethod authenticationMethod;
    /**
     * Authentication key.
     */
    @Length(max = 255)
    @Column(name = "auth_key")
    @NotEmpty
    private String key;
    /**
     * Authentication ticket.
     */
    @Length(max = 255)
    @NotEmpty
    private String ticket;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KnownHost host = (KnownHost) o;

        return id == host.id && authenticationMethod == host.authenticationMethod
                && !(hostName != null ? !hostName.equals(host.hostName) : host.hostName != null)
                && hostURL.equals(host.hostURL) && key.equals(host.key) && ticket.equals(host.ticket);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + hostURL.hashCode();
        result = 31 * result + (hostName != null ? hostName.hashCode() : 0);
        result = 31 * result + authenticationMethod.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + ticket.hashCode();
        return result;
    }
}
