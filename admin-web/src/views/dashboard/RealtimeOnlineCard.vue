<template>
  <el-card class="page-card ro-card" shadow="never">
    <div class="ro-header">
      <span class="ro-title">实时在线人数</span>
    </div>
    <div class="ro-tabs">
      <!-- 统计粒度菜单 -->
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="ro-tab ro-gran">{{ granLabel }}<i class="ro-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: minuteMode }" @mouseenter="sub = 'minute'" @mouseleave="sub = ''">
            <span>按分钟</span><span class="gm-arrow">›</span>
            <div v-show="sub === 'minute'" class="gm-sub">
              <div
                v-for="m in [1, 5, 10]"
                :key="m"
                class="gm-sub-item"
                :class="{ active: bucket === m }"
                @click="pickBucket(m)"
              >{{ m }}分钟</div>
            </div>
          </div>
          <div class="gm-row" :class="{ active: mode === 'intraday' && bucket === 60 }" @click="pickBucket(60)">按小时</div>
          <div class="gm-row" :class="{ active: mode === 'trend' && dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: mode === 'trend' && dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: mode === 'trend' && dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>
      <span class="ro-sep">|</span>
      <!-- 日期范围面板 -->
      <DateRangePanel default-preset="today" @change="onRange" />
      <span class="ro-sep">|</span>
      <span class="ro-tab">VS</span>
    </div>

    <div class="ro-body" v-loading="loading">
      <div class="ro-summary">
        <div class="ro-date">{{ data.latestLabel || '—' }}</div>
        <div class="ro-value">{{ data.latest ?? '—' }}</div>
        <div class="ro-stat"><span class="ro-stat-label">均值</span><span class="ro-stat-num">{{ fmt2(data.mean) }}</span></div>
        <div class="ro-stat"><span class="ro-stat-label">总和</span><span class="ro-stat-num">{{ fmtInt(data.sum) }}</span></div>
      </div>
      <div ref="chartRef" class="ro-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getRealtimeOnline, getOnlineTrend } from '@/api/dashboard'
import DateRangePanel from './DateRangePanel.vue'

const loading = ref(false)
const data = reactive({ labels: [], today: [], prev: [], values: [], latest: null, latestLabel: '', mean: null, sum: null })
const chartRef = ref()
let chart

// mode: intraday(单日曲线，今日+昨日) | trend(多日日均在线趋势)
const mode = ref('intraday')
const bucket = ref(5) // intraday 分桶（分钟）：1/5/10/60(按小时)
const dim = ref('day') // trend 维度：day/week/month
const targetDate = ref('') // intraday 选中的统计日 yyyy-MM-dd（空=后端默认今日）
const rangeStart = ref('') // 日期面板所选区间起（趋势模式用）
const rangeEnd = ref('')
const granPopRef = ref()
const sub = ref('')

const minuteMode = computed(() => mode.value === 'intraday' && [1, 5, 10].includes(bucket.value))
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }
const granLabel = computed(() => {
  if (mode.value === 'trend') return DIM_LABEL[dim.value]
  return bucket.value === 60 ? '按小时' : `按${bucket.value}分钟`
})

function fmt2(v) {
  if (v === null || v === undefined) return '—'
  return Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
function fmtInt(v) {
  if (v === null || v === undefined) return '—'
  return Number(v).toLocaleString('zh-CN')
}

const COMMON_AXIS = {
  grid: { left: 44, right: 24, top: 24, bottom: 70 },
  dataZoom: [
    { type: 'slider', bottom: 28, height: 16, start: 0, end: 100 },
    { type: 'inside' }
  ],
  yAxis: {
    type: 'value',
    min: 0,
    axisLabel: { color: '#909399' },
    splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
  }
}

function render() {
  if (!chart) return
  if (mode.value === 'trend') {
    // 多日「日均在线人数」单线趋势
    chart.setOption(
      {
        tooltip: { trigger: 'axis', valueFormatter: (v) => (v === null || v === undefined ? '—' : Number(v).toFixed(2)) },
        legend: { data: ['日均在线人数'], bottom: 0, icon: 'roundRect' },
        ...COMMON_AXIS,
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: data.labels,
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#909399' }
        },
        series: [
          {
            name: '日均在线人数',
            type: 'line',
            symbol: 'circle',
            symbolSize: 6,
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
  // 单日内曲线：今日(实线) + 昨日(虚线)
  chart.setOption(
    {
      tooltip: { trigger: 'axis' },
      legend: { data: ['实时在线人数'], bottom: 0, icon: 'roundRect' },
      ...COMMON_AXIS,
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: data.labels,
        axisLine: { lineStyle: { color: '#dcdfe6' } },
        axisLabel: { color: '#909399' }
      },
      series: [
        {
          name: '实时在线人数',
          type: 'line',
          showSymbol: false,
          smooth: true,
          connectNulls: false,
          data: data.today,
          itemStyle: { color: '#3b82f6' },
          lineStyle: { color: '#3b82f6', width: 2 }
        },
        {
          name: '昨日',
          type: 'line',
          showSymbol: false,
          smooth: true,
          data: data.prev,
          itemStyle: { color: '#93c5fd' },
          lineStyle: { color: '#93c5fd', width: 1.5, type: 'dashed' }
        }
      ]
    },
    true
  )
}

const EMPTY = { labels: [], today: [], prev: [], values: [], latest: null, latestLabel: '', mean: null, sum: null }

async function load() {
  loading.value = true
  try {
    let res
    if (mode.value === 'trend') {
      const params = { dim: dim.value }
      // 仅当选了真正的多日区间时让面板控制范围；单日(默认今日)则用 dim 的默认窗口
      if (rangeStart.value && rangeEnd.value && rangeStart.value !== rangeEnd.value) {
        params.start = rangeStart.value
        params.end = rangeEnd.value
      }
      res = await getOnlineTrend(params)
    } else {
      const params = { bucket: bucket.value }
      if (targetDate.value) params.date = targetDate.value
      res = await getRealtimeOnline(params)
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
  sub.value = ''
  if (mode.value === 'intraday' && bucket.value === m) return
  mode.value = 'intraday'
  bucket.value = m
  load()
}

function pickDim(d) {
  granPopRef.value && granPopRef.value.hide()
  sub.value = ''
  if (mode.value === 'trend' && dim.value === d) return
  mode.value = 'trend'
  dim.value = d
  load()
}

// 单日曲线模式：取区间结束日作为统计日；趋势模式：用区间作为时间范围
function onRange({ start, end }) {
  rangeStart.value = start || ''
  rangeEnd.value = end || ''
  targetDate.value = end || ''
  load()
}

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
.ro-card {
  width: 100%;
}
.ro-header {
  display: flex;
  align-items: center;
}
.ro-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.ro-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.ro-tab {
  color: #909399;
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
.gm-arrow {
  font-size: 12px;
}
.gm-sub {
  position: absolute;
  left: 100%;
  top: -6px;
  margin-left: 4px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  padding: 6px;
  min-width: 96px;
  z-index: 10;
}
.gm-sub-item {
  padding: 8px 12px;
  font-size: 13px;
  color: #606266;
  border-radius: 4px;
  white-space: nowrap;
}
.gm-sub-item:hover {
  background: #f5f7fa;
}
.gm-sub-item.active {
  color: #409eff;
  font-weight: 600;
}
</style>

<style>
.gm-pop.el-popover.el-popper {
  padding: 6px;
}
</style>
