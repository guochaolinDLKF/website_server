<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <div class="table-toolbar">
        <el-form :inline="true" @submit.prevent>
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="键/名称" clearable style="width: 180px" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item label="分组">
            <el-input v-model="query.group" placeholder="分组" clearable style="width: 140px" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="onSearch">查询</el-button>
            <el-button :icon="Refresh" @click="onReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button v-permission="'config:edit'" type="primary" :icon="Plus" @click="openCreate">新增配置</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="configKey" label="配置键" min-width="160" />
        <el-table-column prop="configName" label="名称" min-width="140" />
        <el-table-column prop="configValue" label="值" min-width="200" show-overflow-tooltip />
        <el-table-column prop="configGroup" label="分组" width="120" />
        <el-table-column prop="updateBy" label="更新人" width="120" />
        <el-table-column prop="updateTime" label="更新时间" width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'config:edit'" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button v-permission="'config:edit'" type="danger" link @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑配置' : '新增配置'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="配置键" prop="configKey"><el-input v-model="form.configKey" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.configName" /></el-form-item>
        <el-form-item label="值"><el-input v-model="form.configValue" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="分组"><el-input v-model="form.configGroup" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
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
import { pageConfig, saveConfig, deleteConfig } from '@/api/sysConfig'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10, keyword: '', group: '' })

const dialogVisible = ref(false)
const formRef = ref()
const emptyForm = () => ({ id: null, configKey: '', configName: '', configValue: '', configGroup: '', remark: '' })
const form = reactive(emptyForm())
const rules = { configKey: [{ required: true, message: '请输入配置键', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const res = await pageConfig(query)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}
function onSearch() { query.current = 1; loadData() }
function onReset() { query.keyword = ''; query.group = ''; query.current = 1; loadData() }
function openCreate() { Object.assign(form, emptyForm()); dialogVisible.value = true }
function openEdit(row) { Object.assign(form, emptyForm(), row); dialogVisible.value = true }
async function onSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await saveConfig({ ...form })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) { /* 已提示 */ } finally { saving.value = false }
}
async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除配置「${row.configKey}」吗？`, '提示', { type: 'warning' })
  await deleteConfig(row.id)
  ElMessage.success('删除成功')
  loadData()
}

loadData()
</script>
