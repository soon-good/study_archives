export function fetchMemos ({commit}){
    // 아래 부분은 추후 axios 사용 로직으로 정리할 예정. 일단은 localStorage로 고고싱...
    const memos = localStorage.memos ? JSON.parse(localStorage.memos) : [];
    commit('FETCH_MEMOS', memos);
}

export default{
    fetchMemos
}