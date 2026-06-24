<template>
  <el-card class="page-card retention-card" shadow="never">
    <div class="rc-header">
      <span class="rc-title">新增用户次日留存</span>
    </div>
    <div class="rc-tabs">
      <span class="rc-tab active">次日</span>
      <span class="rc-sep">|</span>
      <span class="rc-tab">留存</span>
      <span class="rc-sep">|</span>
      <DateRangePanel default-preset="recent30" @change="onRange" />
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
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getRetention } from '@/api/dashboard'
import DateRangePanel from './DateRangePanel.vue'

const loading = ref(false)
const data = reactive({ points: [], latest: null, latestLabel: '', mean: null })
const chartRef = ref()
let chart

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
          name: '次日留存率',
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

async function load(start, end) {
  loading.value = true
  try {
    const res = await getRetention({ start, end })
    Object.assign(data, { points: [], latest: null, latestLabel: '', mean: null }, res.data || {})
    await nextTick()
    render()
  } catch (e) {
    /* 静默：拦截器已提示 */
  } finally {
    loading.value = false
  }
}

function onRange({ start, end }) {
  load(start, end)
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
</style>
