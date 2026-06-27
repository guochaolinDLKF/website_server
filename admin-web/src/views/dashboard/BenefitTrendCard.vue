<template>
  <el-card class="page-card bt-card" shadow="never">
    <div class="bt-header">
      <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!tip">
        <template #content>
          <div style="max-width: 340px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ tip }}</div>
        </template>
        <span class="bt-title" :class="{ 'bt-title-tip': tip }">
          {{ title }}<el-icon v-if="tip" class="bt-title-info"><InfoFilled /></el-icon>
        </span>
      </el-tooltip>
      <div class="bt-actions">
        <el-tooltip content="刷新" placement="top">
          <el-icon class="bt-action-icon" @click="load"><Refresh /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <div class="bt-tabs">
      <!-- 统计粒度 -->
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="bt-tab bt-gran">{{ DIM_LABEL[dim] }}<i class="bt-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>

      <span class="bt-sep">|</span>

      <!-- 分组（权益）选择 -->
      <el-popover trigger="click" placement="bottom-start" :width="200" popper-class="gm-pop">
        <template #reference>
          <span class="bt-tab bt-gran">分组 ({{ visibleCount }}/{{ benefits.length }})<i class="bt-caret">▾</i></span>
        </template>
        <div class="gm-menu bt-group-menu">
          <div v-if="!benefits.length" class="gm-empty">暂无数据</div>
          <label v-for="(name, i) in benefits" :key="name" class="gm-check">
            <el-checkbox :model-value="!hidden.includes(name)" @change="toggleBenefit(name)" />
            <i class="bt-dot" :style="{ background: colorOf(i) }"></i>{{ name }}
          </label>
        </div>
      </el-popover>

      <span class="bt-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" :multi-only="true" @change="onRange" />

      <div class="bt-tabs-right">
        <el-icon class="bt-action-icon" @click="showLabel = !showLabel">
          <component :is="showLabel ? View : Hide" />
        </el-icon>
      </div>
    </div>

    <div class="bt-body" v-loading="loading">
      <div ref="chartRef" class="bt-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Refresh, View, Hide, InfoFilled } from '@element-plus/icons-vue'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '付费流水构成（按权益）' },
  tip: { type: String, default: '' },
  api: { type: Function, required: true }, // 返回 { labels, benefits, series:[{name,data}], latestLabel }
  valueType: { type: String, default: 'amount' }, // amount=金额 | percent=百分比
  defaultVisible: { type: Number, default: 0 }, // >0 时首次仅默认显示前 N 条线（其余可在分组里勾选）
  defaultPreset: { type: String, default: 'past7' }
})

const PALETTE = ['#3b82f6', '#f59e0b', '#6366f1', '#22d3ee', '#fbbf24', '#0ea5e9', '#10b981', '#f97316', '#8b5cf6', '#ec4899', '#14b8a6', '#ef4444', '#a3a3a3']
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }

const loading = ref(false)
const raw = reactive({ labels: [], benefits: [], series: [] })
const chartRef = ref()
let chart

const dim = ref('day')
const rangeStart = ref('')
const rangeEnd = ref('')
const showLabel = ref(false)
const hidden = ref([]) // 被取消勾选的权益名
const granPopRef = ref()
const dateRangeRef = ref()
let inited = false // 是否已应用过默认显示数

const benefits = computed(() => raw.benefits || [])
const visibleCount = computed(() => benefits.value.filter((n) => !hidden.value.includes(n)).length)

function colorOf(i) {
  return PALETTE[i % PALETTE.length]
}
function dataOf(name) {
  const s = raw.series.find((x) => x.name === name)
  return s ? s.data : []
}
function fmtVal(v) {
  if (v === null || v === undefined) return '—'
  const n = Number(v)
  if (props.valueType === 'percent') return `${n.toFixed(2)}%`
  return n.toLocaleString('zh-CN', { maximumFractionDigits: 2 })
}
function fmtAxis(v) {
  if (props.valueType === 'percent') return `${v}%`
  return Number(v).toLocaleString('zh-CN')
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
    const d = res.data || {}
    Object.assign(raw, { labels: [], benefits: [], series: [] }, d)
    // 首次加载：若设置了默认显示数，仅默认显示前 N 条线（其余收起，可在分组里勾选）
    if (!inited && props.defaultVisible > 0 && raw.benefits.length > props.defaultVisible) {
      hidden.value = raw.benefits.slice(props.defaultVisible)
    }
    inited = true
    hidden.value = hidden.value.filter((n) => raw.benefits.includes(n))
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
  const vis = benefits.value.map((name, i) => ({ name, i })).filter((x) => !hidden.value.includes(x.name))
  const legend = vis.map((x) => x.name)
  const series = vis.map((x) => ({
    name: x.name,
    type: 'line',
    smooth: false,
    symbol: 'circle',
    symbolSize: 5,
    showSymbol: true,
    label: { show: showLabel.value, position: 'top', fontSize: 10, color: '#606266', formatter: (p) => (p.value == null ? '' : fmtAxis(p.value)) },
    lineStyle: { width: 2 },
    emphasis: { focus: 'series' },
    data: dataOf(x.name).map((v) => (v == null ? null : Number(v))),
    itemStyle: { color: colorOf(x.i) }
  }))

  chart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        valueFormatter: (v) => (v == null ? '—' : fmtAxis(v))
      },
      legend: { data: legend, bottom: 0, type: 'scroll', icon: 'roundRect' },
      grid: { left: 56, right: 24, top: 20, bottom: 44 },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: raw.labels,
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

function toggleBenefit(name) {
  hidden.value = hidden.value.includes(name) ? hidden.value.filter((n) => n !== name) : [...hidden.value, name]
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
  // 首次加载由 DateRangePanel 挂载时的 change 事件驱动
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart && chart.dispose()
})
</script>

<style scoped>
.bt-card {
  width: 100%;
}
.bt-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.bt-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.bt-title-tip {
  cursor: help;
  display: inline-flex;
  align-items: center;
}
.bt-title-info {
  margin-left: 4px;
  font-size: 14px;
  color: #c0c4cc;
}
.bt-title-tip:hover .bt-title-info {
  color: #409eff;
}
.bt-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.bt-action-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;
}
.bt-action-icon:hover {
  color: #409eff;
}
.bt-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.bt-tab {
  color: #303133;
  font-weight: 600;
}
.bt-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.bt-gran:hover {
  color: #409eff;
}
.bt-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.bt-sep {
  color: #dcdfe6;
}
.bt-tabs-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}
.bt-body {
  margin-top: 12px;
}
.bt-chart {
  width: 100%;
  height: 340px;
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
.bt-group-menu {
  max-height: 280px;
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
.bt-dot {
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
