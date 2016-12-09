package eionet.webq.web;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
public class WebQMvcConfigurator {
    
    private final AntPathMatcher pathMatcher;
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    
    @Autowired
    public WebQMvcConfigurator(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.pathMatcher = new AntPathMatcher();
        this.pathMatcher.setTrimTokens(false);
    }

    @PostConstruct
    public void setUp() {
        this.requestMappingHandlerMapping.setPathMatcher(this.pathMatcher);
        System.setProperty("https.protocols", "TLSv1.2");
    }

}
