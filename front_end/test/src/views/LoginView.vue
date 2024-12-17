<template>
  <div class="login">
    <div class="content-wrapper">
      <img alt="Vue logo" src="../assets/logo.jpg" class="logo-image">
      <h1 class="site-title">商品比价网站</h1>
      <div class="login-container">
        <input 
          type="text" 
          v-model="username" 
          placeholder="用户名"
          class="login-input"
        >
        <input 
          type="password" 
          v-model="password" 
          placeholder="密码"
          class="login-input"
        >
        <button @click="handleLogin" class="login-button">登录</button>
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
      password: ''
    }
  },
  methods: {
    handleLogin() {
      if (this.username && this.password) {
        // 暂时模拟登录成功的情况
        localStorage.setItem('isAuthenticated', 'true');
        localStorage.setItem('username', this.username);
        localStorage.setItem('email', `${this.username}@example.com`);  // 暂时使用模拟的邮箱
        this.$router.push('/home');
        
        // 实际的API调用（等后端ready后启用）
        /*
        this.$axios.post('/login', {
          username: this.username,
          password: this.password
        }).then(response => {
          localStorage.setItem('isAuthenticated', 'true');
          localStorage.setItem('username', this.username);
          localStorage.setItem('email', response.data.email);
          this.$router.push('/home');
        }).catch(error => {
          console.error('登录失败:', error);
          alert('登录失败，请检查用户名和密码');
        });
        */
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
</style> 