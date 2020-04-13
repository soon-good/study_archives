# 작업과정 정리...(메모 애플리케이션)

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

# 3. 메모 생성 기본 컴포넌트 추가
여기서 만들 컴포넌트는 MemoApp.vue, MemoForm.vue 이다.
추후 vuex를 적용시 Memo.vue를 만들 예정이다. 여기서는 MemoApp.vue, MemoForm.vue를 추가한다.  

## MemoApp.vue
현재 작성되고 있는 우리의 애플리케이션에서는 별도의 API 서버 요청 없이 샘플로 로컬스토리지의 데이터를 받아오는 방식으로 작성중이다. 따라서 created 훅에서 실행시켜준다. created 훅은 Vue의 생명주기 중 하나이다. 
```javascript
<template>
    <div class="memo-app">
        <memo-form/>
        <!-- <memo/> -->
    </div>
</template>
<script>
import MemoForm from './MemoForm';

export default {
    name: 'MemoApp',
    components:{
        MemoForm
    },
    data(){
        return {
            memos: [],
        };
    },
    created(){
        // 1) 
        // 만약 localStorage 내에 memos 데이터가 있다면 
        //      created 훅에서 localStorage 의 데이터를 컴포넌트 내의 memos 내에 넣어주고
        // 아니면
        //      컴포넌트 내의 memos 를 비어있는 배열로 초기화 한다.  
        this.memos = localStorage.memos ? JSON.parse(localStorage.memos) : [];
    }
}
</script>
```
  
## MemoForm.vue
```javascript
<template>
    <div class="memo-form">
        <form>
            <fieldset>
                <div>
                    <input  class="momo-form__title-form"
                            type="text"
                            v-model="title"
                            placeholder="메모 제목을 입력해주세요."/>
                    <textarea   class="memo-form__content-form"
                                v-model="content"
                                placeholder="메모의 내용을 입력해주세요."/>
                    <button type="reset">
                        <i class="fas fa-sync-alt">
                        </i>
                    </button>
                </div>
                <button type="submit">등록하기</button>
            </fieldset>
        </form>
    </div>
</template>

<script>
export default {
    // 컴포넌트의 이름을 MemoForm으로 변경한다. 
    name: "MemoForm",
    data(){
        return {
            // 사용자가 입력한 데이터(content, title)에 대한 key, value
            // 여기서 등록하는 데이터는 v-model 디렉티브를 이용해 입력폼의 입력필드에 연결해줘야 한다.
            title: '',
            content: '',
        }
    } 
}
</script>
<style scoped>
  .memo-form {
    margin-bottom: 24px;
    padding-bottom: 40px;
    border-bottom: 1px solid #eee;
  }
  .memo-form form fieldset div {
    position: relative;
    padding: 24px;
    margin-bottom: 20px;
    box-shadow: 0 4px 10px -4px rgba(0, 0, 0, 0.2);
    background-color: #ffffff;
  }
  .memo-form form fieldset div button[type="reset"] {
    position: absolute;
    right: 20px;
    bottom: 20px;
    font-size: 16px;
    background: none;
  }
  .memo-form form fieldset button[type="submit"] {
    float: right;
    width: 96px;
    padding: 12px 0;
    border-radius: 4px;
    background-color: #ff5a00;
    color: #fff;
    font-size: 16px;
  }
  .memo-form form fieldset .memo-form__title-form {
    width: 100%;
    margin-bottom: 12px;
    font-size: 18px;
    line-height: 26px;
  }
  .memo-form form fieldset .memo-form__content-form {
    width: 100%;
    height: 66px;
    font-size: 14px;
    line-height: 22px;
    vertical-align: top;
  }
  .memo-form input:focus {
    outline: none;
  }
</style>
```

# 4. MemoForm - submit 이벤트 추가, submit시 페이지 이동 방지 
- methods 속성내에 메서드로 addMemo() 함수를 추가해준다. 
- addMemo 함수에서는 this.$emit() 함수로 'addMemo'이벤트를 발생시키고,  
  사용자 입력데이터(title, content)를 MemoApp 컴포넌트에 emit을 이용해 전파한다.  
- Vue는 submit 이벤트가 발생할 때 개발자가 직접 event.preventDefault를 호출하지 않아도 되도록 prevent 옵션을 제공해준다. (5.1.4.5.2. v-on 장 참고)