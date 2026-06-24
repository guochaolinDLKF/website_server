<template>
  <div class="app-container">
    <!-- 核心指标卡片 -->
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.key" :xs="12" :sm="12" :md="6" :lg="6">
        <div class="stat-card" :style="{ background: card.bg }">
          <div class="stat-title">{{ card.title }}</div>
          <div class="stat-value">{{ card.value }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <el-card class="page-card" shadow="never">
          <template #header>
            <div class="card-head">
              <span>新增用户趋势</span>
              <el-radio-group v-model="trendDays" size="small" @change="loadTrends">
                <el-radio-button :value="7">近7天</el-radio-button>
                <el-radio-button :value="30">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="userTrendRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="page-card" shadow="never">
          <template #header><span>收入趋势</span></template>
          <div ref="incomeTrendRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="8">
        <el-card class="page-card" shadow="never">
          <template #header><span>商品销售排行</span></template>
          <div ref="goodsRankRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card class="page-card" shadow="never">
          <template #header><span>注册渠道分布</span></template>
          <div ref="channelRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card class="page-card" shadow="never">
          <template #header><span>权益类型分布</span></template>
          <div ref="benefitRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  getOverview,
  getUserTrend,
  getIncomeTrend,
  getGoodsRank,
  getChannelDist,
  getBenefitDist
} from '@/api/dashboard'

const trendDays = ref(30)
const overview = reactive({})

const cards = ref([])
function buildCards() {
  cards.value = [
    { key: 'totalUsers', title: '累计用户', value: overview.totalUsers ?? 0, bg: 'linear-gradient(135deg,#5b8def,#4067e8)' },
    { key: 'todayNewUsers', title: '今日新增', value: overview.todayNewUsers ?? 0, bg: 'linear-gradient(135deg,#36c6a0,#1aa97e)' },
    { key: 'vipUsers', title: 'VIP 用户', value: overview.vipUsers ?? 0, bg: 'linear-gradient(135deg,#f7a35c,#f08c2e)' },
    { key: 'paidUsers', title: '付费用户', value: overview.paidUsers ?? 0, bg: 'linear-gradient(135deg,#9b6cf0,#7a45d8)' },
    { key: 'totalIncome', title: '累计收入(元)', value: fmtMoney(overview.totalIncome), bg: 'linear-gradient(135deg,#ef5b8d,#e83467)' },
    { key: 'todayIncome', title: '今日收入(元)', value: fmtMoney(overview.todayIncome), bg: 'linear-gradient(135deg,#21b3c6,#1689a9)' },
    { key: 'todayOrders', title: '今日订单', value: overview.todayOrders ?? 0, bg: 'linear-gradient(135deg,#6c8cf0,#4567d8)' },
    { key: 'validBenefits', title: '有效权益', value: overview.validBenefits ?? 0, bg: 'linear-gradient(135deg,#67c23a,#4e9e2a)' }
  ]
}
function fmtMoney(v) {
  const n = Number(v || 0)
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const userTrendRef = ref()
const incomeTrendRef = ref()
const goodsRankRef = ref()
const channelRef = ref()
const benefitRef = ref()
const charts = []

function useChart(el) {
  const inst = echarts.init(el)
  charts.push(inst)
  return inst
}

let userChart, incomeChart, goodsChart, channelChart, benefitChart

function lineOption(data, name, color, valueKey = 'count') {
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 16, top: 24, bottom: 30 },
    xAxis: { type: 'category', boundaryGap: false, data: data.map((d) => d.date) },
    yAxis: { type: 'value' },
    series: [
      {
        name,
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.15 },
        itemStyle: { color },
        data: data.map((d) => d[valueKey] ?? d.value ?? 0)
      }
    ]
  }
}

function pieOption(data) {
  return {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, type: 'scroll' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '66%'],
        center: ['50%', '46%'],
        data: data.map((d) => ({ name: d.name ?? d.type ?? '未知', value: d.value ?? d.count ?? 0 }))
      }
    ]
  }
}

function barOption(data) {
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 16, top: 16, bottom: 30 },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: data.map((d) => d.name).reverse() },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d.value ?? d.count ?? 0).reverse(),
        itemStyle: { color: '#409eff', borderRadius: [0, 4, 4, 0] }
      }
    ]
  }
}

async function loadOverview() {
  try {
    const res = await getOverview()
    Object.assign(overview, res.data || {})
  } catch (e) {
    /* zhouyi 未连接或无数据时静默，卡片显示 0 */
  }
  buildCards()
}

async function loadTrends() {
  try {
    const [u, i] = await Promise.all([getUserTrend(trendDays.value), getIncomeTrend(trendDays.value)])
    userChart && userChart.setOption(lineOption(u.data || [], '新增用户', '#409eff', 'count'), true)
    incomeChart && incomeChart.setOption(lineOption(i.data || [], '收入', '#e83467', 'amount'), true)
  } catch (e) {
    /* 静默 */
  }
}

async function loadDistributions() {
  try {
    const [g, c, b] = await Promise.all([getGoodsRank(), getChannelDist(), getBenefitDist()])
    goodsChart && goodsChart.setOption(barOption(g.data || []), true)
    channelChart && channelChart.setOption(pieOption(c.data || []), true)
    benefitChart && benefitChart.setOption(pieOption(b.data || []), true)
  } catch (e) {
    /* 静默 */
  }
}

function handleResize() {
  charts.forEach((c) => c.resize())
}

onMounted(async () => {
  buildCards()
  await nextTick()
  userChart = useChart(userTrendRef.value)
  incomeChart = useChart(incomeTrendRef.value)
  goodsChart = useChart(goodsRankRef.value)
  channelChart = useChart(channelRef.value)
  benefitChart = useChart(benefitRef.value)
  window.addEventListener('resize', handleResize)
  loadOverview()
  loadTrends()
  loadDistributions()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach((c) => c.dispose())
})
</script>

<style scoped>
.chart {
  height: 280px;
  width: 100%;
}
.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stat-card {
  margin-bottom: 16px;
}
</style>
