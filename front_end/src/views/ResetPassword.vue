<template>
  <div class="reset">
    <div class="content-wrapper">
      <SiteHeader :showSubtitle="true" />
      <div class="reset-container">
        <div class="input-group">
          <input 
            type="text" 
            v-model="username" 
            placeholder="Enter username"
          >
        </div>
        <div class="input-group">
          <input 
            type="email" 
            v-model="email" 
            placeholder="Enter email"
          >
        </div>
        <div class="input-group">
          <input 
            :type="!showNewPassword ? 'password' : 'text'" 
            v-model="newPassword" 
            placeholder="Enter new password"
          >
          <i 
            class="fas" 
            :class="showNewPassword ? 'fa-eye' : 'fa-eye-slash'"
            @click="showNewPassword = !showNewPassword"
            style="cursor: pointer; position: absolute; right: 10px; top: 50%; transform: translateY(-50%);"
          ></i>
        </div>
        <div class="input-group">
          <input 
            :type="!showConfirmPassword ? 'password' : 'text'" 
            v-model="confirmPassword" 
            placeholder="Confirm new password"
          >
          <i 
            class="fas" 
            :class="showConfirmPassword ? 'fa-eye' : 'fa-eye-slash'"
            @click="showConfirmPassword = !showConfirmPassword"
            style="cursor: pointer; position: absolute; right: 10px; top: 50%; transform: translateY(-50%);"
          ></i>
        </div>
        <button class="reset-button" @click="handleReset">RESET PASSWORD</button>
      </div>
      <BackButton :returnPath="returnPath" />
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
  name: 'ResetPassword',
  components: {
    SiteHeader,
    BackButton
  },
  data() {
    return {
      username: '',
      email: '',
      newPassword: '',
      confirmPassword: '',
      showNewPassword: false,
      showConfirmPassword: false,
      returnPath: this.$route.query.from || '/'
    }
  },
  created() {
    // 如果是从个人中心页面跳转来的，自动填充用户信息
    if (this.$route.query.from === '/profile') {
      this.username = this.$route.query.username || '';
      this.email = this.$route.query.email || '';
    }
  },
  methods: {
    handleReset() {
      if (!this.username || !this.email || !this.newPassword || !this.confirmPassword) {
        this.$message.warning('Please fill in all fields');
        return;
      }

      if (this.newPassword !== this.confirmPassword) {
        this.$message.error('Passwords do not match');
        return;
      }

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.email)) {
        this.$message.error('Please enter a valid email address');
        return;
      }

      // 这里添加重置密码的API调用
      this.$message.success('Password reset successful!');
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.reset {
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

.reset-container {
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

.reset-button {
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
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

.reset-button:hover {
  background-color: #6b95be;
  transform: translateY(-1px);
}

.reset-button:active {
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