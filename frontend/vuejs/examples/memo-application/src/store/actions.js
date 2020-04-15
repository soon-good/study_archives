export function fetchMemos ({commit}){
    // 아래 부분은 추후 axios 사용 로직으로 정리할 예정. 일단은 localStorage로 고고싱...
    const memos = localStorage.memos ? JSON.parse(localStorage.memos) : [];
    commit('FETCH_MEMOS', memos);
}

export function addMemo({commit}, payload){
    // localStorage 에 memo 저장
    const memos = JSON.parse(localStorage.memos);
    memos.push(payload);
    storeMemo(memos);

    // Mutation 'ADD_MEMO' 발생시키기
    commit('ADD_MEMO', payload);
}

export function deleteMemo({commit}, id){
    // localStorage 에서 memo 삭제하는 로직 
    const memos = JSON.parse(localStorage.memos);
    const idxOfDel = memos.findIndex(_memo=>_memo.id === id);
    memos.splice(idxOfDel, 1);
    storeMemo(memos);

    // Mutation 'DELETE_MEMO' 발생시키기
    commit('DELETE_MEMO', id);
}

export function updateMemo({commit}, payload){
    // localStorage 에서 memo 수정하는 로직
    const memos = JSON.parse(localStorage.memos);
    const {id, content} = payload;
    const idxOfUpdate = memos.findIndex(_memo => _memo.id === id);
    const objOfUpdate = memos[idxOfUpdate];
    memos.splice(idxOfUpdate, 1, {...objOfUpdate, content});
    storeMemo(memos);

    // Mutation 'UPDATE_MEMO' 발생시키기
    commit('UPDATE_MEMO', payload);
}

export function storeMemo(memos){
    const memosToString = JSON.stringify(memos);
    localStorage.setItem('memos', memosToString);
}

export default {
    fetchMemos,     // fetchMemos       액션 함수 등록
    addMemo,        // addMemo          액션 함수 등록
    deleteMemo,     // deleteMemo       액션 함수 등록
    updateMemo,     // updateMemo       액션 함수 등록
}