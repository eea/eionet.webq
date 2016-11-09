package eionet.webq.web.interceptor;

import eionet.webq.service.RequestBasedUserIdProvider;
import eionet.webq.service.UserFileService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component(value = "cdrSessionIdOverwriteInterceptor")
public class CdrSessionIdOverwriteInterceptor extends HandlerInterceptorAdapter  {
    
    private static final Logger LOGGER = Logger.getLogger(CdrSessionIdOverwriteInterceptor.class);
    
    private final RequestBasedUserIdProvider requestBasedUserIdProvider;
    private final UserFileService userFileService;
    
    @Autowired
    public CdrSessionIdOverwriteInterceptor(RequestBasedUserIdProvider requestBasedUserIdProvider, UserFileService userFileService) {
        this.requestBasedUserIdProvider = requestBasedUserIdProvider;
        this.userFileService = userFileService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String intermediateUserId = request.getParameter("sessionid");
        
        if (StringUtils.isBlank(intermediateUserId)) {
            return true;
        }
        
        String userId = this.requestBasedUserIdProvider.getUserId(request);
        
        if (StringUtils.equals(intermediateUserId, userId)) {
            return true;
        }
        
        LOGGER.info("Overriding intermediate user id: " + intermediateUserId + " with: " + userId);
        userFileService.updateUserId(intermediateUserId, userId);
        
        return true;
    }
    
}
