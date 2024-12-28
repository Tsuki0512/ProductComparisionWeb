import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import router from './router'
import '@fortawesome/fontawesome-free/css/all.css'

const app = createApp(App)

// 配置 axios
const axiosInstance = axios.create({
  baseURL: 'http://localhost:80',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 5000
})

// 添加请求拦截器
axiosInstance.interceptors.request.use(
  config => {
    console.log('Request details:', {
      url: config.url,
      method: config.method,
      data: config.data,
      headers: config.headers,
      baseURL: config.baseURL
    })
    return config
  },
  error => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

// 添加响应拦截器
axiosInstance.interceptors.response.use(
  response => {
    console.log('Response:', response)
    return response
  },
  error => {
    console.error('Response Error:', {
      message: error.message,
      config: error.config,
      response: error.response
    })
    return Promise.reject(error)
  }
)

app.config.globalProperties.$axios = axiosInstance
app.use(ElementPlus)
app.use(router)
app.mount('#app')