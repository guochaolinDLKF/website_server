<template>
  <el-card class="page-card mt-card" shadow="never">
    <!-- 头部：标题 -->
    <div class="mt-header">
      <span class="mt-title">{{ title }}</span>
    </div>
    <!-- 维度标签 -->
    <div class="mt-tabs">
      <span class="mt-tab">按天</span>
      <span class="mt-sep">|</span>
      <span class="mt-tab active">{{ rangeText }}</span>
      <span class="mt-sep">|</span>
      <span class="mt-tab">VS</span>
    </div>

    <div class="mt-body" v-loading="loading">
      <!-- 概览：最新值 + 环比同比 + 均值/总和 -->
      <div class="mt-date">{{ s.latestLabel || '—' }}</div>
      <div class="mt-summary">
        <div class="mt-value">{{ fmtVal(s.latest) }}<span v-if="unit" class="mt-unit">{{ unit }}</span></div>
        <div class="mt-rates">
          <span class="mt-rate-item">日环比
            <el-tooltip :content="dodTip" :disabled="!dodTip" placement="top" effect="light">
              <b :class="rateClass(s.dod)">{{ rateText(s.dod) }}</b>
            </el-tooltip>
          </span>
          <span class="mt-rate-item">周同比
            <el-tooltip :content="wowTip" :disabled="!wowTip" placement="top" effect="light">
              <b :class="rateClass(s.wow)">{{ rateText(s.wow) }}</b>
            </el-tooltip>
          </span>
        </div>
        <div v-if="showMeanSum" class="mt-stats">
          <div class="mt-stat"><span class="mt-stat-label">均值</span><span class="mt-stat-num">{{ fmt2(s.mean) }}</span></div>
          <div class="mt-stat"><span class="mt-stat-label">总和</span><span class="mt-stat-num">{{ fmt2(s.sum) }}</span></div>
        </div>
      </div>
      <div ref="chartRef" class="mt-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getOnlineStats } from '@/api/dashboard'

const props = defineProps({
  title: { type: String, required: true },
  // 取后端返回的哪条序列：duration(在线时长) | launch(启动次数)
  metric: { type: String, required: true },
  days: { type: Number, default: 30 },
  rangeText: { type: String, default: '过去30天' },
  seriesName: { type: String, default: '' },
  unit: { type: String, default: '' },
  valueDecimals: { type: Number, default: 2 }, // 最新大数字保留小数位
  showMeanSum: { type: Boolean, default: false }
})

const loading = ref(false)
const s = reactive({ points: [], latest: null, latestLabel: '', dod: null, wow: null, mean: null, sum: null })
const chartRef = ref()
let chart

function fmtVal(v) {
  if (v === null || v === undefined) return '—'
  return Number(v).toFixed(props.valueDecimals)
}
function fmt2(v) {
  if (v === null || v === undefined) return '—'
  return Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
// 涨绿(▲) 跌红(▼)
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

/* ---- 环比/同比 hover 提示：按整自然日对比 ---- */
const WEEK_CN = ['日', '一', '二', '三', '四', '五', '六']
const pad = (n) => String(n).padStart(2, '0')
// 由 latestLabel(如 2026-06-23(二)) 解析最新统计日
function latestDate() {
  const m = (s.latestLabel || '').slice(0, 10).split('-')
  if (m.length !== 3) return null
  return new Date(Number(m[0]), Number(m[1]) - 1, Number(m[2]))
}
const fmtFull = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}(${WEEK_CN[d.getDay()]})`
const fmtShort = (d) => `${pad(d.getMonth() + 1)}-${pad(d.getDate())}(${WEEK_CN[d.getDay()]})`
// offsetDays：日环比=1（对比昨日整天），周同比=7（对比上周同日整天）
function buildTip(v, offsetDays) {
  if (v === null || v === undefined) return ''
  const latest = latestDate()
  if (!latest) return ''
  const base = new Date(latest)
  base.setDate(base.getDate() - offsetDays)
  const baseEnd = new Date(base)
  baseEnd.setDate(baseEnd.getDate() + 1)
  const n = Number(v)
  const dir = n < 0 ? '下降' : n > 0 ? '增长' : '持平'
  return `对比 ${fmtFull(base)} 00:00 到 ${fmtShort(baseEnd)} 00:00，${dir}了${Math.abs(n).toFixed(2)}%`
}
const dodTip = computed(() => buildTip(s.dod, 1))
const wowTip = computed(() => buildTip(s.wow, 7))

function render() {
  if (!chart) return
  const pts = s.points || []
  chart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        valueFormatter: (v) => (v === null || v === undefined ? '—' : Number(v).toFixed(2))
      },
      legend: { data: [props.seriesName], bottom: 0, icon: 'roundRect' },
      grid: { left: 44, right: 24, top: 24, bottom: 40 },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: pts.map((p) => p.label),
        axisLine: { lineStyle: { color: '#dcdfe6' } },
        axisLabel: { color: '#909399', interval: 'auto' }
      },
      yAxis: {
        type: 'value',
        axisLabel: { color: '#909399' },
        splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
      },
      series: [
        {
          name: props.seriesName,
          type: 'line',
          connectNulls: true,
          data: pts.map((p) => p.value),
          symbol: 'circle',
          symbolSize: 6,
          itemStyle: { color: '#5b9bf3' },
          lineStyle: { color: '#5b9bf3', width: 2 }
        }
      ]
    },
    true
  )
}

async function load() {
  loading.value = true
  try {
    const res = await getOnlineStats({ days: props.days })
    const series = (res.data && res.data[props.metric]) || {}
    Object.assign(s, { points: [], latest: null, latestLabel: '', dod: null, wow: null, mean: null, sum: null }, series)
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
.mt-card {
  width: 100%;
}
.mt-header {
  display: flex;
  align-items: center;
}
.mt-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.mt-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.mt-tab {
  color: #909399;
}
.mt-tab.active {
  color: #303133;
  font-weight: 600;
}
.mt-sep {
  color: #dcdfe6;
}
.mt-date {
  margin-top: 12px;
  font-size: 13px;
  color: #909399;
}
.mt-summary {
  display: flex;
  align-items: center;
  gap: 28px;
  margin-top: 2px;
}
.mt-value {
  font-size: 34px;
  font-weight: 700;
  line-height: 1.1;
  color: #303133;
}
.mt-unit {
  margin-left: 4px;
  font-size: 14px;
  font-weight: 400;
  color: #606266;
}
.mt-rates {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}
.mt-rate-item b {
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
.mt-stats {
  display: flex;
  gap: 28px;
  margin-left: auto;
}
.mt-stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
  text-align: right;
}
.mt-stat-label {
  font-size: 13px;
  color: #909399;
}
.mt-stat-num {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.mt-chart {
  height: 280px;
  width: 100%;
  margin-top: 8px;
}
</style>
