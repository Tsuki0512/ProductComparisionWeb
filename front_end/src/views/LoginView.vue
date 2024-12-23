<template>
  <div class="login">
    <div class="content-wrapper">
      <img alt="Vue logo" src="../assets/logo.jpg" class="logo-image">
      <h1 class="site-title">商品比价网站</h1>
      
      <!-- 登录表单 -->
      <div v-if="!showRegister" class="login-container">
        <input type="text" v-model="username" placeholder="用户名" class="login-input">
        <input type="password" v-model="password" placeholder="密码" class="login-input">
        <button @click="handleLogin" class="login-button">登录</button>
        <div class="register-link">
          <a @click="showRegister = true">没有账号？立即注册</a>
        </div>
      </div>

      <!-- 注册表单 -->
      <div v-else class="login-container">
        <input type="email" v-model="registerForm.email" placeholder="邮箱" class="login-input">
        <input type="text" v-model="registerForm.username" placeholder="用户名" class="login-input">
        <input type="password" v-model="registerForm.password" placeholder="密码" class="login-input">
        <input type="password" v-model="registerForm.confirmPassword" placeholder="确认密码" class="login-input">
        <button @click="handleRegister" class="login-button">注册</button>
        <div class="login-link">
          <a @click="showRegister = false">已有账号？返回登录</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginView',
  data() {
    return {
      username: '',
      password: '',
      showRegister: false,
      registerForm: {
        email: '',
        username: '',
        password: '',
        confirmPassword: ''
      }
    }
  },
  methods: {
    async handleLogin() {
      try {
        const response = await this.$axios.post('/user/login', {
          username: this.username,
          password: this.password
        });
        
        console.log('Login response:', response.data);
        
        if (response.data.code === '200') {
          const userData = response.data.data;
          localStorage.setItem('isAuthenticated', 'true');
          localStorage.setItem('username', userData.username);
          localStorage.setItem('email', userData.email);
          
          localStorage.setItem('jdCookie', userData.jd_cookie || '');
          localStorage.setItem('tbCookie', userData.tb_cookie || '');
          
          console.log('Stored data:', {
            username: localStorage.getItem('username'),
            email: localStorage.getItem('email'),
            jdCookie: localStorage.getItem('jdCookie'),
            tbCookie: localStorage.getItem('tbCookie')
          });
          
          this.$router.push('/');
        } else {
          this.$message.error(response.data.msg || '登录失败');
        }
      } catch (error) {
        console.error('Login error:', error);
        this.$message.error('登录失败：' + (error.response?.data?.msg || error.message));
      }
    },
    async handleRegister() {
      // 验证密码是否一致
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        alert('两次输入的密码不一致');
        return;
      }
      
      try {
        const response = await axios.post('/user/register', {
          email: this.registerForm.email,
          username: this.registerForm.username,
          password: this.registerForm.password
        });
        
        if (response.data.code === 200) {
          alert('注册成功！请登录');
          this.showRegister = false; // 返回登录界面
          // 清空注册表单
          this.registerForm = {
            email: '',
            username: '',
            password: '',
            confirmPassword: ''
          };
        } else {
          alert(response.data.msg || '注册失败');
        }
      } catch (error) {
        alert('注册失败：' + error.message);
      }
    }
  }
}
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f8ff;
}

.content-wrapper {
  text-align: center;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.logo-image {
  width: 100px;
  margin-bottom: 10px;
}

.site-title {
  color: #3f5bad;
  font-size: 24px;
  margin-bottom: 20px;
  -webkit-text-stroke: 1px #b1c9f5;
  text-shadow: 
    -1px -1px 0 #a8c6ff,
    1px -1px 0 #a8c6ff,
    -1px 1px 0 #a8c6ff,
    1px 1px 0 #a8c6ff;
}

.login-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 300px;
  margin: 0 auto;
}

.login-input {
  padding: 10px;
  border: 2px solid #7ea8d1;
  border-radius: 4px;
  font-size: 16px;
}

.login-button {
  padding: 10px;
  background-color: #7ea8d1;
  border: none;
  border-radius: 4px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover {
  background-color: #6b95be;
}

.register-link, .login-link {
  text-align: center;
  margin-top: 10px;
}

a {
  color: #4CAF50;
  cursor: pointer;
}
</style> 