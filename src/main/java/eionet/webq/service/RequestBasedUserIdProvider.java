package eionet.webq.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface RequestBasedUserIdProvider {

    String getUserId(HttpServletRequest request);
    
    String getUserId(HttpSession session);
    
}
