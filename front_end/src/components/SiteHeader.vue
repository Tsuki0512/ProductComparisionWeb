<template>
  <div class="header-wrapper">
    <img alt="Vue logo" src="../assets/logo.jpg" class="logo-image">
    <h1 class="site-title">商品比价网站</h1>
    <h2 v-if="showSubtitle" class="site-subtitle">Price Comparison Website</h2>
  </div>
</template>

<script>
export default {
  name: 'SiteHeader',
  props: {
    showSubtitle: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style>
/* 添加全局样式（不使用 scoped） */
input::placeholder {
  color: #7eb3d1;
  font-size: 15px;
}
</style>

<style scoped>
.header-wrapper {
  text-align: center;
  margin-top: 70px;
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
</style> 