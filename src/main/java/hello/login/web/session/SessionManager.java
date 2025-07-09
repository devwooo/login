package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    public static final String SESSION_COOKIE_NAME = "mySessionId";
    //동시성의 문제가 있을 경우 ConcurrentHashMap 써야함
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성
     */
    public void createSession(Object value, HttpServletResponse response) {
        //sessionId 생성, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        //쿠키 생성
        Cookie mySessionId = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(mySessionId);
    }

    /**
     * 세션 조회
     */
    public Object getSession(HttpServletRequest request) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);
        if (cookie == null) {
            return null;
        }
        return sessionStore.get(cookie.getValue());
    }

    /**
     * 세션 만료
     */
    public void expire(HttpServletRequest request) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);
        if (cookie != null) {
            sessionStore.remove(cookie.getValue());
        }
    }

    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        /*
         for (Cookie cookie : cookies) {
            if (cookie.getName().equals(SESSION_COOKIE_NAME)) {
                return sessionStore.get(cookie.getValue());
            }
        }
         */
       return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(SESSION_COOKIE_NAME))
                .findAny()
                .orElse(null);
    }


}
