<template>
  <div class="app-container">
    <el-card class="page-card nd-header-card" shadow="never">
      <div class="nd-h-title">基础新增数据</div>
      <div class="nd-h-sub">监测每日新增账号 / 设备数据情况</div>
      <div class="nd-h-sub">分析登录前转化率和新手引导各步骤转化率，针对低转化率步骤进行调优</div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <DualMetricCard
          title="新增玩家数量及占比"
          :tip="playerTip"
          bar-name="新增玩家"
          bar-unit="次"
          ratio-name="新增玩家占比"
          :api="getNewPlayersTrend"
          default-preset="recent7"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <DualMetricCard
          title="新增设备数量及占比"
          :tip="deviceTip"
          bar-name="设备激活数量"
          bar-unit="次"
          ratio-name="新增设备占比"
          :api="getNewDevicesTrend"
          default-preset="recent7"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <ChannelStackCard
          title="各渠道新增玩家数"
          :tip="channelCountTip"
          mode="count"
          :api="getChannelNewPlayers"
          default-preset="recent7"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <ChannelStackCard
          title="各渠道新增占比"
          :tip="channelRatioTip"
          mode="ratio"
          :api="getChannelNewPlayers"
          default-preset="recent7"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import DualMetricCard from '../../dashboard/DualMetricCard.vue'
import ChannelStackCard from '../../dashboard/ChannelStackCard.vue'
import { getNewPlayersTrend, getNewDevicesTrend, getChannelNewPlayers } from '@/api/dashboard'

// 标题悬停提示：数据计算口径
const playerTip =
  '柱状·新增玩家：所选周期内新增注册的玩家数\n' +
  '折线·新增玩家占比 = 新增玩家 ÷ 当期去重活跃玩家(按天即 DAU) × 100%'

const deviceTip =
  '柱状·设备激活数量：所选周期内新增(首次出现)的设备数\n' +
  '设备 = 机型(device_model) + 系统(device_os) 去重，按首次出现时间计入当期\n' +
  '折线·新增设备占比 = 当期新增设备 ÷ 截至当期末累计设备数 × 100%'

const channelCountTip =
  '按渠道拆分的新增玩家数（堆积面积）\n' +
  '渠道取自玩家最早一条带 channel 的事件；按注册日归入各期\n' +
  '仅统计带渠道记录的新增玩家'

const channelRatioTip =
  '各渠道新增玩家占比（100% 堆积）\n' +
  '某渠道占比 = 当期该渠道新增玩家 ÷ 当期各渠道新增玩家合计 × 100%'
</script>

<style scoped>
.nd-header-card {
  padding: 4px 4px;
}
.nd-h-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2329;
}
.nd-h-sub {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
