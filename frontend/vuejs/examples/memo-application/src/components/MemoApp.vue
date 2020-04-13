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