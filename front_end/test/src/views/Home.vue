<template>
  <div class="home">
    <div class="header">
      <div class="left-section">
        <img alt="Vue logo" src="../assets/logo.jpg" class="logo-image">
        <h1 class="site-title">商品比价网站</h1>
      </div>
      <div class="right-section">
        <div class="welcome-container">
          <h2 class="welcome-text">Hi! {{ username }}</h2>
          <button class="profile-button" @click="handleProfile">
            <i class="fas fa-user"></i>
            个人中心
          </button>
        </div>
        <div class="search-container">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="请输入商品名称"
            class="search-input"
          >
          <button @click="handleSearch" class="search-button">
            <i class="fas fa-search"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- 商品表格 -->
    <div class="products-table" v-if="products.length">
      <el-table :data="products" style="width: 100%" :fit="true">
        <el-table-column 
          type="index" 
          label="序号" 
          width="60" 
          align="center">
        </el-table-column>
        <el-table-column 
          prop="name" 
          label="商品名称" 
          min-width="200" 
          align="center">
        </el-table-column>
        <el-table-column 
          prop="price" 
          label="价格" 
          min-width="120" 
          align="center">
        </el-table-column>
        <el-table-column 
          prop="platform" 
          label="平台" 
          min-width="120" 
          align="center">
        </el-table-column>
        <el-table-column 
          label="操作" 
          min-width="100" 
          align="center"
          fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleDetail(scope.row)"
            >
              详细信息
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      username: localStorage.getItem('username') || '用户',
      searchQuery: '',
      products: [],
      sampleProducts: [
        {
          name: 'iPhone 15 Pro',
          price: '¥7999',
          platform: '京东自营',
          detail: {
            color: '自然钛色',
            storage: '256GB',
            delivery: '次日达',
            seller: '京东自营'
          }
        },
        {
          name: 'iPhone 15 Pro',
          price: '¥8099',
          platform: '天猫旗舰店',
          detail: {
            color: '自然钛色',
            storage: '256GB',
            delivery: '3日内发货',
            seller: 'Apple官方旗舰店'
          }
        },
        {
          name: 'iPhone 15 Pro',
          price: '¥7899',
          platform: '苏宁易购',
          detail: {
            color: '自然钛色',
            storage: '256GB',
            delivery: '隔日达',
            seller: '苏宁自营'
          }
        }
      ]
    }
  },
  methods: {
    handleSearch() {
      if (this.searchQuery.trim()) {
        this.products = this.sampleProducts;
      }
    },
    handleDetail(row) {
      // 使用Element Plus的消息框显示详细信息
      this.$message.info(`
        商品详情：
        颜色：${row.detail.color}
        存储：${row.detail.storage}
        发货：${row.detail.delivery}
        卖家：${row.detail.seller}
      `);
    },
    handleProfile() {
      this.$router.push('/profile');
    }
  }
}
</script>

<style scoped>
.home {
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

.welcome-text {
  color: #3f5bad;
  font-size: 35px;
  margin-bottom: 10px;
  text-align: left;
  line-height: 1;
}

.search-container {
  display: flex;
  gap: 10px;
  max-width: 400px;
  margin-top: 5px;
}

.search-input {
  flex-grow: 1;
  padding: 10px;
  border: 2px solid #7ea8d1;
  border-radius: 4px;
  font-size: 16px;
}

.search-button {
  padding: 10px 15px;
  background-color: #7ea8d1;
  border: none;
  border-radius: 4px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-button i {
  font-size: 18px;
}

.search-button:hover {
  background-color: #6b95be;
  transform: translateY(-1px);
}

.search-button:active {
  transform: translateY(1px);
}

.products-table {
  margin-top: 20px;
  width: 860px;
  margin-left: auto;
  margin-right: auto;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-table) {
  margin: 0 auto;
  width: 100% !important;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
  padding: 8px 0;
}

:deep(.el-table td) {
  padding: 8px 0;
}

:deep(.el-button--small) {
  padding: 8px 15px;
  font-size: 12px;
}

.welcome-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  max-width: 400px;
}

.profile-button {
  padding: 0 15px;
  background-color: #7ea8d1;
  border: none;
  border-radius: 4px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
  height: 45px;
  margin-top: 0;
}

.profile-button i {
  font-size: 16px;
}

.profile-button:hover {
  background-color: #6b95be;
  transform: translateY(-1px);
}

.profile-button:active {
  transform: translateY(1px);
}
</style> 