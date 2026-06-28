<template>
  <el-card class="page-card bl-card" shadow="never">
    <div class="bl-header">
      <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!tip">
        <template #content>
          <div style="max-width: 340px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ tip }}</div>
        </template>
        <span class="bl-title" :class="{ 'bl-title-tip': tip }">
          {{ title }}<el-icon v-if="tip" class="bl-title-info"><InfoFilled /></el-icon>
        </span>
      </el-tooltip>
      <div class="bl-actions">
        <el-tooltip content="刷新" placement="top">
          <el-icon class="bl-action-icon" @click="load"><Refresh /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <div class="bl-tabs">
      <!-- 统计粒度：按天/周/月 -->
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="bl-tab bl-gran">{{ DIM_LABEL[dim] }}<i class="bl-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>

      <span class="bl-sep">|</span>

      <!-- 指标选择（2/2）：至少保留一个 -->
      <el-popover trigger="click" placement="bottom-start" :width="200" popper-class="gm-pop">
        <template #reference>
          <span class="bl-tab bl-gran">指标 ({{ metricCount }}/2)<i class="bl-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <label class="gm-check"><el-checkbox v-model="showLine" :disabled="!showBar" />{{ lineName }}</label>
          <label class="gm-check"><el-checkbox v-model="showBar" :disabled="!showLine" />{{ barName }}</label>
        </div>
      </el-popover>

      <span class="bl-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" :multi-only="true" @change="onRange" />

      <div class="bl-tabs-right">
        <el-icon class="bl-action-icon" @click="showLabel = !showLabel">
          <component :is="showLabel ? View : Hide" />
        </el-icon>
      </div>
    </div>

    <!-- 概览：日期 + 时长（左）+ 次数（右） -->
    <div class="bl-summary">
      <div class="bl-date">{{ data.latestLabel || '—' }}</div>
      <div class="bl-metrics">
        <div v-if="showLine" class="bl-metric">
          <span class="bl-num">{{ fmtLine(data.lineLatest) }}</span>
          <span class="bl-unit">{{ lineUnit }}</span>
          <span class="bl-legend"><i :style="{ background: LINE_COLOR }"></i>{{ lineName }}</span>
        </div>
        <div v-if="showBar" class="bl-metric bl-metric-right">
          <span class="bl-num">{{ fmtBar(data.barLatest) }}</span>
          <span class="bl-unit">{{ barUnit }}</span>
          <span class="bl-legend"><i :style="{ background: BAR_COLOR }"></i>{{ barName }}</span>
        </div>
      </div>
    </div>

    <div class="bl-body" v-loading="loading">
      <div ref="chartRef" class="bl-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Refresh, View, Hide, InfoFilled } from '@element-plus/icons-vue'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '人均登录次数和在线时长' },
  tip: { type: String, default: '' }, // 标题悬停提示：数据计算口径（支持 \n 换行）
  // 柱：人均登录次数（小数值，独立右轴）
  barName: { type: String, default: '人均登录次数' },
  barUnit: { type: String, default: '次' },
  barDecimals: { type: Number, default: 2 },
  // 线：人均登录时长（大数值，独立左轴）
  lineName: { type: String, default: '人均登录时长' },
  lineUnit: { type: String, default: '秒' },
  lineDecimals: { type: Number, default: 2 },
  api: { type: Function, required: true }, // 返回 { labels, bars, lines, barLatest, lineLatest, latestLabel }
  defaultPreset: { type: String, default: 'past7' }
})

const BAR_COLOR = '#3b82f6'
const LINE_COLOR = '#f59e0b'
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }

const loading = ref(false)
const data = reactive({ labels: [], bars: [], lines: [], barLatest: null, lineLatest: null, latestLabel: '' })
const chartRef = ref()
let chart

const dim = ref('day')
const rangeStart = ref('')
const rangeEnd = ref('')
const showBar = ref(true)
const showLine = ref(true)
const showLabel = ref(false)
const granPopRef = ref()
const dateRangeRef = ref()

const metricCount = computed(() => (showBar.value ? 1 : 0) + (showLine.value ? 1 : 0))

function fmtNum(v, decimals) {
  if (v === null || v === undefined) return '—'
  return Number(v).toLocaleString('zh-CN', { minimumFractionDigits: decimals, maximumFractionDigits: decimals })
}
const fmtBar = (v) => fmtNum(v, props.barDecimals)
const fmtLine = (v) => fmtNum(v, props.lineDecimals)

const EMPTY = { labels: [], bars: [], lines: [], barLatest: null, lineLatest: null, latestLabel: '' }

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

  // 轴顺序：线（时长，左轴）在前，柱（次数，右轴）在后；任一指标隐藏时单独占左轴
  if (showLine.value) {
    yAxis.push({
      type: 'value',
      min: 0,
      position: 'left',
      axisLabel: { color: '#909399', formatter: (v) => Number(v).toLocaleString('zh-CN') },
      splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
    })
  }
  if (showBar.value) {
    yAxis.push({
      type: 'value',
      min: 0,
      position: showLine.value ? 'right' : 'left',
      axisLabel: { color: '#909399', formatter: (v) => Number(v).toLocaleString('zh-CN') },
      splitLine: showLine.value ? { show: false } : { lineStyle: { type: 'dashed', color: '#ebeef5' } }
    })
  }
  const lineAxisIndex = 0
  const barAxisIndex = showLine.value ? 1 : 0

  if (showLine.value) {
    legend.push(props.lineName)
    series.push({
      name: props.lineName,
      type: 'line',
      yAxisIndex: lineAxisIndex,
      smooth: false,
      symbol: 'circle',
      symbolSize: 6,
      label: {
        show: showLabel.value,
        position: 'top',
        color: '#e6820a',
        fontSize: 11,
        formatter: (p) => (p.value == null ? '' : fmtLine(p.value))
      },
      data: (data.lines || []).map((v) => (v == null ? null : Number(v))),
      itemStyle: { color: LINE_COLOR },
      lineStyle: { color: LINE_COLOR, width: 2 }
    })
  }
  if (showBar.value) {
    legend.push(props.barName)
    series.push({
      name: props.barName,
      type: 'bar',
      yAxisIndex: barAxisIndex,
      barMaxWidth: 28,
      label: {
        show: showLabel.value,
        position: 'top',
        color: '#303133',
        fontSize: 11,
        formatter: (p) => (p.value == null ? '' : fmtBar(p.value))
      },
      data: (data.bars || []).map((v) => (v == null ? null : Number(v))),
      itemStyle: { color: BAR_COLOR, borderRadius: [2, 2, 0, 0] }
    })
  }

  chart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          const rows = [params[0].axisValue]
          params.forEach((p) => {
            const isBar = p.seriesName === props.barName
            const val =
              p.value == null
                ? '-'
                : isBar
                  ? `${fmtBar(p.value)} ${props.barUnit}`
                  : `${fmtLine(p.value)} ${props.lineUnit}`
            rows.push(`${p.marker}${p.seriesName}: ${val}`)
          })
          return rows.join('<br/>')
        }
      },
      legend: { data: legend, bottom: 0, icon: 'roundRect' },
      grid: { left: 56, right: 48, top: 24, bottom: 40 },
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
  // 切粒度时按维度套用默认窗口（与其它趋势面板一致）
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

watch([showBar, showLine, showLabel], render)

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
.bl-card {
  width: 100%;
}
.bl-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.bl-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.bl-title-tip {
  cursor: help;
  display: inline-flex;
  align-items: center;
}
.bl-title-info {
  margin-left: 4px;
  font-size: 14px;
  color: #c0c4cc;
}
.bl-title-tip:hover .bl-title-info {
  color: #409eff;
}
.bl-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.bl-action-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;
}
.bl-action-icon:hover {
  color: #409eff;
}
.bl-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.bl-tab {
  color: #303133;
  font-weight: 600;
}
.bl-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.bl-gran:hover {
  color: #409eff;
}
.bl-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.bl-sep {
  color: #dcdfe6;
}
.bl-tabs-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}
.bl-summary {
  margin-top: 14px;
}
.bl-date {
  font-size: 13px;
  color: #909399;
}
.bl-metrics {
  margin-top: 6px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}
.bl-metric {
  display: flex;
  align-items: baseline;
  gap: 4px;
}
.bl-metric-right {
  margin-left: auto;
}
.bl-num {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.1;
}
.bl-unit {
  font-size: 13px;
  color: #606266;
}
.bl-legend {
  margin-left: 10px;
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  font-weight: 400;
  color: #606266;
}
.bl-legend i {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
  margin-right: 4px;
}
.bl-body {
  margin-top: 8px;
}
.bl-chart {
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
