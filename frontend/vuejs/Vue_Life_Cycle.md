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

- vm 변수의 의미
  - 엄격하게는 MVVM패턴과 관련이 없다. 하지만 Vue 의 디자인은 부분적으로 MVVM에서 영감을 얻어 고안된 것이라고 한다.
  - 코딩 컨벤션으로 Vue 인스턴스를 참조할 때 변수명 **vm (View Model 의 약자)** 를 사용하는 편이다.

- Vue 의 옵션
  - Vue 인스턴스를 생성할 때에는 데이터, 템플릿, 마운트할 엘리먼트, 메서드, 라이프사이클 콜백 등의 옵션을 포함하는 options 객체를 전달해야 한다. 전체 옵션 목록은 [API Reference](https://kr.vuejs.org/v2/api/) 에서 찾을 수 있다.
- 컴포넌트 생성자
  - Vue 생성자는 미리 정의된 옵션으로 재사용 가능한 컴포넌트 생성자를 생성하도록 확장될 수 있다.
  - 즉, Vue 생성자에 특정 옵션들을 추가하여 컴포넌트를 생성할 수 있다.
  - Vue 인스턴스는 중첩시킬 수 있다. 그리고 재사용 가능하도록 구성할 수도 있다. 재사용 가능한 컴포넌트 트리로 구성할 수도 있다.
  - 컴포넌트에 대한 자세한 설명은 [참고자료](https://kr.vuejs.org/v2/guide/components.html) 에서 확인 가능하다.



## 예제) Vue 컴포넌트를 중첩해 구성하는 예 

Vuejs 제공 Todo 앱 의 컴포넌트 트리

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

  

## 예제) Vue 컴포넌트 생성예제

Vue 인스턴스 생성자인 new Vue({...}) 구문에 컴포넌트를 생성하는 예) 

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



# 2. 주요 라이프 사이클 훅

Vue 는 컴포넌트 기반이다. 각 컴포넌트에는 컴포넌트가 생성되는 시점, 제거되는 시점(ex. 페이지를 벗어나는 시점, 컴포넌트 제거 시점)마다 세부적인 여러가지 이벤트가 존재한다. 그리고 이 이벤트 들에 대한 핸들러를 사용자가 직접 구현하면 Vue.js가 Vue.js 엔진 내부의 리스너에서 해당 이벤트에 해당하는 시점에 이 핸들러를 실행해준다.  

각 이벤트 핸들러들은 라이프 사이클 훅이라고 부른다. 훅이라는 표현을 핸들러라는 표현보다 자주 사용하므로 기억해두어야 한다.  



## 주요 라이프사이클 훅들

자주 사용되는 라이프 사이클 훅 들이다. 

- beforeCreate
  - 인스턴스가 생성되고, 리액티브 초기화가 일어나기 전
- created
  - 인스턴스가 생성되고, 리액티브 초기화가 일어난 후
- beforeMount
  - 인스턴스가 마운트되기 전
- mounted
  - 인스턴스가 마운트된 후
- beforeUpdate
  - 데이터가 변경되어 DOM에 적용되기 전
- updated
  - 데이터가 변경되어 DOM에 적용된 후
- beforeDestroy
  - Vue 인스턴스가 제거되기 전
- destroyed
  - Vue 인스턴스가 제거된 후
- errorCaptured
  - 임의의 자식 컴포넌트에서 오류가 발생했을 때

글로 이해하는 것보다는 라이프 사이클을 그림으로 이해하는 것이 훨씬 이해가 빠르다. 아래의 라이프 사이클 다이어그램을 참고하자.  



# 3. 라이프사이클 

참고자료 : [VueJs 공식 문서](https://kr.vuejs.org/images/lifecycle.png)

![이미자](./img/VUE_LIFE_CYCLE.png)

  

## 설명과 함께 정리한 그림

![이미자](./img/VUE_LIFE_CYCLE_WITH_DESCRIPTION.png)

