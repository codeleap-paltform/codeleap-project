<template>
  <div class="task-container">
    <!-- 头部导航（和首页一样） -->
    <div class="header">
      <h1>CodeLeap 任务管理系统</h1>
      <div class="user-info">
        <span>{{ user.username }}</span>
        <button class="btn btn-primary" @click="logout">退出登录</button>
      </div>
    </div>
    
    <!-- 导航栏（和首页一样） -->
    <div class="nav">
      <router-link to="/home" class="nav-item">首页</router-link>
      <router-link to="/task" class="nav-item active">任务管理</router-link>
    </div>
    
    <!-- 内容区 -->
    <div class="content">
      <div class="task-header">
        <h2>我的任务</h2>
        <button class="btn btn-primary" @click="showAddDialog = true">新增任务</button>
      </div>
      
      <!-- 任务列表 -->
      <div class="task-list">
        <div class="task-item" v-for="task in taskList" :key="task.id">
          <div class="task-info">
            <h3 :class="{ 'task-done': task.status === 1 }">{{ task.title }}</h3>
            <p>{{ task.content }}</p>
            <span class="task-time">{{ task.createTime }}</span>
          </div>
          <div class="task-actions">
            <button class="btn btn-success" @click="completeTask(task)" v-if="task.status === 0">完成</button>
            <button class="btn btn-warning" @click="editTask(task)">编辑</button>
            <button class="btn btn-danger" @click="deleteTask(task.id)">删除</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 新增/编辑任务弹窗（用ElementPlus的弹窗组件） -->
    <el-dialog v-model="showAddDialog" title="新增任务" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="任务标题">
          <el-input v-model="form.title" placeholder="请输入任务标题"></el-input>
        </el-form-item>
        <el-form-item label="任务内容">
          <el-input v-model="form.content" type="textarea" rows="3" placeholder="请输入任务内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()

const user = ref({})
// 任务列表，先用模拟数据
const taskList = ref([
  { id: 1, title: '学习Vue3', content: '学习Vue3的基本语法', status: 0, createTime: '2024-05-20' },
  { id: 2, title: '写任务管理页', content: '完成任务管理页的开发', status: 1, createTime: '2024-05-19' }
])
// 控制弹窗显示/隐藏
const showAddDialog = ref(false)
// 表单数据
const form = ref({
  title: '',
  content: ''
})

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    user.value = JSON.parse(userStr)
    // 等后端接口写完后，取消下面的注释，获取真实的任务列表
    // getTaskList()
  } else {
    router.push('/login')
  }
})

// 获取任务列表
const getTaskList = async () => {
  // const res = await request.get(`/task/user/${user.value.id}`)
  // if (res.code === 200) {
  //   taskList.value = res.data
  // }
}

// 完成任务
const completeTask = (task) => {
  task.status = 1
  // 等后端接口写完后，取消下面的注释
  // await request.put('/task', task)
  ElMessage.success('任务已完成')
}

// 编辑任务
const editTask = (task) => {
  form.value.title = task.title
  form.value.content = task.content
  showAddDialog.value = true
  // 这里可以加一个标记，区分是新增还是编辑
}

// 删除任务
const deleteTask = (id) => {
  taskList.value = taskList.value.filter(item => item.id !== id)
  // 等后端接口写完后，取消下面的注释
  // await request.delete(`/task/${id}`)
  ElMessage.success('删除成功')
}

// 提交表单
const submitForm = () => {
  if (!form.value.title) {
    ElMessage.error('请输入任务标题')
    return
  }
  // 新增任务
  const newTask = {
    id: Date.now(), // 用时间戳作为临时id
    title: form.value.title,
    content: form.value.content,
    status: 0,
    createTime: new Date().toLocaleDateString()
  }
  taskList.value.unshift(newTask)
  // 等后端接口写完后，取消下面的注释
  // const res = await request.post('/task', { ...form.value, userId: user.value.id })
  // if (res.code === 200) {
  //   ElMessage.success('新增成功')
  //   getTaskList()
  // }
  showAddDialog.value = false
  form.value = { title: '', content: '' }
}

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.task-container {
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

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.task-header h2 {
  color: #333;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.task-info h3 {
  margin-bottom: 10px;
  color: #333;
}

.task-info .task-done {
  text-decoration: line-through;
  color: #999;
}

.task-info p {
  margin-bottom: 10px;
  color: #666;
}

.task-time {
  font-size: 12px;
  color: #999;
}

.task-actions {
  display: flex;
  gap: 10px;
}

.btn-success {
  background-color: #67c23a;
  color: white;
}

.btn-success:hover {
  background-color: #85ce61;
}

.btn-warning {
  background-color: #e6a23c;
  color: white;
}

.btn-warning:hover {
  background-color: #ebb563;
}

.btn-danger {
  background-color: #f56c6c;
  color: white;
}

.btn-danger:hover {
  background-color: #f78989;
}
</style>