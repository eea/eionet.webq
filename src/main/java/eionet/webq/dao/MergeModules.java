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
package eionet.webq.dao;

import eionet.webq.dao.orm.MergeModule;

import java.util.Collection;

/**
 */
public interface MergeModules {

    /**
     * Finds all merge modules entries.
     *
     * @return All available merge modules.
     */
    Collection<MergeModule> findAll();

    /**
     * Saves merge module to storage.
     *
     * @param module module
     * @return id of new module
     */
    int save(MergeModule module);

    /**
     * Finds merge module form storage by it's id.
     *
     * @param id merge module id.
     * @return merge module
     */
    MergeModule findById(int id);

    /**
     * Removes Merge modules by id.
     *
     * @param ids ids or modules to remove
     */
    void remove(int... ids);

    /**
     * Finds {@link eionet.webq.dao.orm.MergeModule} by its name.
     * Assumed that module name is unique across all modules.
     *
     * @param moduleName module name.
     * @return merge module.
     */
    MergeModule findByFileName(String moduleName);

    /**
     * Updates merge module.
     *
     * @param module module to update.
     */
    void update(MergeModule module);

    /**
     * Allows to find merge module by xml schema.
     *
     *
     * @param xmlSchemas xml schema.
     * @return merge modules found.
     */
    Collection<MergeModule> findByXmlSchemas(Collection<String> xmlSchemas);
}
