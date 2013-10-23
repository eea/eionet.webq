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
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 */
@Service
public class UserFileMergeServiceImpl implements UserFileMergeService {
    /**
     * This class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UserFileMergeServiceImpl.class);

    @Override
    public byte[] mergeFiles(Collection<UserFile> filesToMerge, MergeModule module) throws TransformerException {
        Queue<UserFile> userFiles = new LinkedList<UserFile>(filesToMerge);
        UserFile first = userFiles.poll();
        LOGGER.info("First file in queue=" + first);

        UserFileProvider resolver = new UserFileProvider(userFiles);
        Transformer transformer = createTransformer(module, resolver);

        byte[] result = first.getContent();

        for (UserFile file : resolver) {
            LOGGER.info("Transforming using file=" + file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            transformer.transform(new StreamSource(new ByteArrayInputStream(result)), new StreamResult(byteArrayOutputStream));
            result = byteArrayOutputStream.toByteArray();
        }
        return result;
    }

    /**
     * Creates Xsl Transformer.
     *
     * @param module merge module.
     * @param resolver URIResolver
     * @return transformer
     */
    private Transformer createTransformer(MergeModule module, URIResolver resolver) {
        byte[] mergeModuleContent = module.getXslFile().getContent().getFileContent();
        Source xslSource = new StreamSource(new ByteArrayInputStream(mergeModuleContent));

        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer(xslSource);
            transformer.setURIResolver(resolver);
        } catch (TransformerConfigurationException e) {
            LOGGER.warn("Unable to create transformer for user files merge", e);
        }
        return transformer;
    }

    /**
     * {@link javax.xml.transform.URIResolver} implementation,
     * which provides access to second user file while transforming first.
     * User file is accessible if requested href is equals to special parameter.
     */
    private static final class UserFileProvider implements URIResolver, Iterable<UserFile> {
        /**
         * User files queue.
         */
        private Queue<UserFile> userFiles;
        /**
         * Current file.
         */
        private UserFile current;

        /**
         * Creates user file provider with user files.
         * @param userFiles user files
         */
        private UserFileProvider(Queue<UserFile> userFiles) {
            this.userFiles = userFiles;
        }

        @Override
        public Source resolve(String href, String base) throws TransformerException {
            if ("current_document".equalsIgnoreCase(href)) {
                return new StreamSource(new ByteArrayInputStream(current.getContent()));
            }
            return null;
        }

        @Override
        public Iterator<UserFile> iterator() {
            return new Iterator<UserFile>() {
                @Override
                public boolean hasNext() {
                    return !userFiles.isEmpty();
                }

                @Override
                public UserFile next() {
                    current = userFiles.poll();
                    return current;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Remove operation not supported");
                }
            };
        }
    }
}
