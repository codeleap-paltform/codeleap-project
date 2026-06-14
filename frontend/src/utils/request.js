import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios请求实例
const request = axios.create({
  baseURL: 'http://localhost:8080', // 后端服务地址，后端改端口这里跟着改
  timeout: 10000 // 10秒超时
})

// 请求拦截器：发请求之前统一处理
request.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：拿到接口返回后统一处理
request.interceptors.response.use(
  response => {
    // 直接返回后端的业务数据，不用每次写 res.data.data
    return response.data
  },
  error => {
    // 统一弹出网络错误提示
    ElMessage.error('请求失败：' + (error.response?.data?.msg || error.message))
    return Promise.reject(error)
  }
)

export default request