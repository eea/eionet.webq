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

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class EncryptedSessionBasedUserIdProviderTest {

    private final String userId = "userId";
    
    @Mock
    private HttpSession session;
    @Mock
    private RequestBasedUserIdProvider requestBasedUserIdProvider;
    @InjectMocks
    private EncryptedSessionBasedUserIdProvider provider;

    @Test
    public void returnsUserIdBasedOnSessionEncryptedWithMD5() throws Exception {
        when(session.getId()).thenReturn(userId);
        when(requestBasedUserIdProvider.getUserId(session)).thenReturn(this.hash(userId));
        assertThat(provider.getUserId(), equalTo(this.hash(userId)));
    }
    
    private String hash(String userId) {
        return DigestUtils.md5Hex(userId);
    }
    
}
