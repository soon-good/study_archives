# 작업과정 정리 ... (vuex, axios 커스터마이징)

# 1. vuex 설치 및 초기 설정
## vuex (npm 패키지) 설치
```bash
$ npm install vuex --save
```

## store 컴포넌트 생성
store를 컴포넌트라고 해야 할지 모르겠지만, 일단은 컴포넌트라고 지칭하고 해보자...  
작업 순서를 글로 정리해보면 아래와 같다.
- src/ 밑에 'store'디렉터리를 생성한다.
- src/store 디렉터리 밑에 
    - actions.js
    - getters.js
    - index.js
    - mutations.js
    - states.js
를 생성한다.  

## index.js 
src/store/index.js 파일 내에는 아래의 코드를 입력하자. index.js 는 store 라는 디렉터리의 진입점이다.
### index.js  
**src/store/index.js**  
```javascript
// vue, vuex import 
import Vue from 'vue';
import Vuex from 'vuex';

/**
 * 우리가 생성한 store 디렉터리내의 
 * - state.js
 * - getters.js
 * - mutations.js
 * - actions.js
 * 들을 import 하고 있다. 
 * 
 * 각각 .js를 붙이는 것이 보기 싫다면 해당 이름으로 디렉터리를 만들고
 * 디렉터리명/index.js를 만들면 된다... 
 * 연습용 예제에 이렇게 까지 복잡하게 초반에 할 필요는 없으므로... skip
 */
import state from './state.js';
import getters from './getters.js';
import mutations from './mutations.js';
import actions from './actions.js';

Vue.use(Vuex); // Vue 가 Vuex를 사용하도록 선언한다. 

// Vuex.Store({...}) 찾아보자 ㅠㅜ
export default new Vuex.Store({
    state,
    getters,
    mutations,
    actions
});
```
### 코딩 컨벤션
**참고) index.js (코딩컨벤션)**  
예를 들어 src/main.js 안에서 위와 같이 
```javascript
// ...
import store from './store';
// ...
```
와 같이 선언하면 이것은 ./store 디렉터리의 index.js 를 기반으로 모듈을 store라는 이름으로 import 하겠다는 의미이다. 쉽게 설명하면, './store' 까지만 적어주면 ./store 디렉터리의 index.js 파일을 찾아서 import 한다.  

## vue 개발자 도구 확인
![이미자](./img/AFTER_VUEX_WAS_REGISTERED.png)  

# 2. vuex에 데이터 저장 & 데이터 표현
