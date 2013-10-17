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

import org.hibernate.SessionFactory;

import java.math.BigInteger;

/**
 * Utility methods for file content table.
 */
public class FileContentUtil {

    private FileContentUtil() {
    }

    /**
     * Get rows count in file content table.
     *
     * @param factory session factory
     * @return rows count
     */
    public static int getFileContentRowsCount(SessionFactory factory) {
        BigInteger count = (BigInteger) factory.getCurrentSession()
                .createSQLQuery("select count(*) from file_content").uniqueResult();
        return count.intValue();
    }
}
