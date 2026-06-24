<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <div class="table-toolbar">
        <span style="font-weight: 600">角色管理</span>
        <el-button v-permission="'role:list'" type="primary" :icon="Plus" @click="openCreate">新增角色</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleCode" label="角色编码" min-width="140" />
        <el-table-column prop="roleName" label="角色名称" min-width="120" />
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'role:assign'" type="primary" link @click="openAssign(row)">分配权限</el-button>
            <el-button v-permission="'role:list'" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button
              v-permission="'role:list'"
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              @click="toggleStatus(row)"
            >{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-button v-permission="'role:list'" type="danger" link :disabled="row.roleCode === 'SUPER_ADMIN'" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑角色 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑角色' : '新增角色'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" :disabled="!!form.id" placeholder="如 OPERATOR" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限 -->
    <el-dialog v-model="assignVisible" :title="`分配权限 - ${current.roleName}`" width="560px">
      <el-tree
        ref="treeRef"
        :data="permTree"
        show-checkbox
        node-key="id"
        :props="{ label: 'name', children: 'children' }"
        default-expand-all
        style="max-height: 460px; overflow: auto"
      />
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="assigning" @click="onAssign">保存分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { listRoles, saveRole, changeRoleStatus, deleteRole, getRolePermissions, assignRolePermissions } from '@/api/role'
import { getPermissionTree } from '@/api/permission'

const loading = ref(false)
const saving = ref(false)
const assigning = ref(false)
const list = ref([])

const dialogVisible = ref(false)
const formRef = ref()
const emptyForm = () => ({ id: null, roleCode: '', roleName: '', description: '', sortOrder: 0, status: 1 })
const form = reactive(emptyForm())
const rules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

const assignVisible = ref(false)
const treeRef = ref()
const permTree = ref([])
const current = reactive({ id: null, roleName: '' })

async function loadData() {
  loading.value = true
  try {
    const res = await listRoles()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openCreate() { Object.assign(form, emptyForm()); dialogVisible.value = true }
function openEdit(row) { Object.assign(form, emptyForm(), row); dialogVisible.value = true }
async function onSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await saveRole({ ...form })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) { /* 已提示 */ } finally { saving.value = false }
}
async function toggleStatus(row) {
  const next = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确定${next === 1 ? '启用' : '禁用'}角色「${row.roleName}」吗？`, '提示', { type: 'warning' })
  await changeRoleStatus(row.id, next)
  ElMessage.success('操作成功')
  loadData()
}
async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除角色「${row.roleName}」吗？`, '提示', { type: 'warning' })
  await deleteRole(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function openAssign(row) {
  current.id = row.id
  current.roleName = row.roleName
  if (!permTree.value.length) {
    const res = await getPermissionTree()
    permTree.value = res.data || []
  }
  const res = await getRolePermissions(row.id)
  assignVisible.value = true
  await nextTick()
  // 仅设置叶子节点为选中，避免父节点半选影响（el-tree 会自动联动父节点）
  treeRef.value.setCheckedKeys(res.data || [], false)
}
async function onAssign() {
  assigning.value = true
  try {
    const checked = treeRef.value.getCheckedKeys()
    const halfChecked = treeRef.value.getHalfCheckedKeys()
    const ids = [...checked, ...halfChecked]
    await assignRolePermissions(current.id, ids)
    ElMessage.success('分配成功')
    assignVisible.value = false
  } catch (e) { /* 已提示 */ } finally { assigning.value = false }
}

loadData()
</script>
