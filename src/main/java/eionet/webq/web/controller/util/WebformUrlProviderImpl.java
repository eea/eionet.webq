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

import eionet.webq.dao.orm.ProjectEntry;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.service.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * {@link WebformUrlProvider} implementation.
 */
@Component
public class WebformUrlProviderImpl implements WebformUrlProvider {
    /**
     * Service for webform projects.
     */
    @Autowired
    private ProjectService projectService;
    /**
     * TODO FIX - change name
     */
    @Value("${cas.service}")
    String webqUrl;

    @Override
    public String getWebformPath(ProjectFile webform) {
        String webformPath = null;
        if (webform.getFileName().endsWith(".html") || webform.getFileName().endsWith(".htm")) {
            if (StringUtils.isEmpty(webform.getProjectIdentifier())) {
                ProjectEntry project = projectService.getById(webform.getProjectId());
                if (project != null) {
                    webform.setProjectIdentifier(project.getProjectId());
                }
            }
            webformPath = webqUrl +
                    "/webform/project/" + webform.getProjectIdentifier() + "/file/"
                            + webform.getFileName() + "?";
        } else {
            webformPath = webqUrl + "/xform/?formId=" + webform.getId() + "&";
        }
        return webformPath;
    }
}
