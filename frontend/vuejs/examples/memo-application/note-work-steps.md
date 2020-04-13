# 작업과정 정리...
# 1. reset.css 추가
reset.css는 초기화 CSS 코드이다. 초기화 CSS는 브라우저가 DOM에 기본 적용하는 스타일에 우리의 CSS를 덮어씌워주는 역할을 한다.  
초기화 CSS 코드를 작성하는 이유는 브라우저마다 DOM에 기본으로 적용하는 스타일이 조금씩 다르기 때문이다. 이런 이유로 같은 DOM과 스타일을 작성했더라도 최종적인 렌더링 결과 또한 조금씩 다를 수 있다. 사용자마다 어떤 브라우저에서 페이지를 보게 되더라도 일관도니 UI/UX를 제공해주기 위해서 reset.css를 작성한다.  
폰트 어썸은 벡터형 아이콘과 로고들을 폰트처럼 사용할 수 있도록 해준다.([참고](https://fontawesome.com))  

- src 밑에 styles 디렉터리 생성
    - src/styles/reset.css 생성
    - src/styles/reset.css 코딩
- App.vue 내에 <style> 영역의 코드들을 삭제 후 src/styles/reset.css import

# 2. App Header 추가 (헤더 컴포넌트 생성)
- src 밑에 components 디렉터리 생성
    - src/components/AppHeader.vue 생성
    - src/components/AppHeader.vue 코딩
- App.vue 내에 <script>...</script> 내용들을 삭제후 아래 내용을 코딩
    ```javascript
    <script>
    // AppHeader.vue 임포트
    import AppHeader from './components/AppHeader';

    export default {
        name: 'app',
        components:{
            AppHeader
        }
    }
    </script>
    ```
- App.vue 내에 <template>...</template> 내의 내용들을 삭제후 아래 내용을 코딩
    ```javascript
    <template>
        <div id="app">
            <app-header/>
        </div>
    </template>
    ```

