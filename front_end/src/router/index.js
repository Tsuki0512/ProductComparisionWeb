import { createRouter, createWebHistory } from 'vue-router'
import RegisterView from '../views/RegisterView.vue'
import Home from '../views/Home.vue'
import Profile from '../views/Profile.vue'
import ResetPassword from '../views/ResetPassword.vue'

// 定义路由组件
const routes = [
  {
    path: '/register',
    name: 'Register',
    component: RegisterView
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    // 可以添加路由守卫，防止未登录用户直接访问
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: ResetPassword
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  // 这里可以根据你的登录状态管理逻辑进行修改
  const isAuthenticated = localStorage.getItem('isAuthenticated')
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    // 如果需要认证但未登录，返回登录页
    next('/')
  } else {
    next()
  }
})

export default router
