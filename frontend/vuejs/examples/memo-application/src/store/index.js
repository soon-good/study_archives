// vue, vuex import 
import Vue from 'vue';
import Vuex from 'vuex';

/**
 * 우리가 생성한 store 디렉터리내의 
 * - state.js
 * - getters.js
 * - mutations.js
 * - actions.js
 * 들을 import 하고 있다. 
 * 
 * 각각 .js를 붙이는 것이 보기 싫다면 해당 이름으로 디렉터리를 만들고
 * 디렉터리명/index.js를 만들면 된다... 
 * 연습용 예제에 이렇게 까지 복잡하게 초반에 할 필요는 없으므로... skip
 */
import state from './states.js';
import getters from './getters.js';
import mutations from './mutations.js';
import actions from './actions.js';

Vue.use(Vuex); // Vue 가 Vuex를 사용하도록 선언한다. 

// Vuex.Store({...}) 찾아보자 ㅠㅜ
export default new Vuex.Store({
    state,
    getters,
    mutations,
    actions
});