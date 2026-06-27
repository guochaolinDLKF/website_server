<template>
  <el-card class="page-card dc-card" shadow="never">
    <div class="dc-header">
      <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!tip">
        <template #content>
          <div style="max-width: 340px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ tip }}</div>
        </template>
        <span class="dc-title" :class="{ 'dc-title-tip': tip }">
          {{ title }}<el-icon v-if="tip" class="dc-title-info"><InfoFilled /></el-icon>
        </span>
      </el-tooltip>
      <div class="dc-actions">
        <el-tooltip content="刷新" placement="top">
          <el-icon class="dc-action-icon" @click="load"><Refresh /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <div class="dc-tabs">
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="dc-tab dc-gran">{{ DIM_LABEL[dim] }}<i class="dc-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>
      <span class="dc-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" :multi-only="true" @change="onRange" />
    </div>

    <div class="dc-body" v-loading="loading">
      <div class="dc-chart-title">{{ chartTitle }}</div>
      <div ref="chartRef" class="dc-chart"></div>
      <div v-if="!hasData" class="dc-empty">{{ emptyText }}</div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Refresh, InfoFilled } from '@element-plus/icons-vue'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '付费流水构成（按权益）' },
  tip: { type: String, default: '' },
  chartTitle: { type: String, default: '付费事件·支付金额总和' },
  valueLabel: { type: String, default: '支付金额' }, // tooltip 中数值的名称
  valueType: { type: String, default: 'amount' }, // amount=2位小数 | int=整数
  emptyText: { type: String, default: '所选区间暂无付费数据' },
  api: { type: Function, required: true },
  defaultPreset: { type: String, default: 'past7' }
})

const PALETTE = ['#3b82f6', '#f59e0b', '#6366f1', '#22d3ee', '#10b981', '#fbbf24', '#14b8a6', '#ef4444', '#8b5cf6', '#ec4899', '#f97316', '#0ea5e9', '#a3a3a3']
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }

const loading = ref(false)
const items = ref([])
const chartRef = ref()
let chart

const dim = ref('day')
const granPopRef = ref()
const dateRangeRef = ref()
const rangeStart = ref('')
const rangeEnd = ref('')

const hasData = computed(() => items.value.some((it) => Number(it.amount) > 0))

async function load() {
  loading.value = true
  try {
    const params = {}
    if (rangeStart.value && rangeEnd.value) {
      params.start = rangeStart.value
      params.end = rangeEnd.value
    }
    const res = await props.api(params)
    items.value = (res.data && res.data.items) || []
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
  const data = items.value
    .map((it) => ({ name: it.name, value: Number(it.amount) || 0 }))
    .filter((it) => it.value > 0)
  chart.setOption(
    {
      tooltip: {
        trigger: 'item',
        formatter: (p) => {
          const v = props.valueType === 'int'
            ? p.value.toLocaleString('zh-CN')
            : p.value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
          return `${p.name}<br/>${props.valueLabel}：${v}<br/>占比：${p.percent}%`
        }
      },
      legend: { type: 'scroll', orient: 'vertical', right: 8, top: 'middle', textStyle: { color: '#606266', fontSize: 12 } },
      color: PALETTE,
      series: [
        {
          name: props.chartTitle,
          type: 'pie',
          radius: ['42%', '66%'],
          center: ['42%', '52%'],
          avoidLabelOverlap: true,
          itemStyle: { borderColor: '#fff', borderWidth: 2 },
          label: {
            show: true,
            formatter: '{b}\n{d}%',
            color: '#606266',
            fontSize: 12,
            lineHeight: 16
          },
          labelLine: { show: true, length: 14, length2: 12 },
          data
        }
      ]
    },
    true
  )
}

function pickDim(d) {
  granPopRef.value && granPopRef.value.hide()
  if (dim.value === d) return
  dim.value = d
  // 切粒度时套用对应默认窗口（与其他面板一致）：按天=过去7天、按周=过去30天、按月=过去一年
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
.dc-card {
  width: 100%;
}
.dc-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.dc-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.dc-title-tip {
  cursor: help;
  display: inline-flex;
  align-items: center;
}
.dc-title-info {
  margin-left: 4px;
  font-size: 14px;
  color: #c0c4cc;
}
.dc-title-tip:hover .dc-title-info {
  color: #409eff;
}
.dc-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.dc-action-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;
}
.dc-action-icon:hover {
  color: #409eff;
}
.dc-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.dc-tab {
  color: #303133;
  font-weight: 600;
}
.dc-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.dc-gran:hover {
  color: #409eff;
}
.dc-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.dc-sep {
  color: #dcdfe6;
}
.dc-body {
  margin-top: 12px;
  position: relative;
}
.dc-chart-title {
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}
.dc-chart {
  width: 100%;
  height: 340px;
}
.dc-empty {
  position: absolute;
  top: 55%;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 13px;
  color: #c0c4cc;
}

/* 统计粒度菜单 */
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
</style>

<style>
.gm-pop.el-popover.el-popper {
  padding: 6px;
}
</style>
