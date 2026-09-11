<template>
  <div class="page">
    <header class="header">
      <h1>码跃任务管理</h1>
      <div class="user-area">
        <span>{{ user.username }}</span>
        <el-button @click="passwordVisible = true">修改密码</el-button>
        <el-button @click="logout">退出登录</el-button>
      </div>
    </header>
    <nav class="nav"><router-link to="/home">首页</router-link><router-link to="/task">任务管理</router-link></nav>
    <main class="content">
      <el-card class="welcome-card">
        <h2>你好，{{ user.username || '同学' }}</h2>
        <p>把目标拆成清晰的小任务，然后一步一步完成。</p>
        <el-button type="primary" @click="$router.push('/task')">查看我的任务</el-button>
      </el-card>
    </main>

    <el-dialog v-model="passwordVisible" title="修改密码" width="min(440px, 92vw)" @closed="resetPasswordForm">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top">
        <el-form-item label="当前密码" prop="oldPassword"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码" prop="newPassword"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword"><el-input v-model="passwordForm.confirmPassword" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer><el-button @click="passwordVisible = false">取消</el-button><el-button type="primary" :loading="changing" @click="changePassword">确认修改</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()
const user = ref(JSON.parse(sessionStorage.getItem('user') || '{}'))
const passwordVisible = ref(false)
const changing = ref(false)
const passwordFormRef = ref()
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const validateConfirm = (_rule, value, callback) => value === passwordForm.newPassword ? callback() : callback(new Error('两次输入的新密码不一致'))
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 72, message: '新密码长度必须为6到72位', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validateConfirm, trigger: 'blur' }]
}

function resetPasswordForm() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}
async function changePassword() {
  try {
    await passwordFormRef.value.validate()
    changing.value = true
    await request.put('/api/v1/users/password', {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    passwordVisible.value = false
    sessionStorage.removeItem('user')
    ElMessage.success('密码修改成功，请重新登录')
    router.replace('/login')
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  } finally { changing.value = false }
}
async function logout() {
  try { await request.post('/api/v1/users/logout') } catch {}
  sessionStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.replace('/login')
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f7fa; }
.header { min-height: 64px; padding: 8px 5%; display: flex; align-items: center; justify-content: space-between; gap: 16px; background: #fff; box-shadow: 0 1px 8px #00000012; }
.header h1 { margin: 0; font-size: 22px; color: #409eff; }
.user-area { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; justify-content: flex-end; }
.nav { display: flex; gap: 28px; padding: 16px 5%; background: #fff; border-top: 1px solid #eee; }
.nav a { color: #4b5563; text-decoration: none; }
.nav a.router-link-active { color: #409eff; }
.content { padding: 40px 5%; }
.welcome-card { max-width: 760px; margin: auto; text-align: left; }
.welcome-card p { margin: 12px 0 24px; color: #6b7280; }
</style>
