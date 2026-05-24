import axios from 'axios'

// 创建一个axios实例，统一管理所有请求配置
const request = axios.create({
  baseURL: 'http://10.224.183.107:8080', // 后端接口的基础地址，后续后端部署后只改这里
  timeout: 5000 // 请求超时时间，5秒，防止请求一直挂着
})

// 请求拦截器：在所有请求发送前执行
request.interceptors.request.use(
  config => {
    // 后续会在这里添加token，实现登录状态验证
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：在所有响应返回后执行
request.interceptors.response.use(
  response => {
    // 直接返回后端统一的Result格式数据，省去每次写response.data
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

export default request // 导出实例，供所有页面使用