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
package eionet.webq.dao.orm.util;

import eionet.webq.dao.orm.MergeModule;
import eionet.webq.dao.orm.ProjectFile;
import eionet.webq.dao.orm.UploadedFile;

/**
 * Utility methods for WebQ files, e.g. {@link ProjectFile}.
 */
public final class WebQFileInfo {
    /**
     * No instantiation.
     */
    private WebQFileInfo() {
    }

    /**
     * Check whether this file is new.
     *
     * @param file file to be checked
     * @return is new
     */
    public static boolean isNew(ProjectFile file) {
        return file.getId() == 0;
    }

    /**
     * Check whether this file is new.
     *
     * @param module file to be checked
     * @return is new
     */
    public static boolean isNew(MergeModule module) {
        return module.getId() == 0;
    }

    /**
     * Check whether this file has empty content.
     *
     * @param file file to be checked
     * @return is empty
     */
    public static boolean fileIsEmpty(UploadedFile file) {
        return file == null || file.getSizeInBytes() == 0;
    }
}
