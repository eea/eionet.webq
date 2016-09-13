package eionet.webq.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CookieValueManagerImpl implements CookieValueManager {
    
    @Override
    public String getUserId(HttpServletRequest request) {
        Cookie userIdCookie = this.getCookieByName(request, COOKIE_NAME_USER_ID);
        
        return this.getCookieValue(userIdCookie);
    }

    @Override
    public void setUserId(HttpServletResponse response, String userId) {
        Cookie userIdCookie = new Cookie(COOKIE_NAME_USER_ID, userId);
        int durationSeconds = 60 * 60 * 80; // 80 hours
        userIdCookie.setMaxAge(durationSeconds);
        response.addCookie(userIdCookie);
    }
    
    protected Cookie getCookieByName(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        
        for (Cookie cookie : request.getCookies()) {
            if (StringUtils.equals(cookie.getName(), cookieName)) {
                return cookie;
            }
        }
        
        return null;
    }
    
    protected String getCookieValue(Cookie cookie) {
        return cookie == null ? null : cookie.getValue();
    }
    
}
