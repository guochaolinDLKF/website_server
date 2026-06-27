<template>
  <el-card class="page-card dl-card" shadow="never">
    <div class="dl-header">
      <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!tip">
        <template #content>
          <div style="max-width: 340px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ tip }}</div>
        </template>
        <span class="dl-title" :class="{ 'dl-title-tip': tip }">
          {{ title }}<el-icon v-if="tip" class="dl-title-info"><InfoFilled /></el-icon>
        </span>
      </el-tooltip>
      <div class="dl-actions">
        <el-tooltip content="刷新" placement="top">
          <el-icon class="dl-action-icon" @click="load"><Refresh /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <div class="dl-tabs">
      <!-- 统计粒度：按天/周/月 -->
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="dl-tab dl-gran">{{ DIM_LABEL[dim] }}<i class="dl-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>

      <span class="dl-sep">|</span>

      <!-- 指标选择（2/2） -->
      <el-popover trigger="click" placement="bottom-start" :width="160" popper-class="gm-pop">
        <template #reference>
          <span class="dl-tab dl-gran">指标 ({{ metricCount }}/2)<i class="dl-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <label class="gm-check"><el-checkbox v-model="show1" :disabled="!show2" />{{ name1 }}</label>
          <label class="gm-check"><el-checkbox v-model="show2" :disabled="!show1" />{{ name2 }}</label>
        </div>
      </el-popover>

      <span class="dl-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" :multi-only="true" @change="onRange" />

      <div class="dl-tabs-right">
        <el-icon class="dl-action-icon" @click="showLabel = !showLabel">
          <component :is="showLabel ? View : Hide" />
        </el-icon>
      </div>
    </div>

    <!-- 概览：日期 + 两个指标最新值 -->
    <div class="dl-summary">
      <div class="dl-date">{{ data.latestLabel || '—' }}</div>
      <div class="dl-metrics">
        <div v-if="show1" class="dl-metric">
          <span class="dl-num">{{ fmtNum(data[latestKey1]) }}</span>
          <span class="dl-legend"><i :style="{ background: COLOR1 }"></i>{{ name1 }}</span>
        </div>
        <div v-if="show2" class="dl-metric dl-metric-right">
          <span class="dl-num">{{ fmtNum(data[latestKey2]) }}</span>
          <span class="dl-legend"><i :style="{ background: COLOR2 }"></i>{{ name2 }}</span>
        </div>
      </div>
    </div>

    <div class="dl-body" v-loading="loading">
      <div ref="chartRef" class="dl-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Refresh, View, Hide, InfoFilled } from '@element-plus/icons-vue'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '趋势' },
  tip: { type: String, default: '' }, // 标题悬停提示（支持 \n 换行）
  name1: { type: String, default: 'arpu' },
  name2: { type: String, default: 'arppu' },
  key1: { type: String, default: 'arpu' }, // 后端返回的数组字段名（线1）
  key2: { type: String, default: 'arppu' }, // 后端返回的数组字段名（线2）
  valueType: { type: String, default: 'amount' }, // amount=2位小数 | int=整数 | percent=2位小数+%
  api: { type: Function, required: true }, // 返回 { labels, [key1], [key2], [key1]Latest, [key2]Latest, latestLabel }
  defaultPreset: { type: String, default: 'recent7' }
})

const COLOR1 = '#3b82f6'
const COLOR2 = '#f59e0b'
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }

const latestKey1 = computed(() => props.key1 + 'Latest')
const latestKey2 = computed(() => props.key2 + 'Latest')

const loading = ref(false)
const data = reactive({ labels: [], latestLabel: '' })
const chartRef = ref()
let chart

const dim = ref('day')
const rangeStart = ref('')
const rangeEnd = ref('')
const show1 = ref(true)
const show2 = ref(true)
const showLabel = ref(false)
const granPopRef = ref()
const dateRangeRef = ref()

const metricCount = computed(() => (show1.value ? 1 : 0) + (show2.value ? 1 : 0))

// 数值格式化：金额(2位小数) | 整数 | 百分比(2位小数+%)
function fmtNum(v) {
  if (v === null || v === undefined) return '—'
  const n = Number(v)
  if (props.valueType === 'int') return n.toLocaleString('zh-CN')
  if (props.valueType === 'percent') return `${n.toFixed(2)}%`
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
// 图表坐标轴/tooltip 数值格式
function fmtAxis(v) {
  if (props.valueType === 'percent') return `${v}%`
  return Number(v).toLocaleString('zh-CN')
}

function empty() {
  const e = { labels: [], latestLabel: '' }
  e[props.key1] = []
  e[props.key2] = []
  e[latestKey1.value] = null
  e[latestKey2.value] = null
  return e
}

async function load() {
  loading.value = true
  try {
    const params = { dim: dim.value }
    if (rangeStart.value && rangeEnd.value) {
      params.start = rangeStart.value
      params.end = rangeEnd.value
    }
    const res = await props.api(params)
    Object.assign(data, empty(), res.data || {})
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
  const legend = []
  const series = []

  if (show1.value) {
    legend.push(props.name1)
    series.push({
      name: props.name1,
      type: 'line',
      smooth: false,
      symbol: 'circle',
      symbolSize: 6,
      label: { show: showLabel.value, position: 'top', color: '#606266', fontSize: 11 },
      data: (data[props.key1] || []).map((v) => (v == null ? null : Number(v))),
      itemStyle: { color: COLOR1 },
      lineStyle: { color: COLOR1, width: 2 }
    })
  }
  if (show2.value) {
    legend.push(props.name2)
    series.push({
      name: props.name2,
      type: 'line',
      smooth: false,
      symbol: 'circle',
      symbolSize: 6,
      label: { show: showLabel.value, position: 'top', color: '#e6820a', fontSize: 11 },
      data: (data[props.key2] || []).map((v) => (v == null ? null : Number(v))),
      itemStyle: { color: COLOR2 },
      lineStyle: { color: COLOR2, width: 2 }
    })
  }

  chart.setOption(
    {
      tooltip: { trigger: 'axis', valueFormatter: (v) => (v == null ? '-' : fmtAxis(v)) },
      legend: { data: legend, bottom: 0, icon: 'roundRect' },
      grid: { left: 48, right: 24, top: 24, bottom: 40 },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: data.labels,
        axisLine: { lineStyle: { color: '#dcdfe6' } },
        axisLabel: { color: '#909399' }
      },
      yAxis: {
        type: 'value',
        min: 0,
        axisLabel: { color: '#909399', formatter: fmtAxis },
        splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
      },
      series
    },
    true
  )
}

function pickDim(d) {
  granPopRef.value && granPopRef.value.hide()
  if (dim.value === d) return
  dim.value = d
  const presetByDim = { day: 'recent7', week: 'past30', month: 'pastYear' }
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

watch([show1, show2, showLabel], render)

function handleResize() {
  chart && chart.resize()
}
onMounted(async () => {
  await nextTick()
  chart = echarts.init(chartRef.value)
  window.addEventListener('resize', handleResize)
  // 首次加载由 DateRangePanel 挂载时的 change 事件驱动
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart && chart.dispose()
})
</script>

<style scoped>
.dl-card {
  width: 100%;
}
.dl-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.dl-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.dl-title-tip {
  cursor: help;
  display: inline-flex;
  align-items: center;
}
.dl-title-info {
  margin-left: 4px;
  font-size: 14px;
  color: #c0c4cc;
}
.dl-title-tip:hover .dl-title-info {
  color: #409eff;
}
.dl-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.dl-action-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;
}
.dl-action-icon:hover {
  color: #409eff;
}
.dl-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.dl-tab {
  color: #303133;
  font-weight: 600;
}
.dl-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.dl-gran:hover {
  color: #409eff;
}
.dl-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.dl-sep {
  color: #dcdfe6;
}
.dl-tabs-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}
.dl-summary {
  margin-top: 14px;
}
.dl-date {
  font-size: 13px;
  color: #909399;
}
.dl-metrics {
  margin-top: 6px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}
.dl-metric {
  display: flex;
  align-items: baseline;
  gap: 4px;
}
.dl-metric-right {
  margin-left: auto;
}
.dl-num {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.1;
}
.dl-legend {
  margin-left: 10px;
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  font-weight: 400;
  color: #606266;
}
.dl-legend i {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
  margin-right: 4px;
}
.dl-body {
  margin-top: 8px;
}
.dl-chart {
  width: 100%;
  height: 300px;
}

/* 统计粒度/指标菜单 */
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
