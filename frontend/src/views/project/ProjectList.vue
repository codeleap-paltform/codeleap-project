<template>
  <div class="page">
    <header class="header">
      <h1>码跃任务管理</h1>
      <div class="user-area"><span>{{ user.username }}</span><el-button @click="logout">退出登录</el-button></div>
    </header>
    <nav class="nav">
      <router-link to="/home">首页</router-link>
      <router-link to="/task">任务管理</router-link>
      <router-link to="/project">项目管理</router-link>
    </nav>
    <main class="content">
      <div class="project-heading">
        <div><h2>我的项目</h2><p>共 {{ projects.length }} 个</p></div>
        <el-button type="primary" @click="createVisible = true">创建项目</el-button>
      </div>

      <el-skeleton v-if="loading" :rows="4" animated />
      <div v-else-if="errorMessage" class="error-tip">
        <el-alert :title="errorMessage" type="error" :closable="false" />
        <el-button style="margin-top: 14px" @click="loadProjects">重新加载</el-button>
      </div>
      <el-empty v-else-if="projects.length === 0" description="还没有项目，点击右上角“创建项目”开始吧" />
      <div v-else class="project-list">
        <el-card v-for="project in projects" :key="project.id" class="project-card">
          <div class="project-info">
            <h3>{{ project.name }}</h3>
            <p>{{ project.description || '暂无描述' }}</p>
            <small>{{ formatTime(project.createTime) }}</small>
          </div>
        </el-card>
      </div>
    </main>

    <ProjectCreateDialog v-model="createVisible" @created="loadProjects" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { getProjects } from '../../api/project'
import ProjectCreateDialog from '../../components/project/ProjectCreateDialog.vue'

const router = useRouter()
const user = ref(JSON.parse(sessionStorage.getItem('user') || '{}'))
const projects = ref([])
const loading = ref(true)
const errorMessage = ref('')
const createVisible = ref(false)

onMounted(loadProjects)

async function loadProjects() {
  loading.value = true
  errorMessage.value = ''
  try {
    projects.value = await getProjects()
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '项目加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function formatTime(value) {
  return value ? new Date(value).toLocaleString('zh-CN') : ''
}

async function logout() {
  try { await request.post('/api/v1/users/logout') } catch {}
  sessionStorage.removeItem('user')
  router.replace('/login')
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f7fa; }
.header { height: 64px; padding: 0 5%; display: flex; align-items: center; justify-content: space-between; background: #fff; box-shadow: 0 1px 8px #00000012; }
.header h1 { margin: 0; font-size: 22px; color: #409eff; }
.user-area { display: flex; align-items: center; gap: 12px; }
.nav { display: flex; gap: 28px; padding: 16px 5%; background: #fff; border-top: 1px solid #eee; }
.nav a { color: #4b5563; text-decoration: none; }
.nav a.router-link-active { color: #409eff; }
.content { max-width: 1000px; margin: auto; padding: 36px 5%; }
.project-heading { display: flex; justify-content: space-between; align-items: center; margin-bottom: 22px; text-align: left; }
.project-heading h2 { margin: 0 0 6px; }
.project-heading p { color: #9ca3af; }
.project-list { display: grid; gap: 14px; }
.project-info { text-align: left; }
.project-info h3 { margin: 0 0 8px; color: #1f2937; }
.project-info p { margin-bottom: 8px; color: #6b7280; overflow-wrap: anywhere; }
.project-info small { color: #9ca3af; }
.error-tip { text-align: center; }
</style>
