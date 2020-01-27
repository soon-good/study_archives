# OAuth2 + 스프링 시큐리티

스프링 시큐리티는 강력한

- 인증(Authentication)
- 인가(Authorization)

기능을 가진 프레임워크이다.  



## 스프링 부트 1.5 와 2.0 의 설정방식 차이점

이전(1.5.x)에는 직접 application.properties에 url 등을 직접 입력해야 했다면, OAuth2의 사용빈도와 저변을 직접 확인한 스프링 개발팀에서 redirectUrl, tockenUrl 등 이런 정보들을 enum으로 고정시킨 듯 하다.  

- 1.5.x 설정 방식
  - spring-security-oauth 라이브러리 사용
  - url 주소를 모두 명시해야 했다. 
- 2.x 설정 방식
  - spring-security-oauth2-client 라이브러리 사용
  - client 인증정보(clientId, clientSecret)만 입력하면 가능
  - 1.5.x에서 직접 입력하던 값들은 모두 enum으로 대체되었다.

를 사용한다.



인터넷 자료들(블로그, 깃허브) 등을 찾다보면 설정 방식이 1.5.x에서 하던방식과 크게 다르지 않은 면을 볼수 있다고 한다. 그 이유는

- spring-security-oauth2-autoconfigure 

라이브러리 덕분이라고 한다.

spring-security-oauth2-autoconfigure를 사용하면 스프링 1.5에서 지정한 설정들을 그대로 2.0으로 가져와 사용할 수 있다고 한다. **즉, 1.5.x의 설정이 2.0에서 호환되도록 하려면 spring-security-oauth2-autoconfigure를 사용하면 된다는 의미이다.** 하지만, 스프링의 버전이 올라가면서 신규기능이 생길 경우 1.5방식의 라이브러리에는 추가되지 않을 예정이라고 한다.



> 스프링 부트 2방식의 자료를 찾으려 할때 인터넷 자료들 사이에서 두가지만 확인하면 된다고 한다.
>
> 1. spring-security-oauth2-autoconfigure 라이브러리를 사용했는가?
> 2. application.properties / application.yml 정보가 차이가 있는지 확인



### ex) 구글, 깃허브, 페이스북, 옥타(Okta)의 url 등 여러가지 기본설정 enum

```java
public enum CommonOAuth2Provider{
  GOOGLE{
    @Override
    public Builder getBuilder(String registrationId){
      ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.BASIC, DEFAULT_REDIRECT_URL);
      builder.scope("openid", "profile", "email");
      builder.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth");
      builder.tokenUri("https://www.googleapis.com/oauth2/v4/token");
      builder.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs");
      builder.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo");
      builder.userNameAttributeName(IdTokenClaimNames.SUB);
      builder.clientName("Google");
      return builder;
    }
  }
}
```





# Spring Security Oauth2 Client의 장점

- 기존 1.5 방식(spring-security-oauth)은 유지상태, 신규기능은 추가되지 않는다. 신규기능은 oauth2-client 라이브러리에서만 지원하겠다고 공식 선언함
- 스프링 부트 전용 라이브러리(starter) 출시
- 기존 1.5 방식은 확장 포인트가 적절하게 오픈되지 않아 직접 상속하거나 오버라이딩 해야 하고, 2.x 라이브러리의 경우 확장 포인트를 고려해서 설계되어 있다.





