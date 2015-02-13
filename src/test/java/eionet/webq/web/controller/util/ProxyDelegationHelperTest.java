package eionet.webq.web.controller.util;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URISyntaxException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
@Ignore // ignore the test until the logic in <eionet.webq.web.controller.util.ProxyDelegationHelper> is fixed.
public class ProxyDelegationHelperTest {

    @Test
    public void allowUrlWithCompanyId() throws URISyntaxException {

        String companyId = "co1111111";
        String envelope = "https://bdr-test.eionet.europa.eu/" + companyId + "/someenvelope";

        assertTrue(ProxyDelegationHelper.isCompanyIdParameterValidForBdrEnvelope("http://someURI?companyId=" + companyId, envelope));
        assertTrue(ProxyDelegationHelper
                .isCompanyIdParameterValidForBdrEnvelope("http://someURI?companyId=" + companyId + "&someotherParam", envelope));

        assertFalse(ProxyDelegationHelper.isCompanyIdParameterValidForBdrEnvelope("http://someURI?companyId=111111", envelope));
        assertFalse(ProxyDelegationHelper
                .isCompanyIdParameterValidForBdrEnvelope("http://someURI?companyId=111111&someotherParam", envelope));
    }

    @Test
    public void allowUrlWithFGasesRegistryId() throws URISyntaxException {

        String companyId = "co1111111";
        String envelope = "https://bdr-test.eionet.europa.eu/" + companyId + "/someenvelope";

        assertTrue(ProxyDelegationHelper
                .isCompanyIdParameterValidForBdrEnvelope("http://fgases_registry/organisation?id=" + companyId, envelope));
        assertTrue(ProxyDelegationHelper
                .isCompanyIdParameterValidForBdrEnvelope("http://fgases_registry/organisation?id=" + companyId + "&someotherParam",
                        envelope));

        assertFalse(ProxyDelegationHelper
                .isCompanyIdParameterValidForBdrEnvelope("http://fgases_registry/organisation?id=111111", envelope));
        assertFalse(ProxyDelegationHelper
                .isCompanyIdParameterValidForBdrEnvelope("http://fgases_registry/organisation?id=111111&someotherParam", envelope));
    }

    @Test
    public void invalidUrls() throws URISyntaxException {

        assertTrue(ProxyDelegationHelper.isCompanyIdParameterValidForBdrEnvelope("", ""));
        assertTrue(ProxyDelegationHelper.isCompanyIdParameterValidForBdrEnvelope(null, null));

    }

}
