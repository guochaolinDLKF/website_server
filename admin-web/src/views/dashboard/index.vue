<template>
  <div class="app-container">
    <!-- 核心指标卡片 -->
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.key" :xs="12" :sm="12" :md="6" :lg="6">
        <div class="metric-card">
          <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!card.tip">
            <template #content>
              <div style="max-width: 340px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ card.tip }}</div>
            </template>
            <div class="metric-title" :class="{ 'panel-title-tip': card.tip }">
              {{ card.title }}<el-icon v-if="card.tip" class="panel-title-info"><InfoFilled /></el-icon>
            </div>
          </el-tooltip>
          <div class="metric-date">{{ card.date }}</div>
          <div class="metric-value">
            <span class="metric-num">{{ card.value }}</span>
            <span v-if="card.unit" class="metric-unit">{{ card.unit }}</span>
          </div>
          <div v-if="card.compares && card.compares.length" class="metric-compares">
            <div v-for="cmp in card.compares" :key="cmp.label" class="metric-cmp">
              <span class="cmp-label">{{ cmp.label }}</span>
              <el-tooltip :content="cmp.tip" placement="top" effect="light">
                <span class="cmp-val" :class="cmp.trend">
                  <span v-if="cmp.trend !== 'flat'" class="cmp-arrow">{{ cmp.trend === 'up' ? '▲' : '▼' }}</span>{{ cmp.percent }}
                </span>
              </el-tooltip>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <RetentionCard />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <MetricTrendCard
          title="人均在线时长(分钟)"
          :tip="durationTip"
          metric="duration"
          range-text="过去30天"
          series-name="在线时长(分钟)"
          :days="30"
          :value-decimals="1"
          :show-mean-sum="true"
          :filterable="true"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <MetricTrendCard
          title="人均启动次数"
          :tip="launchTip"
          metric="launch"
          range-text="过去7天"
          series-name="人均启动次数"
          unit="次"
          :days="7"
          :value-decimals="2"
          :filterable="true"
        />
      </el-col>
    </el-row>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { InfoFilled } from '@element-plus/icons-vue'
import { getPlayerStats } from '@/api/dashboard'
import RetentionCard from './RetentionCard.vue'
import MetricTrendCard from './MetricTrendCard.vue'

// 趋势卡标题悬停提示：计算方式/数值说明
const durationTip =
  '人均在线时长 = 当日各用户在线分钟数合计 ÷ 当日活跃用户数\n' +
  '会话化口径：相邻行为事件间隔 ≤ 20 分钟视为同一次在线\n' +
  '大数字为最新一日值，含日环比/周同比与区间均值、总和'
const launchTip =
  '人均启动次数 = 当日各用户会话数合计 ÷ 当日活跃用户数\n' +
  '会话化口径：相邻行为事件间隔 ≤ 20 分钟视为同一次会话\n' +
  '大数字为最新一日值，含日环比/周同比'

const stats = reactive({})

const WEEK_CN = ['日', '一', '二', '三', '四', '五', '六']
function fmtDay(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}(${WEEK_CN[d.getDay()]})`
}

// 整数（人/笔/次）带千分位
function fmtInt(v) {
  return Number(v || 0).toLocaleString('zh-CN')
}
// 付费金额：<1万 以「元」显示，≥1万 以「万」显示，均保留2位小数。返回 { value, unit }
function fmtMoney(v) {
  const n = Number(v || 0)
  const decimal2 = { minimumFractionDigits: 2, maximumFractionDigits: 2 }
  if (n < 10000) {
    return { value: n.toLocaleString('zh-CN', decimal2), unit: '元' }
  }
  return { value: (n / 10000).toLocaleString('zh-CN', decimal2), unit: '万' }
}
function fmtTime(d) {
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
// 由后端有符号百分比生成对比项：负数=红色▼下跌，正数=绿色▲上涨。
// 始终展示该项（与今日新增一致）；无可对比基数(null)时显示「—」并在 tip 中说明。
// tip 说明对比区间：对比 {基准日} 00:00 到 {当前时刻}，下降/上升了 X%
function compare(label, pct, baseDateLabel, endTime) {
  if (pct === null || pct === undefined) {
    return { label, percent: '—', trend: 'flat', tip: `对比 ${baseDateLabel} 00:00 到 ${endTime}，无可对比数据` }
  }
  const n = Number(pct)
  const dir = n < 0 ? '下降' : n > 0 ? '上升' : '持平'
  const tip = `对比 ${baseDateLabel} 00:00 到 ${endTime}，${dir}了${Math.abs(n).toFixed(2)}%`
  return { label, percent: `${Math.abs(n).toFixed(2)}%`, trend: n < 0 ? 'down' : n > 0 ? 'up' : 'flat', tip }
}
function compares(metric, now) {
  const endTime = fmtTime(now)
  const yest = new Date(now)
  yest.setDate(yest.getDate() - 1)
  const weekAgo = new Date(now)
  weekAgo.setDate(weekAgo.getDate() - 7)
  return [
    compare('日环比', metric?.dod, fmtDay(yest), endTime),
    compare('周同比', metric?.wow, fmtDay(weekAgo), endTime)
  ]
}

const cards = ref([])
function buildCards() {
  const today = new Date()
  const monthStart = new Date(today.getFullYear(), today.getMonth(), 1)
  const todayLabel = fmtDay(today)
  const monthLabel = `${fmtDay(monthStart)}至${fmtDay(today)}`
  const todayAmt = fmtMoney(stats.todayPayAmount?.value)
  const monthAmt = fmtMoney(stats.monthPayAmount)

  cards.value = [
    { key: 'todayNewUsers', title: '今日新增', tip: '今日新增注册用户数（今日 00:00 至当前时刻）\n日环比 vs 昨日同时段，周同比 vs 上周同日同时段', date: todayLabel, value: fmtInt(stats.todayNewUsers?.value), unit: '人', compares: compares(stats.todayNewUsers, today) },
    { key: 'todayActive', title: '今日活跃', tip: '今日有任意行为事件的去重用户数（DAU）\n日环比 vs 昨日同时段，周同比 vs 上周同日同时段', date: todayLabel, value: fmtInt(stats.todayActive?.value), unit: '人', compares: compares(stats.todayActive, today) },
    { key: 'todayPayUsers', title: '今日付费玩家', tip: '今日成功支付的去重用户数（payment_status=SUCCESS）\n日环比 vs 昨日同时段，周同比 vs 上周同日同时段', date: todayLabel, value: fmtInt(stats.todayPayUsers?.value), unit: '人', compares: compares(stats.todayPayUsers, today) },
    { key: 'todayPayAmount', title: '今日付费金额', tip: '今日成功支付金额合计\n日环比 vs 昨日同时段，周同比 vs 上周同日同时段', date: todayLabel, value: todayAmt.value, unit: todayAmt.unit, compares: compares(stats.todayPayAmount, today) },
    { key: 'monthNewUsers', title: '当月新增玩家', tip: '本月 1 日至今新增注册用户数（累计）', date: monthLabel, value: fmtInt(stats.monthNewUsers), unit: '人' },
    { key: 'monthActive', title: '当月活跃玩家', tip: '本月 1 日至今有行为事件的去重用户数（MAU）', date: monthLabel, value: fmtInt(stats.monthActive), unit: '人' },
    { key: 'monthPayUsers', title: '当月付费玩家', tip: '本月 1 日至今成功支付的去重用户数', date: monthLabel, value: fmtInt(stats.monthPayUsers), unit: '人' },
    { key: 'monthPayAmount', title: '当月付费金额', tip: '本月 1 日至今成功支付金额合计', date: monthLabel, value: monthAmt.value, unit: monthAmt.unit }
  ]
}

async function loadOverview() {
  try {
    const res = await getPlayerStats()
    Object.assign(stats, res.data || {})
  } catch (e) {
    /* zhouyi 未连接或无数据时静默，卡片显示 0 */
  }
  buildCards()
}

onMounted(() => {
  buildCards()
  loadOverview()
})
</script>

<style scoped>
.metric-card {
  margin-bottom: 16px;
  padding: 18px 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  min-height: 132px;
}
.metric-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2329;
}
.metric-date {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}
.metric-value {
  margin-top: 12px;
  display: flex;
  align-items: baseline;
}
.metric-num {
  font-size: 30px;
  font-weight: 600;
  color: #1f2329;
  line-height: 1;
}
.metric-unit {
  margin-left: 6px;
  font-size: 13px;
  color: #606266;
}
.metric-compares {
  margin-top: 14px;
  display: flex;
  gap: 28px;
}
.metric-cmp {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}
.cmp-label {
  color: #606266;
}
.cmp-val {
  display: inline-flex;
  align-items: center;
  font-weight: 600;
}
.cmp-val.down {
  color: #f56c6c;
}
.cmp-val.up {
  color: #67c23a;
}
.cmp-val.flat {
  color: #909399;
}
.cmp-arrow {
  margin-right: 2px;
  font-size: 10px;
}
</style>
