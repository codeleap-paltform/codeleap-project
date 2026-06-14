<template>
  <div class="home-container">
    <h1>欢迎来到 CodeLeap 平台！</h1>
    <p>登录成功，这是系统首页</p>
  </div>
</template>

<script setup>
// 这里可以写首页的逻辑
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-family: Arial, sans-serif;
}

h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 20px;
}

p {
  font-size: 18px;
  color: #666;
}
</style>