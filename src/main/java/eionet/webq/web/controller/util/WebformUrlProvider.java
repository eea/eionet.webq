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
package eionet.webq.web.controller.util;

import eionet.webq.dao.orm.ProjectFile;

/**
 * Created by Enriko on 18.03.14.
 */
public interface WebformUrlProvider {
    /**
     * Creates webform file URL depending on webform type. If it is a xform, then the request is forwarded to betterForm URL,
     * otherwise plain HTML is used.
     *
     * @param webform Project file
     * @return path to webform
     */
    public String getWebformPath(ProjectFile webform);

}
