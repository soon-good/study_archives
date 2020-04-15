<template>
    <div class="memo-app">
        <!-- <memo-form v-on::addMemo="addMemo"/> 과 같은 의미 -->
        <memo-form @addMemo="addMemo"/>
        <ul class="memo-list">
            <!-- props 로 memos[i]를 각각 전달해준다. -->
            <!-- deleteMemo 이벤트 : -->
            <!--    Memo 컴포넌트에서 올라오는 이벤트 이므로 @deleteMemo 이벤트 사용 -->
            <!--    @deleteMemo 이벤트에 대한 핸들러는 deleteMemo () 함수, 스크립트에 작성했다. -->
            <memo v-for="memo in memos" :key="memo.id" :memo="memo"
                  @deleteMemo="deleteMemo"
                  @updateMemo="updateMemo"/>
        </ul>
    </div>
</template>
<script>
import MemoForm from './MemoForm';
import Memo from './Memo';
import {mapActions, mapState} from 'vuex';

export default {
    name: 'MemoApp',
    components:{
        MemoForm,
        Memo,
    },
    /** 
    data(){
        return {
            memos: [],
        };
    },
    */
    created(){
        /** 예전 코드 삭제
        // 1) 
        // 만약 localStorage 내에 memos 데이터가 있다면 
        //      created 훅에서 localStorage 의 데이터를 컴포넌트 내의 memos 내에 넣어주고
        // 아니면
        //      컴포넌트 내의 memos 를 비어있는 배열로 초기화 한다.  
        this.memos = localStorage.memos ? JSON.parse(localStorage.memos) : [];
        */
       this.fetchMemos();
    },
    computed: {
        ...mapState([
            'memos'
        ])
    },
    methods: {
        ...mapActions([
            'fetchMemos',
            'addMemo',
        ]),
        // 템플릿의 <memo-form>에 addMemo 이벤트 콜백함수로 연결해줘야 한다. 
        // addMemo 이벤트는 자식 컴포넌트인 MemoForm 으로부터 전달받는다. (이벤트를 전달받으면서 payload도 함께 전달받는다)
        // 위의 template 코드 참고
        /**
        addMemo (payload){
            // MemoForm 에서 전달해주는 데이터를 먼저 컴포넌트 내부 데이터에 추가한다. 
            // (자식 컴포넌트인 MemoForm 에서 부모인 MemoApp 으로 데이터를 올려주는 것)
            this.memos.push(payload);
            
            // storeMemo() 호출
            this.storeMemo();
            this.$emit('change', this.memos.length);
        },
         */
        // 내부 데이터를 문자열로 변환하여, 로컬 스토리지에 저장한다.
        storeMemo (){
            const memosToString = JSON.stringify(this.memos);
            localStorage.setItem('memos', memosToString);
        },
        // <memo> 컴포넌트로부터 id를 전달받아 삭제를 진행한다.
        deleteMemo (id){
            const indexOfDelete = this.memos.findIndex(_memo=>_memo.id===id);
            this.memos.splice(indexOfDelete, 1);
            this.storeMemo();
            this.$emit('change', this.memos.length);
        },
        // @updateMemo 이벤트에 대한 핸들러
        // payload 는 자식 컴포넌트인 Memo 컴포넌트로부터 전달된다. 
        updateMemo (payload){
            const {id, content} = payload;
            const indexOfUpdate = this.memos.findIndex(_memo => _memo.id === id);
            const objOfUpdate = this.memos[indexOfUpdate];

            this.memos.splice(indexOfUpdate, 1, {...objOfUpdate, content});
            this.storeMemo();
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