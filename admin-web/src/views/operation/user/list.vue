<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <!-- 查询条件 -->
      <el-form :inline="true" class="filter-bar" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="昵称/手机号/ID" clearable style="width: 180px" @keyup.enter="onSearch" />
        </el-form-item>
        <el-form-item label="会员">
          <el-select v-model="query.isVip" placeholder="全部" clearable style="width: 120px">
            <el-option label="VIP" :value="1" />
            <el-option label="普通" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="onSearch">查询</el-button>
          <el-button :icon="Refresh" @click="onReset">重置</el-button>
          <el-button v-permission="'user:export'" type="success" :icon="Download" :loading="exporting" @click="onExport">导出</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column label="用户" min-width="160">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="30" :src="row.avatar">{{ (row.nickName || 'U').charAt(0) }}</el-avatar>
              <span>{{ row.nickName || '—' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phoneCode" label="手机号" width="140" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 0 ? '女' : '—' }}</template>
        </el-table-column>
        <el-table-column label="会员" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.isVip === 1" type="warning">VIP</el-tag>
            <el-tag v-else type="info">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'user:detail'" type="primary" link @click="goDetail(row)">详情</el-button>
            <el-button
              v-permission="'user:disable'"
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download } from '@element-plus/icons-vue'
import { pageUsers, changeUserStatus, exportUsers } from '@/api/user'

const router = useRouter()
const loading = ref(false)
const exporting = ref(false)
const list = ref([])
const total = ref(0)
const dateRange = ref([])
const query = reactive({ current: 1, size: 10, keyword: '', isVip: null, status: null })

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await pageUsers(params)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function onSearch() {
  query.current = 1
  loadData()
}
function onReset() {
  query.keyword = ''
  query.isVip = null
  query.status = null
  dateRange.value = []
  query.current = 1
  loadData()
}
function goDetail(row) {
  router.push(`/user/detail/${row.id}`)
}
async function toggleStatus(row) {
  const next = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确定${next === 1 ? '启用' : '禁用'}用户「${row.nickName || row.id}」吗？`, '提示', { type: 'warning' })
  await changeUserStatus(row.id, next)
  ElMessage.success('操作成功')
  loadData()
}
async function onExport() {
  exporting.value = true
  try {
    const params = { keyword: query.keyword, isVip: query.isVip, status: query.status }
    const res = await exportUsers(params)
    downloadCsv(res.data || [])
  } catch (e) {
    /* 已提示 */
  } finally {
    exporting.value = false
  }
}
function downloadCsv(rows) {
  if (!rows.length) {
    ElMessage.warning('无数据可导出')
    return
  }
  const headers = ['ID', '昵称', '手机号', '性别', '会员', '状态', '注册时间']
  const lines = rows.map((r) =>
    [r.id, r.nickName, r.phoneCode, r.gender === 1 ? '男' : '女', r.isVip === 1 ? 'VIP' : '普通', r.status === 1 ? '启用' : '禁用', r.createTime]
      .map((c) => `"${c ?? ''}"`)
      .join(',')
  )
  const csv = '﻿' + [headers.join(','), ...lines].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = `用户列表_${Date.now()}.csv`
  a.click()
  URL.revokeObjectURL(a.href)
}

loadData()
</script>

<style scoped>
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
