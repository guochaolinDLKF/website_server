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
  '柱状·新增玩家：这段时间里新注册账号的人数\n' +
  '折线·新增玩家占比 = 新注册账号的人数 ÷ 登录过的人数 × 100%\n' +
  '（同一个人无论登录几次都只算一人；占比表示新注册的人在登录用户中占多大比例）'

const deviceTip =
  '柱状·设备激活数量：这段时间里新出现的设备数量（同一台设备只在第一次出现时算一次）\n' +
  '折线·新增设备占比 = 新出现的设备数量 ÷ 登录用到的设备数量 × 100%\n' +
  '（设备按"机型+系统"区分；同一台设备无论登录几次都只算一台）'

const channelCountTip =
  '各渠道新注册账号的人数 = 「账号注册」的触发用户数，按渠道拆分（堆积面积）\n' +
  '每个人按注册时所属渠道归类，同一个人只算一次\n' +
  '只统计能识别到渠道的新注册用户'

const channelRatioTip =
  '各渠道新增占比 = 各渠道「账号注册」触发用户数的占比（100% 堆积）\n' +
  '某渠道占比 = 该渠道新注册人数 ÷ 当期各渠道新注册人数合计 × 100%\n' +
  '（新注册人数 = 账号注册的触发用户数；同一人只算一次，只统计能识别到渠道的用户）'
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
