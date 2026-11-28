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

      <!-- 2. 详情请求进行中 -->
      <el-skeleton v-else-if="loading" :rows="5" animated />

      <!-- 3. 详情请求失败：显示失败，不能当成“暂无描述”继续展示空详情 -->
      <el-result v-else-if="errorMessage" icon="error" title="项目加载失败" :sub-title="errorMessage">
        <template #extra>
          <el-button @click="goProjectList">返回项目列表</el-button>
          <el-button type="primary" @click="loadProject">重试</el-button>
        </template>
      </el-result>

      <!-- 4. 正常详情：项目信息 + 成员区 -->
      <template v-else-if="project">
        <div class="detail-heading">
          <div>
            <h2>{{ project.name }}</h2>
            <p>项目编号 {{ project.id }} · 创建于 {{ formatTime(project.createTime) }} · {{ roleText }}</p>
          </div>
          <!-- 操作入口只给创建者：myRole 由后端返回，前端隐藏只是提示，真正拦截靠后端 -->
          <div class="detail-actions">
            <el-button
              v-if="isOwner"
              type="primary"
              plain
              @click="memberAddVisible = true"
            >添加成员</el-button>
            <el-button v-if="isOwner" type="primary" @click="openEdit">编辑项目</el-button>
            <!-- 普通成员看到只读标注，明确知道为什么没有按钮 -->
            <el-tag v-else type="info" effect="plain">只读（普通成员）</el-tag>
          </div>
        </div>

        <el-card class="detail-card">
          <p class="detail-label">项目描述</p>
          <p class="detail-description">{{ project.description || '暂无描述' }}</p>
        </el-card>

        <!-- 成员区：加载、失败、空、正常四种状态独立管理，
             成员请求失败不影响上面已经加载好的项目详情 -->
        <el-card class="detail-card member-card">
          <div class="member-head">
            <p class="detail-label">项目成员</p>
            <!-- 只重试成员请求，不重新拉详情 -->
            <el-button v-if="memberError" size="small" type="primary" plain @click="loadMembers">重试</el-button>
          </div>

          <el-skeleton v-if="memberLoading" :rows="2" animated />
          <el-alert v-else-if="memberError" :title="memberError" type="error" :closable="false" />
          <el-empty
            v-else-if="members.length === 0"
            description="暂时没有成员数据"
            :image-size="60"
          />
          <ul v-else class="member-list">
            <!-- 后端保证创建者排在第一项；用 userId 作 key，不用用户名 -->
            <li v-for="member in members" :key="member.userId" class="member-item">
              <span class="member-name">{{ member.username }}</span>
              <el-tag :type="member.role === 'OWNER' ? 'warning' : 'info'" size="small" effect="plain">
                {{ member.role === 'OWNER' ? '创建者' : '成员' }}
              </el-tag>
            </li>
          </ul>
        </el-card>
      </template>

      <!-- 编辑弹窗：沿用上一轮的行为，成功后重新查详情 -->
      <ProjectEditDialog v-model="editVisible" :project="project" @updated="handleUpdated" />

      <!-- 添加成员弹窗：只有拿到有效详情且我是创建者时才存在；
           added 事件触发后重新 GET 成员，不在页面里猜一个成员对象塞进列表 -->
      <ProjectMemberAddDialog
        v-if="project && isOwner"
        v-model="memberAddVisible"
        :project-id="project.id"
        @added="loadMembers"
      />
    </main>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../../utils/request'
import { getProject, getProjectMembers } from '../../api/project'
import ProjectEditDialog from '../../components/project/ProjectEditDialog.vue'
import ProjectMemberAddDialog from '../../components/project/ProjectMemberAddDialog.vue'

const route = useRoute()
const router = useRouter()
const user = ref(JSON.parse(sessionStorage.getItem('user') || '{}'))

// ---------- 项目详情的状态 ----------
const project = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const editVisible = ref(false)

// ---------- 成员区的状态（和详情分开，互不冲掉） ----------
const members = ref([])
const memberLoading = ref(false)
const memberError = ref('')
const memberAddVisible = ref(false)

// 编号只从地址取，不靠列表传对象；直接刷新或复制链接打开也能拿到
const projectId = computed(() => String(route.params.id ?? ''))
// 正整数才发请求，undefined / 0 / -1 / abc 一律不发
const isIdValid = computed(() => /^[1-9]\d*$/.test(projectId.value))

// 我的角色由后端在详情里返回（myRole），前端只负责按它显示入口
const isOwner = computed(() => project.value?.myRole === 'OWNER')
const roleText = computed(() => (isOwner.value ? '我是创建者' : '我是成员'))

// ---------- 请求失效序号 ----------
// 详情和成员各用一个序号：每发一次就加一，响应回来时序号对不上就丢弃。
// 这样“先发出去、后返回”的旧请求不能覆盖新项目的内容。
let detailSeq = 0
let memberSeq = 0

async function loadProject() {
  // 每次调用先作废在途旧请求，切到非法编号也能作废
  const seq = ++detailSeq

  if (!isIdValid.value) {
    project.value = null
    errorMessage.value = ''
    loading.value = false
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    // getProject 返回的已经是项目对象（含 myRole），request.js 取过 data，不要再 .data.data
    const data = await getProject(projectId.value)
    if (seq !== detailSeq) return // 已经切到别的项目，这次结果作废
    project.value = data
  } catch (error) {
    if (seq !== detailSeq) return
    project.value = null
    const message = error instanceof Error ? error.message : ''
    // “Network Error”这种英文提示不直观，翻译成用户看得懂的话
    errorMessage.value = message === 'Network Error' ? '无法连接服务器，请检查网络或稍后重试' : (message || '项目加载失败，请稍后重试')
  } finally {
    // 旧请求不许把新请求的 loading 关掉，否则新项目会闪一下空白
    if (seq === detailSeq) loading.value = false
  }
}

async function loadMembers() {
  // 没有有效详情就不查成员（详情被拒绝时页面里根本不该有成员数据）
  if (!project.value) return

  const seq = ++memberSeq
  memberLoading.value = true
  memberError.value = ''

  try {
    // 后端返回 [{userId, username, role}]，创建者排第一项
    const data = await getProjectMembers(project.value.id)
    if (seq !== memberSeq) return // 已切到别的项目，这次结果作废
    members.value = data
  } catch (error) {
    if (seq !== memberSeq) return
    // 失败只清成员列表并提示，不动 project.value——详情不能被成员请求冲掉
    members.value = []
    const message = error instanceof Error ? error.message : ''
    memberError.value = message === 'Network Error' ? '无法连接服务器，请检查网络或稍后重试' : (message || '成员加载失败')
  } finally {
    if (seq === memberSeq) memberLoading.value = false
  }
}

// 从 /project/7 切到 /project/8 时组件不会重建，所以要盯着 id 变化重新加载
watch(
  projectId,
  () => {
    project.value = null // 先清空，避免页面留着上一个项目的内容
    members.value = [] // 成员也一起清，旧成员不能挂在新项目下面
    memberError.value = ''
    // 切换瞬间就作废在途的旧成员请求（review 意见）：
    // 不能等新项目详情回来才作废，否则旧项目的迟到成员响应会先写进新页面
    memberSeq++
    memberLoading.value = false
    editVisible.value = false // 编辑弹窗关掉，避免把 A 的输入带到 B
    memberAddVisible.value = false // 添加成员弹窗也关掉
    loadProject()
  },
  { immediate: true }
)

// 详情加载成功后紧接着查一次成员列表
// 放在 watch/project 赋值之后集中触发，保证成员请求永远跟着“当前项目”走
watch(project, value => {
  if (value) loadMembers()
})

function openEdit() {
  if (!project.value) return // 没拿到有效详情不允许打开
  editVisible.value = true
}

function handleUpdated() {
  // 弹窗说服务器已保存成功，但页面内容以重新查询的结果为准
  loadProject()
}

function goProjectList() {
  // 列表页重新挂载时会 onMounted 重新取数据，返回后能看到新名称和
