<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="card-head">
          <span>收入分析</span>
          <el-radio-group v-model="days" size="small" @change="load">
            <el-radio-button :value="7">近7天</el-radio-button>
            <el-radio-button :value="30">近30天</el-radio-button>
            <el-radio-button :value="90">近90天</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="chartRef" class="chart-lg"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getIncomeTrend } from '@/api/dashboard'

const days = ref(30)
const chartRef = ref()
let chart

async function load() {
  if (!chart) return
  try {
    const res = await getIncomeTrend(days.value)
    const data = res.data || []
    chart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 60, right: 24, top: 24, bottom: 40 },
      xAxis: { type: 'category', boundaryGap: true, data: data.map((d) => d.date) },
      yAxis: { type: 'value', name: '收入(元)' },
      series: [{ name: '收入', type: 'bar', itemStyle: { color: '#e83467', borderRadius: [4, 4, 0, 0] }, data: data.map((d) => d.amount ?? 0) }]
    }, true)
  } catch (e) { /* 静默 */ }
}

function resize() { chart && chart.resize() }

onMounted(async () => {
  await nextTick()
  chart = echarts.init(chartRef.value)
  window.addEventListener('resize', resize)
  load()
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  chart && chart.dispose()
})
</script>

<style scoped>
.chart-lg { height: 420px; width: 100%; }
.card-head { display: flex; justify-content: space-between; align-items: center; }
</style>
