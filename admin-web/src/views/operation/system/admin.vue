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
        <el-button v-permission="'admin:edit'" type="primary" :icon="Plus" @click="openCreate">新增管理员</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="账号" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'admin:edit'" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button v-permission="'admin:edit'" type="warning" link @click="openReset(row)">重置密码</el-button>
            <el-button
              v-permission="'admin:edit'"
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              @click="toggleStatus(row)"
            >{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-button v-permission="'admin:edit'" type="danger" link @click="onDelete(row)">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑管理员' : '新增管理员'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password :placeholder="form.id ? '留空表示不修改' : '请输入初始密码'" />
        </el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleIds" multiple placeholder="选择角色" style="width: 100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { pageAdminUsers, getAdminUser, saveAdminUser, changeAdminUserStatus, resetAdminUserPwd, deleteAdminUser } from '@/api/adminUser'
import { listRoles } from '@/api/role'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const roles = ref([])
const query = reactive({ current: 1, size: 10, keyword: '', status: null })

const dialogVisible = ref(false)
const formRef = ref()
const emptyForm = () => ({ id: null, username: '', password: '', realName: '', phone: '', email: '', status: 1, remark: '', roleIds: [] })
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
async function loadRoles() {
  const res = await listRoles()
  roles.value = res.data || []
}
function onSearch() { query.current = 1; loadData() }
function onReset() { query.keyword = ''; query.status = null; query.current = 1; loadData() }

function openCreate() {
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}
async function openEdit(row) {
  const res = await getAdminUser(row.id)
  const u = res.data.user || {}
  Object.assign(form, emptyForm(), { id: u.id, username: u.username, realName: u.realName, phone: u.phone, email: u.email, status: u.status, remark: u.remark, roleIds: res.data.roleIds || [] })
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
  } catch (e) {
    /* 已提示 */
  } finally {
    saving.value = false
  }
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
  await ElMessageBox.confirm(`确定删除管理员「${row.username}」吗？`, '提示', { type: 'warning' })
  await deleteAdminUser(row.id)
  ElMessage.success('删除成功')
  loadData()
}

loadData()
loadRoles()
</script>
