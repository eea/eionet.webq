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

import eionet.webq.service.ProjectFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Collections;
import java.util.Map;

/**
 */
public class AvailableFormsService extends SpringBeanAutowiringSupport {
    /**
     * Project files service.
     */
    @Autowired
    ProjectFileService service;

    /**
     * XML-RPC method.
     *
     * @param xmlSchema xmlSchema array
     * @return map of xml schema to XForm file name
     * @see org.apache.xmlrpc.webserver.XmlRpcServlet
     */
    public Map<String, String> getXForm(Object[] xmlSchema) {
        Object schema = xmlSchema[0];
        return Collections.singletonMap(schema.toString(), "dir199913_2011_xform.xhtml");
    }
}
