<template>
  <div class="app-container">
    <el-card class="page-card pay-header-card" shadow="never">
      <div class="pay-h-title">付费概况</div>
      <div class="pay-h-sub">从各个方面了解游戏当前游戏付费数据</div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
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
          <div class="metric-compares">
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
      <el-col :xs="24" :md="12">
        <DualMetricCard
          title="付费总体趋势"
          :tip="payTrendTip"
          bar-name="付费金额"
          bar-unit=""
          ratio-name="付费率"
          :api="getPayTrend"
          default-preset="past7"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <DualLineCard
          title="ARPU与ARPPU趋势"
          :tip="arpuTrendTip"
          name1="arpu"
          name2="arppu"
          key1="arpu"
          key2="arppu"
          :api="getArpuTrend"
          default-preset="past7"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <DualLineCard
          title="付费人数新老用户分层"
          :tip="paySegmentTip"
          name1="新用户付费人数"
          name2="老用户付费人数"
          key1="newUsers"
          key2="oldUsers"
          value-type="int"
          :api="getPayUserSegmentTrend"
          default-preset="past7"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <DualLineCard
          title="充值成功率和失败率"
          :tip="successRateTip"
          name1="成功率"
          name2="失败率"
          key1="success"
          key2="fail"
          value-type="percent"
          :api="getPaymentSuccessRateTrend"
          default-preset="past7"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <DualMetricCard
          title="注册首日付费情况"
          :tip="firstDayPayTip"
          bar-name="首日付费玩家数"
          bar-unit="人"
          ratio-name="首日付费转化率"
          :api="getFirstDayPayTrend"
          default-preset="past7"
        />
      </el-col>
      <el-col :xs="24" :md="12">
        <CohortPayTable
          title="注册后阶段累计付费人数"
          :tip="cohortPayTip"
          :max-stage="7"
          :api="getRegStagePayCohort"
          default-preset="past7"
        />
      </el-col>
    </el-row>

    <el-card class="page-card pay-header-card" shadow="never" style="margin-top: 16px">
      <div class="pay-h-title">付费内容构成</div>
      <div class="pay-h-sub">了解各个权益的售卖情况</div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="24">
        <DonutCompositionCard
          title="付费流水权益占比"
          :tip="compositionTip"
          chart-title="付费事件·支付金额总和"
          :api="getPayCompositionByBenefit"
          default-preset="past7"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="24">
        <BenefitTrendCard
          title="付费流水构成（按权益）"
          :tip="benefitTrendTip"
          :api="getPayBenefitTrend"
          default-preset="past7"
        />
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="24">
        <BenefitTrendCard
          title="商品复购率"
          :tip="repurchaseTip"
          :api="getProductRepurchaseTrend"
          value-type="percent"
          :default-visible="4"
          default-preset="past7"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { InfoFilled } from '@element-plus/icons-vue'
import { getPayOverview, getPayTrend, getArpuTrend, getPayUserSegmentTrend, getPaymentSuccessRateTrend, getFirstDayPayTrend, getRegStagePayCohort, getPayCompositionByBenefit, getPayBenefitTrend, getProductRepurchaseTrend } from '@/api/dashboard'
import DualMetricCard from '../../dashboard/DualMetricCard.vue'
import DualLineCard from '../../dashboard/DualLineCard.vue'
import CohortPayTable from '../../dashboard/CohortPayTable.vue'
import DonutCompositionCard from '../../dashboard/DonutCompositionCard.vue'
import BenefitTrendCard from '../../dashboard/BenefitTrendCard.vue'

// 标题悬停提示：数据计算口径
const payTrendTip =
  '柱状·付费金额 = 「充值成功」的支付金额总和（所选周期内成功支付的金额合计）\n' +
  '折线·付费率 = 「充值成功」的触发用户数 ÷ 「账号登录」的触发用户数 × 100%\n' +
  '（同一人多次只算一人）'

const arpuTrendTip =
  'arpu  = 付费金额 ÷ 「账号登录」的触发用户数（登录用户人均贡献）\n' +
  'arppu = 付费金额 ÷ 付费人数（即「充值成功」的触发用户数，付费用户人均贡献）\n' +
  '（同一人多次只算一人；均保留 2 位小数）'

const paySegmentTip =
  '从「充值成功」的付费人数里，按是不是新用户拆成两条：\n' +
  '新用户付费人数：付费成功、且在所选时间范围内注册的人数\n' +
  '老用户付费人数：付费成功、但在所选范围之前就注册的人数\n' +
  '（新老按所选时间范围统一判定；同一人在同一周期只算一次）'

const successRateTip =
  '数据取自订单表，按下单时间统计这段时间的充值订单（发起充值=全部订单）：\n' +
  '成功率 = 成功订单数(已支付) ÷ 发起充值订单数 × 100%\n' +
  '失败率 = 失败订单数 ÷ 发起充值订单数 × 100%\n' +
  '（只要不是已支付都算失败，含支付中、已取消；成功率 + 失败率 = 100%）'

const firstDayPayTip =
  '两个指标（按注册日统计，同一人只算一次）：\n' +
  '首日付费玩家数 = 当天注册、且注册当天就充值成功的去重人数\n' +
  '首日付费转化率 = 首日付费玩家数 ÷ 当天注册人数 × 100%'

const repurchaseTip =
  '各权益（及「总体」）的复购率随时间变化（每条线一个权益，纵轴为百分比）\n' +
  '数据取自订单表（已支付订单）。在所选时间范围内，用户对某权益首次购买后，只要再次购买的时间与首购不同就算复购（含同一天不同时刻）\n' +
  '某权益复购率 = 当期发生复购的人数 ÷ 当期购买该权益的人数 × 100%；总体=不分权益、按区间内首次购买时间判定\n' +
  '「分组」可勾选要显示的线（默认显示总体+购买人数最多的几个权益）'

const benefitTrendTip =
  '所选时间段内，「充值成功的支付金额总和」按「权益」分别画成折线（每条线一个权益）\n' +
  '金额取充值成功的支付金额（payments 中 payment_status=SUCCESS），按支付时间分到各天/周/月\n' +
  '「分组」可勾选要显示的权益；纵轴为该权益当期充值成功支付金额合计'

const compositionTip =
  '所选时间段内，「充值成功的支付金额总和」按「权益」拆分的占比（环形图）\n' +
  '金额取充值成功的支付金额（payments 中 payment_status=SUCCESS），按支付时间归入区间\n' +
  '权益 = 订单购买的功能/内容（如绿色无广告、五行知识等）\n' +
  '某权益占比 = 该权益充值成功支付金额 ÷ 全部权益充值成功支付金额合计 × 100%'

const cohortPayTip =
  '按注册时间分组（左上「按天/按周/按月」切换），看每批新用户在注册后第 N 个单位（天/周/月）的付费情况：\n' +
  '指标·留存：每格=累计付费人数（注册当期到第N期内付费成功的去重人数）/ 累计付费率（÷该批注册人数）/ 当期付费人数\n' +
  '指标·流失：每格=累计未付费人数（该批注册人数−累计付费人数）/ 累计流失率（=100%−累计付费率）/ 当期付费人数\n' +
  '颜色越深代表当前指标的占比越高；第N期还没结束的格子显示「—」'

const stats = reactive({})

const WEEK_CN = ['日', '一', '二', '三', '四', '五', '六']
function fmtDay(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}(${WEEK_CN[d.getDay()]})`
}
function fmtTime(d) {
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
// 金额：千分位，最多 2 位小数（无意义的小数零自动省略），如 20,348
function fmtMoney(v) {
  return Number(v || 0).toLocaleString('zh-CN', { maximumFractionDigits: 2 })
}
// 整数（人）千分位
function fmtInt(v) {
  return Number(v || 0).toLocaleString('zh-CN')
}
// 百分比：固定 2 位小数，如 42.96%
function fmtPct(v) {
  return `${Number(v || 0).toFixed(2)}%`
}
// 金额（固定 2 位小数），如 28.38
function fmtAmt2(v) {
  return Number(v || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 由后端有符号百分比生成对比项：负数=红色▼下跌，正数=绿色▲上涨；无基数(null)显示「—」
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
  const label = fmtDay(today)
  const amt = stats.payAmount || {}
  const users = stats.payUsers || {}
  const rate = stats.payRate || {}
  const arpu = stats.arpu || {}
  cards.value = [
    { key: 'amount', title: '付费金额', tip: '付费金额 = 「充值成功」的支付金额总和\n即：今日成功支付(payments SUCCESS)金额合计（今日 00:00 至当前）\n日环比 vs 昨日同时段，周同比 vs 上周同日同时段', date: label, value: fmtMoney(amt.value), unit: '', compares: compares(amt, today) },
    { key: 'users', title: '付费人数', tip: '付费人数 = 「充值成功」的触发用户数\n即：今日成功支付(payments SUCCESS)的去重用户数\n日环比 vs 昨日同时段，周同比 vs 上周同日同时段', date: label, value: fmtInt(users.value), unit: '人', compares: compares(users, today) },
    { key: 'rate', title: '付费率', tip: '付费率 = 「充值成功」的触发用户数 ÷ 「账号登录」的触发用户数 × 100%\n即：今日成功支付(payments SUCCESS)的去重用户数 ÷ 今日触发「账号登录」(user_login) 的去重用户数\n日环比/周同比为付费率的相对变化', date: label, value: fmtPct(rate.value), unit: '', compares: compares(rate, today) },
    { key: 'arpu', title: '人均贡献金额（ARPU）', tip: 'ARPU = 「充值成功」的支付金额总和 ÷ 「账号登录」的触发用户数（保留 2 位小数）\n即：今日成功支付金额合计 ÷ 今日触发「账号登录」(user_login) 的去重用户数', date: label, value: fmtAmt2(arpu.value), unit: '', compares: compares(arpu, today) }
  ]
}

async function load() {
  try {
    const res = await getPayOverview()
    Object.assign(stats, res.data || {})
  } catch (e) {
    /* zhouyi 未连接或无数据时静默，卡片显示 0 */
  }
  buildCards()
}

onMounted(() => {
  buildCards()
  load()
})
</script>

<style scoped>
.pay-header-card {
  padding: 4px 4px;
}
.pay-h-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2329;
}
.pay-h-sub {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
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
