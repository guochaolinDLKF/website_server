import { useUserStore } from '@/store/user'

// 按钮级权限指令：v-permission="'user:export'" 或 v-permission="['a','b']"（任一满足）
// 无权限时移除元素，配合后端 @SaCheckPermission 双重校验。
function checkPermission(el, binding) {
  const userStore = useUserStore()
  const value = binding.value
  if (!value) return
  const codes = Array.isArray(value) ? value : [value]
  const pass = userStore.isSuperAdmin || codes.some((c) => userStore.permissions.includes(c))
  if (!pass && el.parentNode) {
    el.parentNode.removeChild(el)
  }
}

export default {
  mounted(el, binding) {
    checkPermission(el, binding)
  }
}
