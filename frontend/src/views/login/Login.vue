<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="always">
      <h1>欢迎回到码跃</h1>
      <p class="subtitle">登录后继续管理你的学习任务</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" autocomplete="username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" autocomplete="current-password" show-password placeholder="请输入密码" @keyup.enter="submit" />
        </el-form-item>
        <el-button class="submit-button" type="primary" :loading="loading" @click="submit">登录</el-button>
      </el-form>
      <p class="switch-link">没有账号？<router-link to="/register">立即注册</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 72, message: '密码长度必须为6到72位', trigger: 'blur' }
  ]
}

async function submit() {
  if (loading.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    const user = await request.post('/api/v1/users/login', form)
    sessionStorage.setItem('user', JSON.stringify(user))
    ElMessage.success('登录成功')
    router.replace(typeof route.query.redirect === 'string' ? route.query.redirect : '/home')
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
