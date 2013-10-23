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
package eionet.webq.service;

import eionet.webq.dao.orm.MergeModule;
import eionet.webq.dao.orm.UserFile;

import javax.xml.transform.TransformerException;
import java.util.Collection;

/**
 * Service merging {@link UserFile}s using {@link MergeModule}s.
 */
public interface UserFileMergeService {
    /**
     * Performs merging of {@link UserFile#getContent()}
     * using {@link MergeModule#getXslFile()}.
     * @param filesToMerge files to merge
     * @param module merge module
     * @return merged content.
     * @throws javax.xml.transform.TransformerException if transformation fails.
     */
    byte[] mergeFiles(Collection<UserFile> filesToMerge, MergeModule module) throws TransformerException;
}
