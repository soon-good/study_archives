<template>
    <form @submit.prevent="submit">
        <fieldset>
            <input type="text" v-model="email" placeholder="이메일">
            <input type="password" v-model="password" placeholder="비밀번호">
            <button type="submit"> 로그인 </button>
        </fieldset>
    </form>
</template>
<script>
export default {
    name: 'SigninForm',
    data(){
        return {
            email: '',
            password: '',
        }
    },
    methods: {
        submit(){
            const {email, password} = this
            this.$emit('submit', {email, password})
        }
    }
}
</script>