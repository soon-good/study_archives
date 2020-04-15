const FETCH_MEMOS   = 'FETCH_MEMOS';
const ADD_MEMO      = 'ADD_MEMO';

export default {
    [FETCH_MEMOS] (state, payload){
        state.memos = payload;
    },
    [ADD_MEMO] (state, payload){
        state.memos.push(payload);
    }
}