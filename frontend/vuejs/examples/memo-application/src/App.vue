<template>
  <div id="app">
    <app-header :memoCount="memoCount"/>
    <memo-app @change="updateCount"/>
  </div>
</template>

<script>
import AppHeader from './components/AppHeader';
import MemoApp from './components/MemoApp';

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
