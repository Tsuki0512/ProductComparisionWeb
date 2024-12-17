import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import router from './router'
import '@fortawesome/fontawesome-free/css/all.css'

const app = createApp(App)

// 配置axios
axios.defaults.baseURL = "http://localhost:80"
// 将axios设置为全局属性
app.config.globalProperties.$axios = axios

app.use(ElementPlus)
app.use(router)
app.mount('#app')