<template>
    <div class="sign-up-page">
        <h3>회원가입</h3>
        <signup-form @submit="onSubmit"/>
    </div>
</template>
<script>
import SignupForm from '@/components/SignupForm'
import api from '@/api';

export default {
    name: 'Signup',
    components:{
        SignupForm
    },
    methods: {
        onSubmit(payload){
            console.log(payload);
            const{email,password,name} = payload;

            api.post('/auth/signup', { name, email, password })
            .then(res => {
                alert('회원가입이 완료되었습니다.')
                // this.$router.push({ name: 'Signin' })
            })
            .catch(err => {
                alert(err.response.data.msg)
            })
        }
    }
}
</script>