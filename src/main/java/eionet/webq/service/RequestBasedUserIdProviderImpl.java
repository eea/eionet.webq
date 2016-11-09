package eionet.webq.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestBasedUserIdProviderImpl implements RequestBasedUserIdProvider {

    private final CookieValueManager cookieValueManager;
    
    @Autowired
    public RequestBasedUserIdProviderImpl(CookieValueManager cookieValueManager) {
        this.cookieValueManager = cookieValueManager;
    }
    
    @Override
    public String getUserId(HttpServletRequest request) {
        String userId = this.cookieValueManager.getUserId(request);
        
        if (!StringUtils.isBlank(userId)) {
            return userId;
        }
        
        return this.getUserId(request.getSession());
    }

    @Override
    public String getUserId(HttpSession session) {
        return DigestUtils.md5Hex(session.getId());
    }
    
}
