package hello.login.web.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "세션이 없습니다";
        }
        // 세션 데이터 출력
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session name={}, value={}", name, session.getAttribute(name)));
        //session name=loginMember, value=Member(id=1, loginId=test, name=테스터, password=test!)
        log.info("sessionId={}", session.getId());                                  // sessionId=2C3B37058DA7809A46831919B021CB9D    >> 세션의 ID
        log.info("getMaxInactiveInterval={}", session.getMaxInactiveInterval());    // getMaxInactiveInterval=1800                   >> 세션의 유효 시간
        log.info("creationTime={}", new Date(session.getCreationTime()));           // creationTime=Wed Jul 09 12:07:37 KST 2025     >> 세션 생성일시
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));   // lastAccessedTime=Wed Jul 09 12:07:38 KST 2025 >> 최근에 서버에 접근한 시간
        log.info("isNew={}", session.isNew());                                      // isNew=false                                   >> 새로 생성된 세션인지 여부
        return "세션출력";
    }
}
