<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <el-form :inline="true" class="filter-bar" @submit.prevent>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="订单号/商品名/用户ID" clearable style="width: 200px" @keyup.enter="onSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-input v-model="query.orderStatus" placeholder="订单状态" clearable style="width: 140px" @keyup.enter="onSearch" />
        </el-form-item>
        <el-form-item label="下单时间">
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
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="list"
        border
        stripe
        :default-sort="{ prop: 'createTime', order: 'descending' }"
        @sort-change="onSortChange"
      >
        <el-table-column prop="orderNo" label="订单号" min-width="180" show-overflow-tooltip />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="itemName" label="商品" min-width="140" show-overflow-tooltip />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">{{ row.itemType ?? '—' }}</template>
        </el-table-column>
        <el-table-column prop="vipMonth" label="VIP月数" width="90" />
        <el-table-column label="原价" width="100">
          <template #default="{ row }">{{ fmtMoney(row.originalPrice) }}</template>
        </el-table-column>
        <el-table-column label="实付" width="100">
          <template #default="{ row }">{{ fmtMoney(row.discountPrice) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusType(row.orderStatus)">{{ row.orderStatus || '—' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payType" label="支付方式" width="100" />
        <el-table-column prop="createTime" label="下单时间" width="170" sortable="custom" />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'order:detail'" type="primary" link @click="openDetail(row)">详情</el-button>
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

    <el-dialog v-model="detailVisible" title="订单详情" width="720px">
      <el-descriptions :column="2" border v-if="detail.order">
        <el-descriptions-item label="订单号">{{ detail.order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.order.userId }}</el-descriptions-item>
        <el-descriptions-item label="商品">{{ detail.order.itemName }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.order.orderStatus }}</el-descriptions-item>
        <el-descriptions-item label="原价">{{ fmtMoney(detail.order.originalPrice) }}</el-descriptions-item>
        <el-descriptions-item label="实付">{{ fmtMoney(detail.order.discountPrice) }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ detail.order.payType }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ detail.order.createTime }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">支付记录</el-divider>
      <el-table :data="detail.payments" border size="small" :empty-text="'无支付记录'">
        <el-table-column prop="paymentNo" label="支付单号" min-width="160" show-overflow-tooltip />
        <el-table-column label="金额" width="100"><template #default="{ row }">{{ fmtMoney(row.paymentAmount) }}</template></el-table-column>
        <el-table-column prop="paymentMethod" label="方式" width="100" />
        <el-table-column prop="paymentStatus" label="状态" width="110" />
        <el-table-column prop="paymentTime" label="支付时间" width="170" />
      </el-table>

      <el-divider content-position="left">退款记录</el-divider>
      <el-table :data="detail.refunds" border size="small" :empty-text="'无退款记录'">
        <el-table-column prop="refundNo" label="退款单号" min-width="160" show-overflow-tooltip />
        <el-table-column label="金额" width="100"><template #default="{ row }">{{ fmtMoney(row.refundAmount) }}</template></el-table-column>
        <el-table-column prop="refundStatus" label="状态" width="110" />
        <el-table-column prop="refundReason" label="原因" min-width="140" show-overflow-tooltip />
        <el-table-column prop="refundTime" label="退款时间" width="170" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { pageOrders, getOrderDetail } from '@/api/business'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const dateRange = ref([])
const query = reactive({ current: 1, size: 10, keyword: '', orderStatus: '', sortOrder: 'desc' })

const detailVisible = ref(false)
const detail = reactive({ order: null, payments: [], refunds: [] })

function fmtMoney(v) {
  if (v === null || v === undefined || v === '') return '—'
  return Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
function statusType(s) {
  const ok = ['PAID', 'SUCCESS', 'FINISHED', 'COMPLETED']
  const warn = ['REFUND', 'REFUNDED', 'CLOSED', 'CANCELLED', 'CANCELED']
  if (ok.includes(s)) return 'success'
  if (warn.includes(s)) return 'warning'
  return 'info'
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await pageOrders(params)
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
function onReset() { query.keyword = ''; query.orderStatus = ''; dateRange.value = []; query.sortOrder = 'desc'; query.current = 1; loadData() }

// 下单时间 列升/降序：取消排序时回退默认降序（最近优先）
function onSortChange({ prop, order }) {
  if (prop !== 'createTime') return
  query.sortOrder = order === 'ascending' ? 'asc' : 'desc'
  query.current = 1
  loadData()
}
async function openDetail(row) {
  const res = await getOrderDetail(row.id)
  detail.order = res.data.order || null
  detail.payments = res.data.payments || []
  detail.refunds = res.data.refunds || []
  detailVisible.value = true
}

loadData()
</script>
