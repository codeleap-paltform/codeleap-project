<template>
  <div class="home-container">
    <!-- 头部导航 -->
    <div class="header">
      <h1>CodeLeap 任务管理系统</h1>
      <div class="user-info">
        <span>{{ user.username }}</span>
        <button class="btn btn-primary" @click="logout">退出登录</button>
      </div>
    </div>
    
    <!-- 导航栏 -->
    <div class="nav">
      <router-link to="/home" class="nav-item active">首页</router-link>
      <router-link to="/task" class="nav-item">任务管理</router-link>
    </div>
    
    <!-- 内容区 -->
    <div class="content">
      <div class="card">
        <h3>欢迎使用CodeLeap任务管理系统</h3>
        <p>这是一个简单的任务管理系统，支持用户注册、登录、任务的增删改查功能。</p>
        <p>点击上方的"任务管理"按钮，开始管理你的任务吧！</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 定义用户对象
const user = ref({})

// 页面加载时执行
onMounted(() => {
  // 从本地存储中获取用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    user.value = JSON.parse(userStr)
  } else {
    // 如果没有登录，跳转到登录页
    router.push('/login')
  }
})

// 退出登录方法
const logout = () => {
  // 清除本地存储中的用户信息
  localStorage.removeItem('user')
  // 跳转到登录页
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 24px;
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav {
  display: flex;
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
}

.nav-item {
  padding: 15px 30px;
  text-decoration: none;
  color: #333;
  font-size: 16px;
}

.nav-item.active {
  color: #409eff;
  border-bottom: 2px solid #409eff;
}

.content {
  padding: 20px;
}

.card {
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card h3 {
  margin-bottom: 20px;
  color: #333;
}

.card p {
  margin-bottom: 10px;
  color: #666;
  line-height: 1.6;
}
</style>