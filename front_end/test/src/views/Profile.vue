<template>
  <div class="profile">
    <div class="header">
      <div class="left-section">
        <img alt="Vue logo" src="../assets/logo.jpg" class="logo-image">
        <h1 class="site-title">商品比价网站</h1>
      </div>
      <div class="right-section">
        <div class="title-section">
          <h2 class="welcome-text">个人中心</h2>
          <div class="button-group">
            <button class="action-button" @click="handleLogout">登出</button>
            <button class="action-button" @click="handleDelete">注销</button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="profile-content">
      <div class="info-container">
        <div class="info-row">
          <span class="info-label">用户名：{{ username }}</span>
          <button class="update-button">update</button>
        </div>
        <div class="info-row">
          <span class="info-label">邮箱：{{ email }}</span>
          <button class="update-button">update</button>
        </div>
        <div class="divider"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data() {
    const email = localStorage.getItem('email');
    console.log('Email from localStorage:', email);
    return {
      username: localStorage.getItem('username') || '用户',
      email: email || ''
    }
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('isAuthenticated');
      localStorage.removeItem('username');
      localStorage.removeItem('email');  // 登出时也清除邮箱
      this.$router.push('/');
    },
    handleDelete() {
      // 这里添加注销账号的逻辑
      alert('确定要注销账号吗？');
    }
  }
}
</script>

<style scoped>
.profile {
  padding: 20px;
  margin: 0;
  text-align: center;
  min-height: 100vh;
  background-color: #f0f8ff;
  width: 100vw;
  position: absolute;
  left: 0;
  box-sizing: border-box;
}

.header {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  margin-bottom: 30px;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
  gap: 10px;
}

.left-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 180px;
  margin-left: 100px;
  margin-top: 15px;
}

.logo-image {
  width: 100px;
  margin-bottom: 5px;
}

.site-title {
  color: #3f5bad;
  font-size: 20px;
  margin: 5px 0;
  -webkit-text-stroke: 1px #b1c9f5;
  text-shadow: 
    -1px -1px 0 #a8c6ff,
    1px -1px 0 #a8c6ff,
    -1px 1px 0 #a8c6ff,
    1px 1px 0 #a8c6ff;
}

.right-section {
  flex-grow: 1;
  padding-top: 0;
  text-align: left;
  max-width: 600px;
  margin-left: 0;
  margin-top: 15px;
}

.title-section {
  background-color: #d4e5f7;
  padding: 30px 30px;
  border-radius: 8px;
  margin-bottom: 20px;
  margin-right: 130px;
  margin-left: -20px;
}

.welcome-text {
  color: #3f5bad;
  font-size: 35px;
  margin: 0 0 25px 0;
  text-align: left;
  line-height: 1;
  margin-left: 30px;
}

.button-group {
  display: flex;
  gap: 15px;
  margin-bottom: 0;
  margin-left: 30px;
}

.action-button {
  padding: 8px 20px;
  background-color: white;
  border: 2px solid #3f5bad;
  border-radius: 4px;
  color: #3f5bad;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-button:hover {
  background-color: #f5f7ff;
}

.profile-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: left;
}

.info-container {
  margin-left: 100px;
  margin-right: 130px;  /* 与title-section对齐 */
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px 40px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding: 5px 0;
}

.info-label {
  color: #3f5bad;
  font-size: 16px;
}

.update-button {
  padding: 5px 15px;
  background-color: white;
  border: 2px solid #3f5bad;
  border-radius: 4px;
  color: #3f5bad;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.update-button:hover {
  background-color: #f5f7ff;
}

.divider {
  height: 2px;
  background-color: #e6f3ff;
  margin: 20px 0;
}
</style>