import { createRouter, createWebHistory } from 'vue-router'

<<<<<<< HEAD
const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: '登录页',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/home',
    name: '首页',
=======
// 路由规则：URL地址与Vue组件的映射关系
const routes = [
  {
    path: '/', // 网站根路径
    redirect: '/login' // 自动跳转到登录页
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue') // 路由懒加载
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/login/Register.vue')
  },
  {
    path: '/home',
    name: 'Home',
>>>>>>> 09dc229a25f3a480d8922bf9e3b668917763e80e
    component: () => import('../views/home/Home.vue')
  },
  {
    path: '/task',
<<<<<<< HEAD
    name: '任务管理',
    component: () => import('../views/task/TaskList.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
=======
    name: 'Task',
    component: () => import('../views/task/TaskList.vue')
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue')
  },
  {
    path: '/:pathMatch(.*)*', // 通配符，匹配所有不存在的路径
    redirect: '/404'
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(), // 使用HTML5历史模式，URL更美观
  routes
})

export default router
>>>>>>> 09dc229a25f3a480d8922bf9e3b668917763e80e
