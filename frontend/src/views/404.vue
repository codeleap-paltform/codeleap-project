<template>
  <div class="not-found">
    <h1>404</h1>
    <p>页面不存在</p>
    <router-link to="/home" class="btn btn-primary">返回首页</router-link>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  text-align: center;
}

.not-found h1 {
  font-size: 100px;
  color: #409eff;
  margin-bottom: 20px;
}

.not-found p {
  font-size: 24px;
  color: #666;
  margin-bottom: 30px;
}
</style>