<template>
  <div class="home-container">
    <h1>欢迎来到 CodeLeap 平台</h1>
    <p class="welcome-text" v-if="user.username">你好，{{ user.username }}</p>
    <el-button type="danger" @click="logout">退出登录</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
// 初始化空对象，保证模板初次渲染不会报错
const user = ref({})

onMounted(() => {
  const userInfo = localStorage.getItem('user')
  if (userInfo) {
    user.value = JSON.parse(userInfo)
  } else {
    ElMessage.warning('请先登录')
    router.push('/login')
  }
})

const logout = () => {
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  width: 400px;
  margin: 100px auto;
  text-align: center;
}
.welcome-text {
  font-size: 18px;
  margin: 20px 0;
}
</style>