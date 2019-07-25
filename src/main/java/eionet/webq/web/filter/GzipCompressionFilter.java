package eionet.webq.web.filter;

import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.ehcache.constructs.web.filter.GzipFilter;

public class GzipCompressionFilter extends GzipFilter {
    
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        String path = ((HttpServletRequest) request).getServletPath();

        Pattern pattern = Pattern.compile("/projects/.+/export"); // exclude project zip archive from gzip compression
        if (pattern.matcher(path).matches()) {
            chain.doFilter(request, response);    
        } else {
            super.doFilter(request, response, chain);
        }
    }

}