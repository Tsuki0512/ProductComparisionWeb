<template>
  <router-view v-if="$route.path !== '/'"></router-view>
  <div class="content-wrapper" v-else>
    <SiteHeader :showSubtitle="true" />
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
          <a class="forget-password" @click="$router.push('/reset-password')">Forget Password?</a>
        </div>
      </div>
      <button class="login-button" @click="handleLogin">LOGIN</button>
      <button class="register-button" @click="$router.push('/register')">Register NOW!</button>
    </div>
    <UserTable v-if="showTable" :userData="userData" />
  </div>
  <div class="footer">
    Design by Wyr, ZheJiang University
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import UserTable from './components/UserTable.vue'
import SiteHeader from './components/SiteHeader.vue'

export default {
  name: 'App',
  components: {
    HelloWorld,
    UserTable,
    SiteHeader
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
    this.$axios.get('/user').then((response) => {
      this.userData = response.data;
    })
  },
  methods: {
    handleLogin() {
      const user = this.userData.find(user => 
        user.username === this.username && user.password === this.password
      );

      if (user) {
        // 登录成功
        localStorage.setItem('isAuthenticated', 'true');
        localStorage.setItem('username', user.username);
        localStorage.setItem('email', user.email);
        localStorage.setItem('jdCookie', user.jd_cookie);
        localStorage.setItem('tbCookie', user.tb_cookie);
        localStorage.setItem('uid', user.uid);
        console.log('Current uid:', user.uid);

        // 更新用户收藏的商品信息
        this.updateTrackedProducts(user);

        // 使用 Element Plus 的消息提示
        this.$message({
          message: '登录成功',
          type: 'success',
          duration: 2000
        });
        
        // 直接跳转到主页
        this.$router.push('/home');
      } else {
        // 登录失败
        localStorage.removeItem('isAuthenticated');
        
        // 使用 Element Plus 的消息提示
        this.$message({
          message: '用户名或密码错误',
          type: 'error',
          duration: 2000
        });
      }
    },
    async updateTrackedProducts(user) {
      try {
        // 获取用户收藏的商品
        const trackedResponse = await this.$axios.get(`/tracking/details?uid=${user.uid}`);
        if (trackedResponse.data.code === 200) {
          const trackedProducts = trackedResponse.data.data;
          
          // 对每个收藏的商品进行更新
          for (const product of trackedProducts) {
            // 根据平台和商品名称搜索最新信息
            const searchResponse = await this.$axios.get('/product/search', {
              params: {
                keyword: product.productname,
                jdCookie: product.platform === '京东' ? user.jd_cookie : null,
                tbCookie: product.platform === '淘宝' ? user.tb_cookie : null,
                platform: product.platform === '京东' ? 'jd' : 'tb',
                uid: user.uid
              }
            });
            
            console.log(`Updated tracked product: ${product.productname} from ${product.platform}`);
          }
        }
      } catch (error) {
        console.error('Error updating tracked products:', error);
        // 不影响正常登录，所以这里只记录错误
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
  margin: 0;
  padding: 0;
  min-height: 100vh;
  width: 100%;
  overflow-x: hidden;
}

/* 登录页面的渐变背景 */
.content-wrapper {
  width: 100%;  /* 确保宽度100% */
  min-height: 100vh;  /* 确保高度100% */
  margin: 0;  /* 移除所有外边距 */
  padding: 60px 0 0 0;  /* 只保留顶部内边距 */
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
  display: flex;  /* 使用flex布局 */
  flex-direction: column;  /* 垂直排列 */
  align-items: center;  /* 水平居中 */
}

/* 登录页面的内容容器 */
.login-container {
  width: 300px;  /* 固定宽度 */
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