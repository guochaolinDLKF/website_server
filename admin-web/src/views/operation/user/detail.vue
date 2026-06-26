<template>
  <div class="app-container" v-loading="loading">
    <el-page-header content="用户详情" @back="$router.back()" style="margin-bottom: 16px" />

    <el-card class="page-card" shadow="never" v-if="user">
      <template #header><span>基础信息</span></template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ user.nickName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user.phoneCode || '—' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ user.gender === 1 ? '男' : user.gender === 0 ? '女' : '—' }}</el-descriptions-item>
        <el-descriptions-item label="会员">
          <el-tag v-if="user.isVip === 1" type="warning">VIP</el-tag>
          <el-tag v-else type="info">普通</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="user.status === 1 ? 'success' : 'danger'">{{ user.status === 1 ? '启用' : '禁用' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="出生地">{{ [user.birthProvince, user.birthCity, user.birthArea].filter(Boolean).join(' ') || '—' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ user.createTime || '—' }}</el-descriptions-item>
        <el-descriptions-item label="标签">{{ user.tags || '—' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="page-card" shadow="never" style="margin-top: 16px">
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="`订单(${counts.orders})`" name="orders">
          <GenericTable :rows="data.orders" />
        </el-tab-pane>
        <el-tab-pane :label="`权益(${counts.benefits})`" name="benefits">
          <GenericTable :rows="data.benefits" />
        </el-tab-pane>
        <el-tab-pane :label="`八字记录(${counts.eightRecords})`" name="eightRecords">
          <el-table :data="data.eightRecords" border stripe size="small" max-height="420">
            <el-table-column prop="name" label="姓名" min-width="90" show-overflow-tooltip />
            <el-table-column label="性别" min-width="60">
              <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 0 ? '女' : '—' }}</template>
            </el-table-column>
            <el-table-column prop="eightChar" label="八字" min-width="170" show-overflow-tooltip />
            <el-table-column prop="zodiac" label="生肖" min-width="60" />
            <el-table-column prop="tag" label="标签" min-width="80" show-overflow-tooltip />
            <el-table-column label="总笔记" min-width="220" show-overflow-tooltip>
              <template #default="{ row }">{{ row.note || '—' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="130" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="openNotes(row)">
                  查看笔记({{ noteCount(row) }})
                </el-button>
              </template>
            </el-table-column>
            <template #empty><el-empty description="暂无数据" :image-size="60" /></template>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 单条八字记录的断事笔记：总笔记 + 多个单项笔记 -->
    <el-dialog v-model="notesDialog" :title="`断事笔记 — ${current.name || ''}`" width="760px">
      <div class="nd-section">
        <div class="nd-label">总笔记</div>
        <div class="nd-total">{{ current.note || '暂无总笔记' }}</div>
      </div>
      <div class="nd-section">
        <div class="nd-label">单项笔记（{{ noteCount(current) }}）</div>
        <el-table v-if="noteCount(current)" :data="current.notes" border stripe size="small" max-height="380">
          <el-table-column prop="content" label="内容" min-width="240" show-overflow-tooltip />
          <el-table-column prop="eventType" label="事件类别" min-width="110" show-overflow-tooltip />
          <el-table-column prop="resultType" label="吉凶结果" min-width="100" show-overflow-tooltip />
          <el-table-column label="记录时间" min-width="160">
            <template #default="{ row }">{{ fmtTs(row.recordTime) }}</template>
          </el-table-column>
          <el-table-column label="置顶" width="70">
            <template #default="{ row }">{{ row.isTop === 1 ? '是' : '否' }}</template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无单项笔记" :image-size="60" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, h } from 'vue'
import { useRoute } from 'vue-router'
import { getUserDetail } from '@/api/user'
import { ElTable, ElTableColumn, ElEmpty } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const user = ref(null)
const activeTab = ref('orders')
const data = reactive({ orders: [], benefits: [], eightRecords: [] })

const counts = computed(() => ({
  orders: data.orders.length,
  benefits: data.benefits.length,
  eightRecords: data.eightRecords.length
}))

/* ---- 断事笔记弹窗 ---- */
const notesDialog = ref(false)
const current = ref({})
function noteCount(row) {
  return (row && row.notes && row.notes.length) || 0
}
function openNotes(row) {
  current.value = row
  notesDialog.value = true
}
// 兼容秒/毫秒时间戳
function fmtTs(ts) {
  if (!ts) return '—'
  let n = Number(ts)
  if (!n) return '—'
  if (n < 1e12) n *= 1000
  const d = new Date(n)
  if (isNaN(d.getTime())) return '—'
  const p = (x) => String(x).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}

// 通用表格：根据首行 keys 动态生成列，适配未知业务表结构（订单/权益）
const GenericTable = {
  props: { rows: { type: Array, default: () => [] } },
  setup(props) {
    return () => {
      const rows = props.rows || []
      if (!rows.length) return h(ElEmpty, { description: '暂无数据', imageSize: 60 })
      const keys = Object.keys(rows[0])
      return h(
        ElTable,
        { data: rows, border: true, stripe: true, maxHeight: 420, size: 'small' },
        () =>
          keys.map((k) =>
            h(ElTableColumn, {
              prop: k,
              label: k,
              minWidth: 130,
              showOverflowTooltip: true
            })
          )
      )
    }
  }
}

async function load() {
  loading.value = true
  try {
    const res = await getUserDetail(route.params.id)
    const d = res.data || {}
    user.value = d.user || null
    data.orders = d.orders || []
    data.benefits = d.benefits || []
    data.eightRecords = d.eightRecords || []
  } finally {
    loading.value = false
  }
}
load()
</script>

<style scoped>
.nd-section {
  margin-bottom: 18px;
}
.nd-label {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
.nd-total {
  font-size: 13px;
  line-height: 1.7;
  color: #606266;
  white-space: pre-wrap;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 6px;
}
</style>
