<template>
  <div class="register-container">
    <div class="register-box">
      <h2>CodeLeap 注册</h2>
      <form @submit.prevent="register">
        <div class="form-item">
          <label>用户名</label>
          <input type="text" v-model="user.username" placeholder="请输入用户名" required>
        </div>
        <div class="form-item">
          <label>密码</label>
          <input type="password" v-model="user.password" placeholder="请输入密码" required>
        </div>
        <div class="form-item">
          <label>邮箱</label>
          <input type="email" v-model="user.email" placeholder="请输入邮箱" required>
        </div>
        <button type="submit" class="btn btn-primary btn-block">注册</button>
        <p class="login-link">已有账号？<router-link to="/login">立即登录</router-link></p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()

const user = ref({
  username: '',
  password: '',
  email: '',
})

// 注册方法
const register = async () => {
  // 1. 前端非空校验
  if (!user.value.username || !user.value.password || !user.value.email) {
    ElMessage.error('请填写完整的注册信息')
    return
  }

  try {
    // 2. 调用后端注册接口，传入用户名、密码、邮箱
    const res = await request.post('/user/register', {
      username: user.value.username,
      password: user.value.password,
      email: user.value.email
    })

    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      // 注册成功自动跳转到登录页
      router.push('/login')
    } else {
      ElMessage.error(res.msg || '注册失败，用户名可能已存在')
    }
  } catch (err) {
    ElMessage.error('网络异常，请确认后端服务已启动')
    console.error('注册接口报错：', err)
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.register-box {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.register-box h2 {
  text-align: center;
  margin-bottom: 30;
  color: #333;
}

.btn-block {
  width: 100%;
  margin-top: 10px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
}
</style>