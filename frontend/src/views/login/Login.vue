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
import { useRouter } from 'vue-router'
import request from '../../utils/request' // 导入我们封装的Axios

const router = useRouter() // 获取路由实例，用来跳转页面

// 定义用户对象，用来绑定表单输入
const user = ref({
  username: '',
  password: ''
})

// 登录方法
const login = async () => {
  // 向后端发送POST请求，传递用户名和密码
  const res = await request.post('/user/login', user.value)
  
  if (res.code === 200) {
    // 登录成功：把后端返回的用户信息保存到浏览器本地存储
    localStorage.setItem('user', JSON.stringify(res.data))
    // 跳转到首页
    alert('登录成功')
    router.push('/home')
  } else {
    // 登录失败：弹出后端返回的错误信息
    alert(res.msg)
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