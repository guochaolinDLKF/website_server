<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <div class="table-toolbar">
        <el-form :inline="true" @submit.prevent>
          <el-form-item label="关键词">
            <el-input v-model="query.keyword" placeholder="商品ID/描述" clearable style="width: 200px" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item label="类型">
            <el-input v-model="query.itemType" placeholder="类型" clearable style="width: 120px" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="onSearch">查询</el-button>
            <el-button :icon="Refresh" @click="onReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button v-if="canManage" type="primary" :icon="Plus" @click="openCreate">新增商品</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="list"
        border
        stripe
        :default-sort="{ prop: 'itemId', order: 'ascending' }"
        @sort-change="onSortChange"
      >
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="itemId" label="商品ID" min-width="180" show-overflow-tooltip sortable="custom" />
        <el-table-column prop="itemDesc" label="商品描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="itemType" label="类型" width="90" />
        <el-table-column label="价格" width="110">
          <template #default="{ row }">{{ fmtMoney(row.itemPrice) }}</template>
        </el-table-column>
        <el-table-column label="折扣价" width="110">
          <template #default="{ row }">{{ fmtMoney(row.itemDiscount) }}</template>
        </el-table-column>
        <el-table-column v-if="canManage" label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑（ID 不可改：新增时无 ID，编辑时只读展示） -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '新增商品'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item v-if="form.id" label="ID">
          <el-input :model-value="form.id" disabled />
        </el-form-item>
        <el-form-item label="商品ID" prop="itemId">
          <el-input v-model="form.itemId" placeholder="商品/功能标识" />
        </el-form-item>
        <el-form-item label="商品描述" prop="itemDesc">
          <el-input v-model="form.itemDesc" type="textarea" :rows="3" placeholder="商品描述" />
        </el-form-item>
        <el-form-item label="类型" prop="itemType">
          <el-input-number v-model="form.itemType" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="价格" prop="itemPrice">
          <el-input-number v-model="form.itemPrice" :min="0" :precision="2" :step="1" controls-position="right" style="width: 200px" />
          <span class="form-unit">元</span>
        </el-form-item>
        <el-form-item label="折扣价" prop="itemDiscount">
          <el-input-number v-model="form.itemDiscount" :min="0" :precision="2" :step="1" controls-position="right" style="width: 200px" />
          <span class="form-unit">元</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { pageGoods, saveGoods, deleteGoods } from '@/api/business'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
// 写操作仅超级管理员/管理员可见（与后端 @SaCheckRole 双重校验一致）
const canManage = computed(() => userStore.isSuperAdmin || userStore.roles.includes('ADMIN'))

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10, keyword: '', itemType: null, sortOrder: 'asc' })

const dialogVisible = ref(false)
const formRef = ref()
const emptyForm = () => ({ id: null, itemId: '', itemDesc: '', itemType: null, itemPrice: null, itemDiscount: null })
const form = reactive(emptyForm())
const rules = {
  itemId: [{ required: true, message: '请输入商品ID', trigger: 'blur' }]
}

function fmtMoney(v) {
  if (v === null || v === undefined || v === '') return '—'
  return Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

async function loadData() {
  loading.value = true
  try {
    const res = await pageGoods(query)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
function onSearch() { query.current = 1; loadData() }
function onReset() { query.keyword = ''; query.itemType = null; query.sortOrder = 'asc'; query.current = 1; loadData() }

// 商品ID 列升/降序：el-table 的 order 为 ascending/descending/null；取消排序时回退默认升序
function onSortChange({ prop, order }) {
  if (prop !== 'itemId') return
  query.sortOrder = order === 'descending' ? 'desc' : 'asc'
  query.current = 1
  loadData()
}

function openCreate() { Object.assign(form, emptyForm()); dialogVisible.value = true }
function openEdit(row) { Object.assign(form, emptyForm(), row); dialogVisible.value = true }
async function onSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await saveGoods({ ...form })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    /* 已提示 */
  } finally {
    saving.value = false
  }
}
async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除商品「${row.itemId}」吗？该操作将从业务库物理删除，不可恢复。`, '提示', { type: 'warning' })
  await deleteGoods(row.id)
  ElMessage.success('删除成功')
  loadData()
}

loadData()
</script>

<style scoped>
.form-unit {
  margin-left: 8px;
  color: #909399;
}
</style>
