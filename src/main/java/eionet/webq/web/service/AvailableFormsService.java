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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

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
    WebFormService webFormService;

    /**
     * XML-RPC method for querying web forms availability.
     * Expected that xmlSchemas array should contain strings.
     *
     * @param xmlSchemas xmlSchema array
     * @return map of xml schema to XForm file name
     * @see org.apache.xmlrpc.webserver.XmlRpcServlet
     */
    public Map<String, String> getXForm(Object[] xmlSchemas) {
        return transformToSchemaFileNameMap(webFormService.findWebFormsForSchemas(xmlSchemasToList(xmlSchemas)));
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
