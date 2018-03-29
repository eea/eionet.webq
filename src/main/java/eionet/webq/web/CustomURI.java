package eionet.webq.web;

import org.springframework.beans.factory.annotation.Value;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 *
 */
public class CustomURI {

    private URI uri;

    public CustomURI(String webqUrl, String url) throws URISyntaxException {
        String temp = url.replace(" ", "%20");
        uri = new URI(temp);
        if (!uri.isAbsolute()) {
            temp = webqUrl + url;
            uri = new URI(temp);
        }
    }

    public URI getUri() {
        return uri;
    }

    public String getHttpURL() throws URISyntaxException {
        try {
            return uri.toURL().toString();
        } catch (MalformedURLException e) {
            throw new URISyntaxException("Error in URL", "");
        }
    }

}
