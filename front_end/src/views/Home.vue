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

    <!-- 搜索栏下方添加筛选和排序选项 -->
    <div class="search-results-container" v-if="products.length">
      <!-- 平台统计信息 -->
      <div class="platform-stats">
        <div class="stats-card">
          <div class="stats-title">京东商品</div>
          <div class="stats-content">
            <div class="stats-item">
              <span class="stats-label">商品数：</span>
              <span class="stats-value">{{ platformStats.jd.count }}</span>
            </div>
            <div class="stats-item">
              <span class="stats-label">平均价：</span>
              <span class="stats-value">¥{{ platformStats.jd.avgPrice.toFixed(2) }}</span>
            </div>
          </div>
        </div>
        <div class="stats-card">
          <div class="stats-title">淘宝商品</div>
          <div class="stats-content">
            <div class="stats-item">
              <span class="stats-label">商品数：</span>
              <span class="stats-value">{{ platformStats.tb.count }}</span>
            </div>
            <div class="stats-item">
              <span class="stats-label">平均价：</span>
              <span class="stats-value">¥{{ platformStats.tb.avgPrice.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 筛选和排序选项 -->
      <div class="filter-container">
        <div class="price-filter">
          <span class="filter-label">价格区间：</span>
          <el-input
            v-model="priceFilter.min"
            placeholder="最低价"
            class="price-input"
            type="number"
          />
          <span class="separator">-</span>
          <el-input
            v-model="priceFilter.max"
            placeholder="最高价"
            class="price-input"
            type="number"
          />
          <el-button size="small" @click="applyPriceFilter">筛选</el-button>
        </div>
        <div class="price-sort">
          <span class="filter-label">价格排序：</span>
          <el-select v-model="priceSort" @change="applySorting">
            <el-option label="默认" value="" />
            <el-option label="价格从低到高" value="asc" />
            <el-option label="价格从高到低" value="desc" />
          </el-select>
        </div>
      </div>
    </div>

    <!-- 商品表格 -->
    <div class="products-grid" v-if="filteredProducts.length">
      <div v-for="product in filteredProducts" 
           :key="product.pid" 
           class="product-card"
           :class="{ 'is-starred': isProductStarred(product) }">
        <div class="product-card-header">
          <div class="tracked-count">
            <i class="fas fa-user"></i>
            {{ product.trackedCount || 0 }}
          </div>
          <el-icon 
            class="star-icon"
            :class="{ 'is-starred': isProductStarred(product) }"
            @click.stop="handleToggleStar(product)"
          >
            <Star v-if="isProductStarred(product)" style="color: #f0c24b;" />
            <StarFilled v-else />
          </el-icon>
        </div>
        <img :src="product.image_url" :alt="product.productname" class="product-image" @click="handleDetail(product)">
        <div class="product-info">
          <div class="product-name" @click="handleDetail(product)">{{ product.productname }}</div>
          <div class="product-price">¥{{ product.current_price.toFixed(2) }}</div>
          <div class="product-platform">{{ product.platform }}</div>
          <el-button
            size="small"
            type="primary"
            @click="handleDetail(product)"
            class="detail-button"
          >
            详细信息
          </el-button>
        </div>
      </div>
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
      uid: localStorage.getItem('uid'),
      priceFilter: {
        min: '',
        max: ''
      },
      priceSort: '',
      originalProducts: [],  // 保存原始搜索结果
      platformStats: {
        jd: { count: 0, avgPrice: 0 },
        tb: { count: 0, avgPrice: 0 }
      },
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
          this.originalProducts = [...response.data.data];
          this.products.forEach(product => {
            product.isTracked = this.isProductStarred(product);
          });
          this.updatePlatformStats();
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
        historical_prices: row.historical_prices
      };

      console.log('Selected product with history:', this.currentProduct); // 调试日志
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
      try {
        const uid = localStorage.getItem('uid');
        if (!uid) {
          this.$message.warning('请先登录');
          return;
        }

        if (this.isProductStarred(product)) {
          // 取消收藏
          const response = await this.$axios.delete('/tracking/untrack', {
            params: { uid, pid: product.pid }
          });
          if (response.data.code === 200) {
            this.starredProducts = this.starredProducts.filter(id => id !== product.pid);
            // 更新商品的关注数
            product.trackedCount = response.data.data;
            this.$message.success('已取消收藏');
          }
        } else {
          // 添加收藏
          const response = await this.$axios.post('/tracking/track', null, {
            params: { uid, pid: product.pid }
          });
          if (response.data.code === 200) {
            this.starredProducts.push(product.pid);
            // 更新商品的关注数
            product.trackedCount = response.data.data;
            this.$message.success('收藏成功');
          }
        }
      } catch (error) {
        this.$message.error('操作失败，请稍后重试');
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
    updateChart() {
      if (!this.chart || !this.product?.priceHistory?.length) return;

      // 解析历史价格数据
      const history = JSON.parse(this.product.historical_prices || '{}');
      const sortedDates = Object.keys(history).sort();
      const prices = sortedDates.map(date => parseFloat(history[date]));
      
      // 格式化日期显示
      const formattedDates = sortedDates.map(date => {
        const d = new Date(date);
        return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
      });

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            const data = params[0];
            return `时间：${data.name}<br/>价格：¥${data.value.toFixed(2)}`;
          }
        },
        xAxis: {
          type: 'category',
          data: formattedDates,
          axisLabel: {
            rotate: 45
          }
        },
        // ... 其他配置保持不变
      };

      this.chart.setOption(option);
    },
    applyPriceFilter() {
      // 验证输入
      if (this.priceFilter.min && this.priceFilter.max && 
          Number(this.priceFilter.min) > Number(this.priceFilter.max)) {
        this.$message.warning('最低价不能大于最高价');
        return;
      }
      // 筛选会通过 computed 属性自动应用
    },
    applySorting() {
      // 排序会通过 computed 属性自动应用
    },
    resetFilters() {
      this.priceFilter.min = '';
      this.priceFilter.max = '';
      this.priceSort = '';
      this.products = [...this.originalProducts];
    },
    updatePlatformStats(products = this.products) {
      // 重置统计数据
      this.platformStats = {
        jd: { count: 0, avgPrice: 0 },
        tb: { count: 0, avgPrice: 0 }
      };
      
      // 计算每个平台的商品数量和总价
      const jdProducts = products.filter(p => p.platform === '京东');
      const tbProducts = products.filter(p => p.platform === '淘宝');
      
      // 计算京东统计
      if (jdProducts.length > 0) {
        const jdTotal = jdProducts.reduce((sum, p) => sum + p.current_price, 0);
        this.platformStats.jd = {
          count: jdProducts.length,
          avgPrice: jdTotal / jdProducts.length
        };
      }
      
      // 计算淘宝统计
      if (tbProducts.length > 0) {
        const tbTotal = tbProducts.reduce((sum, p) => sum + p.current_price, 0);
        this.platformStats.tb = {
          count: tbProducts.length,
          avgPrice: tbTotal / tbProducts.length
        };
      }
    }
  },
  computed: {
    isLoggedIn() {
      return !!this.uid;
    },
    filteredProducts() {
      let result = [...this.products];
      
      // 应用价格筛选
      if (this.priceFilter.min !== '' || this.priceFilter.max !== '') {
        result = result.filter(product => {
          const price = product.current_price;
          const min = this.priceFilter.min === '' ? -Infinity : Number(this.priceFilter.min);
          const max = this.priceFilter.max === '' ? Infinity : Number(this.priceFilter.max);
          return price >= min && price <= max;
        });
      }
      
      // 应用价格排序
      if (this.priceSort) {
        result.sort((a, b) => {
          if (this.priceSort === 'asc') {
            return a.current_price - b.current_price;
          } else {
            return b.current_price - a.current_price;
          }
        });
      }
      
      // 更新统计信息
      this.updatePlatformStats(result);
      
      return result;
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

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.product-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.product-card.is-starred {
  border: 2px solid #f0c24b;
}

.product-card-header {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  cursor: pointer;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 10px;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  cursor: pointer;
}

.product-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.product-platform {
  color: #909399;
  font-size: 12px;
  margin-bottom: 10px;
}

.detail-button {
  width: 100%;
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

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    padding: 10px;
    margin: 0;
    width: 100%;
  }
  
  .left-section {
    width: auto;
    margin-left: 10px;
    align-items: flex-start;
  }

  .logo-image {
    width: 60px;
    margin-bottom: 5px;
  }

  .right-section {
    width: 100%;
    margin: 0;
    padding: 0 10px;
  }

  .welcome-container {
    width: 100%;
    margin: 10px 0;
    padding: 0;
    justify-content: flex-start;
    gap: 50px;
  }

  .welcome-text {
    font-size: 24px;
    margin: 0;
    margin-left: 10px;
  }

  .profile-button {
    height: 35px;
    font-size: 12px;
    padding: 0 10px;
    margin-left: 10px;
  }

  .search-container {
    width: 90%;
    margin: 10px auto;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px;
  }

  .search-input {
    width: 100%;
    margin-bottom: 8px;
  }

  .platform-select {
    width: calc(100% - 45px);
    margin: 0;
  }

  .search-button {
    width: 35px;
    height: 35px;
    margin: 0;
  }

  .result-container {
    padding: 10px;
    width: 100%;
    box-sizing: border-box;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 10px;
    padding: 10px;
  }

  .product-card {
    width: 100%;
  }

  .product-image {
    height: 150px;
  }

  .product-name {
    font-size: 12px;
    height: 32px;
  }

  .product-price {
    font-size: 16px;
  }

  .filter-container {
    flex-direction: column;
    padding: 10px;
    gap: 10px;
  }

  .price-filter, .price-sort {
    width: 100%;
    justify-content: space-between;
    gap: 15px;
  }

  .price-input {
    width: 80px;
  }

  .filter-label {
    min-width: 70px;
  }

  :deep(.el-select) {
    width: 100%;
    max-width: none;
  }
}

.tracked-count {
  background: rgba(0, 0, 0, 0.5);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.tracked-count i {
  font-size: 10px;
}

.filter-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin: 20px auto;
  padding: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  max-width: 800px;
}

.price-filter, .price-sort {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.filter-label {
  color: #606266;
  font-size: 14px;
  white-space: nowrap;
  min-width: 80px;
}

.price-input {
  width: 100px;
}

.separator {
  color: #909399;
}

.platform-stats {
  display: flex;
  gap: 20px;
  width: 100%;
  margin-bottom: 15px;
}

.stats-card {
  flex: 1;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stats-title {
  font-size: 14px;
  color: #3f5bad;
  font-weight: bold;
  margin-bottom: 8px;
  text-align: center;
}

.stats-content {
  display: flex;
  justify-content: space-around;
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.stats-label {
  color: #606266;
  font-size: 13px;
}

.stats-value {
  color: #f56c6c;
  font-weight: bold;
  font-size: 14px;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .platform-stats {
    flex-direction: column;
    gap: 10px;
  }

  .stats-card {
    width: 100%;
  }

  .stats-content {
    justify-content: space-between;
    padding: 0 10px;
  }
}

.search-results-container {
  max-width: 800px;
  margin: 20px auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.platform-stats {
  display: flex;
  gap: 20px;
  width: 100%;
}

.filter-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .search-results-container {
    width: 90%;
    gap: 10px;
  }

  .platform-stats {
    flex-direction: column;
    gap: 10px;
  }

  .filter-container {
    flex-direction: column;
    padding: 10px;
    gap: 10px;
  }
}

/* 调整选择框宽度 */
:deep(.el-select) {
  width: 200px;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  /* ... 其他移动端样式保持不变 ... */

  :deep(.el-select) {
    width: 100%;
    max-width: none;
  }
}
</style> 