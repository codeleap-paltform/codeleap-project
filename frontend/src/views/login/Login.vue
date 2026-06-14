<template>
  <div class="login-container">
    <div class="login-box">
      <h2>CodeLeap 登录</h2>
      <form @submit.prevent="login">
        <div class="form-item">
          <label>用户名</label>
          <input type="text" v-model="user.username" placeholder="请输入用户名" required>
        </div>
        <div class="form-item">
          <label>密码</label>
          <input type="password" v-model="user.password" placeholder="请输入密码" required>
        </div>
        <button type="submit" class="btn btn-primary btn-block">登录</button>
        <p class="register-link">没有账号？<router-link to="/register">立即注册</router-link></p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()

// 定义用户对象，用来绑定表单输入
const user = ref({
  username: '',
  password: ''
})

// 登录方法
const login = async () => {
  // 前端简单校验：用户名密码不能为空
  if (!user.value.username || !user.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }

  try {
    // 调用后端真实登录接口
    const res = await request.post('/user/login', user.value)
    
    // 判断接口返回结果
    if (res.code === 200) {
      // 登录成功：把用户信息存到浏览器本地，后面页面会用到
      localStorage.setItem('user', JSON.stringify(res.data))
      ElMessage.success('登录成功')
      // 跳转到首页
      router.push('/home')
    } else {
      // 后端返回业务错误（比如密码错、用户不存在）
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (err) {
    // 网络异常、后端没启动等报错
    ElMessage.error('网络异常，请确认后端服务已启动')
    console.error('登录接口报错：', err)
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-box {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.btn-block {
  width: 100%;
  margin-top: 10px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
}
</style>