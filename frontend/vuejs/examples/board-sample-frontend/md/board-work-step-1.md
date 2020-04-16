# 작업내역 정리
# 세팅
- 주의할 점으로는 여기서는 router를 사용하도록 프로젝트를 구성했다는 점이다.. 유의하자.
![이미자](./img/INITIAL_SETTINGS.md)  

# Vue Router
src/router/index.js로 이동하여 모든 내용을 지우고 아래의 내용으로 덮어쓰자  
- mode: 'history'
를 옵션으로 추가해주어야 한다. 

> **참고) Vue Router의 히스토리(history) 모드**  
> 히스토리(history) 모드는 HTML5의 스펙인 history API를 사용하는 방법이다. history API pushState 라는 메서드를 제공하는데, 이 메서드는  
> - URL을 변경하고
> - 브라우저의 히스토리도 남겨지지만
> 실제 페이지는 이동하지 않는 기능을 제공해준다. URL 변경시  
> - 해시모드처럼 URL에 해시(#)를 사용하지 않아도 되고
> - 브라우저 히스토리에 URL 변경 내역이 저장되므로
> 실제 페이지이동은 아니지만 페이지 이동과 유사한 사용자 경험을 제공한다는 장점이 있다.  
> 히스토리 모드의 URL 구조
> - http://localhost:8080/example/page
> Vue Router 는 해시모드든 히스토리 모드든 모두 history API를 사용하여 라우팅을 진행하므로 해시모드라고 해서 브라우저의 히스토리가 쌓이지 않는 것은 아니다.  
```
해시 모드는 해당 브라우저가 History API를 지원하지 않을 경우 window.location.hash를 사용해 URL을 변경해준다. 따라서 실제로 페이지가 이동되지 않고 URL만 갱신된다.  
  
참고로, 브라우저가 History API를 지원하지 않을 경우, 
window.location.assign 메서드를 사용해 URL을 변경하기 때문에 실제로 페이지가 이동해버린다는 단점이 있다.
```

```javascript
import Vue from 'vue'
import Router from 'vue-router'
// Vue CLI로 프로젝트 생성시 자동으로 생성된 컴포넌트 지워도 된다.
// import HelloWorld from '@/components/HelloWorld'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'PostListPage',
      component: null
    },
    // Vue CLI 가 생성해준 예제 라우트 삭제
    // {
    //   path: '/',
    //   name: 'HelloWorld',
    //   component: HelloWorld
    // }
  ]
})
```
아직은 component에 컴포넌트를 등록하지 않았기 때문에 http://localhost:8080 으로 접속해도 빈 페이지만 나타난다.  

# 라우터 코딩 (1)::포스트 리스팅 페이지 컴포넌트 연결
## 디렉터리 구조 잡기 (게시판 페이징 기능 디렉터리 생성)
src 디렉터리 밑에 pages 디렉터리를 생성하자.
![이미자](./img/ROUTER_POST_LIST_DIRECTORY.png)

## 게시판 컴포넌트 생성
생성된 src/pages 디렉터리 밑에 PostListPage.vue 라는 이름의 파일을 생성하자.
![이미자](./img/ROUTER_POST_LIST_COMPONENT.png)

## 게시판 컴포넌트 작성
```html
<template>
    <div class="post-list-page">
        <h1>포스트 게시글</h1>
        <div>
            <table>
                <colgroup>
                    <col style="width: 10%;"/>
                    <col style="width: 60%;"/>
                    <col style="width: 10%;"/>
                    <col style="width: 20%;"/>
                </colgroup>
                <thead>
                    <tr>
                        <th scope="col">번호</th>
                        <th scope="col">제목</th>
                        <th scope="col">작성자</th>
                        <th scope="col">작성일</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td scope="col">1</td>
                        <td scope="col">게시글의 제목이 노출됩니다.[2]</td>
                        <td scope="col">홍길동</td>
                        <td scope="col">2020-04-16 10:58</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
export default {
    name: 'PostListPage',
}
</script>
```

## 게시판 컴포넌트를 라우터에 연결
