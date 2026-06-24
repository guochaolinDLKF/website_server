import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken, getTokenName, getTokenPrefix, removeToken } from '@/utils/auth'
import router from '@/router'

// 统一请求封装：注入后台 Token、统一解包 Result、统一错误处理
const service = axios.create({
  baseURL: '/api/admin',
  timeout: 20000
})

// 请求拦截：注入 Token（请求头名称由登录返回的 tokenName 决定，默认 Authorization）
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      // 若后端配置了 token-prefix，请求头需提交为「前缀 空格 Token」，否则 Sa-Token 视为未登录
      const prefix = getTokenPrefix()
      config.headers[getTokenName()] = prefix ? `${prefix} ${token}` : token
    }
    return config
  },
  (error) => Promise.reject(error)
)

let reloginShown = false

// 响应拦截：解包后端统一 Result{ code, message, data }
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 非业务 JSON（如文件流）直接返回
    if (res === undefined || res === null || typeof res !== 'object' || res.code === undefined) {
      return res
    }
    if (res.code === 0) {
      return res
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || 'Error'))
  },
  (error) => {
    const status = error.response?.status
    const body = error.response?.data
    const msg = body?.message || error.message || '网络异常'

    if (status === 401) {
      handleRelogin()
      return Promise.reject(error)
    }
    if (status === 403) {
      ElMessage.error(msg || '权限不足')
      return Promise.reject(error)
    }
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

function handleRelogin() {
  if (reloginShown) return
  reloginShown = true
  ElMessageBox.confirm('登录状态已失效，请重新登录', '提示', {
    confirmButtonText: '重新登录',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      removeToken()
      router.push(`/login?redirect=${encodeURIComponent(router.currentRoute.value.fullPath)}`)
    })
    .finally(() => {
      reloginShown = false
    })
}

export default service
