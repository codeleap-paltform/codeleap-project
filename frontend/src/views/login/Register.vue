<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="always">
      <h1>创建码跃账号</h1>
      <p class="subtitle">从一个清晰的任务开始</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" maxlength="50" autocomplete="username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password autocomplete="new-password" placeholder="至少6位密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password autocomplete="new-password" placeholder="请再次输入密码" @keyup.enter="submit" />
        </el-form-item>
        <el-button class="submit-button" type="primary" :loading="loading" @click="submit">注册</el-button>
      </el-form>
      <p class="switch-link">已有账号？<router-link to="/login">返回登录</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '' })
const validateConfirm = (_rule, value, callback) => {
  value === form.password ? callback() : callback(new Error('两次输入的密码不一致'))
}
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { max: 50, message: '用户名不能超过50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 72, message: '密码长度必须为6到72位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

async function submit() {
  if (loading.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    await request.post('/api/v1/users/register', { username: form.username, password: form.password })
    ElMessage.success('注册成功，请登录')
    router.replace('/login')
  } catch (error) {
    if (error instanceof Error) ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page { min-height: 100vh; display: grid; place-items: center; padding: 24px; box-sizing: border-box; background: linear-gradient(135deg, #ecf5ff, #f7f2ff); }
.auth-card { width: min(420px, 100%); border: 0; border-radius: 16px; }
h1 { margin: 4px 0 8px; font-size: 28px; color: #1f2937; }
.subtitle { margin-bottom: 28px; color: #6b7280; }
.submit-button { width: 100%; height: 42px; margin-top: 8px; }
.switch-link { margin-top: 22px; text-align: center; color: #6b7280; }
.switch-link a { color: #409eff; text-decoration: none; }
</style>
