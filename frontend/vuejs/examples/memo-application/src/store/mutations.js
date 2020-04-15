const FETCH_MEMOS = 'FETCH_MEMOS';

export default {
    [FETCH_MEMOS] (state, payload){
        state.memos = payload;
    },
}