package eionet.webq.web;

import configuration.ApplicationTestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class AbstractContextControllerTests {

    @Autowired
    protected WebApplicationContext wac;

}
