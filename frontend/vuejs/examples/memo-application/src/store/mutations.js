const FETCH_MEMOS   = 'FETCH_MEMOS';
const ADD_MEMO      = 'ADD_MEMO';
const DELETE_MEMO   = 'DELETE_MEMO';

export default {
    [FETCH_MEMOS] (state, payload){
        state.memos = payload;
    },
    [ADD_MEMO] (state, payload){
        state.memos.push(payload);
    },
    [DELETE_MEMO] (state, id){
        const idxOfDel = state.memos.findIndex(_memo => _memo.id === id);
        state.memos.splice(idxOfDel, 1);
    }
}