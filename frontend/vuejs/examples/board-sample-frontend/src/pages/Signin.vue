<template>
    <div class="sign-in-page">
        <h3>로그인</h3>
        <signin-form @submit="onSubmit"></signin-form>
        <p>회원이 아니신가요? 
            <router-link :to="{name: 'Signup'}">
                회원가입하러 가기
            </router-link>
        </p>
    </div>
</template>

<script>
// SigninForm 컴포넌트 import 
import SigninForm from '@/components/SigninForm';

// 커스텀 axios 모듈 import 
import api from '@/api/'

export default {
    name: 'Signin',
    components:{
        SigninForm
    },
    methods: {
        onSubmit(payload){
            const { email, password } = payload;
            api.post('/auth/signin', { email, password })
            .then(res => {
                console.log('success');
                const { accessToken } = res.data;
                console.log('accessToken :: ', accessToken)
            })
        },
    },
}
</script>