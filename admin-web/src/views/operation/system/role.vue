<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <div class="table-toolbar">
        <el-form :inline="true" @submit.prevent>
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="账号/姓名" clearable style="width: 180px" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="onSearch">查询</el-button>
            <el-button :icon="Refresh" @click="onReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button v-permission="'role:list'" type="primary" :icon="Plus" @click="openCreate">新增成员</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="账号" min-width="140" />
        <el-table-column prop="realName" label="姓名" min-width="120" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag v-if="isSelf(row)" type="warning">超级管理员</el-tag>
            <el-tag v-else type="info">普通成员</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <!-- 超级管理员（当前登录账号）：只能修改自己的密码，不能禁用/删除自己 -->
            <template v-if="isSelf(row)">
              <el-button type="primary" link @click="openChangeOwnPwd">修改密码</el-button>
            </template>
            <!-- 普通成员：完整管理 -->
            <template v-else>
              <el-button v-permission="'role:list'" type="primary" link @click="openEdit(row)">编辑</el-button>
              <el-button v-permission="'role:list'" type="warning" link @click="openReset(row)">重置密码</el-button>
              <el-button
                v-permission="'role:list'"
                :type="row.status === 1 ? 'danger' : 'success'"
                link
                @click="toggleStatus(row)"
              >{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
              <el-button v-permission="'role:list'" type="danger" link @click="onDelete(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑成员 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑成员' : '新增成员'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="角色">
          <el-input model-value="普通成员（MEMBER）" disabled />
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password :placeholder="form.id ? '留空表示不修改' : '请输入初始密码'" />
        </el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 超级管理员修改自己的密码 -->
    <el-dialog v-model="pwdVisible" title="修改密码" width="420px">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="至少6位" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdSaving" @click="onChangeOwnPwd">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { pageAdminUsers, saveAdminUser, changeAdminUserStatus, resetAdminUserPwd, deleteAdminUser } from '@/api/adminUser'
import { changePassword } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10, keyword: '', status: null })

// 是否为当前登录的超级管理员本人（其本行只能改密码，不能禁用/删除）
function isSelf(row) {
  return row.id === userStore.id
}

const dialogVisible = ref(false)
const formRef = ref()
const emptyForm = () => ({ id: null, username: '', password: '', realName: '', status: 1 })
const form = reactive(emptyForm())
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ validator: (r, v, cb) => (!form.id && !v ? cb(new Error('新增时密码必填')) : cb()), trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await pageAdminUsers(query)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}
function onSearch() { query.current = 1; loadData() }
function onReset() { query.keyword = ''; query.status = null; query.current = 1; loadData() }

// 新增：角色编码由系统默认为「普通成员(MEMBER)」，后端在未指定角色时自动绑定，前端无需传角色
function openCreate() { Object.assign(form, emptyForm()); dialogVisible.value = true }
function openEdit(row) {
  Object.assign(form, emptyForm(), { id: row.id, username: row.username, realName: row.realName, status: row.status })
  form.password = ''
  dialogVisible.value = true
}
async function onSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await saveAdminUser({ ...form })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) { /* 已提示 */ } finally { saving.value = false }
}

async function toggleStatus(row) {
  const next = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确定${next === 1 ? '启用' : '禁用'}「${row.username}」吗？`, '提示', { type: 'warning' })
  await changeAdminUserStatus(row.id, next)
  ElMessage.success('操作成功')
  loadData()
}
async function openReset(row) {
  const { value } = await ElMessageBox.prompt(`为「${row.username}」设置新密码`, '重置密码', {
    inputType: 'password',
    inputPlaceholder: '请输入新密码（至少6位）',
    inputValidator: (v) => (v && v.length >= 6 ? true : '密码至少6位')
  })
  await resetAdminUserPwd(row.id, value)
  ElMessage.success('密码已重置')
}
async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除成员「${row.username}」吗？`, '提示', { type: 'warning' })
  await deleteAdminUser(row.id)
  ElMessage.success('删除成功')
  loadData()
}

// ------ 超级管理员修改自己的密码：成功后强制重新登录 ------
const pwdVisible = ref(false)
const pwdSaving = ref(false)
const pwdFormRef = ref()
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (r, v, cb) => (v !== pwdForm.newPassword ? cb(new Error('两次输入的密码不一致')) : cb()), trigger: 'blur' }
  ]
}
function openChangeOwnPwd() {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
  pwdVisible.value = true
}
async function onChangeOwnPwd() {
  await pwdFormRef.value.validate()
  pwdSaving.value = true
  try {
    await changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    pwdVisible.value = false
    await ElMessageBox.alert('密码修改成功，请使用新密码重新登录', '提示', { type: 'success', confirmButtonText: '去登录' })
    await userStore.logout()
    router.push('/login')
  } catch (e) { /* 已提示 */ } finally { pwdSaving.value = false }
}

loadData()
</script>
