package eionet.webq.converter;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

/**
 * Converter for performing bi-directional conversion between list of cookies and String.
 */
@Component
public class CookiesToStringBidirectionalConverter {

    /**
     * Converts the list of cookie objects to semicolon separated string.
     *
     * @param cookies list of cookies.
     * @return all cookies as one string
     */
    public String convertCookiesToString(Cookie[] cookies) {
        if (cookies != null) {
            StringBuilder cookieBuilder = new StringBuilder();
            for (Cookie cookie : cookies) {
                cookieBuilder.append(convertCookieToString(cookie)).append(";");
            }
            return cookieBuilder.toString();
        }
        return null;
    }

    /**
     * Converts one cookie objects to string. Name and value are separated with '='. The value is surrounded with quotas.
     *
     * @param cookie .
     * @return cookies as string
     */
    public String convertCookieToString(Cookie cookie) {
        if (cookie != null) {
            StringBuilder cookieBuilder = new StringBuilder();
            cookieBuilder.append(cookie.getName()).append("=");
            if (!cookie.getValue().startsWith("\"")) {
                cookieBuilder.append("\"");
            }
            cookieBuilder.append(cookie.getValue());
            if (!cookie.getValue().endsWith("\"")) {
                cookieBuilder.append("\"");
            }
            return cookieBuilder.toString();
        }
        return null;
    }

    /**
     * Convert semicolon separated string of cookies to list of cookie objects.
     *
     * @param strCookies cookies in string
     * @return array of cookie objects
     */
    public Cookie[] convertStringToCookies(String strCookies) {
        if (strCookies != null) {
            String[] cookieStringArray = strCookies.split(";");
            Cookie[] cookies = new Cookie[cookieStringArray.length];
            for (int i = 0; i < cookieStringArray.length; i++) {
                int valueSeparatorPos = cookieStringArray[i].indexOf("=");
                Cookie cookie = new Cookie(cookieStringArray[i].substring(0, valueSeparatorPos),
                        cookieStringArray[i].substring(valueSeparatorPos + 1));
                cookies[i] = cookie;
            }
            return cookies;
        }
        return null;
    }
}
