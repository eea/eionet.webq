package eionet.webq.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class CookieValueManagerImplTest {

    private CookieValueManagerImpl cookieValueManager;
    
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.cookieValueManager = new CookieValueManagerImpl();
    }
    
    @Test
    public void testGetNullUserId() {
        String userId = this.cookieValueManager.getUserId(request);
        
        assertThat(userId, is(nullValue()));
    } 
    
    @Test
    public void testGetUserId() {
        Cookie userIdCookie = new Cookie(CookieValueManager.COOKIE_NAME_USER_ID, "1234567890");
        userIdCookie.setPath("/");
        Cookie someOtherCookie = new Cookie("other", "value");
        when(this.request.getCookies()).thenReturn(new Cookie[] { someOtherCookie, userIdCookie });
        
        String userId = this.cookieValueManager.getUserId(request);
        
        assertThat(userId, is(equalTo(userIdCookie.getValue())));
    }
    
    @Test
    public void testSetUserId() {
        final String userId = "1234567890";
        
        this.cookieValueManager.setUserId(response, userId);
        
        ArgumentCaptor<Cookie> userIdCookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(this.response).addCookie(userIdCookieCaptor.capture());
        
        Cookie userIdCookie = userIdCookieCaptor.getValue();
        assertThat(userIdCookie.getName(), is(equalTo(CookieValueManager.COOKIE_NAME_USER_ID)));
        assertThat(userIdCookie.getValue(), is(equalTo(userId)));
    }
    
}
