<template>
  <div class="profile">
    <div class="header">
      <div class="left-section">
        <img alt="Vue logo" src="../assets/logo.jpg" class="logo-image">
        <h1 class="site-title">商品比价网站</h1>
      </div>
      <div class="right-section">
        <div class="title-section">
          <div class="title-container">
            <h2 class="welcome-text">个人中心</h2>
            <button class="back-button" @click="handleBack">
              <i class="fas fa-arrow-left"></i>
              返回主界面
            </button>
          </div>
          <div class="divider-line"></div>
          <div class="button-group">
            <button class="action-button" @click="handleLogout">登出</button>
            <button class="action-button" @click="handleDelete">注销</button>
            <button class="action-button" @click="handleResetPassword">修改密码</button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="profile-content">
      <div class="info-container">
        <div class="info-row">
          <span class="info-label">
            用户名：
            <template v-if="!editingUsername">
              {{ username }}
            </template>
            <input 
              v-else
              v-model="editUsername" 
              class="edit-input"
              type="text"
            >
          </span>
          <div class="button-group-small">
            <template v-if="!editingUsername">
              <button class="update-button" @click="startEditUsername">update</button>
            </template>
            <template v-else>
              <button 
                class="save-button"
                :class="{ 'save-button-disabled': !isUsernameChanged }"
                @click="saveUsername"
                :disabled="!isUsernameChanged"
              >
                save
              </button>
              <button class="cancel-button" @click="cancelEditUsername">取消</button>
            </template>
          </div>
        </div>
        <div class="info-row">
          <span class="info-label">
            邮箱：
            <template v-if="!editingEmail">
              {{ email }}
            </template>
            <input 
              v-else
              v-model="editEmail" 
              class="edit-input"
              type="email"
            >
          </span>
          <div class="button-group-small">
            <template v-if="!editingEmail">
              <button class="update-button" @click="startEditEmail">update</button>
            </template>
            <template v-else>
              <button 
                class="save-button"
                :class="{ 'save-button-disabled': !isEmailChanged }"
                @click="saveEmail"
                :disabled="!isEmailChanged"
              >
                save
              </button>
              <button class="cancel-button" @click="cancelEditEmail">取消</button>
            </template>
          </div>
        </div>
        <div class="info-row">
          <div class="info-content">
            <span class="label-text">京东 Cookie：</span>
            <div class="value-container">
              <template v-if="!editingJDCookie">
                <div class="cookie-text">{{ jdCookie || '未设置' }}</div>
              </template>
              <input 
                v-else
                v-model="editJDCookie" 
                class="edit-input cookie-input"
                type="text"
              >
            </div>
          </div>
          <div class="button-group-small">
            <template v-if="!editingJDCookie">
              <button class="update-button" @click="startEditJDCookie">update</button>
            </template>
            <template v-else>
              <button 
                class="save-button"
                :class="{ 'save-button-disabled': !isJDCookieChanged }"
                @click="saveJDCookie"
                :disabled="!isJDCookieChanged"
              >
                save
              </button>
              <button class="cancel-button" @click="cancelEditJDCookie">取消</button>
            </template>
          </div>
        </div>
        <div class="info-row">
          <div class="info-content">
            <span class="label-text">淘宝 Cookie：</span>
            <div class="value-container">
              <template v-if="!editingTBCookie">
                <div class="cookie-text">{{ tbCookie || '未设置' }}</div>
              </template>
              <input 
                v-else
                v-model="editTBCookie" 
                class="edit-input cookie-input"
                type="text"
              >
            </div>
          </div>
          <div class="button-group-small">
            <template v-if="!editingTBCookie">
              <button class="update-button" @click="startEditTBCookie">update</button>
            </template>
            <template v-else>
              <button 
                class="save-button"
                :class="{ 'save-button-disabled': !isTBCookieChanged }"
                @click="saveTBCookie"
                :disabled="!isTBCookieChanged"
              >
                save
              </button>
              <button class="cancel-button" @click="cancelEditTBCookie">取消</button>
            </template>
          </div>
        </div>
        <div class="divider"></div>
      </div>
      <div class="watched-products">收藏的商品：</div>
      <div class="products-table">
        <el-table :data="trackedProducts" style="width: 100%" :fit="true" v-loading="loading">
          <el-table-column 
            type="index" 
            label="序号" 
            width="80" 
            align="center">
          </el-table-column>
          <el-table-column 
            prop="productname" 
            label="商品名称" 
            width="300" 
            align="center">
          </el-table-column>
          <el-table-column 
            label="价格"
            prop="price"
            width="150"
            align="center">
            <template #default="scope">
              ¥{{ scope.row.current_price.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column 
            prop="platform" 
            label="平台" 
            width="120" 
            align="center">
          </el-table-column>
          <el-table-column 
            label="操作" 
            width="260" 
            align="center">
            <template #default="scope">
              <div class="button-container">
                <el-button
                  size="small"
                  type="primary"
                  @click="handleDetail(scope.row)"
                >
                  详细信息
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="untrackProduct(scope.row)"
                >
                  取消收藏
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <ProductDetail 
      v-model:visible="showProductDetail"
      :product="currentProduct"
      :isStarred="true"
      @toggle-star="handleToggleStar"
    />
  </div>
</template>

<script>
import ProductDetail from '../components/ProductDetail.vue'

export default {
  name: 'Profile',
  components: {
    ProductDetail
  },
  data() {
    return {
      username: '',
      email: '',
      jdCookie: '',
      tbCookie: '',
      editingUsername: false,
      editingEmail: false,
      editingJDCookie: false,
      editingTBCookie: false,
      editUsername: '',
      editEmail: '',
      originalUsername: '',
      originalEmail: '',
      showProductDetail: false,
      editJDCookie: '',
      editTBCookie: '',
      originalJDCookie: '',
      originalTBCookie: '',
      trackedProducts: [],
      loading: false,
      currentProduct: null
    }
  },
  computed: {
    isUsernameChanged() {
      return this.editUsername.trim() !== this.originalUsername;
    },
    isEmailChanged() {
      return this.editEmail.trim() !== this.originalEmail;
    },
    isJDCookieChanged() {
      return this.editJDCookie.trim() !== this.originalJDCookie;
    },
    isTBCookieChanged() {
      return this.editTBCookie.trim() !== this.originalTBCookie;
    }
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('isAuthenticated');
      localStorage.removeItem('username');
      localStorage.removeItem('email');
      localStorage.removeItem('jdCookie');
      localStorage.removeItem('tbCookie');
      this.$router.push('/');
    },
    handleDelete() {
      this.$confirm('确定要注销账号吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定注销',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const email = localStorage.getItem('email');
          const response = await this.$axios.delete(`/user/delete?email=${email}`);
          
          if (response.data.code === 200) {
            this.$message.success('账号已注销');
            localStorage.removeItem('isAuthenticated');
            localStorage.removeItem('username');
            localStorage.removeItem('email');
            localStorage.removeItem('jdCookie');
            localStorage.removeItem('tbCookie');
            this.$router.push('/');
          } else {
            this.$message.error(response.data.msg || '注销失败');
          }
        } catch (error) {
          console.error('Delete account error:', error);
          this.$message.error(error.response?.data?.msg || '注销失败');
        }
      }).catch(() => {
        this.$message.info('已取消注销操作');
      });
    },
    handleBack() {
      this.$router.push('/home');
    },
    handleDetail(row) {
      console.log('Opening detail for product:', row);
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
      this.showProductDetail = true;
      console.log('Current product set to:', this.currentProduct);
    },
    startEditUsername() {
      this.editUsername = this.username;
      this.originalUsername = this.username;
      this.editingUsername = true;
    },
    startEditEmail() {
      this.editEmail = this.email;
      this.originalEmail = this.email;
      this.editingEmail = true;
    },
    startEditJDCookie() {
      this.editJDCookie = this.jdCookie;
      this.originalJDCookie = this.jdCookie;
      this.editingJDCookie = true;
    },
    startEditTBCookie() {
      this.editTBCookie = this.tbCookie;
      this.originalTBCookie = this.tbCookie;
      this.editingTBCookie = true;
    },
    async saveUsername() {
      if (!this.editUsername.trim()) {
        this.$message.warning('用户名不能为空');
        return;
      }

      try {
        const response = await this.$axios.put('/user/update-username', {
          email: this.email,
          newUsername: this.editUsername
        });

        if (response.data.code === 200) {
          this.username = this.editUsername;
          localStorage.setItem('username', this.editUsername);
          this.editingUsername = false;
          this.$message.success('用户名修改成功！');
        } else {
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        console.error('Update username error:', error);
        this.$message.error(error.response?.data?.msg || '用户名修改失败');
      }
    },
    async saveEmail() {
      if (!this.editEmail.trim()) {
        this.$message.warning('邮箱不能为空！');
        return;
      }

      // 邮箱格式验证
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.editEmail)) {
        this.$message.error('请输入有效的邮箱地址！');
        return;
      }

      try {
        const response = await this.$axios.put('/user/update-email', {
          oldEmail: this.email,
          newEmail: this.editEmail
        });

        if (response.data.code === 200) {
          this.email = this.editEmail;
          localStorage.setItem('email', this.editEmail);
          this.editingEmail = false;
          this.$message.success('邮箱修改成功！');
        } else {
          this.$message.error(response.data.msg);
        }
      } catch (error) {
        console.error('Update email error:', error);
        this.$message.error(error.response?.data?.msg || '邮箱修改失败');
      }
    },
    async saveJDCookie() {
      try {
        const response = await this.$axios.put('/user/update-jd-cookie', {
          email: this.email,
          jdCookie: this.editJDCookie
        });
        
        if (response.data.code === 200) {
          this.jdCookie = this.editJDCookie;
          localStorage.setItem('jdCookie', this.jdCookie);
          this.editingJDCookie = false;
          this.$message.success('京东 Cookie 修改成功！');
        } else {
          this.$message.error(response.data.msg || '修改失败');
        }
      } catch (error) {
        console.error('Update JD cookie error:', error);
        this.$message.error(error.response?.data?.msg || '修改失败');
      }
    },
    async saveTBCookie() {
      try {
        const response = await this.$axios.put('/user/update-tb-cookie', {
          email: this.email,
          tbCookie: this.editTBCookie
        });
        
        if (response.data.code === 200) {
          this.tbCookie = this.editTBCookie;
          localStorage.setItem('tbCookie', this.tbCookie);
          this.editingTBCookie = false;
          this.$message.success('淘宝 Cookie 修改成功！');
        } else {
          this.$message.error(response.data.msg || '修改失败');
        }
      } catch (error) {
        console.error('Update TB cookie error:', error);
        this.$message.error(error.response?.data?.msg || '修改失败');
      }
    },
    cancelEditUsername() {
      this.editingUsername = false;
    },
    cancelEditEmail() {
      this.editingEmail = false;
    },
    cancelEditJDCookie() {
      this.editingJDCookie = false;
    },
    cancelEditTBCookie() {
      this.editingTBCookie = false;
    },
    handleResetPassword() {
      // 获取当前用户信息
      const username = localStorage.getItem('username');
      const email = localStorage.getItem('email');
      
      // 跳到修改密码页面并传递用户信息
      this.$router.push({
        path: '/reset-password',
        query: { 
          from: '/profile',
          username: username,
          email: email
        }
      });
    },
    async loadTrackedProducts() {
      this.loading = true;
      try {
        const uid = localStorage.getItem('uid');
        const response = await this.$axios.get('/tracking/details', {
          params: { uid }
        });
        if (response.data.code === 200) {
          this.trackedProducts = response.data.data.map(product => {
            const price = parseFloat(product.current_price) || 0;
            
            let historicalPrices;
            try {
              historicalPrices = product.historical_prices ? JSON.parse(product.historical_prices) : null;
            } catch (e) {
              historicalPrices = null;
            }
            
            return {
              pid: product.pid,
              productname: product.productname,
              platform: product.platform,
              current_price: price,
              price: `¥${price.toFixed(2)}`,
              specification: product.specification,
              barcode: product.barcode,
              image_url: product.image_url,
              historical_prices: historicalPrices || [
                {
                  date: new Date().toISOString().split('T')[0],
                  price: price,
                  variant: '默认规格'
                }
              ]
            };
          });
          console.log('Processed tracked products:', this.trackedProducts);
        }
      } catch (error) {
        console.error('加载收藏商品失败:', error);
        this.$message.error('加载收藏商品失败');
      } finally {
        this.loading = false;
      }
    },
    async untrackProduct(product) {
      try {
        const uid = localStorage.getItem('uid')
        const response = await this.$axios.post('/tracking/toggle', {
          uid: parseInt(uid),
          pid: product.pid,
          track: false
        })
        if (response.data.code === 200) {
          this.$message.success('取消收藏成功')
          await this.loadTrackedProducts()
        }
      } catch (error) {
        console.error('取消收藏失败:', error)
        this.$message.error('取消收藏失败')
      }
    },
    handleToggleStar(product) {
      this.untrackProduct(product)
    }
  },
  created() {
    // 从 localStorage 获取数据
    this.username = localStorage.getItem('username') || '用户';
    this.email = localStorage.getItem('email') || '';
    this.jdCookie = localStorage.getItem('jdCookie') || '';
    this.tbCookie = localStorage.getItem('tbCookie') || '';
    
    console.log('Profile loaded data:', {
      username: this.username,
      email: this.email,
      jdCookie: this.jdCookie,
      tbCookie: this.tbCookie
    });
    this.loadTrackedProducts()
  }
}
</script>

<style scoped>
/* 页面基础样式 */
.profile {
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
  ) !important;
  width: 100vw;
  position: absolute;
  left: 0;
  top: 0;
  box-sizing: border-box;
  overflow-x: hidden;
}

/* 顶部区域样式 */
.header {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
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
  text-align: left;
  max-width: 560px;
  margin-left: 12px;
  margin-top: 20px;
}

/* 标题区域样式 */
.title-section {
  background-color: #d4e5f7;
  padding: 30px;
  border-radius: 8px;
  margin: 0 130px 0 -20px;
}

.title-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.welcome-text {
  color: #3f5bad;
  font-size: 35px;
  margin: 0 0 0 55px;
  line-height: 0.5;
  font-family: 'Didot', 'Bodoni MT', 'Noto Serif Display', serif;
  font-weight: bold;
  letter-spacing: 1px;
  text-shadow: 
    -1px -1px 0 white,
    1px -1px 0 white,
    -1px 1px 0 white,
    1px 1px 0 white;
}

/* 按钮样式 */
.back-button {
  padding: 8px 15px;
  background-color: white;
  border: 2px solid #3f5bad;
  border-radius: 4px;
  color: #3f5bad;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.back-button:hover {
  background-color: #f5f7ff;
}

.button-group {
  display: flex;
  gap: 30px;
  margin: 15px 70px -10px 70px;
}

.action-button {
  padding: 5px 8px;
  background-color: rgb(240, 250, 255);
  border: 2px solid #4a63ae;
  border-radius: 4px;
  color: #3f5bad;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-button:hover {
  background-color: #f5f7ff;
}

/* 分隔线样式 */
.divider {
  height: 2px;
  background-color: #e6f3ff;
  margin: 20px 0;
}

.divider-line {
  height: 2px;
  background-color: #a8c6ff;
  margin: 15px 30px;
}

/* 内区样式 */
.profile-content {
  max-width: 1000px;
  margin: 0 auto;
  text-align: left;
}

.info-container {
  margin: 15px 130px 0 100px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px 40px;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 10px 0;
  gap: 20px;
}

.info-label {
  flex: 1;
  display: flex;
  align-items: center;
  min-width: 0;
  color: #3f5bad;
  font-size: 16px;
}

.info-label > span {
  width: 120px;
  flex-shrink: 0;
}

.info-label > template {
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.edit-input {
  flex: 1;
  min-width: 0;
  border: 1px solid #3f5bad;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 16px;
  color: #3f5bad;
}

.cookie-input {
  width: 100%;
}

.button-group-small {
  display: flex;
  gap: 8px;
  width: 120px;
  flex-shrink: 0;
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

.save-button {
  padding: 5px 15px;
  background-color: #4CAF50;
  border: none;
  border-radius: 4px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.save-button:hover {
  background-color: #45a049;
}

.save-button-disabled {
  background-color: #a8dba8 !important;
  cursor: not-allowed !important;
  opacity: 0.6;
}

.cancel-button {
  padding: 5px 15px;
  background-color: white;
  border: 2px solid #ff4d4f;
  border-radius: 4px;
  color: #ff4d4f;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-button:hover {
  background-color: #fff1f0;
}

/* 商品列表区样式 */
.watched-products {
  background-color: #3f5bad;
  color: white;
  padding: 8px 15px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  display: inline-block;
  margin: 30px 0 0 100px;
}

.products-table {
  margin: 20px 50px;
  width: calc(100% - 100px);
}

.button-container {
  display: flex;
  justify-content: center;
  gap: 10px;
}

/* Element Plus 样式覆盖 */
:deep(.el-table) {
  background-color: transparent;
  width: 100% !important;
  table-layout: fixed;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
  padding: 8px 0;
}

:deep(.el-table td) {
  padding: 8px 0;
  background-color: white;
}

:deep(.el-button--small) {
  padding: 8px 15px;
  font-size: 12px;
  margin: 0;
}

.cookie-input {
  width: 350px !important;
  max-width: calc(100% - 150px) !important;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-left: 120px;
}

/* 添加标签文本的样式 */
.info-label > span {
  flex-shrink: 0;
  margin-right: 10px;
  min-width: fit-content;
  position: absolute;
  left: 0;
}

/* 内容区域容器 */
.info-content {
  display: flex;
  flex: 1;
  min-width: 0;
  position: relative;
}

/* 标签文本 */
.label-text {
  width: 120px;
  color: #3f5bad;
  font-size: 16px;
  flex-shrink: 0;
}

/* 值容器 */
.value-container {
  flex: 1;
  min-width: 0;
  padding-right: 20px;
  margin-left: 0;
}

/* cookie 文本显示 */
.cookie-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #3f5bad;
  margin-left: 8px;
}

/* cookie 输入框 */
.cookie-input {
  width: 100% !important;
  max-width: none !important;
  border: 1px solid #3f5bad;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 16px;
  color: #3f5bad;
  margin-left: 8px;
}

/* 按钮组 */
.button-group-small {
  display: flex;
  gap: 8px;
  width: 120px;
  flex-shrink: 0;
}

/* 按钮样式 */
.update-button, .save-button, .cancel-button {
  padding: 5px 15px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.tracked-products {
  margin-top: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.tracked-products h3 {
  margin-bottom: 20px;
  color: #3f5bad;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .profile {
    padding: 10px;
    background: #f5f7fa;
  }

  .header {
    flex-direction: row;
    align-items: center;
    padding: 10px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    justify-content: space-between;
    margin: 0 45px 45px 2px;
    width: calc(100% - 0px);
  }

  .left-section {
    width: auto;
    margin-bottom: 0;
    display: flex;
    align-items: center;
    margin-left: 10px;
  }

  .logo-image {
    width: 70px;
    margin-right: 0;
  }

  .site-title {
    display: none;
  }

  .right-section {
    width: auto;
    text-align: right;
    margin-right: 5px;
    flex-grow: 1;
  }

  .title-section {
    padding: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    margin-right: 10px;
  }

  .welcome-text {
    font-size: 24px;
    margin: 0;
    color: #3f5bad;
    font-weight: bold;
    text-align: center;
    width: 100%;
    white-space: nowrap;
    letter-spacing: 2px;
    margin-top: 20px;
  }

  .back-button {
    font-size: 10px;
    padding: 3px 12px;
    margin-top: 12px;
    margin-left: 10%;
    min-width: 100px;
  }

  .fa-arrow-left {
    font-size: 10px;
    margin-right: 4px;
  }

  .button-group {
    display: flex;
    justify-content: center;
    gap: 5px;
    flex-wrap: wrap;
    margin-top: -20px;
    margin-bottom: 10px;
  }

  .info-container {
    width: calc(100% - 10px);
    padding: 15px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin: 20px 0px;
    margin-top: -30px;
  }

  .info-row {
    flex-direction: row;
    margin-bottom: 15px;
    background: #f8f9fc;
    padding: 10px;
    border-radius: 6px;
    align-items: center;
    justify-content: space-between;
  }

  .info-label {
    width: auto;
    font-size: 13px;
    color: #666;
    margin-bottom: 0;
    display: flex;
    align-items: center;
  }

  .edit-input {
    width: calc(100% - 70px);
    padding: 6px;
    border: 1px solid #ddd;
    border-radius: 4px;
    margin-bottom: 0;
    font-size: 13px;
  }

  .button-group-small {
    width: auto;
    justify-content: flex-end;
    margin-top: 0;
    gap: 8px;
  }

  .update-button, .save-button, .cancel-button {
    padding: 4px 8px;
    font-size: 12px;
    border-radius: 4px;
    min-width: 50px;
  }

  .watched-products {
    font-size: 14px;
    margin: 20px 0px 0px 10px;
    padding: 8px;
    color: #3f5bad;
    font-weight: bold;
    display: block;
    background: white;
    border-radius: 8px 8px 0 0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    width: calc(100% - 10px);
  }

  .products-table {
    margin: -8px 0px 0px 0px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    padding: 8px;
    width: calc(100% - 10px);
  }

  /* 表格适配 */
  :deep(.el-table) {
    font-size: 12px;
    width: 100% !important;
  }

  :deep(.el-table th) {
    padding: 6px 2px;
    font-size: 12px;
  }

  :deep(.el-table td) {
    padding: 6px 2px;
  }

  :deep(.el-button--small) {
    padding: 4px 6px;
    font-size: 11px;
    min-width: 40px;
  }

  :deep(.el-table .cell) {
    padding: 0 2px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  /* Cookie 输入框特殊处理 */
  .cookie-input {
    width: calc(100% - 90px) !important;
    margin-left: 0 !important;
    font-size: 12px;
  }

  .cookie-text {
    font-size: 12px;
    margin-left: 0;
  }

  .label-text {
    font-size: 13px;
    width: auto;
    margin-right: 10px;
  }
}

/* 超小屏幕适配 */
@media screen and (max-width: 480px) {
  .action-button {
    font-size: 12px;
    padding: 4px 8px;
    min-width: 70px;
  }

  .info-container {
    padding: 10px;
  }

  .info-row {
    padding: 8px;
    margin-bottom: 15px;
  }

  .watched-products {
    font-size: 14px;
    margin: 15px 5px 5px;
  }

  :deep(.el-table) {
    font-size: 11px;
  }
}
</style>