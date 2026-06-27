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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { InfoFilled } from '@element-plus/icons-vue'
import { getPayOverview } from '@/api/dashboard'

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
    { key: 'amount', title: '付费金额', tip: '今日成功支付金额合计（payments 中 payment_status=SUCCESS，今日 00:00 至当前）\n日环比 vs 昨日同时段，周同比 vs 上周同日同时段', date: label, value: fmtMoney(amt.value), unit: '', compares: compares(amt, today) },
    { key: 'users', title: '付费人数', tip: '今日成功支付的去重用户数\n日环比 vs 昨日同时段，周同比 vs 上周同日同时段', date: label, value: fmtInt(users.value), unit: '人', compares: compares(users, today) },
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
