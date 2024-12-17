<template>
  <div class="content-wrapper">
    <img alt="Vue logo" src="./assets/logo.jpg" class="logo-image">
    <h1 class="site-title">商品比价网站</h1>
    <h2 class="site-subtitle">Price Comparison Website</h2>
    <div class="login-container">
      <div class="input-group">
        <input 
          type="text" 
          v-model="username" 
          placeholder="Enter username"
        >
      </div>
      <div class="input-group">
        <input 
          type="password" 
          v-model="password" 
          placeholder="Enter password"
        >
        <div class="forget-password-wrapper">
          <a href="#" class="forget-password">Forget Password?</a>
        </div>
      </div>
      <button class="login-button" @click="handleLogin">LOGIN</button>
      <button class="register-button">Register NOW!</button>
    </div>
    <UserTable v-if="showTable" :userData="userData" />
    <div class="footer">
      Design by Wyr, ZheJiang University
    </div>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import UserTable from './components/UserTable.vue'
import axios from 'axios'

export default {
  name: 'App',
  components: {
    HelloWorld,
    UserTable
  },
  data() {
    return {
      username: '',
      password: '',
      userData: [],
      showTable: false
    }
  },
  created() {
    axios.get('http://localhost:80/user').then((response) => {
      this.userData = response.data;
    })
  },
  methods: {
    handleLogin() {
      // 查找匹配的用户
      const user = this.userData.find(user => 
        user.username === this.username && user.password === this.password
      );

      if (user) {
        // 登录成功
        alert('登录成功！');
        this.showTable = false;
      } else {
        // 登录失败，显示用户表格
        alert('登录失败，显示所有用户信息');
        this.showTable = true;
      }
    }
  }
}
</script>

<style>
/* 添加全局样式 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0;  /* 移除顶部边距 */
  min-height: 100vh;
  background: linear-gradient(
    to right,
    #b1d5fa 0%,
    #b1d5fa 5%,
    rgba(126, 179, 209, 0.3) 20%,
    white 40%,
    white 60%,
    rgba(126, 179, 209, 0.3) 80%,
    #b1d5fa 95%,
    #b1d5fa 100%
  );
  padding-top: 60px;  /* 替代之前的margin-top */
}

.content-wrapper {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: transparent;  /* 改为透明背景 */
  min-height: calc(100vh - 60px);  /* 减去padding-top的高度 */
}

.login-container {
  max-width: 300px;
  margin: 20px auto;
}

.input-group {
  margin: 10px 0;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.logo-image {
  width: 250px;
  margin: 20px auto;
  display: block;
}

.site-title {
  color: #3f5bad;
  font-size: 40px;
  margin: 15px 0 3px;
  -webkit-text-stroke: 1px #b1c9f5;
  text-shadow: 
    -1px -1px 0 #a8c6ff,
    1px -1px 0 #a8c6ff,
    -1px 1px 0 #a8c6ff,
    1px 1px 0 #a8c6ff;
}

.site-subtitle {
  color: #7eb3d1;
  font-size: 18px;
  margin: 3px 0 40px;
  font-weight: normal;
  font-family: 'Palatino Linotype', 'Book Antiqua', Palatino, serif;
  letter-spacing: 1.2px;
  font-style: italic;
}

input::placeholder {
  color: #7eb3d1;
}

.forget-password-wrapper {
  text-align: right;
  margin-top: 5px;
}

.forget-password {
  color: #7ea8d1;
  text-decoration: underline;
  font-size: 14px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.forget-password:hover {
  color: #3f5bad;
}

.login-button {
  width: 100%;
  padding: 12px;
  margin-top: 20px;
  background-color: #a9d4ff;
  border: 2px solid #3f5bad;
  border-radius: 4px;
  color: white;
  font-weight: bold;
  font-size: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-button:hover {
  background-color: #6b95be;
  transform: translateY(-1px);
}

.login-button:active {
  transform: translateY(1px);
}

.register-button {
  width: 100%;
  padding: 12px;
  margin-top: 15px;
  background-color: transparent;
  border: none;
  border-radius: 4px;
  color: #576db0;
  font-weight: bold;
  font-size: 25px;
  font-family: 'Baskerville', 'Libre Baskerville', 'Times New Roman', serif;
  font-style: italic;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: underline;
}

.register-button:hover {
  background-color: rgba(63, 91, 173, 0.1);
  transform: translateY(-1px);
}

.register-button:active {
  transform: translateY(1px);
}

.footer {
  color: #9ab3cb;
  font-size: 14px;
  font-family: 'Palatino Linotype', 'Book Antiqua', Palatino, serif;
  font-style: italic;
  position: fixed;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
}
</style>