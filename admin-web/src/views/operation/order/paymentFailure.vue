<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <el-form :inline="true" class="filter-bar" @submit.prevent>
        <el-form-item label="状态">
          <el-input v-model="query.status" placeholder="处理状态" clearable style="width: 140px" @keyup.enter="onSearch" />
        </el-form-item>
        <el-form-item label="失败类型">
          <el-input v-model="query.failureType" placeholder="失败类型" clearable style="width: 160px" @keyup.enter="onSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="onSearch">查询</el-button>
          <el-button :icon="Refresh" @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="paymentNo" label="支付单号" min-width="180" show-overflow-tooltip />
        <el-table-column prop="transactionId" label="交易号" min-width="180" show-overflow-tooltip />
        <el-table-column prop="failureType" label="失败类型" min-width="140" />
        <el-table-column prop="retryCount" label="重试次数" width="100" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.status || '—' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nextRetryTime" label="下次重试" width="170" />
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
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { pagePaymentFailures } from '@/api/business'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10, status: '', failureType: '' })

function statusType(s) {
  if (['SUCCESS', 'RESOLVED', 'DONE'].includes(s)) return 'success'
  if (['FAILED', 'ERROR'].includes(s)) return 'danger'
  return 'warning'
}

async function loadData() {
  loading.value = true
  try {
    const res = await pagePaymentFailures(query)
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
function onReset() { query.status = ''; query.failureType = ''; query.current = 1; loadData() }

loadData()
</script>
