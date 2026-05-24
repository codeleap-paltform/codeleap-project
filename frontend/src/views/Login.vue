<template>
  <div class="login">
    <el-card shadow="hover" style="width:400px;margin:100px auto;padding:30px">
      <h2 style="text-align:center;margin-bottom:20px">用户登录</h2>
      <el-input v-model="username" placeholder="请输入账号" style="margin-bottom:15px"></el-input>
      <el-input v-model="password" type="password" placeholder="请输入密码" style="margin-bottom:20px"></el-input>
      <el-button type="primary" block @click="goHome">登录</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const username = ref('')
const password = ref('')
const goHome = () => {
  router.push('/home')
}
</script>