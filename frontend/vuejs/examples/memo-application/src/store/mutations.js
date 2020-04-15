const FETCH_MEMOS   = 'FETCH_MEMOS';
const ADD_MEMO      = 'ADD_MEMO';
const DELETE_MEMO   = 'DELETE_MEMO';
const UPDATE_MEMO     = 'UPDATE_MEMO';

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
    },
    [UPDATE_MEMO] (state, payload){
        const {id, content} = payload;
        const idxOfEdit = state.memos.findIndex(_memo => _memo.id === id);
        const objOfEdit = state.memos[idxOfEdit];
        state.memos.splice(idxOfEdit, 1, { ...objOfEdit, content });
    }
}