# Vue Life Cycle

Vue 의 라이프 사이클을 이해하려면 먼저 Vue 인스턴스를 구성하는 기본요소를 먼저 이해해야 한다. 라이프 사이클의 각 요소들 또한 각각에 대해 정리해보자.

- 참고자료 

  - Vue.js 공식 가이드 문서

    - https://kr.vuejs.org/v2/guide/instance.html

    



# 1. Vue 인스턴스의 5가지 기본요소들

## Vue 인스턴스의 생성

Vue 앱은 보통 Vue 함수로 Vue 인스턴스를 새로 만드는 것으로 시작된다.

```javascript
var vm = new Vue({
	// 옵션
})
```

- MVVM 패턴 
  - 엄격하게는 MVVM패턴과 관련이 없다. 하지만 Vue 의 디자인은 부분적으로 MVVM에서 영감을 얻어 고안된 것이라고 한다.
  - 코딩 컨벤션으로 Vue 인스턴스를 참조할 때 변수명 **vm (View Model 의 약자)** 를 사용하는 편이다.

- Vue 의 옵션
  - Vue 인스턴스를 생성할 때에는 데이터, 템플릿, 마운트할 엘리먼트, 메서드, 라이프사이클 콜백 등의 옵션을 포함하는 options 객체를 전달해야 한다. 전체 옵션 목록은 [API Reference](https://kr.vuejs.org/v2/api/) 에서 찾을 수 있다.
- 컴포넌트 생성자
  - Vue 생성자는 미리 정의된 옵션으로 재사용 가능한 컴포넌트 생성자를 생성하도록 확장될 수 있다.
  - Vue 인스턴스는 중첩시킬 수 있다. 그리고 재사용 가능하도록 구성할 수도 있다. 재사용 가능한 컴포넌트 트리로 구성할 수도 있다.

예)

```vue
Root Instance
└─ TodoList
   ├─ TodoItem
   │  ├─ DeleteTodoButton
   │  └─ EditTodoButton
   └─ TodoListFooter
      ├─ ClearTodosButton
      └─ TodoListStatistics
```



위에서 제시했던 new Vue({...}) 구문은 컴포넌트 기반으로 프로젝트 진행시에는 아래와 같이 사용한다.

```vue
<template>
	<div id="app">
    <app-header :memoCount="memoCount"/>
    <memo-app @change="updateCount"/>
  </div>
</template>
<script>
  import AppHeader from './components/AppHeader';
  // ...
  
  export default {
    name: 'app',
    components:{
      AppHeader,
      MemoApp
    },
    data(){
      return {
        memoCount: 0,
      };
    },
    methods: {
      updateCount(count){
        this.memoCount = count;
      },
    }
  }
</script>
<style>
@import "./styles/reset.css";

#app{
  width: 560px;
  margin: 0 auto;
}
</style>
```







  

# 2. 라이프사이클 



# 3. 주요 라이프 사이클 훅







