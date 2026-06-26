<template>
  <el-card class="page-card cs-card" shadow="never">
    <div class="cs-header">
      <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!tip">
        <template #content>
          <div style="max-width: 340px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ tip }}</div>
        </template>
        <span class="cs-title" :class="{ 'cs-title-tip': tip }">
          {{ title }}<el-icon v-if="tip" class="cs-title-info"><InfoFilled /></el-icon>
        </span>
      </el-tooltip>
      <div class="cs-actions">
        <el-tooltip content="刷新" placement="top">
          <el-icon class="cs-action-icon" @click="load"><Refresh /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <div class="cs-tabs">
      <!-- 统计粒度 -->
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="cs-tab cs-gran">{{ DIM_LABEL[dim] }}<i class="cs-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>

      <span class="cs-sep">|</span>

      <!-- 分组（渠道）选择 -->
      <el-popover trigger="click" placement="bottom-start" :width="180" popper-class="gm-pop">
        <template #reference>
          <span class="cs-tab cs-gran">分组 ({{ visibleCount }}/{{ raw.channels.length }})<i class="cs-caret">▾</i></span>
        </template>
        <div class="gm-menu cs-group-menu">
          <div v-if="!raw.channels.length" class="gm-empty">暂无渠道数据</div>
          <label v-for="ch in raw.channels" :key="ch" class="gm-check">
            <el-checkbox :model-value="!hidden.includes(ch)" @change="toggleChannel(ch)" />
            <i class="cs-dot" :style="{ background: colorOf(ch) }"></i>{{ channelName(ch) }}
          </label>
        </div>
      </el-popover>

      <span class="cs-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" :multi-only="true" @change="onRange" />

      <div class="cs-tabs-right">
        <!-- 堆积图开关（仅数量面板） -->
        <span v-if="mode === 'count'" class="cs-tab cs-stack" :class="{ active: stacked }" @click="toggleStack">堆积图</span>
        <el-icon class="cs-action-icon" @click="showLabel = !showLabel">
          <component :is="showLabel ? View : Hide" />
        </el-icon>
      </div>
    </div>

    <!-- 概览：最新一期 Top 渠道 -->
    <div class="cs-summary">
      <div class="cs-date">{{ raw.latestLabel || '—' }}</div>
      <div class="cs-tops">
        <div v-for="it in summaryItems" :key="it.channel" class="cs-top">
          <span class="cs-top-num">{{ it.text }}</span>
          <span class="cs-top-legend"><i :style="{ background: it.color }"></i>{{ it.name }}</span>
        </div>
        <div v-if="!summaryItems.length" class="cs-top-empty">暂无数据</div>
      </div>
    </div>

    <div class="cs-body" v-loading="loading">
      <div ref="chartRef" class="cs-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Refresh, View, Hide, InfoFilled } from '@element-plus/icons-vue'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '各渠道新增玩家数' },
  tip: { type: String, default: '' },
  mode: { type: String, default: 'count' }, // count=堆积面积(绝对数) | ratio=100%堆积(占比)
  api: { type: Function, required: true },
  defaultPreset: { type: String, default: 'recent7' }
})

// 渠道码 → 展示名（不可直接展示原始码）
const CHANNEL_LABEL = {
  XiaoMi: '小米应用商城',
  YingYongBao: '腾讯应用宝',
  Oppo: 'oppo',
  Huawei: '华为应用商城',
  Vivo: 'vivo',
  official: '易德官网'
}
function channelName(code) {
  return CHANNEL_LABEL[code] || '其他'
}

const PALETTE = ['#3b82f6', '#f59e0b', '#6366f1', '#22d3ee', '#10b981', '#ef4444', '#8b5cf6', '#ec4899', '#14b8a6', '#a3a3a3']
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }

const loading = ref(false)
const raw = reactive({ labels: [], channels: [], series: [], latestLabel: '' })
const chartRef = ref()
let chart

const dim = ref('day')
const rangeStart = ref('')
const rangeEnd = ref('')
const showLabel = ref(false)
const stacked = ref(true) // 数量面板：是否堆积
const hidden = ref([]) // 被取消勾选的渠道码
const granPopRef = ref()
const dateRangeRef = ref()

const visibleChannels = computed(() => raw.channels.filter((c) => !hidden.value.includes(c)))
const visibleCount = computed(() => visibleChannels.value.length)

// 渠道码 → 颜色（按 channels 顺序稳定分配）
function colorOf(code) {
  const i = raw.channels.indexOf(code)
  return PALETTE[(i < 0 ? 0 : i) % PALETTE.length]
}

function dataOf(code) {
  const s = raw.series.find((x) => x.channel === code)
  return s ? s.data : []
}

// 概览：最新一期 Top3 渠道（数量或占比）
const summaryItems = computed(() => {
  const idx = raw.labels.length - 1
  if (idx < 0) return []
  const vis = visibleChannels.value
  const vals = vis.map((c) => ({ channel: c, name: channelName(c), color: colorOf(c), v: Number(dataOf(c)[idx] || 0) }))
  if (props.mode === 'ratio') {
    const total = vals.reduce((s, x) => s + x.v, 0)
    vals.forEach((x) => (x.text = total > 0 ? `${((x.v / total) * 100).toFixed(2)}%` : '0.00%'))
  } else {
    vals.forEach((x) => (x.text = x.v.toLocaleString('zh-CN')))
  }
  return vals.sort((a, b) => b.v - a.v).slice(0, 3)
})

async function load() {
  loading.value = true
  try {
    const params = { dim: dim.value }
    if (rangeStart.value && rangeEnd.value) {
      params.start = rangeStart.value
      params.end = rangeEnd.value
    }
    const res = await props.api(params)
    const d = res.data || {}
    Object.assign(raw, { labels: [], channels: [], series: [], latestLabel: '' }, d)
    // 移除已不存在的渠道隐藏项（新渠道默认可见）
    hidden.value = hidden.value.filter((c) => raw.channels.includes(c))
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
  const vis = visibleChannels.value
  const legend = vis.map(channelName)

  let yAxis
  let series
  if (props.mode === 'ratio') {
    // 100% 堆积：每期按可见渠道归一化为百分比
    const totals = raw.labels.map((_, i) => vis.reduce((s, c) => s + Number(dataOf(c)[i] || 0), 0))
    yAxis = {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { color: '#909399', formatter: '{value}%' },
      splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
    }
    series = vis.map((c) => ({
      name: channelName(c),
      type: 'bar',
      stack: 'total',
      barMaxWidth: 26,
      label: { show: showLabel.value, color: '#fff', fontSize: 10, formatter: (p) => (p.value ? Math.round(p.value) + '%' : '') },
      data: raw.labels.map((_, i) => (totals[i] > 0 ? Number(((Number(dataOf(c)[i] || 0) / totals[i]) * 100).toFixed(2)) : 0)),
      itemStyle: { color: colorOf(c) }
    }))
  } else {
    // 堆积面积（或非堆积折线）
    yAxis = {
      type: 'value',
      min: 0,
      minInterval: 1,
      axisLabel: { color: '#909399', formatter: (v) => v.toLocaleString('zh-CN') },
      splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
    }
    series = vis.map((c) => ({
      name: channelName(c),
      type: 'line',
      stack: stacked.value ? 'total' : undefined,
      smooth: true,
      symbol: 'circle',
      symbolSize: showLabel.value ? 5 : 0,
      showSymbol: showLabel.value,
      label: { show: showLabel.value, position: 'top', fontSize: 10, color: '#606266' },
      areaStyle: stacked.value ? { opacity: 0.6 } : undefined,
      lineStyle: { width: 2 },
      emphasis: { focus: 'series' },
      data: dataOf(c).map((v) => Number(v || 0)),
      itemStyle: { color: colorOf(c) }
    }))
  }

  chart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: props.mode === 'ratio' ? 'shadow' : 'line' },
        valueFormatter: props.mode === 'ratio' ? (v) => v + '%' : undefined
      },
      legend: { data: legend, bottom: 0, type: 'scroll', icon: 'roundRect' },
      grid: { left: 48, right: 24, top: 20, bottom: 44 },
      xAxis: {
        type: 'category',
        boundaryGap: props.mode === 'ratio',
        data: raw.labels,
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

function toggleChannel(ch) {
  hidden.value = hidden.value.includes(ch) ? hidden.value.filter((c) => c !== ch) : [...hidden.value, ch]
  render()
}

function toggleStack() {
  stacked.value = !stacked.value
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
.cs-card {
  width: 100%;
}
.cs-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.cs-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.cs-title-tip {
  cursor: help;
  display: inline-flex;
  align-items: center;
}
.cs-title-info {
  margin-left: 4px;
  font-size: 14px;
  color: #c0c4cc;
}
.cs-title-tip:hover .cs-title-info {
  color: #409eff;
}
.cs-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.cs-action-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;
}
.cs-action-icon:hover {
  color: #409eff;
}
.cs-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.cs-tab {
  color: #303133;
  font-weight: 600;
}
.cs-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.cs-gran:hover {
  color: #409eff;
}
.cs-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.cs-sep {
  color: #dcdfe6;
}
.cs-tabs-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}
.cs-stack {
  cursor: pointer;
  padding: 1px 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  color: #909399;
  font-size: 12px;
  font-weight: 400;
}
.cs-stack:hover {
  color: #409eff;
  border-color: #c6e2ff;
}
.cs-stack.active {
  color: #fff;
  background: #409eff;
  border-color: #409eff;
}
.cs-summary {
  margin-top: 14px;
}
.cs-date {
  font-size: 13px;
  color: #909399;
}
.cs-tops {
  margin-top: 6px;
  display: flex;
  gap: 48px;
}
.cs-top {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.cs-top-num {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.1;
}
.cs-top-legend {
  display: inline-flex;
  align-items: center;
  font-size: 12px;
  color: #606266;
}
.cs-top-legend i {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
  margin-right: 4px;
}
.cs-top-empty {
  font-size: 20px;
  font-weight: 700;
  color: #c0c4cc;
}
.cs-body {
  margin-top: 8px;
}
.cs-chart {
  width: 100%;
  height: 300px;
}

/* 菜单 */
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
.cs-group-menu {
  max-height: 260px;
  overflow-y: auto;
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
.cs-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 2px;
}
.gm-empty {
  padding: 10px 12px;
  font-size: 12px;
  color: #c0c4cc;
}
</style>

<style>
.gm-pop.el-popover.el-popper {
  padding: 6px;
}
</style>
