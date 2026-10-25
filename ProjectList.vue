<template>
  <div class="project-container">
    <!-- 头部导航（和任务页保持一致） -->
    <div class="header">
      <h1>CodeLeap 任务管理系统</h1>
      <div class="user-info">
        <span>{{ user.username }}</span>
        <button class="btn btn-primary" @click="logout">退出登录</button>
      </div>
    </div>

    <!-- 导航栏 -->
    <div class="nav">
      <router-link to="/home" class="nav-item">首页</router-link>
      <router-link to="/task" class="nav-item">任务管理</router-link>
      <router-link to="/project" class="nav-item active">项目管理</router-link>
    </div>

    <!-- 内容区 -->
    <div class="content">
      <div class="project-header">
        <h2>我的项目（{{ projects.length }}）</h2>
        <button class="btn btn-primary" @click="createVisible = true">创建项目</button>
      </div>

      <!-- 状态1：加载中 -->
      <div v-if="loading" class="state-tip">
        <p>项目加载中...</p>
      </div>

      <!-- 状态2：请求失败（有错误信息且不是空列表） -->
      <div v-else-if="errorMessage" class="state-tip error">
        <p>{{ errorMessage }}</p>
        <button class="btn btn-primary" @click="loadProjects">重新加载</button>
      </div>

      <!-- 状态3：空列表（请求成功但数组为空） -->
      <div v-else-if="projects.length === 0" class="state-tip">
        <p>还没有项目，点击右上角"创建项目"开始吧</p>
      </div>

      <!-- 状态4：成功列表 -->
      <div v-else class="project-list">
        <div class="project-item" v-for="project in projects" :key="project.id">
          <h3>{{ project.name }}</h3>
          <p>{{ project.description || '暂无描述' }}</p>
          <span class="project-time">{{ project.createTime }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const user = ref({})
// 项目数组：从 [] 开始，请求成功后由后端数据填充
const projects = ref([])
// 是否正在等待请求：从 true 开始，页面打开先显示加载中
const loading = ref(true)
// 失败信息：空字符串表示没有错误
const errorMessage = ref('')
// 创建对话框开关：任务5接入汪昱东的 ProjectCreateDialog 后使用
const createVisible = ref(false)

// 【临时模拟】模拟后端返回项目列表
// 任务4：删除本函数，改用汪昱东的 getProjects()（import { getProjects } from '../../api/project'）
// 想查看不同状态：想看成列表，取消下面数组注释；想看成失败，把 resolve 改成 reject
const mockGetProjects = () => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      resolve([
        // { id: 1, name: '示例项目一', description: '这是项目一的描述', createTime: '2026-10-25 10:00:00' },
        // { id: 2, name: '示例项目二', description: '', createTime: '2026-10-25 11:30:00' }
      ])
    }, 500)
  })
}

// 加载项目：严格按「开始loading → 清旧错误 → GET → 成功存数组/失败存错误 → finally结束loading」
const loadProjects = async () => {
  loading.value = true       // 1. 开始 loading
  errorMessage.value = ''    // 2. 清掉上次的错误
  try {
    const res = await mockGetProjects()  // 3. 发起请求（任务4替换为 getProjects()）
    projects.value = res     // 4a. 成功：数组存进 projects
  } catch (e) {
    errorMessage.value = '项目加载失败，请稍后重试'  // 4b. 失败：保存错误信息
  } finally {
    loading.value = false    // 5. finally：无论成功还是失败都会执行，loading 一定结束
  }
}

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    user.value = JSON.parse(userStr)
    loadProjects()
  } else {
    router.push('/login')
  }
})

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.project-container {
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

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.project-header h2 {
  color: #333;
}

.state-tip {
  padding: 60px 20px;
  text-align: center;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  color: #999;
}

.state-tip.error {
  color: #f56c6c;
}

.state-tip p {
  margin-bottom: 15px;
}

.project-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.project-item {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.project-item h3 {
  margin-bottom: 10px;
  color: #333;
}

.project-item p {
  margin-bottom: 10px;
  color: #666;
}

.project-time {
  font-size: 12px;
  color: #999;
}
</style>
