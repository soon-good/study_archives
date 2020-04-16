# axios 단순 설정
axios를 약식으로 설정하는 과정을 다뤄보자.  
  
# axios 설치
```bash
$ npm install axios --save
```

# axios 커스텀 모듈 생성
## 디렉터리 생성
axios 설정을 커스터마이징한 모듈을 따로 별도의 디렉터리에 모아둘 예정이다.  

- src 디렉터리 밑에 api 디렉터리를 생성하자.
- src/api 디렉터리 밑에 index.js 파일을 생성하자.

## index.js 내용
```javascript
import axios from 'axios';

export default axios.create({
    baseURL: '//localhost:8000/api'
});
```

# 커스텀 모듈로 만들어 사용하는 이용
index.js 안의 소스는 굉장히 간단하다. Axios 라이브러리를 가져온 후 Axios 인스턴스의 create 메서드로 기본값을 설정한 것을 export default 로 외부로 내보내는 모듈이다.  
  
커스텀 모듈을 사용하지 않고 기본 axios api만을 사용할 경우 아래와 같이 사용하면 된다. 
```javascript
import axios from 'axios'

axios.get('//localhost:8000/api/call');
```
만약 30번 이상의 http api 요청 로직이 있다고 해보자.
```javascript
axios.get('//localhost:8000/api/call1');
axios.get('//localhost:8000/api/call2');
// ...
axios.get('//localhost:8000/api/call30');
// ...
```
소스 전반에 걸쳐서 api 의 baseURL 주소를 텍스트로 일일이 넣어줄 필요는 없다. 위에서 작성한 커스텀 모듈을 사용할 경우의 예를 살펴보자
**src/api/index.js**  
```javascript
// src/api/index.js
import axios from 'axios';

const api = axios.create({
    baseURL: '//localhost:8080/api'
});
```
실제 프로젝트 로직에서 사용하게 되는 경우를 예로 들어보자.
```javascript
api.get('/call1');
api.get('/call2');
api.get('/call3');
// ...
api.get('/call30');
// ...
```
  
중복되는 baseURL이 없어지니 다른 변수들에 의존적이지 않게 되었다.