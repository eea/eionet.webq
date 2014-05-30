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
 *        Argo Aava
 */
package eionet.webq.web.controller.util;

import eionet.webq.dao.orm.UserFile;

import java.util.ArrayList;
import java.util.List;

/**
 * List container to help Spring to bind collection automatically.
 */
public class UserFileList {

    /**
     *  user file collection.
     */
    private List<UserFile> userFiles;

    /**
     *  empty constructor for instantiation by reflection.
     */
    public UserFileList() {
        this.userFiles = new ArrayList<UserFile>();
    }

    /**
     * Constructor for setting up with user file collection.
     *
     * @param userFiles user file collection
     */
    public UserFileList(List<UserFile> userFiles) {
        this.userFiles = userFiles;
    }

    /**
     * get collection of files.
     *
     * @return collection of user files
     */
    public List<UserFile> getUserFiles() {
        return userFiles;
    }

    /**
     * set collection of files.
     *
     * @param userFiles     list of files to set for object
     */
    public void setUserFiles(List<UserFile> userFiles) {
        this.userFiles = userFiles;
    }
}
