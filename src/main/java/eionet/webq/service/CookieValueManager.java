package eionet.webq.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CookieValueManager {

    static final String COOKIE_NAME_USER_ID = "eionet.webq.cookies.userid";
    
    String getUserId(HttpServletRequest request);
    
    void setUserId(HttpServletResponse response, String userId);
    
}
