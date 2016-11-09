package eionet.webq.web.interceptor;

import eionet.webq.service.CookieValueManager;
import eionet.webq.service.RequestBasedUserIdProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component(value = "userIdCookieRefreshInterceptor")
public class UserIdCookieRefreshInterceptor extends HandlerInterceptorAdapter {
    
    private final RequestBasedUserIdProvider requestBasedUserIdProvider;
    private final CookieValueManager cookieValueManager;

    @Autowired
    public UserIdCookieRefreshInterceptor(RequestBasedUserIdProvider requestBasedUserIdProvider, CookieValueManager cookieValueManager) {
        this.requestBasedUserIdProvider = requestBasedUserIdProvider;
        this.cookieValueManager = cookieValueManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = this.requestBasedUserIdProvider.getUserId(request);
        this.cookieValueManager.setUserId(response, userId);
        
        return true;
    }
    
}
