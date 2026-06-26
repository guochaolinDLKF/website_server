<template>
  <el-card class="page-card nd-card" shadow="never">
    <div class="nd-header">
      <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!tip">
        <template #content>
          <div style="max-width: 340px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ tip }}</div>
        </template>
        <span class="nd-title" :class="{ 'nd-title-tip': tip }">
          {{ title }}<el-icon v-if="tip" class="nd-title-info"><InfoFilled /></el-icon>
        </span>
      </el-tooltip>
      <div class="nd-actions">
        <el-tooltip content="刷新" placement="top">
          <el-icon class="nd-action-icon" @click="load"><Refresh /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <div class="nd-tabs">
      <!-- 统计粒度：按天/周/月 -->
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="nd-tab nd-gran">{{ DIM_LABEL[dim] }}<i class="nd-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>

      <span class="nd-sep">|</span>

      <!-- 指标选择（2/2） -->
      <el-popover trigger="click" placement="bottom-start" :width="160" popper-class="gm-pop">
        <template #reference>
          <span class="nd-tab nd-gran">指标 ({{ metricCount }}/2)<i class="nd-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <label class="gm-check"><el-checkbox v-model="showBar" :disabled="!showRatio" />{{ barName }}</label>
          <label class="gm-check"><el-checkbox v-model="showRatio" :disabled="!showBar" />{{ ratioName }}</label>
        </div>
      </el-popover>

      <span class="nd-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" :multi-only="true" @change="onRange" />

      <div class="nd-tabs-right">
        <el-icon class="nd-action-icon" @click="showLabel = !showLabel">
          <component :is="showLabel ? View : Hide" />
        </el-icon>
      </div>
    </div>

    <!-- 概览：日期 + 柱值 + 占比 -->
    <div class="nd-summary">
      <div class="nd-date">{{ data.latestLabel || '—' }}</div>
      <div class="nd-metrics">
        <div v-if="showBar" class="nd-metric">
          <span class="nd-num">{{ fmtInt(data.barLatest) }}</span>
          <span class="nd-unit">{{ barUnit }}</span>
          <span class="nd-legend"><i :style="{ background: BAR_COLOR }"></i>{{ barName }}</span>
        </div>
        <div v-if="showRatio" class="nd-metric nd-metric-right">
          <span class="nd-num">{{ fmtPct(data.ratioLatest) }}</span>
          <span class="nd-legend"><i :style="{ background: LINE_COLOR }"></i>{{ ratioName }}</span>
        </div>
      </div>
    </div>

    <div class="nd-body" v-loading="loading">
      <div ref="chartRef" class="nd-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Refresh, View, Hide, InfoFilled } from '@element-plus/icons-vue'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '新增数据' },
  tip: { type: String, default: '' }, // 标题悬停提示：数据计算口径（支持 \n 换行）
  barName: { type: String, default: '新增玩家' },
  barUnit: { type: String, default: '次' },
  ratioName: { type: String, default: '新增玩家占比' },
  api: { type: Function, required: true }, // 返回 { labels, bars, ratios, barLatest, ratioLatest, latestLabel }
  defaultPreset: { type: String, default: 'recent7' }
})

const BAR_COLOR = '#3b82f6'
const LINE_COLOR = '#f59e0b'
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }

const loading = ref(false)
const data = reactive({ labels: [], bars: [], ratios: [], barLatest: null, ratioLatest: null, latestLabel: '' })
const chartRef = ref()
let chart

const dim = ref('day')
const rangeStart = ref('')
const rangeEnd = ref('')
const showBar = ref(true)
const showRatio = ref(true)
const showLabel = ref(false)
const granPopRef = ref()
const dateRangeRef = ref()

const metricCount = computed(() => (showBar.value ? 1 : 0) + (showRatio.value ? 1 : 0))

function fmtInt(v) {
  if (v === null || v === undefined) return '—'
  return Number(v).toLocaleString('zh-CN')
}
function fmtPct(v) {
  if (v === null || v === undefined) return '—'
  return `${Number(v).toFixed(2)}%`
}

const EMPTY = { labels: [], bars: [], ratios: [], barLatest: null, ratioLatest: null, latestLabel: '' }

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
  const legend = []
  const series = []
  const yAxis = []

  // 左轴（数量）+ 柱。轴名由顶部概览与底部图例标注，这里不设 name 以免顶端文字被裁切
  if (showBar.value) {
    yAxis.push({
      type: 'value',
      min: 0,
      minInterval: 1,
      position: 'left',
      axisLabel: { color: '#909399', formatter: (v) => v.toLocaleString('zh-CN') },
      splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
    })
  }
  // 右轴（占比%）+ 线
  if (showRatio.value) {
    yAxis.push({
      type: 'value',
      min: 0,
      position: 'right',
      axisLabel: { color: '#909399', formatter: '{value}%' },
      splitLine: { show: false }
    })
  }
  const ratioAxisIndex = showBar.value ? 1 : 0

  if (showBar.value) {
    legend.push(props.barName)
    series.push({
      name: props.barName,
      type: 'bar',
      yAxisIndex: 0,
      barMaxWidth: 28,
      label: { show: showLabel.value, position: 'top', color: '#303133', fontSize: 11 },
      data: data.bars,
      itemStyle: { color: BAR_COLOR, borderRadius: [2, 2, 0, 0] }
    })
  }
  if (showRatio.value) {
    legend.push(props.ratioName)
    series.push({
      name: props.ratioName,
      type: 'line',
      yAxisIndex: ratioAxisIndex,
      smooth: false,
      symbol: 'circle',
      symbolSize: 6,
      label: { show: showLabel.value, position: 'top', color: '#e6820a', fontSize: 11, formatter: (p) => (p.value == null ? '' : p.value + '%') },
      data: data.ratios,
      itemStyle: { color: LINE_COLOR },
      lineStyle: { color: LINE_COLOR, width: 2 }
    })
  }

  chart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        valueFormatter: undefined
      },
      legend: { data: legend, bottom: 0, icon: 'roundRect' },
      grid: { left: 48, right: 48, top: 24, bottom: 40 },
      xAxis: {
        type: 'category',
        boundaryGap: true,
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
  // 切粒度时按维度套用默认窗口（与实时面板一致）
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

watch([showBar, showRatio, showLabel], render)

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
.nd-card {
  width: 100%;
}
.nd-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.nd-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.nd-title-tip {
  cursor: help;
  display: inline-flex;
  align-items: center;
}
.nd-title-info {
  margin-left: 4px;
  font-size: 14px;
  color: #c0c4cc;
}
.nd-title-tip:hover .nd-title-info {
  color: #409eff;
}
.nd-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.nd-action-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;
}
.nd-action-icon:hover {
  color: #409eff;
}
.nd-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.nd-tab {
  color: #303133;
  font-weight: 600;
}
.nd-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.nd-gran:hover {
  color: #409eff;
}
.nd-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.nd-sep {
  color: #dcdfe6;
}
.nd-tabs-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}
.nd-summary {
  margin-top: 14px;
}
.nd-date {
  font-size: 13px;
  color: #909399;
}
.nd-metrics {
  margin-top: 6px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}
.nd-metric {
  display: flex;
  align-items: baseline;
  gap: 4px;
}
.nd-metric-right {
  margin-left: auto;
}
.nd-num {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.1;
}
.nd-unit {
  font-size: 13px;
  color: #606266;
}
.nd-legend {
  margin-left: 10px;
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  font-weight: 400;
  color: #606266;
}
.nd-legend i {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
  margin-right: 4px;
}
.nd-body {
  margin-top: 8px;
}
.nd-chart {
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
