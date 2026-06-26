<template>
  <el-card class="page-card ro-card" shadow="never">
    <div class="ro-header">
      <span class="ro-title">{{ title }}</span>
      <div class="ro-actions">
        <el-tooltip content="刷新" placement="top">
          <el-icon class="ro-action-icon" @click="load"><Refresh /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <div class="ro-tabs">
      <!-- 统计粒度菜单 -->
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="ro-tab ro-gran">{{ granLabel }}<i class="ro-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: mode === 'intraday' && bucket === 60 }" @click="pickBucket(60)">按小时</div>
          <div class="gm-row" :class="{ active: mode === 'trend' && dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: mode === 'trend' && dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: mode === 'trend' && dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>

      <span class="ro-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" :single="mode === 'intraday'" :multi-only="mode === 'trend'" @change="onRange" />

      <div class="ro-tabs-right">
        <el-icon class="ro-action-icon" @click="showSymbol = !showSymbol">
          <component :is="showSymbol ? View : Hide" />
        </el-icon>
      </div>
    </div>

    <div class="ro-body" v-loading="loading">
      <div class="ro-summary">
        <div class="ro-date">{{ data.latestLabel || '—' }}</div>
        <div class="ro-value">{{ fmtNum(data.latest) }}</div>
        <template v-if="showStats">
          <div class="ro-stat"><span class="ro-stat-label">均值</span><span class="ro-stat-num">{{ fmtNum(data.mean) }}</span></div>
          <div class="ro-stat"><span class="ro-stat-label">总和</span><span class="ro-stat-num">{{ fmtNum(data.sum) }}</span></div>
        </template>
      </div>
      <div ref="chartRef" class="ro-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Refresh, View, Hide } from '@element-plus/icons-vue'
import { getRealtimeOnline, getOnlineTrend } from '@/api/dashboard'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '实时在线' },
  seriesName: { type: String, default: '实时在线人数' }, // intraday 主线图例
  trendSeriesName: { type: String, default: '活跃用户数' }, // trend 线图例
  defaultMode: { type: String, default: 'trend' }, // 默认粒度模式：intraday(按小时) | trend(按天/周/月)
  defaultPreset: { type: String, default: 'recent7' }, // 日期面板初始预设
  intradayApi: { type: Function, default: null }, // 单日/区间数据接口，默认 getRealtimeOnline
  trendApi: { type: Function, default: null }, // 趋势数据接口，默认 getOnlineTrend
  extraParams: { type: Object, default: () => ({}) }, // 附加请求参数（如 { cumulative: 1 }）
  showStats: { type: Boolean, default: false } // 是否显示均值/总和
})

const fetchIntraday = props.intradayApi || getRealtimeOnline
const fetchTrend = props.trendApi || getOnlineTrend

const loading = ref(false)
const data = reactive({ labels: [], prevLabels: [], today: [], prev: [], values: [], latest: null, latestLabel: '', mean: null, sum: null })
const chartRef = ref()
let chart

const mode = ref(props.defaultMode)
const bucket = ref(60) // intraday 分桶（分钟）：固定 60(按小时，最小粒度)
const dim = ref('day') // trend 维度：day/week/month
const targetDate = ref('') // intraday 选中的统计日 yyyy-MM-dd（空=后端默认今日）
const rangeStart = ref('')
const rangeEnd = ref('')
const granPopRef = ref()
const dateRangeRef = ref()

const showSymbol = ref(false) // 眼睛图标：显示/隐藏数据点 + 数值标签

const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }
const granLabel = computed(() => (mode.value === 'trend' ? DIM_LABEL[dim.value] : '按小时'))

// 千分位 + 智能小数：整数不显示小数，非整数保留 2 位（兼容人数与金额）
function fmtNum(v) {
  if (v === null || v === undefined) return '—'
  const n = Number(v)
  if (!isFinite(n)) return '—'
  return Number.isInteger(n)
    ? n.toLocaleString('zh-CN')
    : n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
function fmtDate(d) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
// 日内曲线 X 轴双行日期：跟随所选统计日（targetDate 为空时即今日）及其前一日
const baseDate = computed(() => (targetDate.value ? new Date(targetDate.value + 'T00:00:00') : new Date()))
const todayDate = computed(() => fmtDate(baseDate.value))
const prevDate = computed(() => fmtDate(new Date(baseDate.value.getTime() - 86400000)))

const VALUE_LABEL = {
  show: false,
  position: 'top',
  color: '#303133',
  fontSize: 12,
  formatter: (p) => (p.value === null || p.value === undefined ? '' : Math.round(p.value))
}
function valueLabel() {
  return { ...VALUE_LABEL, show: showSymbol.value }
}

function render() {
  if (!chart) return
  if (mode.value === 'trend') {
    chart.setOption(
      {
        tooltip: { trigger: 'axis', valueFormatter: (v) => fmtNum(v) },
        legend: { data: [props.trendSeriesName], bottom: 0, icon: 'roundRect' },
        grid: { left: 56, right: 24, top: 24, bottom: 70 },
        dataZoom: [
          { type: 'slider', bottom: 28, height: 16, start: 0, end: 100 },
          { type: 'inside' }
        ],
        yAxis: {
          type: 'value',
          min: 0,
          minInterval: 1,
          axisLabel: { color: '#909399', formatter: (v) => v.toLocaleString('zh-CN') },
          splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: data.labels,
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#909399' }
        },
        series: [
          {
            name: props.trendSeriesName,
            type: 'line',
            symbol: 'circle',
            symbolSize: showSymbol.value ? 6 : 0,
            showSymbol: showSymbol.value,
            label: valueLabel(),
            data: data.values,
            itemStyle: { color: '#3b82f6' },
            lineStyle: { color: '#3b82f6', width: 2 }
          }
        ]
      },
      true
    )
    return
  }

  // 区间模式：后端回传 prevLabels（虚线每点完整时刻），X 轴双行用它；对比线名为「上一周期」
  const isRange = !!(data.prevLabels && data.prevLabels.length)
  const series = [
    {
      name: props.seriesName,
      type: 'line',
      showSymbol: showSymbol.value,
      sampling: 'lttb',
      label: valueLabel(),
      labelLayout: { hideOverlap: true },
      connectNulls: false,
      data: data.today,
      itemStyle: { color: '#3b82f6' },
      lineStyle: { color: '#3b82f6', width: 2 }
    },
    {
      name: isRange ? '上一周期' : '昨日',
      type: 'line',
      showSymbol: showSymbol.value,
      sampling: 'lttb',
      label: valueLabel(),
      labelLayout: { hideOverlap: true },
      data: data.prev,
      itemStyle: { color: '#93c5fd' },
      lineStyle: { color: '#93c5fd', width: 1.5, type: 'dashed' }
    }
  ]
  chart.setOption(
    {
      tooltip: { trigger: 'axis', valueFormatter: (v) => fmtNum(v) },
      legend: { data: [props.seriesName], bottom: 0, icon: 'roundRect' },
      grid: { left: 56, right: 24, top: 24, bottom: 70 },
      dataZoom: [
        { type: 'slider', bottom: 28, height: 16, start: 0, end: 100 },
        { type: 'inside' }
      ],
      yAxis: {
        type: 'value',
        min: 0,
        minInterval: 1,
        axisLabel: { color: '#909399', formatter: (v) => v.toLocaleString('zh-CN') },
        splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: data.labels,
        axisLine: { lineStyle: { color: '#dcdfe6' } },
        axisLabel: {
          color: '#909399',
          formatter: isRange
            ? (value, index) => `${value}\n${data.prevLabels[index] || ''}`
            : (value) => `${todayDate.value} ${value}\n${prevDate.value} ${value}`
        }
      },
      series
    },
    true
  )
}

const EMPTY = { labels: [], prevLabels: [], today: [], prev: [], values: [], latest: null, latestLabel: '', mean: null, sum: null }

async function load() {
  loading.value = true
  try {
    let res
    const isRange = rangeStart.value && rangeEnd.value && rangeStart.value !== rangeEnd.value
    if (mode.value === 'trend') {
      const params = { dim: dim.value, ...props.extraParams }
      if (isRange) {
        params.start = rangeStart.value
        params.end = rangeEnd.value
      }
      res = await fetchTrend(params)
    } else {
      const params = { bucket: bucket.value, ...props.extraParams }
      if (isRange) {
        params.start = rangeStart.value
        params.end = rangeEnd.value
      } else if (targetDate.value) {
        params.date = targetDate.value
      }
      res = await fetchIntraday(params)
    }
    Object.assign(data, EMPTY, res.data || {})
    await nextTick()
    render()
  } catch (e) {
    /* 静默：拦截器已提示 */
  } finally {
    loading.value = false
  }
}

function pickBucket(m) {
  granPopRef.value && granPopRef.value.hide()
  if (mode.value === 'intraday' && bucket.value === m) return
  mode.value = 'intraday'
  bucket.value = m
  // 按小时仅支持单日：若之前是多日区间，收敛为结束日单日
  if (rangeStart.value && rangeEnd.value && rangeStart.value !== rangeEnd.value) {
    targetDate.value = rangeEnd.value
    rangeStart.value = ''
    rangeEnd.value = ''
  }
  load()
}

function pickDim(d) {
  granPopRef.value && granPopRef.value.hide()
  if (mode.value === 'trend' && dim.value === d) return
  mode.value = 'trend'
  dim.value = d
  // 切换维度时的默认区间：按天=最近7天、按周=过去30天、按月=过去一年
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
  targetDate.value = start && end && start === end ? end : ''
  load()
}

watch(showSymbol, render)

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
.ro-card {
  width: 100%;
}
.ro-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.ro-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.ro-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.ro-action-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;
}
.ro-action-icon:hover {
  color: #409eff;
}
.ro-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.ro-tab {
  color: #303133;
  font-weight: 600;
}
.ro-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.ro-gran:hover {
  color: #409eff;
}
.ro-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.ro-sep {
  color: #dcdfe6;
}
.ro-tabs-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}
.ro-body {
  display: flex;
  align-items: stretch;
  margin-top: 12px;
}
.ro-summary {
  width: 160px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
  padding-left: 4px;
}
.ro-date {
  font-size: 13px;
  color: #909399;
}
.ro-value {
  font-size: 34px;
  font-weight: 700;
  line-height: 1.1;
  color: #303133;
}
.ro-stat {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.ro-stat-label {
  font-size: 13px;
  color: #909399;
}
.ro-stat-num {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}
.ro-chart {
  flex: 1;
  min-width: 0;
  height: 320px;
}

/* 统计粒度菜单 */
.gm-menu {
  display: flex;
  flex-direction: column;
}
.gm-row {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
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
</style>

<style>
.gm-pop.el-popover.el-popper {
  padding: 6px;
}
</style>
