<template>
  <div class="product-detail-overlay" v-if="visible" @click.self="handleClose">
    <div class="product-detail-box">
      <div class="detail-header">
        <h3>商品详情</h3>
        <div class="header-actions">
          <i 
            class="fas" 
            :class="isStarred ? 'fa-star starred' : 'fa-star-o'" 
            @click="toggleStar"
            title="收藏商品"
          ></i>
          <i class="fas fa-times close-button" @click="handleClose"></i>
        </div>
      </div>
      <div class="detail-content">
        <div class="product-image">
          <img :src="product.image" :alt="product.name">
        </div>
        <div class="detail-section">
          <div class="detail-item">
            <span class="label">商品名称：</span>
            <span class="value">{{ product.name }}</span>
          </div>
          <div class="detail-item">
            <span class="label">平台：</span>
            <span class="value">{{ product.platform }}</span>
          </div>
          <div class="detail-item">
            <span class="label">条码：</span>
            <span class="value">{{ product.barcode }}</span>
          </div>
          <div class="detail-item">
            <span class="label">链接：</span>
            <span class="value">
              <a :href="product.link" target="_blank" class="detail-link">
                <i class="fas fa-external-link-alt"></i> 查看详情
              </a>
            </span>
          </div>
          <div class="link-tip">
            <p>商品的多级品类和规格请点击上方链接查看详情</p>
          </div>
          <div class="detail-item">
            <span class="label">当前价格：</span>
            <span class="value price">{{ product.price }}</span>
          </div>
        </div>
      </div>
      <div class="price-history">
        <h4>价格走势</h4>
        <div class="chart-container" ref="chartContainer"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'ProductDetail',
  props: {
    visible: Boolean,
    product: Object,
    isStarred: Boolean
  },
  data() {
    return {
      chart: null
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(value) {
        this.$emit('update:visible', value)
      }
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.$nextTick(() => {
          this.initChart()
        })
      }
    },
    'product.priceHistory': {
      handler() {
        this.$nextTick(() => {
          if (this.visible) {
            this.updateChart()
          }
        })
      },
      deep: true
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
    },
    toggleStar() {
      this.$emit('toggle-star', this.product)
    },
    initChart() {
      if (this.chart) {
        this.chart.dispose()
      }
      this.chart = echarts.init(this.$refs.chartContainer)
      this.updateChart()
    },
    updateChart() {
      if (!this.chart || !this.product?.priceHistory?.length) return

      const dates = this.product.priceHistory.map(item => item.date)
      const prices = this.product.priceHistory.map(item => item.price)

      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            const data = params[0]
            return `日期：${data.name}<br/>价格：¥${data.value}`
          }
        },
        grid: {
          top: 30,
          bottom: 60,
          left: 60,
          right: 30
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [{
          data: prices,
          type: 'line',
          smooth: true,
          symbolSize: 8,
          symbol: 'circle',
          lineStyle: {
            color: '#3f5bad'
          },
          itemStyle: {
            color: '#3f5bad'
          },
          emphasis: {
            itemStyle: {
              borderWidth: 2,
              borderColor: '#3f5bad',
              color: '#fff'
            },
            label: {
              show: true,
              formatter: function(params) {
                return `¥${params.value}`
              },
              backgroundColor: '#3f5bad',
              color: '#fff',
              padding: [4, 8],
              borderRadius: 4
            }
          }
        }]
      }

      this.chart.setOption(option)
    }
  },
  beforeUnmount() {
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  }
}
</script>

<style scoped>
.product-detail-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.product-detail-box {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  width: 700px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.detail-header h3 {
  color: #3f5bad;
  margin: 0;
  font-size: 18px;
}

.close-button {
  cursor: pointer;
  color: #999;
  font-size: 18px;
  transition: all 0.3s;
}

.close-button:hover {
  color: #666;
}

.detail-content {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.product-image {
  width: 250px;
  height: 250px;
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-section {
  flex: 1;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
}

.detail-item .label {
  color: #666;
  width: 100px;
  text-align: right;
  margin-right: 15px;
}

.detail-item .value {
  color: #333;
  flex: 1;
  text-align: left;
}

.buy-link {
  margin-left: 10px;
  color: #3f5bad;
  text-decoration: none;
}

.buy-link:hover {
  text-decoration: underline;
}

.variant-price {
  float: right;
  color: #f56c6c;
}

.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.price-history {
  border-top: 1px solid #eee;
  padding-top: 10px;
}

.price-history h4 {
  color: #3f5bad;
  margin: 0 0 15px 0;
}

.chart-container {
  height: 300px;
  width: 100%;
}

:deep(.el-select) {
  width: 200px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.fa-star, .fa-star-o {
  cursor: pointer;
  font-size: 18px;
  color: #999;
  transition: all 0.3s;
}

.fa-star:hover, .fa-star-o:hover {
  color: #f0c24b;
  transform: scale(1.1);
}

.starred {
  color: #f0c24b !important;
}

.link-tip {
  margin: 10px 0;
  padding: 2px 2px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #666;
  font-size: 14px;
}

.detail-link {
  color: #3f5bad;
  text-decoration: none;
  transition: all 0.3s;
}

.detail-link:hover {
  text-decoration: underline;
}
</style> 