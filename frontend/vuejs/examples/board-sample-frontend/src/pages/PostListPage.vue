<template>
    <div class="post-list-page">
        <h1>포스트 게시글</h1>
        <div>
            <table>
                <colgroup>
                    <col style="width: 10%;"/>
                    <col style="width: 60%;"/>
                    <col style="width: 10%;"/>
                    <col style="width: 20%;"/>
                </colgroup>
                <thead>
                    <tr>
                        <th scope="col">번호</th>
                        <th scope="col">제목</th>
                        <th scope="col">작성자</th>
                        <th scope="col">작성일</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td scope="col">1</td>
                        <td scope="col">게시글의 제목이 노출됩니다.[2]</td>
                        <td scope="col">홍길동</td>
                        <td scope="col">2020-04-16 10:58</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
export default {
    name: 'PostListPage',
}
</script>