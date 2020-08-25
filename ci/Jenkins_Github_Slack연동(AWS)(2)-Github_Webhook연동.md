# Jenkins/Github/Slack연동 - Github Webhook 연동



# 1. 젠킨스 build 잡 구성

## 1) 젠킨스 사용자 api token 생성

> Github에서 젠킨스에 접근할 때 인증과정에 사용할 api token 을 생성하는 과정이다.  

젠킨스 > 사용자명(여기서는 sgjung) 클릭 > 설정

![이미지](./img/JENKINS_GITHUB_WEBHOOK/5.png)



API Token > Add New Token

![이미지](./img/JENKINS_GITHUB_WEBHOOK/6.png)



Generate

![이미지](./img/JENKINS_GITHUB_WEBHOOK/7.png)



복사 버튼을 눌러서 Api Token을 복사해두고 다른 곳에 꼭 메모를 해놓아야 한다. 뒤에서 Github 리포지터리 Settings에 등록할 예정이다. 복사해두는 것이 완료되었다면 Save 버튼을 누르자.

![이미지](./img/JENKINS_GITHUB_WEBHOOK/8.png)



## 2) build job 생성

Pull Request시 Github에서 오는 json 메시지를 받아서 처리하는 역할을 하는 Build Job을 생성하자.  

![이미지](./img/JENKINS_GITHUB_WEBHOOK/1.png)



- 빌드아이템은 Pipeline을 선택
- 빌드 Job의 이름 입력
  - 빌드 Job의 이름은 앞으로도 계속 GITHUB_WEBHOOK\_[리포지터리명]\_[이벤트명] 으로 지을 생각이다.

![이미지](./img/JENKINS_GITHUB_WEBHOOK/2.png)



- 이 빌드는 매개변수가 있습니다. 체크
- 매개변수 추가 > String Parameter 선택
- 매개변수명 : payload 로 지정

![이미지](./img/JENKINS_GITHUB_WEBHOOK/3.png)



Build Triggers > 빌드를 원격으로 유발(예 : 스크립트 사용) 선택

- Authentication Token 

  - 방금전 생성한 Authentication Token에 대한 이름을 지정해주는 과정

  - 이름은 jenkins\_[사용자명]\_api\_token 으로 지정해주었다.

  - 여기서 등록한 Authentication Token 은 github 리포지터리 Webhook 설정에서 payload URL을 명시할 때 사용된다.

  - github Webhook 내에서 payload URL을 지정할 때 

    - 젠킨스 사용자명
    - authentication token
    - authentication token 명 (별칭)  

    을 명시해주는데 이렇게 하는 이유는 인증시 키가 되는 것을 복합키로 해두기 위한 것으로 보인다. authentication token을 등록하고 이 토큰명을 요구하고, 젠킨스 사용자명 까지 요구하는 것을 보면 그런듯 하다.

![이미지](./img/JENKINS_GITHUB_WEBHOOK/4.png)



## 3) Groovy Script 

- Pipeline 탭
- Definition
  - Pipeline script 를 선택합니다.
  - script 아래와 같은 스크립트를 입력합니다.

![이미지](./img/JENKINS_GITHUB_WEBHOOK/12.png)



groovy script

```groovy
node {
    stage('github push event'){
        print "DEBUG:payload=${payload}"
    }
}
```





# 2. Github Webhook 설정



## 1) Webhook 추가 (정리필요!!!)

![이미지](./img/JENKINS_GITHUB_WEBHOOK/9.png)



http://ec2-3-34-170-110.ap-northeast-2.compute.amazonaws.com:7777/  



http://ec2-3-34-170-110.ap-northeast-2.compute.amazonaws.com:7777/job/GITHUB_WEBHOOK_CHARTNOMY_COMMIT/buildWithParameters?token=jenkins_admin_api_token  



http://sgjung:11541fce8dae7356e1d9d363f78da1dc4a@ec2-3-34-170-110.ap-northeast-2.compute.amazonaws.com:7777/job/GITHUB_WEBHOOK_CHARTNOMY_COMMIT/buildWithParameters?token=jenkins_admin_api_token  



![이미지](./img/JENKINS_GITHUB_WEBHOOK/10.png)



![이미지](./img/JENKINS_GITHUB_WEBHOOK/11.png)





## 2) 테스트

### Github 에서 테스트

> github webhook 을 처음 생성하고 나면 bulk payload 같은것이 미리 생성되어 있다. 이것을 redeliver 해보자  

Recent Deliveries > 웹훅 클릭  

![이미지](./img/JENKINS_GITHUB_WEBHOOK/13.png)



Redeliver 버튼 클릭

![이미지](./img/JENKINS_GITHUB_WEBHOOK/14.png)



빌드가 원격에서 보낸 Web Hook 에 의해 유발된 모습

![이미지](./img/JENKINS_GITHUB_WEBHOOK/15.png)

