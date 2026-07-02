import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import router from '@/router'

const service = axios.create({
  baseURL: 'http://localhost:8080', // 按实际后端地址修改
  timeout: 10000
})

service.interceptors.request.use(
  config => {
    const token = store.state.token
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token   
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一处理 401 和业务错误
service.interceptors.response.use(
  response => {
    // 如果请求的是 blob 类型，直接返回完整 response（以获取 headers）
    if (response.config.responseType === 'blob') {
      return response
    }
    // 非 blob 请求，按原有逻辑处理 JSON 数据
    const res = response.data
    if (res.code === 1) {
      Message.error(res.message || '操作失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    if (error.response && error.response.status === 401) {
      Message.error('登录已过期，请重新登录')
      store.dispatch('logout')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export default service