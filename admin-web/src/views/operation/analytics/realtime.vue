<template>
  <div class="app-container">
    <el-card class="page-card rt-header-card" shadow="never">
      <div class="rt-title">实时数据</div>
      <div class="rt-subtitle">监控实时新增、在线、付费数据，若出现异常情况及时排查处理</div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <RealtimeMetricCard :tip="onlineTip" />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <RealtimeMetricCard
          title="实时新增"
          :tip="newTip"
          series-name="新增用户数"
          trend-series-name="新增用户数"
          default-mode="trend"
          default-preset="recent7"
          :intraday-api="getRealtimeNewUsers"
          :trend-api="getNewUsersTrend"
          :show-stats="true"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <RealtimeMetricCard
          title="实时新增（累计）"
          :tip="newCumTip"
          series-name="新增用户数"
          trend-series-name="新增用户数"
          default-mode="trend"
          default-preset="recent7"
          :intraday-api="getRealtimeNewUsers"
          :trend-api="getNewUsersTrend"
          :extra-params="{ cumulative: 1 }"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <RealtimeMetricCard
          title="实时付费"
          :tip="payTip"
          series-name="实时付费金额"
          trend-series-name="实时付费金额"
          default-mode="trend"
          default-preset="recent7"
          :intraday-api="getRealtimeRevenue"
          :trend-api="getRevenueTrend"
          :show-stats="true"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <RealtimeMetricCard
          title="实时付费（累计）"
          :tip="payCumTip"
          series-name="实时付费金额"
          trend-series-name="实时付费金额"
          default-mode="trend"
          default-preset="recent7"
          :intraday-api="getRealtimeRevenue"
          :trend-api="getRevenueTrend"
          :extra-params="{ cumulative: 1 }"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import RealtimeMetricCard from '../../dashboard/RealtimeMetricCard.vue'
import { getRealtimeNewUsers, getNewUsersTrend, getRealtimeRevenue, getRevenueTrend } from '@/api/dashboard'

// 各面板标题悬停提示：计算方式/数值说明
const onlineTip =
  '某时刻在线人数 = 该时刻最近 20 分钟内有行为事件的去重用户数\n' +
  '大数字为最新时刻在线人数；实线=所选日/区间，虚线=对比期(昨日/上一周期)'
const newTip =
  '各时段新增注册用户数（按天即每日新增）\n' +
  '大数字为最新一期值，均值/总和为所选区间统计；实线=当前，虚线=对比期'
const newCumTip = '所选区间内累计新增注册用户数（随时间运行累加）\n大数字为最新一期累计值'
const payTip =
  '各时段成功支付金额合计\n' +
  '大数字为最新一期金额，均值/总和为所选区间统计；实线=当前，虚线=对比期'
const payCumTip = '所选区间内累计成功支付金额（随时间运行累加）\n大数字为最新一期累计金额'
</script>

<style scoped>
.rt-header-card {
  padding: 4px 4px;
}
.rt-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2329;
}
.rt-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
