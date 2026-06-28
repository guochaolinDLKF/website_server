<template>
  <el-card class="page-card cp-card" shadow="never">
    <div class="cp-header">
      <el-tooltip placement="top-start" effect="dark" :show-after="100" :disabled="!tip">
        <template #content>
          <div style="max-width: 360px; line-height: 1.7; white-space: pre-line; font-size: 12px">{{ tip }}</div>
        </template>
        <span class="cp-title" :class="{ 'cp-title-tip': tip }">
          {{ title }}<el-icon v-if="tip" class="cp-title-info"><InfoFilled /></el-icon>
        </span>
      </el-tooltip>
      <div class="cp-actions">
        <el-tooltip content="刷新" placement="top">
          <el-icon class="cp-action-icon" @click="load"><Refresh /></el-icon>
        </el-tooltip>
      </div>
    </div>

    <div class="cp-tabs">
      <!-- 统计粒度：按天/按周/按月（取代参考图中的「7日」，采用通用时间筛选） -->
      <el-popover ref="granPopRef" trigger="click" placement="bottom-start" :width="120" popper-class="gm-pop">
        <template #reference>
          <span class="cp-tab cp-gran">{{ DIM_LABEL[dim] }}<i class="cp-caret">▾</i></span>
        </template>
        <div class="gm-menu">
          <div class="gm-row" :class="{ active: dim === 'day' }" @click="pickDim('day')">按天</div>
          <div class="gm-row" :class="{ active: dim === 'week' }" @click="pickDim('week')">按周</div>
          <div class="gm-row" :class="{ active: dim === 'month' }" @click="pickDim('month')">按月</div>
        </div>
      </el-popover>
      <span class="cp-sep">|</span>
      <span class="cp-tab">留存</span>
      <span class="cp-sep">|</span>
      <DateRangePanel ref="dateRangeRef" :default-preset="defaultPreset" :multi-only="true" @change="onRange" />
    </div>

    <div class="cp-body" v-loading="loading">
      <div class="cp-table-wrap">
        <table class="cp-table">
          <thead>
            <tr>
              <th class="cp-col-date">日期</th>
              <th class="cp-col-reg">充值成功用户数</th>
              <th v-for="n in data.stages" :key="n">{{ stageLabel(n) }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="data.summary" class="cp-row-summary">
              <td class="cp-col-date">阶段值</td>
              <td class="cp-col-reg">{{ fmtInt(data.summary.reg) }}</td>
              <td v-for="(c, i) in data.summary.cells" :key="i" :style="cellStyle(c)">
                <template v-if="c.rate !== null && c.rate !== undefined">
                  <div class="cp-cell-cum">{{ fmtInt(c.day) }}</div>
                  <div class="cp-cell-rate">{{ fmtPct(c.rate) }}</div>
                </template>
                <span v-else class="cp-empty">-</span>
              </td>
            </tr>
            <tr v-for="row in data.rows" :key="row.date">
              <td class="cp-col-date">{{ row.label || fmtDate(row.date) }}</td>
              <td class="cp-col-reg">{{ fmtInt(row.reg) }}</td>
              <td v-for="(c, i) in row.cells" :key="i" :style="cellStyle(c)">
                <template v-if="c.rate !== null && c.rate !== undefined">
                  <div class="cp-cell-cum">{{ fmtInt(c.day) }}</div>
                  <div class="cp-cell-rate">{{ fmtPct(c.rate) }}</div>
                </template>
                <span v-else class="cp-empty">-</span>
              </td>
            </tr>
            <tr v-if="!data.rows || !data.rows.length">
              <td :colspan="2 + (data.stages ? data.stages.length : 0)" class="cp-empty-row">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="cp-legend">每格两行：第 N 期留存人数 / 留存率（留存 = 首次付费同期群用户在该期内有行为事件）</div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Refresh, InfoFilled } from '@element-plus/icons-vue'
import { getFirstPayRetentionCohort } from '@/api/dashboard'
import DateRangePanel from './DateRangePanel.vue'

const props = defineProps({
  title: { type: String, default: '首次付费留存率' },
  tip: { type: String, default: '' },
  maxStage: { type: Number, default: 6 },
  defaultPreset: { type: String, default: 'past7' }
})

const WEEK_CN = ['日', '一', '二', '三', '四', '五', '六']
const DIM_LABEL = { day: '按天', week: '按周', month: '按月' }
const UNIT_CN = { day: '日', week: '周', month: '月' }

const loading = ref(false)
const data = reactive({ stages: [], rows: [], summary: null })
const dateRangeRef = ref()
const granPopRef = ref()
const rangeStart = ref('')
const rangeEnd = ref('')
const dim = ref('day')

function fmtInt(v) {
  if (v === null || v === undefined) return '—'
  return Number(v).toLocaleString('zh-CN')
}
function fmtPct(v) {
  if (v === null || v === undefined) return '—'
  return `${Number(v).toFixed(2)}%`
}
function fmtDate(s) {
  if (!s) return '—'
  const d = new Date(s + 'T00:00:00')
  return `${s}(${WEEK_CN[d.getDay()]})`
}
function stageLabel(n) {
  const u = UNIT_CN[dim.value] || '日'
  return n === 0 ? `当${u}` : `第${n}${u}`
}
// 蓝色热力：按留存率深浅着色
function cellStyle(c) {
  const rate = c && c.rate
  if (rate === null || rate === undefined) return {}
  const r = Math.max(0, Math.min(100, Number(rate)))
  const alpha = 0.08 + (r / 100) * 0.72
  return { background: `rgba(59, 130, 246, ${alpha.toFixed(3)})`, color: r > 55 ? '#fff' : '#1f2329' }
}

async function load() {
  loading.value = true
  try {
    const params = { dim: dim.value, maxStage: props.maxStage }
    if (rangeStart.value && rangeEnd.value) {
      params.start = rangeStart.value
      params.end = rangeEnd.value
    }
    const res = await getFirstPayRetentionCohort(params)
    Object.assign(data, { stages: [], rows: [], summary: null }, res.data || {})
  } catch (e) {
    /* 静默：拦截器已提示 */
  } finally {
    loading.value = false
  }
}

function pickDim(d) {
  granPopRef.value && granPopRef.value.hide()
  if (dim.value === d) return
  dim.value = d
  // 切粒度时套用对应默认窗口（与其他同期群面板一致）
  const presetByDim = { day: 'past7', week: 'past30', month: 'pastYear' }
  if (presetByDim[d] && dateRangeRef.value) {
    dateRangeRef.value.applyPresetAndEmit(presetByDim[d])
  } else {
    load()
  }
}

function onRange({ start, end }) {
  rangeStart.value = start || ''
  rangeEnd.value = end || ''
  load()
}

onMounted(() => {
  // 首次加载由 DateRangePanel 挂载时的 change 事件驱动
})
</script>

<style scoped>
.cp-card {
  width: 100%;
}
.cp-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.cp-title {
  font-size: 16px;
  font-weight: 700;
  color: #303133;
}
.cp-title-tip {
  cursor: help;
  display: inline-flex;
  align-items: center;
}
.cp-title-info {
  margin-left: 4px;
  font-size: 14px;
  color: #c0c4cc;
}
.cp-title-tip:hover .cp-title-info {
  color: #409eff;
}
.cp-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.cp-action-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;
}
.cp-action-icon:hover {
  color: #409eff;
}
.cp-tabs {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}
.cp-tab {
  color: #303133;
  font-weight: 600;
}
.cp-gran {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
}
.cp-gran:hover {
  color: #409eff;
}
.cp-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.cp-sep {
  color: #dcdfe6;
}
.cp-body {
  margin-top: 12px;
}
.cp-table-wrap {
  overflow-x: auto;
}
.cp-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}
.cp-table th,
.cp-table td {
  border: 1px solid #ebeef5;
  padding: 6px 8px;
  text-align: center;
  white-space: nowrap;
}
.cp-table th {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}
.cp-col-date {
  text-align: left;
  color: #303133;
}
.cp-col-reg {
  color: #303133;
}
.cp-row-summary {
  font-weight: 700;
}
.cp-row-summary .cp-col-date,
.cp-row-summary .cp-col-reg {
  background: #fafafa;
}
.cp-cell-cum {
  font-weight: 700;
  line-height: 1.3;
}
.cp-cell-rate {
  line-height: 1.3;
}
.cp-empty {
  color: #c0c4cc;
}
.cp-empty-row {
  color: #c0c4cc;
  padding: 24px 0;
}
.cp-legend {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

/* 选择器菜单 */
.gm-menu {
  display: flex;
  flex-direction: column;
}
.gm-row {
  padding: 8px 12px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  border-radius: 4px;
}
.gm-row:hover {
  background: #f5f7fa;
}
.gm-row.active {
  color: #409eff;
  background: #ecf5ff;
}
</style>

<style>
.gm-pop.el-popover.el-popper {
  padding: 6px;
}
</style>
