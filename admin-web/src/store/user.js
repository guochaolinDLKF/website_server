import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, getInfo } from '@/api/auth'
import { getToken, setToken, setTokenName, setTokenPrefix, removeToken } from '@/utils/auth'

// 用户/权限状态：token、管理员信息、角色码、权限码、菜单树
export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    id: null,
    username: '',
    realName: '',
    avatar: '',
    roles: [],
    permissions: [],
    menus: [],
    loaded: false
  }),
  getters: {
    // 是否超级管理员（拥有全部权限）
    isSuperAdmin: (state) => state.roles.includes('SUPER_ADMIN')
  },
  actions: {
    // 登录：保存 token 与请求头名称，并写入管理员信息
    async login(payload) {
      const res = await loginApi(payload)
      const data = res.data
      setToken(data.tokenValue)
      setTokenName(data.tokenName)
      setTokenPrefix(data.tokenPrefix)
      this.token = data.tokenValue
      this.applyInfo(data.adminInfo)
      this.loaded = true
      return data
    },
    // 拉取当前管理员信息（刷新页面后恢复状态）
    async fetchInfo() {
      const res = await getInfo()
      this.applyInfo(res.data)
      this.loaded = true
      return res.data
    },
    applyInfo(info) {
      if (!info) return
      this.id = info.id
      this.username = info.username
      this.realName = info.realName
      this.avatar = info.avatar
      this.roles = info.roles || []
      this.permissions = info.permissions || []
      this.menus = info.menus || []
    },
    // 是否拥有某权限码（超级管理员放行）
    hasPermission(code) {
      if (!code) return true
      if (this.isSuperAdmin) return true
      return this.permissions.includes(code)
    },
    async logout() {
      try {
        await logoutApi()
      } catch (e) {
        // 忽略登出接口异常，前端强制清理
      }
      this.reset()
    },
    reset() {
      removeToken()
      this.token = ''
      this.id = null
      this.username = ''
      this.realName = ''
      this.avatar = ''
      this.roles = []
      this.permissions = []
      this.menus = []
      this.loaded = false
    }
  }
})
