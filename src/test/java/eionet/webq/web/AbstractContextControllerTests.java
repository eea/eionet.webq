package eionet.webq.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/application-context.xml",
        "file:src/main/webapp/WEB-INF/spring/servlet-context.xml", "file:src/test/resources/test-datasource-context.xml"})
public class AbstractContextControllerTests {

    @Autowired
    protected WebApplicationContext wac;

}
