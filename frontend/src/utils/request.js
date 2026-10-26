import axios from 'axios'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  withCredentials: true
})

request.interceptors.response.use(
  response => {
    const result = response.data
    if (result.code !== 2000) {
      return Promise.reject(new Error(result.msg || '操作失败'))
    }
    return result.data
  },
  error => {
    if (error.response?.status === 401) {
      sessionStorage.removeItem('user')
    }
    const message = error.response?.data?.msg || error.message || '网络请求失败'
    return Promise.reject(new Error(message))
  }
)

export default request
