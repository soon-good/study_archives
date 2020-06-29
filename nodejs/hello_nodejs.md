# node helloworld

간단한 문법 검사 등을 위한 웹 에디터는 많다. code sandbox, fiddler 등등...  

근데, 빌드 스크립트 등을 테스트해보고 node에서 빌드해보는 것을 로컬에서 해봐야 할 때가 있다. 로컬에서 node로 javascript 파일을 실행시키는 방식을 정리해보자.    

실무에서 1년 이상을 사용하고나서 근 1년을 손놓고 있다가 막상 다시 시작하려니 겁부터 나는 것이 현실이긴 하다 ㅋㅋ. 이번기회에 헷갈렸던 개념들도 같이 조져놓고 시작하려 한다.  

# helloworld.js

예) helloworld.js

```javascript
function helloworld(){
  console.log("hello world ~ ");
  helloKorean();
}

function helloKorean(){
  console.log("hello Korean ~ 반가워요!! ");
}

helloworld();
```



# 실행

예) 출력결과

```bash
$ node helloworld
hello world~ 
hello Korean ~ 반가워요 !!
```

