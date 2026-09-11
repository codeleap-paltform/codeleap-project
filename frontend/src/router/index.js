import { createRouter, createWebHistory } from 'vue-router'
import request from '../utils/request'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', name: 'Login', component: () => import('../views/login/Login.vue'), meta: { guestOnly: true } },
  { path: '/register', name: 'Register', component: () => import('../views/login/Register.vue'), meta: { guestOnly: true } },
  { path: '/home', name: 'Home', component: () => import('../views/home/Home.vue'), meta: { requiresAuth: true } },
  { path: '/task', name: 'Task', component: () => import('../views/task/TaskList.vue'), meta: { requiresAuth: true } },
  { path: '/404', name: 'NotFound', component: () => import('../views/404.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/404' }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach(async to => {
  if (!to.meta.requiresAuth && !to.meta.guestOnly) return true
  try {
    const user = await request.get('/api/v1/users/me')
    sessionStorage.setItem('user', JSON.stringify(user))
    return to.meta.guestOnly ? '/home' : true
  } catch {
    sessionStorage.removeItem('user')
    return to.meta.requiresAuth ? { path: '/login', query: { redirect: to.fullPath } } : true
  }
})

export default router
