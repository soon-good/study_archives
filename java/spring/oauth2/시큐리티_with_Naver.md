# 시큐리티 with Naver

네이버에서는 스프링 시큐리티를 공식으로 지원하지 않는다. 따라서 CommonOAuth2Provider 라는 enum에서 제공해주던 값들도 모두 수동으로 입력해야 한다.  

스프링 부트의  CommonOAuth2Provider에서 기본적으로 제공하는 정보들이 없을 경우 직접 수동으로 지정하는 경우에 대한 예를 습득할 수 있기 때문에 이번 네이버 아이디 로그인 예제는 좋은 예제가 되는 것 같다.  



- application-oauth.properties
- OAuthAttributes
- index.html



# application-oauth.properties

```properties
# ...
# naver
spring.security.oauth2.client.registration.naver.client-id=[클라이언트 id]
spring.security.oauth2.client.registration.naver.client-secret=[클라이언트 시크릿]
#spring.security.oauth2.client.registration.naver.redirect_uri_template={baseUrl}/{action}/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.naver.redirect-uri={baseUrl}/{action}/oauth2/code/{registrationId}
#spring.security.oauth2.client.registration.naver.authorization_grant_type=authorization_code
spring.security.oauth2.client.registration.naver.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.naver.scope=name,email,profile_image
spring.security.oauth2.client.registration.naver.client-name=Naver
# provider
#spring.security.oauth2.client.provider.naver.authorization_uri=
spring.security.oauth2.client.provider.naver.authorization-uri=https://nid.naver.com/oauth2.0/authorize
#spring.security.oauth2.client.provider.naver.token_uri=
spring.security.oauth2.client.provider.naver.token-uri=https://nid.naver.com/oauth2.0/token
spring.security.oauth2.client.provider.naver.user-info-uri=https://openapi.naver.com/v1/nid/me
#spring.security.oauth2.client.provider.naver.user_name_attribute=
spring.security.oauth2.client.provider.naver.user-name-attribute=response
# 기준이 되는 user_name의 이름을 네이버에서는 response로 해야 한다.
# 이유는 네이버의 회원 조회시 반환되는 JSON 형태때문이다.
```



- user-name-attribute = response
  - 기준이 되는 user_name의 이름은 response로 해준다.(네이버 일 경우에만 한정)
  - 네이버에서 json으로 리턴해주는 JSON의 형태가 일반적인 경우와 다르기 때문이다.

  

**네이버 json 응답 예**

```json
{
  "resultcode": "00",
  "message": "success",
  "response": {
    "email": "openapi@naver.com",
    "nickname": "OpenAPI",
    "profile_image": "https://ssl.pstatic.net/static/pwe/address/nodata_33x33.gif",
    "age": "40-49",
    "gender": "F",
    "id": "32742776",
    "name": "오픈API",
    "birthday": "10-01"
  }
}
```

스프링 시큐리티에서는 하위 필드를 명시하는 것이 불가능하다. 최상위 필드들만 user_name으로 지정가능하다. 그런데, 네이버 응답값의 최상위 필드들은 

- resultCode
- message
- response

이다. 이런 이유로 스프링 시큐리티에서 인식 가능한 필드는 resultCode, message, response 중에 하나를 골라야 한다. 이런 이유로  

1. 응답 메시지 본문에서 담고 있는 response를  
   - user_name으로 지정하고
2.  자바 코드로 response의 id를 
   - user_name으로 지정한다.

  

# OAuthAttributes - 스프링 시큐리티 설정 

1. ofNaver(...) 에서 attribute의 각 필드들을 파싱해 builder 패턴으로 OAuthAttributes 인스턴스를 생성한다.
2. of(...) 메서드에서 Naver 타입의 응답일 경우에 대해 Naver 타입으로 지정해 생성한 OAuthAttribute 인스턴스를 ofNaver(...) 메서드로 리턴한다.  



# index.html

  

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title> WELCOME HOME </title>
  <link rel="stylesheet"
        href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>
  <a th:if="${userName}" href="/logout" class="btn btn-info active" role="button">
    로그아웃
  </a>
  <a th:unless="${userName}"
     href="/oauth2/authorization/google"
     class="btn btn-success active"
     role="button">
    구글 로그인
  </a>
  <a th:unless="${userName}"
     href="/oauth2/authorization/naver"
     class="btn btn-secondary active"
     role="button">
    네이버 로그인
  </a>
</body>
</html>
```



- /oauth2/authorization/naver
  - 네이버 로그인의 URL은 application-oauth.properties에 등록한 redirect_uri_template 값에 맞춰 자동으로 등록된다.
  - /oauth2/authorization/ 까지는 고정, 마지막 Path(여기서는 "naver")만 소셜 로그인 코드를 사용하면 된다.

