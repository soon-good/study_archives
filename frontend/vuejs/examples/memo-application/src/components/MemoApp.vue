<template>
    <div class="memo-app">
        <!-- <memo-form v-on::addMemo="addMemo"/> 과 같은 의미 -->
        <memo-form @addMemo="addMemo"/>
        <ul class="memo-list">
            <memo v-for="memo in memos" :key="memo.id" :memo="memo"/>
        </ul>
    </div>
</template>
<script>
import MemoForm from './MemoForm';
import Memo from './Memo';

export default {
    name: 'MemoApp',
    components:{
        MemoForm,
        Memo,
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
    },
    methods: {
        // 템플릿의 <memo-form>에 addMemo 이벤트 콜백함수로 연결해줘야 한다. 
        // addMemo 이벤트는 자식 컴포넌트인 MemoForm 으로부터 전달받는다. (이벤트를 전달받으면서 payload도 함께 전달받는다)
        // 위의 template 코드 참고
        addMemo (payload){
            // MemoForm 에서 전달해주는 데이터를 먼저 컴포넌트 내부 데이터에 추가한다. 
            // (자식 컴포넌트인 MemoForm 에서 부모인 MemoApp 으로 데이터를 올려주는 것)
            this.memos.push(payload);
            
            // storeMemo() 호출
            this.storeMemo();
        },
        // 내부 데이터를 문자열로 변환하여, 로컬 스토리지에 저장한다.
        storeMemo (){
            const memosToString = JSON.stringify(this.memos);
            localStorage.setItem('memos', memosToString);
        }
    }
}
</script>
<style scoped>
  .memo-list {
    padding: 20px 0;
    margin: 0;
  }
</style>