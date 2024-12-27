<template>
  <div class="login-container">
    <div class="login-form">
      <h2>登录</h2>
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    async handleLogin() {
      try {
        console.log('Attempting login with:', {
          username: this.form.username,
          password: this.form.password
        });

        const response = await this.$axios({
          method: 'post',
          url: '/user/login',
          data: {
            username: this.form.username,
            password: this.form.password
          },
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });

        console.log('Login response:', response);
        
        if (response.data.code === 200) {
          localStorage.setItem('username', this.form.username);
          const userData = response.data.data;
          console.log('Login success userData:', userData);
          localStorage.setItem('uid', userData.uid.toString());
          this.$router.push('/home');
        } else {
          console.error('Login failed:', response.data);
          this.$message.error(response.data.msg || '登录失败');
        }
      } catch (error) {
        console.error('Login error details:', {
          message: error.message,
          config: error.config,
          response: error.response
        });
        this.$message.error('登录失败，请稍后再试');
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  width: 300px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 20px;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form .el-form-item .el-input {
  width: 100%;
}

.login-form .el-form-item .el-button {
  width: 100%;
}
</style> 