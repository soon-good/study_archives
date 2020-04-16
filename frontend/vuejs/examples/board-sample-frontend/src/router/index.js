import Vue from 'vue'
import Router from 'vue-router'
// Vue CLI로 프로젝트 생성시 자동으로 생성된 컴포넌트 지워도 된다.
// import HelloWorld from '@/components/HelloWorld'

// PostListPage 컴포넌트 import
import PostListPage from '@/pages/PostListPage';

// Signup 컴포넌트 import
import Signup from '@/pages/Signup';

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'PostListPage',
      component: PostListPage,
    },
    {
      path: '/signup',
      name: 'Signup',
      component: Signup,
    },
    // Vue CLI 가 생성해준 예제 라우트 삭제
    // {
    //   path: '/',
    //   name: 'HelloWorld',
    //   component: HelloWorld
    // }
  ]
})
