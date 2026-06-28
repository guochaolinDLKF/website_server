// 后台账号密码强度规则（与后端 PasswordPolicy 保持一致）：
// 必须包含 数字 + 大小写字母 + 特殊符号，且长度大于 12 位（即至少 13 位）。

export const PASSWORD_RULE_TEXT = '密码须包含数字、大小写字母、特殊符号，且长度大于12位'

// 系统默认密码（与后端 AdminUserService.DEFAULT_PASSWORD 一致）：新增成员、重置密码统一使用
export const DEFAULT_PASSWORD = 'ydzz.@KeJi#.6688'

export const PASSWORD_REGEX = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{13,}$/

export function isPasswordValid(v) {
  return PASSWORD_REGEX.test(v || '')
}

// el-form 校验器：空值交由 required 规则处理，非空则按强度规则校验
export function passwordValidator(rule, value, callback) {
  if (!value) return callback()
  return PASSWORD_REGEX.test(value) ? callback() : callback(new Error(PASSWORD_RULE_TEXT))
}
