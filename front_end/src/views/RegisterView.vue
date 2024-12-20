<template>
  <div class="register">
    <div class="content-wrapper">
      <SiteHeader :showSubtitle="true" />
      <div class="register-container">
        <div class="input-group">
          <input 
            type="email" 
            v-model="email" 
            placeholder="Enter email:"
          >
        </div>
        <div class="input-group">
          <input 
            type="text" 
            v-model="username" 
            placeholder="Enter username:"
          >
        </div>
        <div class="input-group">
          <input 
            :type="!showPassword ? 'password' : 'text'" 
            v-model="password" 
            placeholder="Enter password:"
          >
          <i 
            class="fas" 
            :class="showPassword ? 'fa-eye' : 'fa-eye-slash'"
            @click="showPassword = !showPassword"
            style="cursor: pointer; position: absolute; right: 10px; top: 50%; transform: translateY(-50%);"
          ></i>
        </div>
        <div class="input-group">
          <input 
            :type="!showConfirmPassword ? 'password' : 'text'" 
            v-model="confirmPassword" 
            placeholder="Confirm password:"
          >
          <i 
            class="fas" 
            :class="showConfirmPassword ? 'fa-eye' : 'fa-eye-slash'"
            @click="showConfirmPassword = !showConfirmPassword"
            style="cursor: pointer; position: absolute; right: 10px; top: 50%; transform: translateY(-50%);"
          ></i>
        </div>
        <button class="register-button" @click="handleRegister">REGISTER</button>
      </div>
      <BackButton />
    </div>
    <div class="footer">
      Design by Wyr, ZheJiang University
    </div>
  </div>
</template>

<script>
import SiteHeader from '../components/SiteHeader.vue'
import BackButton from '../components/BackButton.vue'

export default {
  name: 'RegisterView',
  components: {
    SiteHeader,
    BackButton
  },
  data() {
    return {
      email: '',
      username: '',
      password: '',
      confirmPassword: '',
      showPassword: false,
      showConfirmPassword: false
    }
  },
  methods: {
    handleRegister() {
      // 这里添加注册逻辑
      if (!this.email || !this.username || !this.password || !this.confirmPassword) {
        this.$message.warning('Please fill in all fields');
        return;
      }
      
      if (this.password !== this.confirmPassword) {
        this.$message.error('Passwords do not match');
        return;
      }

      // 简单的邮箱格式验证
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.email)) {
        this.$message.error('Please enter a valid email address');
        return;
      }

      // 这里可以添加API调用来注册用户
      this.$message.success('Registration successful!');
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
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
}

.content-wrapper {
  position: relative;
  text-align: center;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.register-container {
  width: 300px;
  margin: 20px auto;
}

.input-group {
  margin: 10px 0;
  position: relative;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 16px;
}

input:focus {
  outline: none;
  border-color: #7ea8d1;
}

.register-button {
  width: 100%;
  padding: 0px;
  margin-top: 20px;
  background-color: #a9d4ff;
  border: 2px solid #3f5bad;
  border-radius: 4px;
  height: 50px;
  color: white;
  font-weight: bold;
  font-size: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  font-style: normal;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

.register-button:hover {
  background-color: #6b95be;
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