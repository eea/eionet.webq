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
package eionet.webq.dto;

import java.io.Serializable;

/**
 */
public class CdrRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Envelope URL.
     */
    private String envelopeUrl;
    /**
     * Only requested schema.
     */
    private String schema;
    /**
     * Allow to create new form.
     */
    private boolean newFormCreationAllowed;
    /**
     * New file name.
     */
    private String newFileName;
    /**
     * Is authorization set.
     */
    private boolean authorizationSet;
    /**
     * Basic authorization information.
     */
    private String basicAuthorization;
    /**
     * Authentication user name.
     */
    private String userName;
    /**
     * Authentication password.
     */
    private String password;
    /**
     * Cookies information.
     */
    private String cookies;
    /**
     * URL of xml file stored in CDR.
     */
    private String instanceUrl;
    /**
     * Instance name stripped off instance url.
     */
    private String instanceName;
    /**
     * XML file title.
     */
    private String instanceTitle;
    /**
     * Additional parameters passed with this request.
     */
    private String additionalParametersAsQueryString;
    /**
     * Request context path.
     */
    private String contextPath;

    /**
     * Session ID the request was received.
     */
    private String sessionId;

    public String getEnvelopeUrl() {
        return envelopeUrl;
    }

    public void setEnvelopeUrl(String envelopeUrl) {
        this.envelopeUrl = envelopeUrl;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public boolean isNewFormCreationAllowed() {
        return newFormCreationAllowed;
    }

    public void setNewFormCreationAllowed(boolean newFormCreationAllowed) {
        this.newFormCreationAllowed = newFormCreationAllowed;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public boolean isAuthorizationSet() {
        return authorizationSet;
    }

    public void setAuthorizationSet(boolean authorizationSet) {
        this.authorizationSet = authorizationSet;
    }

    public String getBasicAuthorization() {
        return basicAuthorization;
    }

    public void setBasicAuthorization(String basicAuthorization) {
        this.basicAuthorization = basicAuthorization;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceTitle() {
        return instanceTitle;
    }

    public void setInstanceTitle(String instanceTitle) {
        this.instanceTitle = instanceTitle;
    }

    public String getAdditionalParametersAsQueryString() {
        return additionalParametersAsQueryString;
    }

    public void setAdditionalParametersAsQueryString(String additionalParametersAsQueryString) {
        this.additionalParametersAsQueryString = additionalParametersAsQueryString;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
    @Override
    public String toString() {
        return "CdrRequest [envelopeUrl=" + envelopeUrl + ", schema=" + schema + ", newFormCreationAllowed="
                + newFormCreationAllowed + ", newFileName=" + newFileName + ", authorizationSet=" + authorizationSet
                + ", userName=" + userName
                + ", instanceUrl=" + instanceUrl + ", instanceName=" + instanceName + ", instanceTitle=" + instanceTitle
                + ", additionalParametersAsQueryString=" + additionalParametersAsQueryString + ", contextPath=" + contextPath
                + ", sessionId=" + sessionId + "]";
    }
}
