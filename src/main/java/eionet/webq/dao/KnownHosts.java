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

import eionet.webq.dao.orm.KnownHost;

import java.util.Collection;

/**
 * Known hosts repository.
 */
public interface KnownHosts {
    /**
     * Save host to repository.
     *
     *
     * @param host host to save.
     * @return host id.
     */
    int save(KnownHost host);

    /**
     * Finds known host by id.
     *
     * @param id id
     * @return known host for specified id
     */
    KnownHost findById(int id);

    /**
     * Finds all hosts.
     *
     * @return all hosts
     */
    Collection<KnownHost> findAll();

    /**
     * Update host.
     *
     * @param host host
     */
    void update(KnownHost host);
}
