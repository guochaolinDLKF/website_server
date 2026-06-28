<template>
  <div class="app-container">
    <el-card class="page-card act-header-card" shadow="never">
      <div class="act-h-title">基础活跃数据</div>
      <div class="act-h-sub">从活跃人数 / 活跃构成 / 在线时长 / 周活跃天数等多个方面分析和评估用户活跃情况</div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <ActiveTrendCard title="活跃数据" :tip="activeTrendTip" :api="getActiveTrend" default-preset="past7" />
      </el-col>
      <el-col :xs="24" :md="12">
        <DonutCompositionCard
          title="活跃用户生命周期天数构成"
          :tip="lifecycleTip"
          chart-title="活跃用户数"
          value-label="活跃用户数"
          value-type="int"
          empty-text="所选区间暂无活跃数据"
          :api="getActiveLifecycleDist"
          default-preset="past7"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <DualBarLineCard
          title="人均登录次数和在线时长"
          :tip="loginStatsTip"
          bar-name="人均登录次数"
          bar-unit="次"
          line-name="人均登录时长"
          line-unit="秒"
          :api="getLoginStatsTrend"
          default-preset="past7"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import ActiveTrendCard from '../../dashboard/ActiveTrendCard.vue'
import DonutCompositionCard from '../../dashboard/DonutCompositionCard.vue'
import DualBarLineCard from '../../dashboard/DualBarLineCard.vue'
import { getActiveTrend, getActiveLifecycleDist, getLoginStatsTrend } from '@/api/dashboard'

const loginStatsTip =
  '会话化口径：从登录开始，相邻操作间隔 ≤20 分钟算同一次在线\n' +
  '柱·人均登录次数 = 当期会话数（启动次数）合计 ÷ 当期活跃用户数\n' +
  '折线·人均登录时长(秒) = 当期会话时长合计 ÷ 当期活跃用户数\n' +
  '仅统计已完整结束的自然日；按周/月时取周期内各日人均值的平均'

const activeTrendTip =
  '活跃 = 当期有行为事件的去重用户\n' +
  'DAU=当日活跃；WAU=近 7 天滚动去重活跃；MAU=近 30 天滚动去重活跃\n' +
  'DAU/MAU（粘性）= DAU ÷ MAU × 100%；按周/按月时取该周期最后一天的快照值'

const lifecycleTip =
  '所选时间段内的活跃用户，按「生命周期天数」拆分占比（环形图）\n' +
  '生命周期天数 = 区间结束日 − 用户注册日（天）\n' +
  '某天数占比 = 该生命周期天数的活跃用户数 ÷ 全部活跃用户数 × 100%（取前 10 项，其余归入「其他」）'
</script>

<style scoped>
.act-header-card {
  padding: 4px 4px;
}
.act-h-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2329;
}
.act-h-sub {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
