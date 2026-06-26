<template>
  <div class="app-container">
    <el-card class="page-card rt-header-card" shadow="never">
      <div class="rt-title">实时数据</div>
      <div class="rt-subtitle">监控实时新增、在线、付费数据，若出现异常情况及时排查处理</div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <RealtimeMetricCard />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <RealtimeMetricCard
          title="实时新增"
          series-name="新增用户数"
          trend-series-name="新增用户数"
          default-mode="intraday"
          default-preset="today"
          :intraday-api="getRealtimeNewUsers"
          :trend-api="getNewUsersTrend"
          :show-stats="true"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <RealtimeMetricCard
          title="实时新增（累计）"
          series-name="新增用户数"
          trend-series-name="新增用户数"
          default-mode="intraday"
          default-preset="today"
          :intraday-api="getRealtimeNewUsers"
          :trend-api="getNewUsersTrend"
          :extra-params="{ cumulative: 1 }"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import RealtimeMetricCard from '../../dashboard/RealtimeMetricCard.vue'
import { getRealtimeNewUsers, getNewUsersTrend } from '@/api/dashboard'
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
