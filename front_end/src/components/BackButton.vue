<template>
  <div class="back-button-container">
    <button class="back-button" @click="handleBack">
      <i class="fas fa-arrow-left"></i>
      返回
    </button>
  </div>
</template>

<script>
export default {
  name: 'BackButton',
  props: {
    returnPath: {
      type: String,
      default: '/'  // 默认返回登录页
    }
  },
  methods: {
    handleBack() {
      this.$router.push(this.returnPath);
    }
  }
}
</script>

<style scoped>
.back-button-container {
  position: relative;
  margin-top: 15px;
  margin-left: 230px;
  display: flex;
  justify-content: flex-end;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 15px;
  background-color: rgb(255, 255, 255);
  border: none;
  border-radius: 4px;
  color: #5391cf;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.back-button:hover {
  background-color: rgb(195, 223, 225);
  transform: translateX(-5px);
}

.back-button i {
  font-size: 16px;
}
</style> 