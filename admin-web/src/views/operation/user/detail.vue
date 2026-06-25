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
          <GenericTable :rows="data.eightRecords" />
        </el-tab-pane>
        <el-tab-pane :label="`断事笔记(${counts.notes})`" name="notes">
          <GenericTable :rows="data.notes" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
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
const data = reactive({ orders: [], benefits: [], eightRecords: [], notes: [] })

const counts = computed(() => ({
  orders: data.orders.length,
  benefits: data.benefits.length,
  eightRecords: data.eightRecords.length,
  notes: data.notes.length
}))

// 通用表格：根据首行 keys 动态生成列，适配未知业务表结构
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
    data.notes = d.notes || []
  } finally {
    loading.value = false
  }
}
load()
</script>
