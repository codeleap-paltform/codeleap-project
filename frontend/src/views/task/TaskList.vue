<template>
  <div class="page">
    <header class="header">
      <h1>码跃任务管理</h1>
      <div class="user-area"><span>{{ user.username }}</span><el-button @click="logout">退出登录</el-button></div>
    </header>
    <nav class="nav"><router-link to="/home">首页</router-link><router-link to="/task">任务管理</router-link></nav>
    <main class="content">
      <div class="task-heading"><div><h2>我的任务</h2><p>共 {{ tasks.length }} 项</p></div><el-button type="primary" @click="openCreate">新增任务</el-button></div>
      <el-skeleton :loading="loading" :rows="4" animated>
        <el-empty v-if="!tasks.length" description="还没有任务，先创建一个吧" />
        <div v-else class="task-list">
          <el-card v-for="task in tasks" :key="task.id" class="task-card">
            <div class="task-info">
              <h3 :class="{ done: task.status === 1 }">{{ task.title }}</h3>
              <p>{{ task.content || '暂无描述' }}</p>
              <small>{{ formatTime(task.createTime) }}</small>
            </div>
            <div class="actions">
              <el-button :type="task.status === 1 ? 'info' : 'success'" @click="toggleStatus(task)">{{ task.status === 1 ? '设为未完成' : '完成' }}</el-button>
              <el-button type="warning" @click="openEdit(task)">编辑</el-button>
              <el-button type="danger" @click="removeTask(task)">删除</el-button>
            </div>
          </el-card>
        </div>
      </el-skeleton>
    </main>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑任务' : '新增任务'" width="min(500px, 92vw)" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="任务标题" prop="title"><el-input v-model.trim="form.title" maxlength="100" show-word-limit /></el-form-item>
        <el-form-item label="任务内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4" maxlength="1000" show-word-limit /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="saving" @click="saveTask">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()
const user = ref(JSON.parse(sessionStorage.getItem('user') || '{}'))
const tasks = ref([])
const loading = ref(true)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const form = reactive({ title: '', content: '' })
const rules = { title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }] }

onMounted(loadTasks)

async function loadTasks() {
  loading.value = true
  try { tasks.value = await request.get('/api/v1/tasks') }
  catch (error) { ElMessage.error(error.message) }
  finally { loading.value = false }
}
function openCreate() { editingId.value = null; dialogVisible.value = true }
function openEdit(task) { editingId.value = task.id; form.title = task.title; form.content = task.content || ''; dialogVisible.value = true }
function resetForm() { editingId.value = null; form.title = ''; form.content = ''; formRef.value?.clearValidate() }
async function saveTask() {
  try {
    await formRef.value.validate()
    saving.value = true
    if (editingId.value) {
      const current = tasks.value.find(item => item.id === editingId.value)
      await request.put('/api/v1/tasks/' + editingId.value, { ...form, status: current.status })
    } else {
      await request.post('/api/v1/tasks', form)
    }
    ElMessage.success(editingId.value ? '修改成功' : '创建成功')
    dialogVisible.value = false
    await loadTasks()
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  } finally { saving.value = false }
}
async function toggleStatus(task) {
  try {
    await request.put('/api/v1/tasks/' + task.id, { title: task.title, content: task.content, status: task.status === 1 ? 0 : 1 })
    await loadTasks()
  } catch (error) { ElMessage.error(error.message) }
}
async function removeTask(task) {
  try {
    await ElMessageBox.confirm('确定删除“' + task.title + '”吗？', '删除任务', { type: 'warning' })
    await request.delete('/api/v1/tasks/' + task.id)
    ElMessage.success('删除成功')
    await loadTasks()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close' && error instanceof Error) ElMessage.error(error.message)
  }
}
function formatTime(value) { return value ? new Date(value).toLocaleString('zh-CN') : '' }
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
.user-area, .actions { display: flex; align-items: center; gap: 12px; }
.nav { display: flex; gap: 28px; padding: 16px 5%; background: #fff; border-top: 1px solid #eee; }
.nav a { color: #4b5563; text-decoration: none; }
.nav a.router-link-active { color: #409eff; }
.content { max-width: 1000px; margin: auto; padding: 36px 5%; }
.task-heading { display: flex; justify-content: space-between; align-items: center; margin-bottom: 22px; text-align: left; }
.task-heading h2 { margin: 0 0 6px; }
.task-heading p { color: #9ca3af; }
.task-list { display: grid; gap: 14px; }
.task-card :deep(.el-card__body) { display: flex; justify-content: space-between; align-items: center; gap: 20px; }
.task-info { min-width: 0; text-align: left; }
.task-info h3 { margin: 0 0 8px; color: #1f2937; }
.task-info h3.done { color: #9ca3af; text-decoration: line-through; }
.task-info p { margin-bottom: 8px; color: #6b7280; overflow-wrap: anywhere; }
.task-info small { color: #9ca3af; }
@media (max-width: 680px) { .task-card :deep(.el-card__body) { align-items: stretch; flex-direction: column; } .actions { flex-wrap: wrap; } }
</style>
