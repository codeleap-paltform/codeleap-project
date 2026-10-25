import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/login/Register.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/home/Home.vue')
  },
  {
    path: '/task',
    name: 'Task',
    component: () => import('../views/task/TaskList.vue')
  },
  {
    path: '/project',
    name: 'Project',
    component: () => import('../views/project/ProjectList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫：目标页面标记了 requiresAuth 且未登录时，跳转到登录页
router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  if (to.meta.requiresAuth && !userStr) {
    next('/login')
  } else {
    next()
  }
})

export default router