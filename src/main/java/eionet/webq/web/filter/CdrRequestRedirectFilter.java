package eionet.webq.web.filter;

import eionet.webq.service.UserFileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * The request from CDR come from another domain. The purpose of this filter is to keep the user in the same session, when WebQ
 * redirects the xforms request to its own domain.
 */
@Component
public class CdrRequestRedirectFilter implements Filter {
    /**
     * User file service.
     */
    @Autowired
    UserFileService userFileService;

    @Override
    public void destroy() {
    }

    /**
     * This class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CdrRequestRedirectFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);

        // SessionId received from WebQMenu or WebQEdit request.
        String initialSessionId = httpRequest.getParameter("sessionid");
        String current = DigestUtils.md5Hex(session.getId());

        LOGGER.debug("Current JSESSIONID:" + current);
        LOGGER.debug("Initial JSESSIONID:" + initialSessionId);

        if (StringUtils.isNotEmpty(initialSessionId) && !current.equals(initialSessionId)) {
            LOGGER.info("Update sessionid hash: " + initialSessionId + " -> " + current);
            userFileService.updateUserId(initialSessionId, current);
            LOGGER.info("User session updated successfully");
        }
        chain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }
}
