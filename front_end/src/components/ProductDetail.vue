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
            <span class="value">
              {{ product.platform }}
              <a :href="product.link" target="_blank" class="buy-link">
                <i class="fas fa-external-link-alt"></i> 购买链接
              </a>
            </span>
          </div>
          <div class="detail-item">
            <span class="label">条码：</span>
            <span class="value">{{ product.barcode }}</span>
          </div>
          <div class="detail-item">
            <span class="label">规格：</span>
            <span class="value">{{ product.spec }}</span>
          </div>
          <div class="detail-item">
            <span class="label">品类：</span>
            <div class="value">
              <el-select v-model="selectedVariant" placeholder="请选择品类">
                <el-option
                  v-for="item in product.variants"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                >
                  <span>{{ item.name }}</span>
                  <span class="variant-price">¥{{ item.price }}</span>
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="detail-item">
            <span class="label">当前价格：</span>
            <span class="value price">¥{{ currentPrice }}</span>
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
    visible: {
      type: Boolean,
      default: false
    },
    product: {
      type: Object,
      default: () => ({
        name: '',
        platform: '',
        link: '',
        barcode: '',
        image: '',
        spec: '',
        variants: [],
        priceHistory: [],
        detail: {
          color: '',
          storage: '',
          delivery: '',
          seller: ''
        }
      })
    },
    isStarred: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      selectedVariant: null,
      chart: null
    }
  },
  computed: {
    currentPrice() {
      if (!this.selectedVariant) return '---'
      const variant = this.product.variants.find(v => v.id === this.selectedVariant)
      return variant ? variant.price : '---'
    }
  },
  watch: {
    visible: {
      handler(newVal) {
        if (newVal && this.product?.variants?.length) {
          this.selectedVariant = this.product.variants[0].id
          this.$nextTick(() => {
            setTimeout(() => {
              this.initChart()
            }, 100)
          })
        }
      },
      immediate: true
    },
    selectedVariant: {
      handler() {
        this.$nextTick(() => {
          this.initChart()
        })
      }
    }
  },
  methods: {
    handleClose() {
      this.$emit('update:visible', false)
    },
    initChart() {
      if (this.chart) {
        this.chart.dispose()
      }
      
      const chartDom = this.$refs.chartContainer
      if (!chartDom) return
      
      this.chart = echarts.init(chartDom)
      
      const selectedVariantName = this.product.variants.find(v => v.id === this.selectedVariant)?.name
      const priceHistory = this.product?.priceHistory?.filter(item => 
        item.variant === selectedVariantName
      ) || []
      
      const option = {
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          borderColor: '#3f5bad',
          borderWidth: 1,
          textStyle: {
            color: '#666'
          },
          formatter: function(params) {
            return `<div style="padding: 3px;">
              <div style="margin-bottom: 3px;font-weight:bold;color:#3f5bad">${params.name}</div>
              <div style="color:#f56c6c">价格：¥${params.value.toLocaleString()}</div>
            </div>`
          },
          position: 'top'
        },
        grid: {
          left: '10%',
          right: '10%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: priceHistory.map(item => item.date || ''),
          axisLabel: {
            interval: 0,
            rotate: 45
          },
          axisLine: {
            lineStyle: {
              color: '#3f5bad'
            }
          }
        },
        yAxis: {
          type: 'value',
          name: '价格',
          axisLabel: {
            formatter: '¥{value}'
          },
          splitLine: {
            show: true,
            lineStyle: {
              type: 'dashed',
              color: '#eee'
            }
          },
          axisLine: {
            lineStyle: {
              color: '#3f5bad'
            }
          }
        },
        series: [{
          data: priceHistory.map(item => item.price || 0),
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#3f5bad',
            width: 2
          },
          symbol: 'circle',
          symbolSize: 8,
          itemStyle: {
            color: '#3f5bad',
            borderWidth: 2,
            borderColor: '#fff'
          },
          emphasis: {
            scale: true,
            itemStyle: {
              color: '#ff6b6b',
              borderColor: '#fff',
              borderWidth: 2,
              shadowColor: 'rgba(0, 0, 0, 0.3)',
              shadowBlur: 10
            }
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0,
                color: 'rgba(63,91,173,0.3)'
              }, {
                offset: 1,
                color: 'rgba(63,91,173,0.1)'
              }]
            }
          }
        }]
      }
      
      this.chart.setOption(option)
    },
    toggleStar() {
      this.$emit('toggle-star', this.product);
    }
  },
  beforeUnmount() {
    if (this.chart) {
      this.chart.dispose()
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
  width: 200px;
  height: 200px;
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
  padding-top: 20px;
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
</style> 