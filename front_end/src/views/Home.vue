<template>
  <div class="home">
    <!-- 顶部栏 -->
    <div class="header">
      <div class="left-section">
        <img alt="Vue logo" src="../assets/logo.jpg" class="logo-image">
      </div>
      <div class="right-section">
        <div class="welcome-container">
          <h2 class="welcome-text">Welcome, {{ username }}!</h2>
          <button class="profile-button" @click="handleProfile">
            <i class="fas fa-user"></i>
            个人中心
          </button>
        </div>
      </div>
    </div>

    <!-- 分隔线 -->
    <div class="divider"></div>

    <!-- 搜索栏 -->
    <div class="search-container">
      <input 
        type="text" 
        v-model="searchQuery" 
        placeholder="请输入商品名称"
        class="search-input"
      >
      <el-select 
        v-model="selectedPlatform" 
        placeholder="选择平台"
        class="platform-select"
      >
        <el-option label="全部平台" value="all" />
        <el-option label="京东" value="jd" />
        <el-option label="淘宝" value="tb" />
      </el-select>
      <button @click="handleSearch" class="search-button">
        <i class="fas fa-search"></i>
      </button>
    </div>

    <!-- 搜索指引 -->
    <div v-if="!products.length" class="search-guide">
      <i class="fas fa-search guide-icon"></i>
      <p class="guide-text">输入你想搜索的商品，选择搜索平台后单击搜索即可！</p>
    </div>

    <!-- 商品表格 -->
    <div class="products-table" v-else>
      <el-table 
        :data="products" 
        style="width: 100%" 
        :fit="true"
        :row-class-name="getRowClassName"
      >
        <el-table-column 
          width="50" 
          align="center"
          fixed="left">
          <template #default="scope">
            <el-icon 
              class="star-icon"
              :class="{ 'is-starred': isProductStarred(scope.row) }"
              @click.stop="handleToggleStar(scope.row)"
            >
              <Star v-if="isProductStarred(scope.row)" style="color: #f0c24b;" />
              <StarFilled v-else />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column 
          type="index" 
          label="序号" 
          width="60" 
          align="center">
        </el-table-column>
        <el-table-column 
          prop="productname" 
          label="商品名称" 
          min-width="200" 
          align="center">
        </el-table-column>
        <el-table-column 
          label="价格" 
          min-width="120" 
          align="center">
          <template #default="scope">
            ¥{{ scope.row.current_price.toFixed(2) }}
          </template>
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

    <!-- 商品详情弹窗 -->
    <ProductDetail 
      v-model:visible="showProductDetail"
      :product="currentProduct"
      :isStarred="isProductStarred(currentProduct)"
      @toggle-star="handleToggleStar"
    />
  </div>
</template>

<script>
import ProductDetail from '../components/ProductDetail.vue'
import { Star, StarFilled } from '@element-plus/icons-vue'

export default {
  name: 'Home',
  components: {
    ProductDetail,
    Star,
    StarFilled
  },
  data() {
    return {
      username: localStorage.getItem('username') || '户',
      searchQuery: '',
      products: [],
      showProductDetail: false,
      currentProduct: null,
      selectedPlatform: 'all',
      starredProducts: [],
      uid: localStorage.getItem('uid')
    }
  },
  created() {
    if (this.uid) {
      this.loadStarredProducts();
    }
  },
  mounted() {
    if (!this.isLoggedIn) {
      console.log('User not logged in');
      return;
    }
    console.log('User logged in with uid:', this.uid);
  },
  methods: {
    async handleSearch() {
      if (!this.searchQuery.trim()) {
        this.$message.warning('请输入搜索关键词');
        return;
      }
      
      try {
        const response = await this.$axios.get('/product/search', {
          params: {
            keyword: this.searchQuery,
            jdCookie: localStorage.getItem('jdCookie'),
            tbCookie: localStorage.getItem('tbCookie'),
            platform: this.selectedPlatform,
            uid: this.uid
          }
        });
        
        if (response.data.code === 200) {
          this.products = response.data.data;
          this.products.forEach(product => {
            product.isTracked = this.isProductStarred(product);
          });
        }
      } catch (error) {
        console.error('搜索失败:', error);
        this.$message.error('搜索失败，请稍后再试');
      }
    },
    handleDetail(row) {
      this.currentProduct = {
        name: row.productname,
        price: row.current_price,
        image: row.image_url,
        spec: row.specification || '',
        link: row.specification,
        pid: row.pid,
        platform: row.platform,
        barcode: row.barcode,
        productname: row.productname,
        current_price: row.current_price,
        image_url: row.image_url,
        variants: [
          {
            id: 1,
            name: row.specification || '默认规格',
            price: row.current_price.toString()
          }
        ],
        priceHistory: [
          {
            date: new Date().toISOString().split('T')[0],
            price: row.current_price,
            variant: '默认规格'
          }
        ]
      };

      if (row.historical_prices) {
        try {
          const history = typeof row.historical_prices === 'string' 
            ? JSON.parse(row.historical_prices) 
            : row.historical_prices;
          
          if (history.current) {
            this.currentProduct.priceHistory.push({
              date: new Date(Date.now() - 86400000).toISOString().split('T')[0],
              price: parseFloat(history.current),
              variant: '默认规格'
            });
          }
          if (history.original && history.original !== history.current) {
            this.currentProduct.priceHistory.push({
              date: new Date(Date.now() - 86400000 * 7).toISOString().split('T')[0],
              price: parseFloat(history.original),
              variant: '默认规格'
            });
          }
        } catch (e) {
          console.error('解析价格历史失败:', e);
        }
      }

      this.currentProduct.priceHistory.sort((a, b) => new Date(a.date) - new Date(b.date));
      console.log('Price history:', this.currentProduct.priceHistory);
      this.showProductDetail = true;
    },
    handleProfile() {
      this.$router.push('/profile');
    },
    getRowClassName({ row }) {
      return this.isProductStarred(row) ? 'starred-row' : '';
    },
    isProductStarred(product) {
      if (!product) return false;
      return product.isTracked || this.starredProducts.includes(product.pid);
    },
    async handleToggleStar(product) {
      if (!product) return;
      if (!this.uid) {
        this.$message.warning('请先登录');
        return;
      }
      try {
        const isStarred = this.isProductStarred(product);
        const response = await this.$axios.post('/tracking/toggle', {
          uid: parseInt(this.uid),
          pid: product.pid,
          track: !isStarred
        });
        
        if (response.data.code === 200) {
          if (!isStarred) {
            this.starredProducts.push(product.pid);
            product.isTracked = true;
            this.$message.success('收藏成功');
          } else {
            this.starredProducts = this.starredProducts.filter(id => id !== product.pid);
            product.isTracked = false;
            this.$message.success('已取消收藏');
          }
        }
      } catch (error) {
        console.error('收藏操作失败:', error);
        this.$message.error('操作失败，请稍后再试');
      }
    },
    async loadStarredProducts() {
      try {
        const response = await this.$axios.get('/tracking/list', {
          params: { uid: this.uid }
        });
        if (response.data.code === 200) {
          this.starredProducts = response.data.data;
        }
      } catch (error) {
        console.error('加载收藏商品失败:', error);
      }
    },
  },
  computed: {
    isLoggedIn() {
      return !!this.uid;
    }
  },
  watch: {
    isLoggedIn(newVal) {
      if (newVal) {
        this.loadStarredProducts();
      }
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
  background: linear-gradient(
    to bottom,
    #ffffff 0%,
    #f0f8ff 30%,
    #e6f3ff 70%,
    #d9edff 100%
  );
  width: 100vw;
  position: absolute;
  left: 0;
  box-sizing: border-box;
  overflow-x: hidden;
}

.header {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  margin-bottom: 0;
  max-width: 850px;
  margin: 0 auto;
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
  margin-bottom: 0;
}

.right-section {
  flex-grow: 1;
  text-align: center;
  max-width: 600px;
  margin-left: -20px;
  margin-top: 45px;
}

.welcome-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 400px;
  margin: 0 auto;
  padding-left: 40px;
  margin-right: 120px;
}

.welcome-text {
  color: #3f5bad;
  font-size: 35px;
  margin: 0;
  line-height: 1;
  font-family: 'Playfair Display', 'Didot', 'Bodoni MT', serif;
  font-weight: 600;
  letter-spacing: 1px;
  text-shadow: 
    2px 2px 4px rgba(63, 91, 173, 0.1),
    -1px -1px 0 white,
    1px -1px 0 white,
    -1px 1px 0 white,
    1px 1px 0 white;
  font-style: italic;
  text-align: left;
  margin-left: -50px;
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
  margin-right: 0;
}

.profile-button:hover {
  background-color: #6b95be;
  transform: translateY(-1px);
}

.profile-button:active {
  transform: translateY(1px);
}

.profile-button i {
  font-size: 16px;
}

.divider {
  width: 860px;
  height: 1px;
  background-color: #d4e3f5;
  margin: 20px auto;
  opacity: 0.6;
}

.search-container {
  display: flex;
  gap: 10px;
  width: 600px;
  margin: 30px auto;
  align-items: stretch;
}

.search-input {
  flex-grow: 1;
  padding: 10px 15px;
  border: 2px solid #7ea8d1;
  border-radius: 4px;
  font-size: 16px;
  height: 35px;
  box-sizing: border-box;
}

.platform-select {
  width: 120px;
  height: 35px;
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
  height: 35px;
  width: 35px;
  padding: 0;
}

.search-button:hover {
  background-color: #6b95be;
  transform: translateY(-1px);
}

.search-button:active {
  transform: translateY(1px);
}

.search-button i {
  font-size: 18px;
}

.search-guide {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 100px;
}

.guide-icon {
  font-size: 48px;
  color: #90b1d2;
  margin-bottom: 20px;
  opacity: 0.8;
}

.guide-text {
  font-size: 20px;
  color: #bcc8d1;
  margin: 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.products-table {
  width: 860px;
  margin: 20px auto;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* Element Plus 样式覆盖 */
:deep(.el-input__wrapper) {
  background-color: white !important;
  border: 2px solid #7ea8d1 !important;
  box-shadow: none !important;
  height: 44px !important;
  line-height: 44px !important;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: none !important;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 4px;
}

:deep(.el-select-dropdown__item) {
  padding: 0 15px;
}

:deep(.el-select-dropdown__item.selected) {
  color: #3f5bad;
  font-weight: bold;
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

/* 改星星图标样式 */
:deep(.fa-star), :deep(.fa-star-o) {
  font-size: 16px;
  color: #999;
  transition: all 0.3s;
  padding: 5px;
}

:deep(.fa-star:hover), :deep(.fa-star-o:hover) {
  color: #f0c24b;
  transform: scale(1.1);
}

:deep(.fa-star.starred) {
  color: #f0c24b;
}

/* 收藏行样式 */
:deep(.starred-row) {
  background-color: #fff9e6 !important;
}

:deep(.starred-row:hover > td) {
  background-color: #fff3d6 !important;
}

.star-icon {
  cursor: pointer;
  font-size: 20px;
  transition: all 0.3s;
}

.star-icon:hover {
  transform: scale(1.2);
}

:deep(.el-table .starred-row) {
  background-color: #fff9e6;
}

:deep(.el-table .starred-row:hover > td) {
  background-color: #fff3d6 !important;
}

:deep(.el-table .el-table__row:not(.starred-row)) {
  background-color: white;
}

:deep(.el-table .el-table__row:not(.starred-row):hover > td) {
  background-color: #f5f7fa !important;
}

:deep(.el-icon) {
  color: #999;
}

:deep(.is-starred .el-icon) {
  color: #f0c24b;
}

.cookie-input {
  width: 300px;
  height: 40px;
  padding: 0 15px;
  margin-right: 10px;
  border: 2px solid #7ea8d1;
  border-radius: 4px;
  font-size: 14px;
}

.cookie-input:focus {
  outline: none;
  border-color: #3f5bad;
}
</style> 