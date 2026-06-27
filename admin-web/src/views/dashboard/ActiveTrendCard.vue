<template>
  <el-card class="page-card at-card" shadow="never">
    <div class="at-header">
      <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!tip">
        <template #content>
          <div style="max-width: 340px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ tip }}</div>
        </template>
        <span class="at-title" :class="{ 'at-title-tip': tip }">
          {{ title }}<el-icon v-if="tip" class="at-title-info"><InfoFilled /></el-icon>
        </span>
      </el-tooltip>
      <div class="at-actions">
        <el-tooltip content="刷新" placement="top">
          <el-icon class="at-action-icon" @click="load"><Refresh /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <div class="at-tabs">
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="at-tab at-gran">{{ DIM_LABEL[dim] }}<i class="at-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>

      <span class="at-sep">|</span>

      <el-popover trigger="click" placement="bottom-start" :width="160" popper-class="gm-pop">
        <template #reference>
          <span class="at-tab at-gran">指标 ({{ shownCount }}/4)<i class="at-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <label v-for="mt in METRICS" :key="mt.key" class="gm-check">
            <el-checkbox :model-value="show[mt.key]" :disabled="shownCount === 1 && show[mt.key]" @change="toggle(mt.key)" />
            <i class="at-dot" :style="{ background: mt.color }"></i>{{ mt.name }}
          </label>
        </div>
      </el-popover>

      <span class="at-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" :multi-only="true" @change="onRange" />

      <div class="at-tabs-right">
        <el-icon class="at-action-icon" @click="showLabel = !showLabel">
          <component :is="showLabel ? View : Hide" />
        </el-icon>
      </div>
    </div>

    <div class="at-summary">
      <div class="at-date">{{ data.latestLabel || '—' }}</div>
      <div class="at-metrics">
        <div v-for="mt in shownMetrics" :key="mt.key" class="at-metric">
          <span class="at-num">{{ fmtLatest(mt) }}</span>
          <span class="at-legend"><i :style="{ background: mt.color }"></i>{{ mt.name }}</span>
        </div>
      </div>
    </div>

    <div class="at-body" v-loading="loading">
      <div ref="chartRef" class="at-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Refresh, View, Hide, InfoFilled } from '@element-plus/icons-vue'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '每日活跃数据' },
  tip: { type: String, default: '' },
  api: { type: Function, required: true },
  defaultPreset: { type: String, default: 'past7' }
})

// 指标定义：dau/wau/mau 为柱（左轴计数），stickiness 为折线（右轴百分比）
const METRICS = [
  { key: 'dau', name: 'DAU', color: '#3b82f6', type: 'bar', percent: false },
  { key: 'wau', name: 'WAU', color: '#f59e0b', type: 'bar', percent: false },
  { key: 'mau', name: 'MAU', color: '#2c3e9e', type: 'bar', percent: false },
  { key: 'stickiness', name: 'DAU/MAU', color: '#22d3ee', type: 'line', percent: true }
]
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }

const loading = ref(false)
const data = reactive({ labels: [], dau: [], wau: [], mau: [], stickiness: [], latestLabel: '' })
const chartRef = ref()
let chart

const dim = ref('day')
const rangeStart = ref('')
const rangeEnd = ref('')
const showLabel = ref(false)
const show = reactive({ dau: true, wau: true, mau: true, stickiness: true })
const granPopRef = ref()
const dateRangeRef = ref()

const shownMetrics = computed(() => METRICS.filter((m) => show[m.key]))
const shownCount = computed(() => shownMetrics.value.length)

function fmtInt(v) {
  if (v === null || v === undefined) return '—'
  return Number(v).toLocaleString('zh-CN')
}
function fmtPct(v) {
  if (v === null || v === undefined) return '—'
  return `${Number(v).toFixed(2)}%`
}
function fmtLatest(mt) {
  const v = data[mt.key + 'Latest']
  return mt.percent ? fmtPct(v) : fmtInt(v)
}

const EMPTY = { labels: [], dau: [], wau: [], mau: [], stickiness: [], latestLabel: '' }

async function load() {
  loading.value = true
  try {
    const params = { dim: dim.value }
    if (rangeStart.value && rangeEnd.value) {
      params.start = rangeStart.value
      params.end = rangeEnd.value
    }
    const res = await props.api(params)
    Object.assign(data, EMPTY, res.data || {})
    await nextTick()
    render()
  } catch (e) {
    /* 静默：拦截器已提示 */
  } finally {
    loading.value = false
  }
}

function render() {
  if (!chart) return
  const hasBar = METRICS.some((m) => m.type === 'bar' && show[m.key])
  const hasLine = show.stickiness
  const yAxis = []
  if (hasBar) {
    yAxis.push({
      type: 'value',
      min: 0,
      minInterval: 1,
      position: 'left',
      axisLabel: { color: '#909399', formatter: (v) => v.toLocaleString('zh-CN') },
      splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
    })
  }
  if (hasLine) {
    yAxis.push({
      type: 'value',
      min: 0,
      position: 'right',
      axisLabel: { color: '#909399', formatter: '{value}%' },
      splitLine: { show: false }
    })
  }
  const lineAxisIndex = hasBar ? 1 : 0

  const legend = []
  const series = []
  for (const mt of METRICS) {
    if (!show[mt.key]) continue
    legend.push(mt.name)
    if (mt.type === 'bar') {
      series.push({
        name: mt.name,
        type: 'bar',
        yAxisIndex: 0,
        barMaxWidth: 18,
        label: { show: showLabel.value, position: 'top', color: '#303133', fontSize: 10 },
        data: (data[mt.key] || []).map((v) => Number(v || 0)),
        itemStyle: { color: mt.color, borderRadius: [2, 2, 0, 0] }
      })
    } else {
      series.push({
        name: mt.name,
        type: 'line',
        yAxisIndex: lineAxisIndex,
        smooth: false,
        symbol: 'circle',
        symbolSize: 6,
        label: { show: showLabel.value, position: 'top', color: '#0e7490', fontSize: 10, formatter: (p) => (p.value == null ? '' : p.value + '%') },
        data: (data[mt.key] || []).map((v) => (v == null ? null : Number(v))),
        itemStyle: { color: mt.color },
        lineStyle: { color: mt.color, width: 2 }
      })
    }
  }

  chart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        valueFormatter: undefined
      },
      legend: { data: legend, bottom: 0, icon: 'roundRect' },
      grid: { left: 52, right: 52, top: 24, bottom: 40 },
      xAxis: {
        type: 'category',
        data: data.labels,
        axisLine: { lineStyle: { color: '#dcdfe6' } },
        axisLabel: { color: '#909399' }
      },
      yAxis,
      series
    },
    true
  )
}

function pickDim(d) {
  granPopRef.value && granPopRef.value.hide()
  if (dim.value === d) return
  dim.value = d
  const presetByDim = { day: 'past7', week: 'past30', month: 'pastYear' }
  if (presetByDim[d] && dateRangeRef.value) {
    dateRangeRef.value.applyPresetAndEmit(presetByDim[d])
  } else {
    load()
  }
}

function onRange({ start, end }) {
  rangeStart.value = start || ''
  rangeEnd.value = end || ''
  load()
}

function toggle(key) {
  if (shownCount.value === 1 && show[key]) return
  show[key] = !show[key]
  render()
}

watch(showLabel, render)

function handleResize() {
  chart && chart.resize()
}
onMounted(async () => {
  await nextTick()
  chart = echarts.init(chartRef.value)
  window.addEventListener('resize', handleResize)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart && chart.dispose()
})
</script>

<style scoped>
.at-card {
  width: 100%;
}
.at-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.at-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.at-title-tip {
  cursor: help;
  display: inline-flex;
  align-items: center;
}
.at-title-info {
  margin-left: 4px;
  font-size: 14px;
  color: #c0c4cc;
}
.at-title-tip:hover .at-title-info {
  color: #409eff;
}
.at-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.at-action-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;
}
.at-action-icon:hover {
  color: #409eff;
}
.at-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.at-tab {
  color: #303133;
  font-weight: 600;
}
.at-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.at-gran:hover {
  color: #409eff;
}
.at-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.at-sep {
  color: #dcdfe6;
}
.at-tabs-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}
.at-summary {
  margin-top: 14px;
}
.at-date {
  font-size: 13px;
  color: #909399;
}
.at-metrics {
  margin-top: 6px;
  display: flex;
  gap: 48px;
}
.at-metric {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.at-num {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  line-height: 1.1;
}
.at-legend {
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  color: #606266;
}
.at-legend i {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
  margin-right: 4px;
}
.at-body {
  margin-top: 8px;
}
.at-chart {
  width: 100%;
  height: 320px;
}
.at-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
}

.gm-menu {
  display: flex;
  flex-direction: column;
}
.gm-row {
  padding: 8px 12px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  border-radius: 4px;
}
.gm-row:hover {
  background: #f5f7fa;
}
.gm-row.active {
  color: #409eff;
  background: #ecf5ff;
}
.gm-check {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
}
</style>

<style>
.gm-pop.el-popover.el-popper {
  padding: 6px;
}
</style>
