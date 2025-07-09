## 로그인 처리하기 - 쿠키 사용
 - 로그인의 상태를 유지 해야 한다.
   - 쿼리 파라미터를 유지하면서 계속 보내는것은 매우 번거롭다, 주로 쿠키를 사용한다.
 
## 쿠키 
 - 서버에서 로그인에 성공하면 HTTP 응답에 쿠키를 담아서 브라우저에 전달 > 그럼 브라우저는앞으로 해당 쿠키를 지속해 보내줌
 - 로그인 이후 welcome 페이지 접근
 - 영속 쿠키 : 만료 날짜를 입력하면 해당 날짜까지 유지
 - 세션 쿠키 : 만료 날짜를 생략하면 브라우저 종료시 까지만 유지

 - 따라서 쿠키를 만들어 로그인 상태를 유지해보자
 - Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
 - 리턴해준 쿠키값을 서버에서 받을 때는 
   - @CookieValue(value = "memberId", required = false)  이렇게 받는다. 이때 required = false 인
   - 이유는 로그인이 안한 사람도 해당 로그인을 타야하기 때문이다.

## 쿠키와 보안 문제
 - 쿠키를 사용해서 로그인 ID를 전달해서 로그인을 유지할 수 있었다. 
 - 하지만 보안 문제가 있다
   - 보안문제
      - 1.  쿠키 값은 임의로 변경 할 수 있다.
      - 2. 쿠키에 보관된 정보는 훔쳐갈 수 있다.
      - 3. 해커가 쿠키를 한번 훔쳐가면 평생 사용할 수 있다.
   - 대안
     - 1. 쿠키에 중요한 값을 노출하지 않고, 사용자 별로 예측 불가능한 임의의 토큰을 노출하고, 서버에서 토큰과 사용자 ID를 매핑해서 인식한다
     - 그리고 서버에서 토큰을 관리한다.
     - 2. 토큰은 해커가 임의의 값을 넣어도 찾을 수 없도록 예상 불가능 해야 한다.
     - 3. 해커가 토큰을 털어가도 시간이 지나면 사용할 수 없도록 서버에서 해당 토큰의 만료시간을 짧게 유지한다.
     - 또는 해킹이 의심되는 경우 서버에서 해당 토큰을 강제로 제거하면 된다.

## 로그인 처리하기 - 세션 동작 방식
 - 쿠키에 중요한 정보를 보관하는 방법은 여러가지 보안 이슈가 있다. 따라서 중요한 정보를 모두 서버에 저장 해야 한다.
 - 그리고 클라이언트와 서버는 추정 불가능한 임의의 식별자 값으로 연결해야 한다.

 - 동작방식
   - POST > id,pwd > 서버 > DB > 확인(O) > 세션저장소에 저장 > 추정불가능한 세션 ID 생성하여 KEY 값으로 사용
   - 세션ID를 쿠키로 전달 (클라이언트와 서버는 결국 쿠키로 연결이 되어야 한다.)

## 직접만든 세션 - Map
 - 세션 생성
   - 생성 후 > 세션 보관소에 저장 > 클라이언트에 쿠키로 전달
 - 세션 조회
   - 쿠키의 값으로 세션 저장소에 저장된 값 조회
 - 세션 만료
   - 세션 저장소에 보관한 키와 값 제거
 - Request(서버가 브라우저로 부터 받는 요청 == 브라우저가 보내는 요청) / Response(서버에서 브라우저로 보내는 요청 == 브라우저가 서버로 부터받는 요청)

## 서블릿이 제공하는 HTTP Session
 - 서브릿을 통해 HttpSession을 생성하면 쿠키 이름이 JSESSIONID 이고, 값은 추정 불가능한 랜덤한 값이다.
 - request.getSession()  
   - 옵션 : true / false , default 는 true임
     - true > 기존 세션이 있으면 세션 반환, 없으면 새로운 세션을 생성하여 반환
     - false > 기존 세션이 있으면 세션 반환, 없으면 null 반환 >> 홈화면이나, 세션삭제시 세션을 생성하지 않기 위해서 주로 사용된다
 - 세션을 더 편리하게 쓸수 있게 @SessionAttribute() 어노테이션을 제공한다. (세션을 생성하진 않음)
   - @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember

## Tracking Mode
 - 로그인을 처음시도하면 URL이 jsessionid를 포함하고 있는 것을 알수 있다
   - http://localhost:8080/;jsessionid=59ADF9E92F238D843BFE210A30992DB8
 - 이는 웹브라우저가 쿠키를 지원하지 않을 때 쿠키대신 URL을 통해서 세션을 유지하는 방법이다.
 - URL 전달 방식을 끄고 항상 쿠키를 통해서만 세션을 유지하고 싶으면
   - application.properties = server.servlet.session.tracking-modes=cookie 넣어주면된다.

## 세션 정보와 타임 아웃 설정
 - 세션의 정보
```
        log.info("sessionId={}", session.getId());                                  // sessionId=2C3B37058DA7809A46831919B021CB9D    >> 세션의 ID
        log.info("getMaxInactiveInterval={}", session.getMaxInactiveInterval());    // getMaxInactiveInterval=1800                   >> 세션의 유효 시간
        log.info("creationTime={}", new Date(session.getCreationTime()));           // creationTime=Wed Jul 09 12:07:37 KST 2025     >> 세션 생성일시
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));   // lastAccessedTime=Wed Jul 09 12:07:38 KST 2025 >> 최근에 서버에 접근한 시간
        log.info("isNew={}", session.isNew());                                      // isNew=false                                   >> 새로 생성된 세션인지 여부
```
 - 세션의 타임아웃 설정
   - session.invalidate()가 호출되는 경우 삭제 된다.
   - 하지만 대부분의 사용자는 로그아웃 하지 않고 그냥 웹을 종료한다. 문제는 HTTP가 비연결성이므로 서버입장에서
   - 사용자가 웹브라우저를 종료한것인지 알 수 가 없다. 
   - 세션은 사용자가 서버에 최근에 요청한 시간을 기준으로 30분 정도 유지해주는게 좋다. HttpSession은 이 방식을 사용한다.
   - 세션 타입 아웃 설정 >> application-properties
     - server.servlet.session.timeout=60( >> 60초 / 초단위로 작동하니 설정할 초를 지정해 주면된다. / 글로벌 설정은 분단위로 설정해야 한다.) 
     - 특정 세션 단위로 시간 설정 session.setMaxInactiveInterval(1800) // 특정 세션에만 적용
   - 세션의 타임아웃 시간은 해당 세션관 관련된 JSESSIONID를 전달하는 HTTP 요청이 있으면 현재 시간으로 다시 초기화 된다.  



## 서블릿 필터 - 필더, 인터셉터
 - 로그인을 한 사용자만 상품관리 페이지에 접근이 가등해야 한다.
 - 이뿐만 아니라 상품 관리 컨트롤러에서 작성되는 등록, 수정, 삭제, 조회 등 모든 컨트롤러 로직에 공통적으로 로그인 여부를 확인해야 한다. 
 - 이렇게 애플리케이션 여러 로직에서 공통으로 관심이 있는 것을 공통 관심사라고 한다.
 - 이러한 공통 관심사는 스프링의 AOP로도 해결 할 수 있지만, 웹과 관련된 공통 관심사는 지금부터 설명할 서블릿 필터 또는 스프링 인터셉터를 사용하는 것이 좋다.

 - 서블릿 필터 : 필터는 서블릿이 지원하는 수문장
 - 흐름 : HTTP 요청 > WAS > 필터 > 서블릿 > 컨트롤러
   - 필터를 적용하면 필터가 호출된 다음에, 서블릿이 호출된다. 그래서 모든 고객의 요청 로그를 남기는 요구사항이 있다면 필터를 사용하면 된다.
   - 필터는 특정 URL에 패턴을 적용할 수 있다.
 - 필터 제한
    - HTTP 요청 > WAS > 필터 > 서블릿 > 컨트롤러 // 로그인 사용자
    - HTTP 요청 > WAS > 필터(적절하지 않은 요청인경우, 서블릿 호출X)  // 비로그인 사용자
 - 필터 체인
   - HTTP 요청 > WAS > 필터1 > 필터2 > 필터3 > 서블릿 > 컨트롤러
 - 필터 인터페이스
   - 필터 인터페이스를 구현하고 등록하면 서블릿 컨테이너가 필터를 싱글톤 객체로 생성하고 관리한다.

 - HTTP 요청이 오면 doFilter 가 실행된다. ServletRequest, ServletResponse 같은경우 다운캐스핑하여 HttpservletRequest 등으로 다운캐스팅 하면된다.
 - chain.doFilter(request, response); 호출하지 않으면 서블릿, 컨트롤러 호출이 되지 않는다.

 - 필터 등록은 아래와 같이 하면된다.
 - 로그에 모두 같은 식별자를 자동으로 남기는 방법은 logback mdc로 검색해보자
 - @ServletComponentScan, @WebFilter(filterName= "logFilter", urlPatterns = "/*") 로 필터 등록이 가능하지만 필터 순서 조절이 안되므로
 - FilterRegistrationBean을 사용하여 등록하는게 좋다.
```
    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<Filter>();
        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        return filterFilterRegistrationBean;
    }

```


## 스프링 인터셉터 
 - 스프링 인터셉터는 스프링 MVC가 제공하는 기술이다.
 - 둘다 웹과 관련된 공통 관심 사항을 처리하지만 적용되는 순서와 범위 사용방법이 다르다

 - 스프링 인터셉터 흐름
   - HTTP 요청 > WAS > 필터 > 서블릿 > 스프링 인터셉터 > 컨트롤러 
     - 스프링 인터셉터는 디스패처 서블릿과 컨트롤러 사이에서 컨트롤러 호출 직전에 호출된다.
     - 디스패처 서블릿 이후에 등장한다.
     - 스프링 인터셉터에서도 URL 패턴을 적용 할 수 있는데, 서블릿 URL 패턴과 다르게 정밀하게 설정할 수 있다.
 - 인터셉터 제한 // 로그인 여부 체크하기에 딱좋다
   - HTTP 요청 > WAS > 필터 > 서블릿 > 스프링 인터셉터 > 컨트롤러 //로그인사용자
   - HTTP 요청 > WAS > 필터 > 서블릿 > 스프링 인터셉터 (적절하지 않은 요청이라 판단, 컨트롤러 호출X) // 비 로그인 사용자 
 - 체인
   - HTTP 요청 > WAS > 필터 > 서블릿 > 인터셉터1 > 인터셉터2 > 컨트롤러
 - 스프링 인터셉터 인터페이스 (HandlerInterceptor) 인터페이스를 구현하면 된다.
   - 인터셉터는 컨트롤러 호출전(preHandle), 호출후(postHandle), 요청 완료 이후 (afterCompletion) 과같이 단계적으로 세분화 되어있다.
     - preHandle 의 응답값이 true 이면 다음으로 진행하고, false인경우 더 진행하지 않는다.
     - postHandle 컨트롤러 호출 이후에 호출된다.
     - afterCompletion 뷰가 렌더링된 이후에 호출된다
       - controller에서 예외 발생시 postHandle 호출되지 않는다, afterCompletion은 예외 발생여부와 달리 무조건 호출된다.
   - 서블릿 필터의 경우 request, response 만제공했지만, 인터셉터는 어떤 컨트롤러(handler)가 호출되는지 호출 정보도 받을 수 있다.
   - 또한 어떤 modelAndView 가 반환되는지 응답 정보도 받을 수 있다.
   - 인터셉터는 스프링 MVC 구조에 특화된 필터 기능을 제공하며, 스프링 MVC를 사용하고, 특별히 필터를 사용해야 하는게 아니라면 인터셉터를 사용하는게 편리하다.


## 인터셉터 - 인증로그
 - 서블릿 필터의 경우 지역변수로 해결이 가능하지만, 스프링 인터셉터의 경우 호출시점이 분리되어 있어 preHandle, postHandle, afterCompletion에서 함꼐 사용하려면
 - 해당 변수를 담아둬야 하는데. 멤버변수로 사용할 경우 해당 객체가 싱글톤처럼 사용되므로 멤버변수로 사용하면 위험하다. 따라서 request에 담아두어 나중에 request.getAttribute() 하여 값을 꺼내온다
 - 또한 true는 진행 false는 중지
```
 
    또한 WebMvcConfigurer 를 상속하여 addInterceptors를 오버라이딩 하여 인터셉터를 추가해줘야 한다. 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error"); //제외할 경로
    }
```

## 인터셉터 - 인증 체크
 - 인증이라는 것은 컨트롤러 호출전에만 호출 되면 된다. 따라서 preHandle 만 구현하면 된다.
 - 서블릿 필터와, 스프링 인터셉터는 웹과 관련된 공통 관심사를 해결하기 위한 기술이다.
 - 하지만 인터셉터가 개발자 입장에서는 훨씬 편리하게 사용할수 있다.

## ArgumentResolver 활용
 - @Login 애노테이션이 있으면 직접 만든 ArgumentResovler가 동작하여 자동으로 세션에 있는 로그인 회원을 찾아주고 아니면 null을 반환한다.
 - 
```
    @Target(ElementType.PARAMETER)      // Parameter에만 사용
    @Retention(RetentionPolicy.RUNTIME) // 리플렉션을 활용 할 수 있도록 런타임까지 애노테이션 정보가 남아있음
    public @interface Login {
    }

    [LoginMemberArgumentResolver.java] HandlerMethodArgumentResolver의 구현체임 >> 여기서 직접 구현한다 argumentReoslver의 기능을
    public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        supportsParameter...  // 해당값이 true인경우 작성한 LoginMemberArgumentResolver 이 동작한다. 
        
        @Override
        resolveArgument...    // 컨트롤러 호출 직전에 호출되어 필요한 파라미터 정보를 생성해준다.
        
    }
 
    [WebConfig.java]
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

```
