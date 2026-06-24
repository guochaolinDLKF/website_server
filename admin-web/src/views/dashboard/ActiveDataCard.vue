<template>
  <el-card class="page-card active-card" shadow="never">
    <!-- 头部：标题 + 维度切换 -->
    <div class="ac-header">
      <span class="ac-title">活跃数据</span>
      <el-radio-group v-model="type" size="small" @change="load">
        <el-radio-button value="hour">今日每小时</el-radio-button>
        <el-radio-button value="week">过去7天</el-radio-button>
        <el-radio-button value="month">本月</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 概览：左侧最新值/环比同比，右侧均值/总和 -->
    <div class="ac-summary" v-loading="loading">
      <div class="ac-left">
        <div class="ac-date">{{ data.latestLabel || '—' }}</div>
        <div class="ac-value">{{ formatNum(data.latest) }}</div>
        <div class="ac-rates">
          <span class="ac-rate-item">
            {{ dodLabel }}
            <b :class="rateClass(data.dod)">{{ rateText(data.dod) }}</b>
          </span>
          <span class="ac-rate-item" v-if="data.wow !== null && data.wow !== undefined">
            周同比
            <b :class="rateClass(data.wow)">{{ rateText(data.wow) }}</b>
          </span>
        </div>
      </div>
      <div class="ac-right">
        <div class="ac-stat"><span class="ac-stat-label">均值</span><span class="ac-stat-num">{{ data.mean ?? '—' }}</span></div>
        <div class="ac-stat"><span class="ac-stat-label">总和</span><span class="ac-stat-num">{{ formatNum(data.sum) }}</span></div>
      </div>
    </div>

    <div ref="chartRef" class="ac-chart"></div>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { getActiveData } from '@/api/dashboard'

const type = ref('week')
const loading = ref(false)
const data = reactive({ points: [], latest: 0, latestLabel: '', mean: 0, sum: 0, dod: null, wow: null })

const chartRef = ref()
let chart

const dodLabel = computed(() => (type.value === 'hour' ? '环比' : '日环比'))

function formatNum(v) {
  if (v === null || v === undefined) return '—'
  return Number(v).toLocaleString('zh-CN')
}
// 涨绿跌红，与图片一致（下跌红色 ▼）
function rateClass(v) {
  if (v === null || v === undefined) return 'rate-flat'
  return Number(v) < 0 ? 'rate-down' : Number(v) > 0 ? 'rate-up' : 'rate-flat'
}
function rateText(v) {
  if (v === null || v === undefined) return '—'
  const n = Number(v)
  const arrow = n < 0 ? '▼' : n > 0 ? '▲' : ''
  return `${arrow} ${Math.abs(n).toFixed(2)}%`
}

function render() {
  if (!chart) return
  const pts = data.points || []
  chart.setOption(
    {
      tooltip: { trigger: 'axis' },
      legend: { data: ['活跃数'], bottom: 0, icon: 'roundRect' },
      grid: { left: 48, right: 24, top: 30, bottom: 40 },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: pts.map((p) => p.label),
        axisLine: { lineStyle: { color: '#dcdfe6' } },
        axisLabel: { color: '#909399' }
      },
      yAxis: {
        type: 'value',
        axisLabel: { color: '#909399' },
        splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
      },
      series: [
        {
          name: '活跃数',
          type: 'line',
          data: pts.map((p) => p.value),
          symbol: 'circle',
          symbolSize: 7,
          itemStyle: { color: '#5b9bf3' },
          lineStyle: { color: '#5b9bf3', width: 2 },
          label: { show: true, position: 'top', color: '#606266', fontSize: 12 }
        }
      ]
    },
    true
  )
}

async function load() {
  loading.value = true
  try {
    const res = await getActiveData(type.value)
    Object.assign(data, { points: [], latest: 0, latestLabel: '', mean: 0, sum: 0, dod: null, wow: null }, res.data || {})
    await nextTick()
    render()
  } catch (e) {
    /* 静默：拦截器已提示 */
  } finally {
    loading.value = false
  }
}

function handleResize() {
  chart && chart.resize()
}

onMounted(async () => {
  await nextTick()
  chart = echarts.init(chartRef.value)
  window.addEventListener('resize', handleResize)
  load()
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart && chart.dispose()
})
</script>

<style scoped>
.active-card {
  width: 100%;
}
.ac-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.ac-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.ac-summary {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 8px 4px 4px;
}
.ac-left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.ac-date {
  font-size: 13px;
  color: #909399;
}
.ac-value {
  font-size: 34px;
  font-weight: 700;
  line-height: 1.1;
  color: #303133;
}
.ac-rates {
  display: flex;
  gap: 18px;
  margin-top: 2px;
  font-size: 13px;
  color: #909399;
}
.ac-rate-item b {
  margin-left: 4px;
  font-weight: 600;
}
.rate-down {
  color: #f56c6c;
}
.rate-up {
  color: #67c23a;
}
.rate-flat {
  color: #909399;
}
.ac-right {
  display: flex;
  gap: 28px;
  text-align: right;
}
.ac-stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.ac-stat-label {
  font-size: 13px;
  color: #909399;
}
.ac-stat-num {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.ac-chart {
  height: 300px;
  width: 100%;
  margin-top: 8px;
}
</style>
