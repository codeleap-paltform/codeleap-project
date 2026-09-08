<template>
  <div class="page">
    <header class="header">
      <h1>码跃任务管理</h1>
      <div class="user-area"><span>{{ user.username }}</span><el-button @click="logout">退出登录</el-button></div>
    </header>
    <nav class="nav"><router-link to="/home">首页</router-link><router-link to="/task">任务管理</router-link></nav>
    <main class="content">
      <el-card class="welcome-card">
        <h2>你好，{{ user.username || '同学' }}</h2>
        <p>把目标拆成清晰的小任务，然后一步一步完成。</p>
        <el-button type="primary" @click="$router.push('/task')">查看我的任务</el-button>
      </el-card>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()
const user = ref(JSON.parse(sessionStorage.getItem('user') || '{}'))

async function logout() {
  try { await request.post('/api/v1/users/logout') } catch {}
  sessionStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.replace('/login')
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f7fa; }
.header { height: 64px; padding: 0 5%; display: flex; align-items: center; justify-content: space-between; background: #fff; box-shadow: 0 1px 8px #00000012; }
.header h1 { margin: 0; font-size: 22px; color: #409eff; }
.user-area { display: flex; align-items: center; gap: 16px; }
.nav { display: flex; gap: 28px; padding: 16px 5%; background: #fff; border-top: 1px solid #eee; }
.nav a { color: #4b5563; text-decoration: none; }
.nav a.router-link-active { color: #409eff; }
.content { padding: 40px 5%; }
.welcome-card { max-width: 760px; margin: auto; text-align: left; }
.welcome-card p { margin: 12px 0 24px; color: #6b7280; }
</style>
