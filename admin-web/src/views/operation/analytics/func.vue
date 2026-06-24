<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col :xs="24" :md="12">
        <el-card class="page-card" shadow="never">
          <template #header><span>商品销售排行</span></template>
          <div ref="goodsRef" class="chart-md"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="page-card" shadow="never">
          <template #header><span>权益类型分布</span></template>
          <div ref="benefitRef" class="chart-md"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getGoodsRank, getBenefitDist } from '@/api/dashboard'

const goodsRef = ref()
const benefitRef = ref()
let goodsChart, benefitChart

async function load() {
  try {
    const [g, b] = await Promise.all([getGoodsRank(), getBenefitDist()])
    const gd = g.data || []
    goodsChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 80, right: 24, top: 16, bottom: 30 },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: gd.map((d) => d.name).reverse() },
      series: [{ type: 'bar', itemStyle: { color: '#409eff', borderRadius: [0, 4, 4, 0] }, data: gd.map((d) => d.value ?? d.count ?? 0).reverse() }]
    }, true)
    const bd = b.data || []
    benefitChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0, type: 'scroll' },
      series: [{ type: 'pie', radius: ['40%', '66%'], data: bd.map((d) => ({ name: d.name ?? d.type ?? '未知', value: d.value ?? d.count ?? 0 })) }]
    }, true)
  } catch (e) { /* 静默 */ }
}

function resize() {
  goodsChart && goodsChart.resize()
  benefitChart && benefitChart.resize()
}

onMounted(async () => {
  await nextTick()
  goodsChart = echarts.init(goodsRef.value)
  benefitChart = echarts.init(benefitRef.value)
  window.addEventListener('resize', resize)
  load()
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  goodsChart && goodsChart.dispose()
  benefitChart && benefitChart.dispose()
})
</script>

<style scoped>
.chart-md { height: 340px; width: 100%; }
</style>
