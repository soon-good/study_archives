<template>
    <form @submit.prevent="submit" novalidate>
        <fieldset>
            <input type="text" v-model="name" placeholder="이름"/>
            <input type="email" v-model="email" placeholder="이메일"/>
            <input type="password" v-model="password" placeholder="비밀번호"/>
            <input type="password" v-model="passwordConfirm" placeholder="비밀번호 확인"/>
            <button type="submit"> 회원가입 </button>
        </fieldset>
    </form>
</template>

<script>
export default {
    name: 'SignupForm',
    data(){
        return {
            name: '',
            email: '',
            password: '',
            passwordConfirm: ''
        }
    },
    methods:{
        submit(){
            const {name, email, password, passwordConfirm} = this;
            if(!name || !email || !password || !passwordConfirm){
                alert('항목이 누락되었습니다.');
            }
            if(password !== passwordConfirm){
                alert('비밀번호를 확인해주세요.');
                return;
            }
            this.$emit('submit', {name, email, password});
        }
    }
}
</script>