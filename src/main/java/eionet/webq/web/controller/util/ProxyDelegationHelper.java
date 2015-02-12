package eionet.webq.web.controller.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class provides utility methods to protect the proxy services  request from parameter manipulation.
 * BDR provides services to fetch confidential company data.  The services should be available only for reporting company.
 * The utility methods validate if the provided company Ids are valid for envelope stored in user file.
 */
public class ProxyDelegationHelper {

    /**
     * Method checks if company ID parameter is present in targetUrl. In this case BDR envelope URL has to contain
     * the company ID token in path. This is a security check that validates if users use only their company IDs
     * registered in BDR envelope path.
     *
     * @param targetUrl   - target URL from where WebQ tries to fetch data
     * @param envelopeUrl - envelope URL related to user file
     * @return false if the targetUrl is conflicting with envelopeUrl
     */
    public static boolean isCompanyIdParameterValidForBdrEnvelope(String targetUrl, String envelopeUrl) throws URISyntaxException {
        boolean result = true;

        if (StringUtils.isBlank(targetUrl) || StringUtils.isBlank(envelopeUrl) || !envelopeUrl.contains("bdr")) {
            return true;
        }
        URI targetURI = new URI(targetUrl);
        List<NameValuePair> targetParams = URLEncodedUtils.parse(targetURI, "UTF-8");

        if (targetParams != null) {
            List<String> paramKes = new ArrayList<String>();
            for (NameValuePair paramKey : targetParams) {
                paramKes.add(paramKey.getName());
            }
            if (paramKes.contains("companyId")) {
                return envelopeUrl.contains("/" + targetParams.get(paramKes.lastIndexOf("companyId")).getValue() + "/");
            } else if (targetUrl.contains("/fgases_registry/organisation")) {
                if (paramKes.contains("id")) {
                    return envelopeUrl.contains("/" + targetParams.get(paramKes.lastIndexOf("id")).getValue() + "/");
                }
            }
        }

        return result;
    }
}
