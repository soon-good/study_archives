<template>
    <li class="memo-item">
        <strong>{{memo.title}}</strong>
        <p @dblclick="handleDblClick">
          <template v-if="!isEditing">{{memo.content}}</template>
          <input v-else type="text" ref="content" :value="memo.content"
                 @keydown.enter="updateMemo"
                 @blur="handleBlur"/>
        </p>
        <button type="button" @click="deleteMemo">
          <i class="fas fa-times"></i>
        </button>
    </li>
</template>

<script>
export default {
    name: "Memo",
    data() {
      return {
        isEditing: false,
      }
    },
    props: {
        memo: {
            type: Object
        },
    },
    // beforeUpdate() {
    //   console.log("beforeUpdate :: ", this.$refs.content);
    // },
    // updated(){
    //   console.log("updated :: ", this.$refs.content);
    // },
    methods: {
        updateMemo(e){
          const id = this.memo.id;
          const content = e.target.value.trim();
          
          if(content.length <=0){
            return false;
          }

          // "updateMemo" 이벤트를 데이터 {id,content}와 함께 부모 컴포넌트로 전파 
          this.$emit('updateMemo', {id, content});
          // update가 완료된 후에는 수정가능 여부 플래그를 false 로 세팅
          this.isEditing = false;
        },
        deleteMemo() {
          const id = this.memo.id;
          this.$emit('deleteMemo', id);
        },
        handleDblClick(){
          this.isEditing = true;
          // console.log('handleDblClick :: ', this.$refs.content);
          // this.$refs.content.focus();
          this.$nextTick(()=>{
            this.$refs.content.focus();
          });
        },
        handleBlur(){
          this.isEditing = false;
        }
    }
}
</script>

<style scoped>
  .memo-item {
    overflow: hidden;
    position: relative;
    margin-bottom: 20px;
    padding: 24px;
    box-shadow: 0 4px 10px -4px rgba(0, 0, 0, 0.2);
    background-color: #fff;
    list-style: none;
  }
  .memo-item input[type="text"] {
    border: 1px solid #ececec;
    font-size: inherit;
  }
  .memo-item button {
    position: absolute;
    right: 20px;
    top: 20px;
    font-size: 20px;
    color: #e5e5e5;
    border: 0;
  }
  .memo-item strong {
    display: block;
    margin-bottom: 12px;
    font-size: 18px;
    font-weight: normal;
    word-break: break-all;
  }
  .memo-item p {
    margin: 0;
    font-size: 14px;
    line-height: 22px;
    color: #666;
  }
  .memo-item p input[type="text"] {
    box-sizing: border-box;
    width: 100%;
    font-size: inherit;
  }
</style>