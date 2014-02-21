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

import java.net.URISyntaxException;

import de.betterform.agent.web.flux.FluxProcessor;
import de.betterform.generator.UIGenerator;
import de.betterform.xml.xforms.exception.XFormsException;

/**
 * Extends betterForm FluxProcessor class to add some WebQ specific functionality.
 *
 * @author Enriko Käsper
 */
public class WebQFluxProcessor extends FluxProcessor {

    public WebQFluxProcessor() {
        super();
    }

    @Override
    protected UIGenerator createUIGenerator() throws URISyntaxException, XFormsException {
        UIGenerator uiGenerator = super.createUIGenerator();
        if (request.getParameter("envelope") != null) {
            uiGenerator.setParameter("envelope", request.getParameter("envelope"));
        }
        if (request.getParameter("instance") != null) {
            uiGenerator.setParameter("instance", request.getParameter("instance"));
        }
        return uiGenerator;
    }

}
