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
      <div class="detail-toolbar">
        <el-button @click="goProjectList">← 返回项目列表</el-button>
      </div>

      <!-- 1. 地址里的编号不合法：不发请求，直接说明 -->
      <el-result
        v-if="!isIdValid"
        icon="warning"
        title="项目编号无效"
        sub-title="地址中的项目编号必须是正整数，例如 /project/7"
      >
        <template #extra>
          <el-button type="primary" @click="goProjectList">返回项目列表</el-button>
        </template>
      </el-result>

      <!-- 2. 请求进行中 -->
      <el-skeleton v-else-if="loading" :rows="5" animated />

      <!-- 3. 请求失败：显示失败，不能当成“暂无描述”继续展示空详情 -->
      <el-result v-else-if="errorMessage" icon="error" title="项目加载失败" :sub-title="errorMessage">
        <template #extra>
          <el-button @click="goProjectList">返回项目列表</el-button>
          <el-button type="primary" @click="loadProject">重试</el-button>
        </template>
      </el-result>

      <!-- 4. 正常详情 -->
      <template v-else-if="project">
        <div class="detail-heading">
          <div>
            <h2>{{ project.name }}</h2>
            <p>项目编号 {{ project.id }} · 创建于 {{ formatTime(project.createTime) }}</p>
          </div>
          <el-button type="primary" @click="openEdit">编辑项目</el-button>
        </div>

        <el-card class="detail-card">
          <p class="detail-label">项目描述</p>
          <p class="detail-description">{{ project.description || '暂无描述' }}</p>
        </el-card>
      </template>

      <ProjectEditDialog v-model="editVisible" :project="project" @updated="handleUpdated" />
    </main>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../../utils/request'
import { getProject } from '../../api/project'
import ProjectEditDialog from '../../components/project/ProjectEditDialog.vue'

const route = useRoute()
const router = useRouter()
const user = ref(JSON.parse(sessionStorage.getItem('user') || '{}'))

const project = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const editVisible = ref(false)

// 编号只从地址取，不靠列表传对象；直接刷新或复制链接打开也能拿到
const projectId = computed(() => String(route.params.id ?? ''))
// 正整数才发请求，undefined / 0 / -1 / abc 一律不发
const isIdValid = computed(() => /^[1-9]\d*$/.test(projectId.value))

// 每发一次请求就加一，用来丢掉“先发出去、后返回”的旧响应
let latestSeq = 0

async function loadProject() {
  if (!isIdValid.value) {
    project.value = null
    errorMessage.value = ''
    loading.value = false
    return
  }

  const seq = ++latestSeq
  loading.value = true
  errorMessage.value = ''

  try {
    // getProject 返回的已经是项目对象，request.js 取过 data，这里不要再 .data.data
    const data = await getProject(projectId.value)
    if (seq !== latestSeq) return // 已经切到别的项目，这次结果作废
    project.value = data
  } catch (error) {
    if (seq !== latestSeq) return
    project.value = null
    const message = error instanceof Error ? error.message : ''
    // “Network Error”这种英文提示不直观，翻译成用户看得懂的话
    errorMessage.value = message === 'Network Error' ? '无法连接服务器，请检查网络或稍后重试' : (message || '项目加载失败，请稍后重试')
  } finally {
    // 旧请求不许把新请求的 loading 关掉，否则新项目会闪一下空白
    if (seq === latestSeq) loading.value = false
  }
}

// 从 /project/7 切到 /project/8 时组件不会重建，所以要盯着 id 变化重新加载
watch(
  projectId,
  () => {
    project.value = null // 先清空，避免页面留着上一个项目的内容
    editVisible.value = false // 弹窗也关掉，避免把 A 的输入带到 B
    loadProject()
  },
  { immediate: true }
)

function openEdit() {
  if (!project.value) return // 没拿到有效详情不允许打开
  editVisible.value = true
}

function handleUpdated() {
  // 弹窗说服务器已保存成功，但页面内容以重新查询的结果为准
  loadProject()
}

function goProjectList() {
  // 列表页重新挂载时会 onMounted 重新取数据，返回后能看到新名称和描述
  router.push('/project')
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
.content { max-width: 1000px; margin: auto; padding: 24px 5% 36px; }
.detail-toolbar { margin-bottom: 18px; text-align: left; }
.detail-heading { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; margin-bottom: 18px; text-align: left; }
.detail-heading h2 { margin: 0 0 8px; color: #1f2937; overflow-wrap: anywhere; }
.detail-heading p { margin: 0; color: #9ca3af; }
.detail-card { text-align: left; }
.detail-label { margin: 0 0 10px; color: #9ca3af; font-size: 13px; }
.detail-description { margin: 0; color: #4b5563; line-height: 1.75; white-space: pre-wrap; overflow-wrap: anywhere; }
</style>
