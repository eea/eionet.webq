package eionet.webq.converter;

import org.junit.Test;

import javax.servlet.http.Cookie;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Enriko on 22.11.2014.
 */
public class CookiesToStringBidirectionalConverterTest {

    @Test
    public void testConvertStringToCookies() {
        CookiesToStringBidirectionalConverter converter = new CookiesToStringBidirectionalConverter();
        Cookie[] cookies = converter.convertStringToCookies(getCookieString());
        Cookie[] preparedCookies = getCookies();

        assertThat(cookies[0].getName(), equalTo(preparedCookies[0].getName()));
        assertThat(cookies[0].getValue(), equalTo(preparedCookies[0].getValue()));

        assertThat(cookies[1].getName(), equalTo(preparedCookies[1].getName()));
        assertThat(cookies[1].getValue(), equalTo(preparedCookies[1].getValue()));

        assertThat(cookies[2].getName(), equalTo(preparedCookies[2].getName()));
        assertThat(cookies[2].getValue(), equalTo(preparedCookies[2].getValue()));
    }

    @Test
    public void testConvertCookiesToString() {

        CookiesToStringBidirectionalConverter converter = new CookiesToStringBidirectionalConverter();
        String cookiesString = converter.convertCookiesToString(getCookies());
        String preparedString = getCookieString();

        assertThat(cookiesString, equalTo(preparedString));
    }

    @Test
    public void testConvertCookieToString() {

        CookiesToStringBidirectionalConverter converter = new CookiesToStringBidirectionalConverter();
        String cookiesString = converter.convertCookieToString(getCookies()[0]);

        assertThat(cookiesString, equalTo("name1=\"value1\""));
    }

    private Cookie[] getCookies() {
        Cookie cookie1 = new Cookie("name1", "\"value1\"");
        Cookie cookie2 = new Cookie("name2", "\"value2\"");
        Cookie cookie3 = new Cookie("name3", "\"==value3\"");

        Cookie[] cookies = {cookie1, cookie2, cookie3};
        return cookies;
    }

    private String getCookieString() {
        return "name1=\"value1\";name2=\"value2\";name3=\"==value3\";";
    }
}
