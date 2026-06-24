// Token 本地存储工具
const TOKEN_KEY = 'admin_token'
const TOKEN_NAME_KEY = 'admin_token_name'
const TOKEN_PREFIX_KEY = 'admin_token_prefix'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

// Token 前缀（对应后端 sa-token.token-prefix）。提交请求头时格式为「前缀 空格 Token」。
export function getTokenPrefix() {
  return localStorage.getItem(TOKEN_PREFIX_KEY) || ''
}

export function setTokenPrefix(prefix) {
  localStorage.setItem(TOKEN_PREFIX_KEY, prefix || '')
}

// 后端登录返回 tokenName（请求头名称），存下以便请求头使用，默认 Authorization
export function getTokenName() {
  return localStorage.getItem(TOKEN_NAME_KEY) || 'Authorization'
}

export function setTokenName(name) {
  localStorage.setItem(TOKEN_NAME_KEY, name || 'Authorization')
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(TOKEN_NAME_KEY)
  localStorage.removeItem(TOKEN_PREFIX_KEY)
}
