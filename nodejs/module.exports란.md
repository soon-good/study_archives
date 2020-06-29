# module.exports

angularjs 를 실무에서 다루면서 어느정도 익혔다 싶었는데, 1년 가까이 지금 현 시점에서 한번도 안쓰니 다 까먹었다. 하나하나 정리한다 오늘부터 ㅋㅋ.  

정리를 안하면 뇌 속에 공간이 만들어지지 않는듯 하다. 논리적인 공간의 방을 뇌속에 만들어보자.  



# 모듈이란?

쉽게 설명해보면 Node.js에서 전역적으로 사용할 수 있는 하나의 모듈을 선언하는 것을 말한다. 모듈 내에 지정할 수 있는 대상은 함수, 객체, 변수 모두에 해당한다.  

예제) 안녕하세요~ 문구를 출력하는 두가지 버전(kr, en)의 함수이다.

```javascript
getGreetingKr = function(){
  return "안뇽하세요";
};

getGreetingEn = function(){
  return "Hello";
}
```



# 브라우저에서의 모듈

2015년 자바스크립트에도 import/export라는 모듈 개념이 도입되었다. 하지만 브라우저에는 구현되지 않아 사용할 수 없었다. 크롬 60버전부터 브라우저에서 모듈을 사용할 수 있게 되었다고 한다.  



# 모듈에 등록하기

## greetings.js

module.exports 에

- getGreetingKr
- getGreetingEn

함수를 등록해보자.  

**예) greetings.js**

```javascript
var exports = module.exports = {};

exports.getGreetingKr = function(){
  return "안뇽하세요";
};

exports.getGreetingEn = function(){
  return "Hello~";
};
```

## concat.js

module.exports

- getGreetingKrAndEn()

함수를 등록하자.  

**예) concat.js**

```java
const {getGreetingEn, getGreetingKr} = require("./greetings");

function getGreetingKrAndEn(){
    return getGreetingEn() + ":: (한국어) >>> " + getGreetingKr();
}

module.exports = getGreetingKrAndEn;
```



# 모듈 사용하기

**main.js**

```javascript
console.log("======= SAMPLE =======");
const {getGreetingEn, getGreetingKr} = require("./greetings");

console.log(getGreetingEn());
console.log(getGreetingKr());

console.log('');
console.log('======= OLD STYLE =======');
var greetings = require("./greetings");
console.log(greetings.getGreetingEn());
console.log(greetings.getGreetingKr());

const getGreetingKrAndEn = require('./concat')
console.log('');
console.log('======= CONCAT =======');
console.log(getGreetingKrAndEn());
```



출력결과)

```bash
$ node main
======= SAMPLE =======
Hello ~ 
안뇽하세요 !!

======= OLD STYLE =======
Hello ~ 
안뇽하세요 !!

======= CONCAT =======
Hello ~ :: (한국어) >>> 안뇽하세요 !!
```



# concat.js 개선

ES2015 이후의 버전에서는 concat.js를 아래와 같이 바꿀 수 있다.

다른 점은

- const {getGreetingEn, getGreetingKr} = require("./greetings");
  - 를  import {getGreetingEn, getGreetingKr} from './greetings';  
    로 변경 가능하다.
- module.exports = getGreetingKrAndEn;
  - 는 exports default getGreetingKrAndEn; 으로 변경 가능하다.



# 참고자료

- [module.exports와 exports 차이 이해하기](https://jongmin92.github.io/2016/08/25/Node/module-exports_exports/)
- [chullin 블로그]([https://medium.com/@chullino/require-exports-module-exports-%EA%B3%B5%EC%8B%9D%EB%AC%B8%EC%84%9C%EB%A1%9C-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0-1d024ec5aca3](https://medium.com/@chullino/require-exports-module-exports-공식문서로-이해하기-1d024ec5aca3))
- [Node.js 교과서](http://www.yes24.com/Product/Goods/62597864?scode=032&OzSrank=1)

