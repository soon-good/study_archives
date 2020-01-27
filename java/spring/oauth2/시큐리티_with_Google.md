# 시큐리티 with Google

# 참고자료

- [thymeleaf 설정1](https://eblo.tistory.com/54)
- [thymeleaf 설정2](https://araikuma.tistory.com/30)
- [thymeleaf 조건문](https://elfinlas.github.io/2018/02/18/thymeleaf-if-exam/)
- 



# 0) build.gradle

oauth2-client 의존성을 추가해주자

> ```groovy
> compile('org.springframework.boog:spring-boot-starter-oauth2-client')
> ```

그리고 login을 web에서 확인해보기 위해 thymeleaf도 추가해주자

> ```groovy
> compile('org.springframework.boot:spring-boot-starter-thymeleaf')
> ```



spring-boot-starter-oauth2-client

- SNS 로그인 구현시 클라이언트 입장에서 필요한 의존성
- spring-security-oauth2-client, spring-security-oauth2-jose를 기본으로 관리해준다.



참고) build.gradle

```groovy
buildscript {
    ext{
        springBootVersion = '2.1.7.RELEASE'
    }
    repositories{
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}

apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

group 'com.stock.data'
version '1.0-SNAPSHOT'
sourceCompatibility = 1.8

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile('org.springframework.boot:spring-boot-starter-web')
    compile('org.projectlombok:lombok')
    annotationProcessor('org.projectlombok:lombok')
//    compile('org.springframework.boot:spring-boot-starter-test')
    compile('org.springframework.boot:spring-boot-starter-data-jpa')
    compile('org.springframework.boog:spring-boot-starter-oauth2-client')
    compile('org.springframework.boot:spring-boot-starter-thymeleaf')
    compile('com.h2database:h2')
    testCompile('org.springframework.boot:spring-boot-starter-test')
}
```





# 1) properties 설정

## properties 파일 설정

src/main/resources 디렉터리 아래에 application-oauth.properties 파일을 생성한다.

스프링 에서는 properties의 이름을 application-xxx.properties로 만들면 xxx라는 이름의 profile이 생성되어 이것으로 관리할 수 있다.  

### application.properties

```properties
...
spring.profiles.include=oauth
```



### application-oauth.properties

```properties
spring.security.oauth2.client.registration.google.client-id=[클라이언트 ID]
spring.security.oauth2.client.registration.google.client-secret=[클라이언트 비밀번호]
spring.security.oauth2.client.registration.google.scope=profile,email
```



- scope 에 profile, email을 명시적으로 지정했다

  - 기본값은 openid, profile, email이다.

  - profile, email로 강제 지정한 이유는 기본값 그대로 사용시 OpenId Provider가 아닌 서비스도 지원해야 하므로 OAuth2Service를 두개로 나누어 만들어야 한다. 

  - 즉, 하나의 OAuth2Service 내에서 

    - OpenId Provider인 서비스  
      ex) 구글
    - 그렇지 않은 서비스  
      ex) 네이버/카카오 등등...

    를 구현하기 위해서이다. 



## .gitignore 등록

클라이언트 ID, 클라이언트 보안 비밀은 보안이 중요한 정보들이다. 외부에 이 정보들이 노출되면 개인정보를 뺏길수 있는 사태가 발생할 수 있다. 깃허브내에서 보안 키를 수집하는 해커들도 있다는 이야기도 들은적이 있다.  

이런 이유로 보안을 위해 깃허브에 application-oauth.properties파일이 올라가는 것을 방지해야 한다. .gitignore 파일에 application-oauth.properties 파일을 지정해서 커밋 목록에서 제외하자.

### .gitignore

```properties
application-oauth.properties
```



# 2) 도메인 구현

domain에 대해  

  repository 코드, Entity 내의 dirty checking 코드, Role enum 추가 

등을 할 예정.  



1. Entity
2. Role
3. UserRepository -> JpaRepository



## Entity

Entity를 구현한다요

```java
package com.stock.data.domain.user;

import com.stock.data.domain.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column
	private String picture;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Builder
	public User(String name, String email, String picture, Role role){
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.role = role;
	}

	public User update(String name, String picture){
		this.name = name;
		this.picture = picture;
		return this;
	}

	public String getRoleKey(){
		return this.role.getKey();
	}
}
```



- @Enumerated(EnumType.STRING)
  - JPA로 데이터베이스에 저장할때 Enum을 어떤형태로 저장할지 지정
  - 기본적으로는 int로 된 숫자가 저장된다.
  - 숫자로 데이터베이스에 저장되면 데이터베이스 확인시 그 값의 의미 파악이 어렵다. 이런 이유로 문자열(EnumType.STRING)로 저장되도록 선언한다.

## Role

스프링 시큐리티에서는 권한 코드에 항상 ROLE_이 앞에 있어야 한다. 따라서 코드별 키 값을 

- ROLE_GUEST
- ROLE_USER

등 으로 지정하는 편이다.



## UserRepository -> JpaRepository

User의 CRUD를 책임질 UserRepository를 생성한다.  

```java
package com.stock.data.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);
}
```



- findByEmail
  - 소셜 로그인으로 반환되는 값 들 중에서 email을 이용해 이미 생성된 사자인지, 처음 가입하는 사용자인지 파악하기 위한 메서드



# 3) 시큐리티 설정(Java Config)

- config.auth 패키지를 생성한다. 
- SecurityConfig.java를 그 안에 작성한다.
- 이전에 공부했다면 알고있겠지만, UserDetailService 관련 로직은 바로 뒤에서 구현하니, 여기서는 빨간 줄로 에러 표시가 뜨더라도 무시하고 작성하자.

**SecurityConfig.java**

```java
package com.stock.data.config.auth;

import com.stock.data.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity // 1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CustomOAuth2UserService customOAuth2UserService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.headers().frameOptions().disable() // 2)
			.and()

			.authorizeRequests() // 3)
			.antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**")
				.permitAll() // 4)
			.antMatchers("/api/v1/**")
				.hasRole(Role.USER.name()) // 4)
			.anyRequest() // 5)
				.authenticated()
			.and()

			.logout().logoutSuccessUrl("/") //  6)
			.and()

			.oauth2Login() // 7)
				.userInfoEndpoint() // 8)
				.userService(customOAuth2UserService); // 9)

	}
}
```



- 1) @EnableWebSecurity  
  - Spring Security 설정들을 활성화 시켜준다.
- 2) csrf().disable().headers().frameOptions().disable()  
  - h2-console 화면을 사용하기 위해 해당 옵션들을 disable 한다.
- 3) authorizeRequests
  - URL별 권한 관리를 설정하는 옵션의 시작점
  - authorizeRequests가 선언되어야만 antMatchers 옵션을 사용할 수 있다.
- 4) antMatchers
  - 권한 관리 대상을 지정하는 옵션
  - URL, HTTP 메소드 별로 관리가 가능하다.
  - "/" 등으로 지정된 URL들은 permitAll()옵션으로 전체 열람권한을 주었다.
  - Post메서드이면서 "/api/v1/**" 주소를 가진 API는 USER 권한을 가진 사람만 가능하도록 함
- 5) anyRequest
  - 설정된 값들 이외의 나머지 URL들을 나타낸다.
  - authenticated()를 추가해 나머지 URL들은 모두 인증된 사용자들에게만 허용하도록 함
  - 인증된 사용자는 로그인한 사용자들
- 6) logout().logoutSuccessUrl("/")
  - 로그아웃 기능에 대한 여러 설정의 진입점
  - 로그아웃 성공시 / 주소로 이동한다.
- 7) oauth2Login
  - OAuth2 로그인 기능에 대한 여러 설정의 진입점
- 8) userInfoEndpoint
  - OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당한다.
- 9) userService
  - 소셜로그인 성공시 후 처리를 담당할 클래스를 등록
  - 여기서 등록하는 클래스는 UserService 인터페이스를  implements한 클래스여야 한다.
  - 리소스 서버(소셜 서비스 서버)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있다.



# 4) implements UserService



```java
package com.stock.data.config.auth;

import com.stock.data.config.auth.dto.OAuthAttributes;
import com.stock.data.config.auth.dto.SessionUser;
import com.stock.data.domain.user.User;
import com.stock.data.domain.user.UserRepository;
import java.util.Collections;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;
	private final HttpSession httpSession;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService delegator = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegator.loadUser(userRequest);

		// 1)
		String registrationId =
			userRequest.getClientRegistration().getRegistrationId();

		// 2)
		String userNameAttributeName =
			userRequest.getClientRegistration().getProviderDetails()
						.getUserInfoEndpoint()
						.getUserNameAttributeName();

		// 3)
		OAuthAttributes attributes =
			OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

		User user = saveOrUpdate(attributes);

		// 4)
		httpSession.setAttribute("user", new SessionUser(user));

		return new DefaultOAuth2User(
			Collections.singleton(
				new SimpleGrantedAuthority(user.getRoleKey())
			),
			attributes.getAttributes(),
			attributes.getNameAttributeKey()
		);
	}

	private User saveOrUpdate(OAuthAttributes attributes){
		User user =
			userRepository.findByEmail(attributes.getEmail())
							.map(entity->entity.update(attributes.getName(), attributes.getPicture()))
							.orElse(attributes.toEntity());

		return userRepository.save(user);
	}
}
```



- 1) registrationId
  - 현재 로그인 진행중인 서비스(GOOGLE, FACEBOOK, 네이버/카카오) 를 구분하는 코드  
  - 네이버 로그인 구현시 네이버/구글 로그인을 구별하기 위해 사용
- 2) userNameAttributeName
  - OAuth2 로그인 진행 시 키가 되는 필드값 (Primary Key와 같은 의미)
  - 구글의 경우 기본적으로 코드를 지원한다. 네이버/카카오는 기본 지원하지 않는다. 구글의 기본코드는 "sub"이다.  
  - 추후 네이버 로그인/ 구글 로그인을 동시 지원할 때 사용
- 3) OAuthAttributes
  - OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
  - 네이버 로그인 구현시에도 이 클래스를 사용 예정
  - 뒤의 Dto 구현부에서 코드가 나온다. 지금은 IDE에서 빨간줄이 나오더라도 쭉 지나갈것
- 4) SessionUser
  - 세션에 사용자 정보를 저장하기 위한 Dto 클래스
  - User 클래스를 재사용하지 않고 새로 만들어서 쓰는지에 대한 이유는 뒤에서 정리



# 5) dto 구현

## OAuthAttributes

```java
package com.stock.data.config.auth.dto;

import com.stock.data.domain.user.Role;
import com.stock.data.domain.user.User;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String name;
	private String email;
	private String picture;

	@Builder
	public OAuthAttributes(Map<String, Object> attributes,
							String nameAttributeKey, String name,
							String email, String picture){
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.name = name;
		this.email = email;
		this.picture = picture;
	}

	// 1)
	public static OAuthAttributes of(String registrationId,
										String userNameAttributeName,
										Map<String, Object> attributes){
		return ofGoogle(userNameAttributeName, attributes);
	}

	private static OAuthAttributes ofGoogle(String userNameAttributeName,
												Map<String, Object> attributes){
		return OAuthAttributes.builder()
			.name((String) attributes.get("name"))
			.email((String) attributes.get("email"))
			.picture((String) attributes.get("picture"))
			.attributes(attributes)
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	// 2)
	public User toEntity(){
		return User.builder()
			.name(name)
			.email(email)
			.picture(picture)
			.role(Role.GUEST)
			.build();
	}
}
```



- 1) of()
  - OAuth2User에서 반환하는 사용자 정보가 Map이므로 값의 한쌍 하나하나를 직접 변환해야 한다.
- 2) toEntity()
  - User 엔티티 생성
  - OAuthAttributes 에서 엔티티를 생성하는 시점은 처음 가입할 때이다.
  - 가입할 때의 기본 권한을 GUEST로 주기 위해 role빌더 값에는 Role.GUEST를 사용했다.



## SessionUser

인증된 사용자 정보만 담아둘 Dto.  

인증된 사용자 정보에 꼭 필요한 정보인 name, email, picture 만을 필드로 선언.  

**SessionUser.java**

```java
package com.stock.data.config.auth.dto;

import com.stock.data.domain.user.User;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class SessionUser implements Serializable {
	private String name;
	private String email;
	private String picture;

	public SessionUser(User user){
		this.name = user.getName();
		this.email = user.getEmail();
		this.picture = user.getPicture();
	}
}
```

  

### User 클래스를 재사용하지 않는 이유

만약 User 클래스를 그대로 사용하면 

> Failed to convert from type [java.lang.Object] to type [byte[]] for value 'com.stock.data.domain.user.User@1242c1'

와 같은 에러 메시지를 접하게 된다.  

User클래스를 세션에 저장하려 할때 **User클래스가 직렬화를 구현하지 않았다.** 라는 의미의 에러를 낸다. 

- 이 문제를 해결하기 위해 직렬화코드를 넣으면 ? 
  - User 클래스는 엔티티 역할을 수행하고 있따. 엔티티 클래스에는 언제 다른 엔티티와 관계가 형성될지 모른다.
  - 예를 들면 @OneToMany, @ManyToMany 등 자식 엔티티를 갖고 있다면 직렬화 대상에 자식들까지 포함되게 될수 있다.
  - 이로 인해 성능이슈, Side effect(부수효과)가 발생할 확률이 높다.

이런 이유로 직렬화 기능을 가진 세선 Dto를 직접 하나 더 추가해 만드는 것이 추후 운영/유지보수 시 많은 도움이 된다.  



# 로그인 테스트

메인 페이지를 만들어서 

- 로그인 하지 않았을 경우  
  로그인 버튼을 보여주고
- 로그인 했을 경우  
  '환영합니다.' 문구를 보여주기



> - http://localhost:8080/ 에 대한 Mapping을 가진 컨트롤러는
>   - IndexController
> - 리소스 파일(index.html) 파일은
>   - resources/templates/index.html 로 생성한다.



- application.properties
- IndexController
- index.html (thymeleaf)



## application.properties

java config로도 리소스의 위치를 지정할수 있다. 하지만 여기서는 properties 파일에 지정하도록 한다. 

- properites파일을 사용한다면 

  - spring의 profile을 적용해 java 코딩없이 리소스 위치를 동적으로 지정하는 것도 가능하지 않을까? 

  하는 생각에서다.  

**application.properties**

```properties
spring.thymeleaf.prefix=classpath:templates/
spring.thymeleaf.check-template-location=true
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML5
# 개발 모드로 수정하면서 내용을 확인하고자 할떼 cache를 false로 선언한다.
# 운영모드에서는 반드시 true로 설정해야 한다.
spring.thymeleaf.cache=false
spring.thymeleaf.order=0
```



## IndexController

- src/main/java/com.stock.data.web 아래에
  - index 패키지 생성
- src/main/java/com.stock.data.web.index 패키지 아래에
  - IndexController.java 생성

```java
package com.stock.data.web.index;

import com.stock.data.config.auth.dto.SessionUser;
import com.stock.data.domain.user.User;
import com.stock.data.web.posts.PostsService;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {

	private final PostsService postsService;
	private final HttpSession httpSession;

	@GetMapping("/")
	public String index(Model model){
		SessionUser user = (SessionUser) httpSession.getAttribute("user");

		if(user != null){
			model.addAttribute("userName", user.getName());
		}

		return "index";
	}
}
```



## index.html

대망의 index.html 페이지까지 왔다.

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







