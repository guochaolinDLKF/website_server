<template>
  <div class="app-container">
    <el-card class="page-card rt-header-card" shadow="never">
      <div class="rt-h-title">新增/活跃/付费留存</div>
      <div class="rt-h-sub">监控新增 / 活跃 / 付费用户留存情况，评估游戏质量，辅助调整运营节奏</div>
      <div class="rt-h-sub">评估参与各核心玩法对用户留存的影响</div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <RetentionTrendCard
          title="新增用户次日留存"
          :tip="d1Tip"
          period-label="次日"
          series-name="次日留存率"
          :retention-day="1"
          default-preset="past30"
          show-granularity
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <RetentionTrendCard
          title="新增用户7日留存"
          :tip="d7Tip"
          period-label="7日"
          series-name="7日留存率"
          :retention-day="7"
          default-preset="past30"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <FirstPayRetentionTable :tip="firstPayTip" />
      </el-col>
    </el-row>

    <el-card class="page-card rt-header-card" shadow="never" style="margin-top: 16px">
      <div class="rt-h-title">流失分析</div>
      <div class="rt-h-sub">分析流失用户行为共性，寻找流失原因</div>
      <div class="rt-h-sub">分析流失曲线，在合适的时机推送礼包 / 活动，促进用户上线，降低流失率</div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <BenefitTrendCard
          title="流失用户"
          :tip="churnTip"
          :api="getChurnTrend"
          value-type="amount"
          :show-granularity="false"
          default-preset="recent30"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// 留存数据页面：新增 / 活跃 / 付费用户留存分析 + 流失分析
import RetentionTrendCard from '../../dashboard/RetentionTrendCard.vue'
import FirstPayRetentionTable from '../../dashboard/FirstPayRetentionTable.vue'
import BenefitTrendCard from '../../dashboard/BenefitTrendCard.vue'
import { getChurnTrend } from '@/api/dashboard'

const churnTip =
  '第 D 日「N 日流失」= 过去 N 天 [D−N, D−1] 内有过行为事件(活跃)、但第 D 日未活跃的去重用户数\n' +
  'N 越大回看窗口越长，统计到的流失用户越多\n' +
  '可在「分组」里勾选要显示的 3/7/14/30 日流失曲线'

const firstPayTip =
  '同期群 = 用户「首次成功支付」的日期（按天/周/月归组）\n' +
  '第 N 期留存率 = 该群用户中、首次付费后第 N 期内有行为事件(活跃)的人数 ÷ 同期群人数 × 100%\n' +
  '「当期」即首次付费当期的活跃率（≈100%）；仅展示「该期已完整」的格子，未完整显示 -\n' +
  '阶段值（汇总）按「该期已完整的同期群」加权'

const d1Tip =
  '次日留存率 = 当日注册用户中、次日(注册日+1)有行为事件的去重用户数 ÷ 当日注册数 × 100%\n' +
  '大数字为最新一期留存率，右侧为所选区间各期留存率的均值\n' +
  '仅统计「次日已完整」的注册日（截至 today-2）'

const d7Tip =
  '7日留存率 = 当日注册用户中、第7日(注册日+7)有行为事件的去重用户数 ÷ 当日注册数 × 100%\n' +
  '大数字为最新一期留存率，右侧为所选区间各期留存率的均值\n' +
  '仅统计「第7日已完整」的注册日（截至 today-8）'
</script>

<style scoped>
.rt-header-card {
  padding: 4px 4px;
}
.rt-h-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2329;
}
.rt-h-sub {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
