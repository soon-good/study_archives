# kubectl 명령어 치트시트

## 리소스 조회

### namespace 조회

```bash
$ kubectl get namespace
...
```

  

### pod, deployment, service 조회

```bash
$ kubectl get pod,deployment,service
```

  

### 명령 실행

컨테이너 내부를 셸로 접근해보자.  

컨테이너가 여러개인 경우 -c옵션으로 컨테이너 명을 지정한다. 

```bash
$ kubectl exec -it pod-helloworld-sgjung sh -c nginx
```

표준출력을 보자(실행했을 때 stdout으로 나오는 메시지 확인)

```bash
$ kubectl logs -f pod-helloworld-sgjung -c echo
```



## 리소스 생성

ex) pod-helloworld.yaml

```yaml
apiVersion: v1
kind: Pod
metadata:
    name: pod-helloworld-sgjung
spec:
    containers:
    - name: nginx
      image: gihyodocker/nginx:latest
      env:
      - name: BACKEND_HOST
        value: localhost:8080
      ports:
      - containerPort: 80
    - name: echo
      image: gihyodocker/echo:latest
      ports:
      - containerPort: 8080
```



### 매니페스트 파일로 리소스 생성

```bash
$ kubectl apply -f pod-helloworld.yaml
pod/pod-helloworld-sgjung created
```



## 리소스 삭제

### 매니페스트 파일로 리소스 삭제

```bash
$ kubectl delete -f pod-helloworld.yaml
pod "pod-helloworld-sgjung" deleted
```

  

### 단순 파드명 지정하여 삭제

```bash
$ kubectl delete pod pod-helloworld-sgjung
pod "pod-helloworld-sgjung" deleted
```

# context 조작하기

원격의 cluster에 접속할 때 context를 switch 해가면서 접속할 수있다.

## 컨텍스트 조회

```bash
$ kubectl config get-contexts
CURRENT   NAME                  CLUSTER               AUTHINFO         NAMESPACE
          docker-desktop        docker-desktop        docker-desktop
          docker-for-desktop    docker-desktop        docker-desktop
          kyle-bot-context      kyle-bot-cluster      kyle.sgjung
*         kyle-cluter-context   kyle-cluter-cluster   kyle.sgjung
```



## 컨텍스트 변경

```bash
$ kubectl config use-context kyle-bot-context
Switched to context "kyle-bot-context".
```



## 컨텍스트 삭제

```bash
$ kubectl config delete-context kyle-bot-context
deleted context kyle-bot-context from /Users/kyle.sgjung/.kube/config
```

