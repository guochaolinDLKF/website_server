<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <div class="table-toolbar">
        <el-form :inline="true" @submit.prevent>
          <el-form-item label="账号">
            <el-input v-model="query.username" placeholder="管理员账号" clearable style="width: 160px" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item label="模块">
            <el-input v-model="query.module" placeholder="模块名" clearable style="width: 160px" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="onSearch">查询</el-button>
            <el-button :icon="Refresh" @click="onReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="operation" label="操作" min-width="140" />
        <el-table-column prop="requestMethod" label="方法" width="80" />
        <el-table-column prop="requestUri" label="接口" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column prop="costMs" label="耗时(ms)" width="100" />
        <el-table-column label="结果" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '成功' : '异常' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column label="详情" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showDetail(row)">查看</el-button>
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

    <el-dialog v-model="detailVisible" title="操作日志详情" width="640px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作人">{{ detail.username }}</el-descriptions-item>
        <el-descriptions-item label="模块/操作">{{ detail.module }} / {{ detail.operation }}</el-descriptions-item>
        <el-descriptions-item label="方法">{{ detail.method }}</el-descriptions-item>
        <el-descriptions-item label="接口">{{ detail.requestMethod }} {{ detail.requestUri }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre class="log-pre">{{ detail.requestParam }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="返回结果">
          <pre class="log-pre">{{ detail.responseResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="异常" v-if="detail.errorMsg">
          <pre class="log-pre">{{ detail.errorMsg }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { pageOperationLog } from '@/api/log'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ current: 1, size: 10, username: '', module: '' })

const detailVisible = ref(false)
const detail = reactive({})

async function loadData() {
  loading.value = true
  try {
    const res = await pageOperationLog(query)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}
function onSearch() { query.current = 1; loadData() }
function onReset() { query.username = ''; query.module = ''; query.current = 1; loadData() }
function showDetail(row) { Object.assign(detail, row); detailVisible.value = true }

loadData()
</script>

<style scoped>
.log-pre {
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow: auto;
  margin: 0;
  font-size: 12px;
}
</style>
