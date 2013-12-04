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
package eionet.webq.web.service;

import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.service.WebFormService;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 */
public class AvailableFormsService extends SpringBeanAutowiringSupport {
    /**
     * Project files service.
     */
    @Autowired
    @Qualifier("remoteWebForms")
    WebFormService webFormService;
    /**
     * Xml-Rpc client for querying previous version of WebQ.
     */
    @Autowired
    XmlRpcClient xmlRpcClient;
    /**
     * Fallback url if no schemas found.
     */
    @Value("#{ws['webq1.rpc.url']}")
    String webQ1Url;
    /**
     *
     */
    @Value("#{ws['webq1.get.xform']}")
    String webQ1GetXForm;
    /**
     * XML-RPC method for querying web forms availability.
     * Expected that xmlSchemas array should contain strings.
     *
     * @param xmlSchemas xmlSchema array
     * @return map of xml schema to XForm file name
     * @throws java.net.MalformedURLException if webQ1 URL is malformed.
     * @throws org.apache.xmlrpc.XmlRpcException if exception during call to webQ1
     * @see org.apache.xmlrpc.webserver.XmlRpcServlet
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getXForm(Object[] xmlSchemas) throws MalformedURLException, XmlRpcException {
        Map<String, String> files =
                transformToSchemaFileNameMap(webFormService.findWebFormsForSchemas(xmlSchemasToList(xmlSchemas)));
        if (files.isEmpty()) {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(webQ1Url));
            return (Map<String, String>) xmlRpcClient.execute(config, webQ1GetXForm, new Object[] {xmlSchemas});
        }
        return files;
    }

    /**
     * Transforms project files to map where key is xml schema and value is file name.
     *
     * @param webForms web forms
     * @return map where key is xml schema and value is file name
     */
    private Map<String, String> transformToSchemaFileNameMap(Collection<ProjectFile> webForms) {
        MultiValueMap<String, String> schemaToFileNameMap = new LinkedMultiValueMap<String, String>();
        for (ProjectFile file : webForms) {
            schemaToFileNameMap.add(file.getXmlSchema(), file.getFileName());
        }
        return schemaToFileNameMap.toSingleValueMap();
    }

    /**
     * Transforms {@link java.lang.Object[]} to {@link java.util.Collection<String>}.
     *
     * @param xmlSchemas xmlSchema array
     * @return collections of strings
     */
    private Collection<String> xmlSchemasToList(Object[] xmlSchemas) {
        List<String> transformationResult = new ArrayList<String>();
        if (xmlSchemas == null) {
            return transformationResult;
        }
        for (Object xmlSchema : xmlSchemas) {
            transformationResult.add(xmlSchema.toString());
        }
        return transformationResult;
    }
}
