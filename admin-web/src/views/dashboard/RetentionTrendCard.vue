<template>
  <el-card class="page-card retention-card" shadow="never">
    <div class="rc-header">
      <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!tip">
        <template #content>
          <div style="max-width: 340px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ tip }}</div>
        </template>
        <span class="rc-title" :class="{ 'panel-title-tip': tip }">
          {{ title }}<el-icon v-if="tip" class="panel-title-info"><InfoFilled /></el-icon>
        </span>
      </el-tooltip>
    </div>
    <div class="rc-tabs">
      <!-- 粒度：按天/周/月（showGranularity 时取代固定的「次日/7日」标签） -->
      <el-popover v-if="showGranularity" ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="rc-tab rc-gran">{{ granLabel }}<i class="rc-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>
      <span v-else class="rc-tab active">{{ periodLabel }}</span>
      <span class="rc-sep">|</span>
      <span class="rc-tab active">留存</span>
      <span class="rc-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" multi-only @change="onRange" />
    </div>

    <div class="rc-body" v-loading="loading">
      <div class="rc-summary">
        <div class="rc-block">
          <div class="rc-date">{{ data.latestLabel || '—' }}</div>
          <div class="rc-value">{{ fmtPct(data.latest) }}</div>
        </div>
        <div class="rc-block rc-mean">
          <div class="rc-mean-label">留存率均值</div>
          <div class="rc-mean-value">{{ fmtPct(data.mean) }}</div>
        </div>
      </div>
      <div ref="chartRef" class="rc-chart"></div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { InfoFilled } from '@element-plus/icons-vue'
import { getNewUserRetention } from '@/api/dashboard'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '新增用户次日留存' },
  tip: { type: String, default: '' }, // 标题悬停提示：计算口径（支持 \n 换行）
  periodLabel: { type: String, default: '次日' }, // 留存周期标签：次日 / 7日（showGranularity=false 时显示）
  retentionDay: { type: Number, default: 1 }, // 留存天数：1=次日，7=7日
  defaultPreset: { type: String, default: 'past30' },
  seriesName: { type: String, default: '留存率' },
  // 显示「按天/按周/按月」粒度下拉（取代固定的周期标签）；周/月维度按加权留存率聚合
  showGranularity: { type: Boolean, default: false }
})

const loading = ref(false)
const data = reactive({ points: [], latest: null, latestLabel: '', mean: null })
const chartRef = ref()
const dateRangeRef = ref()
const granPopRef = ref()
let chart

const dim = ref('day') // 统计粒度：day/week/month
const rangeStart = ref('')
const rangeEnd = ref('')
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }
const granLabel = computed(() => DIM_LABEL[dim.value])
// 各粒度默认区间：按天=过去30天、按周/按月=过去一年（保证有足够周期点）
const presetByDim = { day: 'past30', week: 'pastYear', month: 'pastYear' }

function fmtPct(v) {
  if (v === null || v === undefined) return '—'
  return `${Number(v).toFixed(2)}%`
}

function render() {
  if (!chart) return
  const pts = data.points || []
  chart.setOption(
    {
      tooltip: {
        trigger: 'axis',
        valueFormatter: (v) => (v === null || v === undefined ? '—' : `${Number(v).toFixed(2)}%`)
      },
      grid: { left: 48, right: 24, top: 24, bottom: 36 },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: pts.map((p) => p.label),
        axisLine: { lineStyle: { color: '#dcdfe6' } },
        axisLabel: { color: '#909399', interval: 'auto' }
      },
      yAxis: {
        type: 'value',
        min: 0,
        axisLabel: { color: '#909399', formatter: '{value}%' },
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
    const params = { dim: dim.value, retentionDay: props.retentionDay }
    if (rangeStart.value && rangeEnd.value) {
      params.start = rangeStart.value
      params.end = rangeEnd.value
    }
    const res = await getNewUserRetention(params)
    Object.assign(data, { points: [], latest: null, latestLabel: '', mean: null }, res.data || {})
    await nextTick()
    render()
  } catch (e) {
    /* 静默：拦截器已提示 */
  } finally {
    loading.value = false
  }
}

function pickDim(d) {
  granPopRef.value && granPopRef.value.hide()
  if (dim.value === d) return
  dim.value = d
  // 切粒度时套用该维度默认区间并立即重新加载（与其它趋势面板一致）
  dateRangeRef.value && dateRangeRef.value.applyPresetAndEmit(presetByDim[d])
}

function onRange({ start, end }) {
  rangeStart.value = start || ''
  rangeEnd.value = end || ''
  load()
}

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
.retention-card {
  width: 100%;
}
.rc-header {
  display: flex;
  align-items: center;
}
.rc-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.rc-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.rc-tab {
  color: #909399;
}
.rc-tab.active {
  color: #303133;
  font-weight: 600;
}
.rc-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.rc-gran:hover {
  color: #409eff;
}
.rc-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.rc-sep {
  color: #dcdfe6;
}
.rc-body {
  display: flex;
  align-items: stretch;
  margin-top: 12px;
}
.rc-summary {
  width: 160px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 28px;
  padding-left: 4px;
}
.rc-date {
  font-size: 13px;
  color: #909399;
}
.rc-value {
  font-size: 34px;
  font-weight: 700;
  line-height: 1.1;
  color: #303133;
}
.rc-mean-label {
  font-size: 13px;
  color: #909399;
}
.rc-mean-value {
  font-size: 26px;
  font-weight: 600;
  color: #303133;
}
.rc-chart {
  flex: 1;
  min-width: 0;
  height: 300px;
}

/* 统计粒度菜单 */
.gm-menu {
  display: flex;
  flex-direction: column;
}
.gm-row {
  display: flex;
  align-items: center;
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
