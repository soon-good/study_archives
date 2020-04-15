<template>
    <div class="app-header">
        <h1>메모 애플리케이션</h1>
        <p>{{memoCount}}</p>
    </div>
</template>

<script>
export default {
    name: 'AppHeader',
    props:{
        memoCount : {
            type: Number,
            default: 0,
        }
    }
}
</script>

<style scoped>
    .app-header {
        overflow: hidden;
        padding: 52px 0 27px;
    }

    .app-header h1{
        float: left;
        font-size: 24px;
        text-align: center;
    }
</style>