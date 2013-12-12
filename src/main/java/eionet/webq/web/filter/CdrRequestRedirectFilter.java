package eionet.webq.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * The request from CDR come from another domain. The purpose of this filter is to keep the user in the same session, when WebQ
 * redirects the xforms request to its own domain.
 */
public class CdrRequestRedirectFilter implements Filter {

    /**
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {
    }

    /**
     * This class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CdrRequestRedirectFilter.class);

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);

        // SessionId received from WebQMenu or WebQEdit request.
        String initialSessionId = httpRequest.getParameter("jsessionid");

        LOGGER.debug("Current JSESSIONID:" + session.getId());
        LOGGER.debug("Initial JSESSIONID:" + initialSessionId);

        if (StringUtils.isNotEmpty(initialSessionId) && !session.getId().equals(initialSessionId)) {
            Cookie sessionCookie = new Cookie("JSESSIONID", initialSessionId);
            sessionCookie.setPath(httpRequest.getContextPath());
            sessionCookie.setDomain(httpRequest.getServerName());
            httpResponse.setHeader("Set-Cookie", "JSESSIONID=" + initialSessionId + "; Path=" + httpRequest.getContextPath());

            AddSessionCookieHeaderRequestWrapper requestWrapper = new AddSessionCookieHeaderRequestWrapper(httpRequest);
            requestWrapper.setSessionCookie(sessionCookie);

            chain.doFilter(requestWrapper, response);

        } else {
            chain.doFilter(httpRequest, httpResponse);
        }
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

}

/**
 *
 * Override the getCookie method and return the initial jsessionid. If the new cookie is present then replace the existing one.
 *
 * @author Enriko Käsper
 */
class AddSessionCookieHeaderRequestWrapper extends HttpServletRequestWrapper {

    /** Session cookie. */
    Cookie cookie;

    public AddSessionCookieHeaderRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * Set the initial session cookie.
     *
     * @param cookie
     */
    public void setSessionCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    @Override
    public Cookie[] getCookies() {
        // replace jsessionid cookie if defined.
        if (cookie != null) {
            List<Cookie> cookies = new ArrayList<Cookie>();
            if (super.getCookies() != null) {
                for (Cookie existingSessionCookie : super.getCookies()) {
                    if (!existingSessionCookie.getName().equalsIgnoreCase("jsessionid")) {
                        cookies.add(existingSessionCookie);
                    }
                }
            }
            cookies.add(cookie);
            return cookies.toArray(new Cookie[0]);
        } else {
            return super.getCookies();
        }
    }
}
