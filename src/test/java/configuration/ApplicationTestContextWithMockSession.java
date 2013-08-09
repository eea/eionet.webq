package configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;

@Configuration
@Import(ApplicationTestContext.class)
public class ApplicationTestContextWithMockSession {
    @Bean
    public HttpSession mockSession() {
        return new MockHttpSession();
    }
}
