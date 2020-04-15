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

export function storeMemo(memos){
    const memosToString = JSON.stringify(memos);
    localStorage.setItem('memos', memosToString);
}

export default {
    fetchMemos,
    addMemo,
}