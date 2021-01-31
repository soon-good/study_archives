# AWS SDK 서명 & 접속권한 관련 네비게이션

> 인증에 관련된 자료들을 찾아보기 편하도록 정리한 네비게이션이다. 화이팅!!

찾아본 자료가 정말 많기는 하다. 처음에는 호기만 넘치고 AWS ES를 잘 모르는 상태로 두시간내에 할 수 있어 이런 마음으로 했는데 막상 하다보니 매뉴얼 문서를 차분히 읽어보고 있다. 개념도 이것 저것 머릿속에 퍼져있었는데 조금씩 잡혀가는것 같다. 화이팅!!  



# 참고한 자료

https://aws.amazon.com/ko/premiumsupport/knowledge-center/anonymous-not-authorized-elasticsearch

이 자료에서 각종 문서들의 링크를 제공하고 있는데 내용이 무지 방대하다...;; 아무것도 모르는 상태에서는 망망대해를 조각배로 떠다니는 기분이었다.  

참고) IAM 정책 설명  

https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies.html



# **Elasticsearch 클러스터 직접 접속 시의 에러**‌

Elasticsearch 클러스터에 접속하려 할 때 에러가 나는 경우가 있다. 참고해본 자료는 https://aws.amazon.com/ko/premiumsupport/knowledge-center/anonymous-not-authorized-elasticsearch 이다.

```plain
User: anonymous is not authorized
Amazon Elasticsearch Service(Amazon ES) 도메인 또는 Kibana에 액세스하려고 하면 "사용자: 익명이 승인되지 않습니다(User: anonymous is not authorized)"라는 오류 메시지가 표시됩니다. 이 오류를 해결하려면 어떻게 해야 합니까?
```

  

여기서는 여러가지 해결책을 제시하고 있다.  

- IP 기반 액세스 정책
  - https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-ac.html#es-ac-types-ip
- 액세스 정책에 지정된 IP 주소에 CIDR 표기법이 제대로 표시되어있는지 확인해본다.
- 액세스 정책에 지정한 IP 주소가 클러스터에 액세스할 수 있도록 화이트리스트에 추가된 주소인지 확인해본다.
  - [https://checkip.amazonaws.com](https://checkip.amazonaws.com/)/ 에서 로컬 컴퓨터의 퍼블릭 IP 주소를 확인할 수 있다.
  - **예전에 탄력적 IP 관련 내용 추가하면서 IP 허용정책을 정리한 적이 있는데 그부분 다시한번 보고 적용해보자.**
- VPC 도메인 액세스 정책
  - https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-vpc.html#es-vpc-security

  

# **클라이언트/앱/웹 프로그램 내에서의 접속시의 에러**‌

요청 서명을 지원하는 클라이언트를 사용하는 경우 아래의 내용을 확인해야 한다.소프트웨어 프로그램에서 접속하도록 하는 코드를 짠 경우 Sigv4 를 제대로 사용해서 접속 코드를 작성해야 한다.

- 요청에 올바르게 서명한 것인지 확인해야 한다. Sigv4 프로세스 (서명 버전 4 서명 프로세스)를 확인하여 요청 서명 확인
  - 서명 버전 4 서명 프로세스 :
    - https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html
  - Amazon ES 요청 서명 및 인증
    - https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-ac.html#es-managedomains-signing-service-requests
  - Amazon 리소스 이름(ARN) 확인
    - https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html



# **Amazon Elasticsearch Service 의 자격증명 및 액세스 관리**

> 가이드 문서 :  https://docs.aws.amazon.com/ko_kr/elasticsearch-service/latest/developerguide/es-ac.html#es-managedomains-signing-service-requests

  ‌

참고할 만한 문서 :

- AWS SDK 대시보드
  - https://aws.amazon.com/ko/tools/#sdk



개별 문서로 따로 정리해야 할 듯 하다. 중요한 내용들도 많이 있는 것 같다. Amazon ES 에서 허용하는 액세스 정책은‌

- 리소스 기반 정책
- 자격 증명 기반 정책
- IP 기반 정책‌

이 있다.

## **리소스 기반 정책**‌

대략적인 구조와 모습은 아래와 같다. 좀 더 자세한 예제는 개별 문서에 따로 정리할 예정이다.

```plain
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::123456789012:user/test-user"
        ]
      },
      "Action": [
        "es:*"
      ],
      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/*"
    }
  ]
}
```



## 자격증명 기반 정책

AWS Identity and Access Management (IAM) 서비스를 사용하는 방식이다. IAM을 사용하는 경우 Cognito 역시 연동해야 하는데 이에 대해서는 개별문서로 정리할 예정이다. 자격증명 (IAM)기반 정책의 대략적인 구조와 모습은 아래와 같다.

```plain
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "es:Describe*",
        "es:List*"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
```

또 다른 예제이다.

```plain
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "es:*"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
```



## IP 기반 정책‌

IP 기반 정책은 Amazon ES 도메인에 대해 서명되지 않음 무서명 요청이 가능하다. 이렇게 하면 IP 주소 등을 화이트리스트에 넣어두면 curl, kibana 를 사용하여 프록시 서버를 통해 도메인에 액세스 할 수 있다. 키바나를 프록시를 사용해서 Amazon ES에 액세스하는 방식에 대한 가이드 문서는 https://docs.aws.amazon.com/ko_kr/elasticsearch-service/latest/developerguide/es-kibana.html#es-kibana-proxy 에 있다. 간략한 예제를 통해 대략적인 형식을 정리해보면 아래와 같다.지정된 IP 범위(192.0.2.0/24) 내의 모든 HTTP 요청에 대해 액세스 권한을 부여하고 있다.

```plain
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "*"
      },
      "Action": [
        "es:ESHttp*"
      ],
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": [
            "192.0.2.0/24"
          ]
        }
      },
      "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/*"
    }
  ]
}
```

IAM 과 결합해서 사용하는 예제 역시 있다. 아래의 예제를 확인해보자.

```plain
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {
      "AWS": [
        "arn:aws:iam::987654321098:user/test-user"
      ]
    },
    "Action": [
      "es:ESHttp*"
    ],
    "Condition": {
      "IpAddress": {
        "aws:SourceIp": [
          "192.0.2.0/24"
        ]
      }
    },
    "Resource": "arn:aws:es:us-west-1:987654321098:domain/test-domain/*"
  }]
}
```



## **Amazon ES 요청 서명 및 인증**

완전히 개방적인 리소스 기반 액세스 정책을 구성해도 구성 API 에 대한 모든 Amazon ES 요청에 대해 서명해야 한다. 정책에서 IAM 사용자 or 역할을 지정할 때 Elasticsaerch APIs 에 대한 요청 역시 **AWS 서명 버전 4** 를 사용해 서명되어야 한다. Java 샘플 코드는 아래에서 제공하고 있다.‌

- Amazon Elasticsearch Service와 함께 AWS SDK 사용
  - https://docs.aws.amazon.com/ko_kr/elasticsearch-service/latest/developerguide/es-configuration-samples.html
  - https://docs.aws.amazon.com/ko_kr/elasticsearch-service/latest/developerguide/es-ac.html#es-managedomains-signing-service-requests
- Amazon Elasticsearch Service 에 대한 HTTP 요청 서명
  - AWS SDK 의 서명 기능을 적용하는 예제에 대한 문서를 제공하고 있다.
  - Java, Python 등에 대한 모든 예제가 있다.
  - https://docs.aws.amazon.com/ko_kr/elasticsearch-service/latest/developerguide/es-request-signing.html

‌

구성 API를 호출하려면 Amazon ES AWS SDKs 중 하나를 사용해야 한다고 한다. 프로세스를 대폭 줄이게 되며 시간이 절약된단다. SDKs 구성 API 엔드 포인트는 아래의 형식이다. 구성 API라는 게 아직은 뭔지 잘 모른다.

```plain
es.region.amazonaws.com/2015-01-01/
```

아래의 경우도 예제로 제공하고 있는데 이 경우 full text로 하드코딩으로 서명을 지정하는 경우의 예제이다. curl 명령어에 json 속성을 지정해 보내는 경우이다.

```plain
POST https://es.us-east-1.amazonaws.com/2015-01-01/es/domain/movies/config
{
  "ElasticsearchClusterConfig": {
    "InstanceType": "c5.xlarge.elasticsearch"
  }
}
```



# Kinana 에 대한 Amazon Cognito 인증

>  가이드 문서 : https://docs.aws.amazon.com/ko_kr/elasticsearch-service/latest/developerguide/es-cognito-auth.html

이 부분에 대해서도 역시 https://aws.amazon.com/ko/premiumsupport/knowledge-center/anonymous-not-authorized-elasticsearch 에서 참고 자료를 네비게이션으로 제공해준다.  

Amazon Elasticsearch 사용시에 IAM 정책을 적용하면 KIBANA 세팅시에 Cognito 를 연동해야 한다. 해당 과정이 복잡하고 지금 바로 적용하지 못해서 언젠가 해야겠다하고 벼르고 있기는 하다. 빠른 적용을 위해서 관련 자료를 정리해두었다.  



# **Amazon Elasticsearch Service 문제 해결**

>  가이드 문서 : https://docs.aws.amazon.com/ko_kr/elasticsearch-service/latest/developerguide/aes-handling-errors.html

이 부분에 대해서도 역시 https://aws.amazon.com/ko/premiumsupport/knowledge-center/anonymous-not-authorized-elasticsearch 에서 이 문서의 최하단에 **Amazon Elasticsearch Service** 문제 해결 이라는 이름의 네비게이션으로 URL 을 제공하고 있다.  

여러가지 자료들이 있는데 대부분이 VPC를 사용할 경우에 대한 에러들이 대부분이다**.**  

가이드 문서의 내용들 중에서 도움이 되었던 챕터는 **SDK를 사용할 때 인증서 오류** 라는 부분이다. CA 인증서가 오류를 낼 때에 대한 경우인데, 아직은 이 부분까지 에러를 겪어보지는 않아서 모르겠다.  



# **Kibana 에 대한 액세스 제어**

>  가이드 문서 : https://docs.aws.amazon.com/ko_kr/elasticsearch-service/latest/developerguide/es-kibana.html#es-kibana-access

이 부분에 대해서도 역시 https://aws.amazon.com/ko/premiumsupport/knowledge-center/anonymous-not-authorized-elasticsearch 에서 이 문서의 최하단에 **Kibana에 대한 액세스 제어** 라는 이름의 네비게이션으로 URL 을 제공하고 있다.Kibana 사용시 사용하게 되는 여러가지의 인증 방식에 대해 잘 정리된 문서이다. 위에 정리해둔 Kibana 에 대한 Amazon Cognito 인증 부분에 비해 이해가 쉽도록 큰 흐름으로 정리된 문서이다.  



# **Amazon 리소스 이름(ARN)**

> 가이드 문서 : https://docs.aws.amazon.com/ko_kr/general/latest/gr/aws-arns-and-namespaces.html

아마존 리소스 이름이라는 것에 대해 이해가 잘 가지 않았고 어떤 형식인지 잘 모르고 있었는데 이 문서를 보고 제대로 파악하게 되었다. ARN의 형식을 외우고 있다면 그건 정말 뻔뻔하게 거짓말을 하고 있는 걸지도 모른다. 아니면 네트워크 엔지니어라 워낙 자주 이 업무를 하시는 분이라 가능하신 걸지도 모르겠다. 아무튼 나중에 형식을 다시 찾아볼때, Amazon ES 를 연동하면서 ARN을 찾아봤었는데? 여기서 찾아봐야겠군 하는 마음으로 찾아보게 될것 같아 링크를 남겨두었다.  