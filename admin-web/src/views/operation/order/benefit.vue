<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <el-form :inline="true" class="filter-bar" @submit.prevent>
        <el-form-item label="用户ID">
          <el-input v-model="query.userId" placeholder="用户ID" clearable style="width: 160px" @keyup.enter="onSearch" />
        </el-form-item>
        <el-form-item label="有效性">
          <el-select v-model="query.isValid" placeholder="全部" clearable style="width: 120px">
            <el-option label="有效" :value="1" />
            <el-option label="失效" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="onSearch">查询</el-button>
          <el-button :icon="Refresh" @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="itemName" label="权益名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="itemType" label="类型" width="90" />
        <el-table-column prop="vipMonth" label="VIP月数" width="90" />
        <el-table-column prop="orderNo" label="来源订单" min-width="170" show-overflow-tooltip />
        <el-table-column prop="purchaseTime" label="购买时间" width="170" />
        <el-table-column prop="expireTime" label="到期时间" width="170" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isValid === 1 ? 'success' : 'info'">{{ row.isValid === 1 ? '有效' : '失效' }}</el-tag>
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
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { pageBenefits } from '@/api/business'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10, userId: null, isValid: null })

async function loadData() {
  loading.value = true
  try {
    const res = await pageBenefits(query)
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
function onReset() { query.userId = null; query.isValid = null; query.current = 1; loadData() }

loadData()
</script>
