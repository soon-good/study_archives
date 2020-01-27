# Google OAuth 생성



## OAuth 클라이언트 ID 만들기

![google oauth 생성](/Users/kyle.sgjung/workspace/sgjung/study_archives/java/spring/oauth2/img/OAuth_클라이언트ID_만들기.png)



- 승인된 리다이렉션 URI

  - 서비스에서 파라미터로 인증정보를 주었을 때 인증이 성공하면 구글에서 리다이렉트할 URL

  - 스프링부트 2 시큐리티에서는 기본적으로  
    {도메인}/login/oauth2/code/[소셜서비스코드]  
    형식의 리다이렉트 URL을 지원한다.

  - 사용자가 별도로 리다이렉트 URL을 Mapping하는 Controller를 만들 필요가 없다.  
    시큐리티에서 이미 구현해놓은 상태이다.

  - 일단, 승인된 리다이렉션 URI를  
    http://localhost:8080/login/oauth2/code/google 로 등록했다.  

  - AWS 서버에 배포시에는  

    localhost외에 추가로 주소를 추가해야 한다. 추후 AWS관련 내용 정리시 이 부분 정리예정.

  

  

  

  

