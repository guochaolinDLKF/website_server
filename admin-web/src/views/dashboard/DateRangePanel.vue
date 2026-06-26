<template>
  <el-popover
    ref="popRef"
    placement="bottom-start"
    trigger="click"
    :width="640"
    popper-class="drp-pop"
    @show="onPanelShow"
  >
    <template #reference>
      <span class="drp-trigger">{{ committedLabel || '选择日期范围' }}<i class="drp-caret">▾</i></span>
    </template>

    <div class="drp-panel">
      <div class="drp-head">
        <div class="drp-head-title">日期范围</div>
        <div class="drp-head-sub">{{ draftLabel }} ({{ slash(draftStart) }}<template v-if="!sameDay(draftStart, draftEnd)"> → {{ slash(draftEnd) }}</template>)</div>
      </div>

      <div class="drp-body">
        <!-- 左侧预设 -->
        <div class="drp-presets">
          <div v-for="(row, i) in visiblePresetRows" :key="i" class="drp-preset-row">
            <button
              v-for="p in row"
              :key="p.key"
              class="drp-preset-btn"
              :class="{ active: activePreset === p.key, disabled: isPresetDisabled(p.key) }"
              :disabled="isPresetDisabled(p.key)"
              @click="applyPreset(p.key)"
            >
              {{ p.label }}
            </button>
          </div>
          <button
            v-for="p in visibleFixedPresets"
            :key="p.key"
            class="drp-preset-btn drp-preset-full"
            :class="{ active: activePreset === p.key }"
            @click="applyPreset(p.key)"
          >
            {{ p.label }}
          </button>
        </div>

        <!-- 右侧：时间模式切换 + 动态输入/双日历 -->
        <div class="drp-right">
          <div v-if="!single" class="drp-mode-tabs">
            <span class="drp-mode-tab" :class="{ active: timeMode === 'dynamic' }" @click="setMode('dynamic')">动态时间</span>
            <span class="drp-mode-tab" :class="{ active: timeMode === 'static' }" @click="setMode('static')">静态时间</span>
          </div>

          <!-- 动态时间：相对今天的偏移天数（single 模式禁用） -->
          <div v-if="!single && timeMode === 'dynamic'" class="drp-dynamic">
            <div class="drp-dyn-row">
              <el-input-number v-model="startOffset" :min="0" :max="3650" :controls="false" size="small" @change="onDynamicChange" />
              <span class="drp-dyn-unit">天前</span>
              <span class="drp-dyn-arrow">→</span>
              <el-input-number v-model="endOffset" :min="0" :max="3650" :controls="false" size="small" @change="onDynamicChange" />
              <span class="drp-dyn-unit">天前</span>
            </div>
            <div class="drp-dyn-hint">{{ slash(draftStart) }} → {{ endLabelText }}</div>
          </div>

          <!-- 静态时间：双月日历 -->
          <div v-else class="drp-cals">
            <div v-for="(m, ci) in [leftMonth, rightMonth]" :key="ci" class="drp-cal">
              <div class="drp-cal-head">
                <span class="drp-nav" @click="shiftMonth(-1)">‹</span>
                <span class="drp-cal-title">{{ monthTitle(m) }}</span>
                <span class="drp-nav" @click="shiftMonth(1)">›</span>
              </div>
              <div class="drp-week"><span v-for="w in weekHeads" :key="w">{{ w }}</span></div>
              <div class="drp-grid">
                <span
                  v-for="(c, idx) in monthCells(m)"
                  :key="ci + '-' + idx"
                  class="drp-day"
                  :class="cellClass(c)"
                  @click="c && clickDay(c.date)"
                >{{ c ? c.day : '' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="drp-foot">
        <el-button size="small" @click="cancel">取消</el-button>
        <el-button size="small" type="primary" :disabled="!draftStart || !draftEnd" @click="apply">应用</el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'

const props = defineProps({
  defaultPreset: { type: String, default: 'recent30' },
  // single=true：仅允许选择单日（用于按小时等粒度），隐藏多日预设与动态时间，日历点选即为单日
  single: { type: Boolean, default: false },
  // multiOnly=true：仅允许多日（用于按天/周/月趋势），禁用「昨日/今日」单日预设
  multiOnly: { type: Boolean, default: false }
})
const emit = defineEmits(['change'])

// 多日模式下禁用单日预设（昨日/今日）
function isPresetDisabled(key) {
  return props.multiOnly && (key === 'yesterday' || key === 'today')
}

/* ---- 日期工具 ---- */
const pad = (n) => String(n).padStart(2, '0')
const atMidnight = (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate())
const ymd = (d) => (d ? `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}` : '')
const slash = (d) => (d ? `${d.getFullYear()}/${pad(d.getMonth() + 1)}/${pad(d.getDate())}` : '—')
const addDays = (d, n) => { const x = new Date(d); x.setDate(x.getDate() + n); return x }
const addMonths = (d, n) => new Date(d.getFullYear(), d.getMonth() + n, 1)
const firstOfMonth = (d) => new Date(d.getFullYear(), d.getMonth(), 1)
const lastOfMonth = (d) => new Date(d.getFullYear(), d.getMonth() + 1, 0)
const startOfWeek = (d) => addDays(d, -((d.getDay() + 6) % 7))
const sameDay = (a, b) => a && b && a.getTime() === b.getTime()
const inRange = (d, s, e) => s && e && d.getTime() > s.getTime() && d.getTime() < e.getTime()

const TODAY = atMidnight(new Date())
const weekHeads = ['一', '二', '三', '四', '五', '六', '日']

const presetRows = [
  [{ key: 'yesterday', label: '昨日' }, { key: 'today', label: '今日' }],
  [{ key: 'lastWeek', label: '上周' }, { key: 'thisWeek', label: '本周' }],
  [{ key: 'lastMonth', label: '上月' }, { key: 'thisMonth', label: '本月' }],
  [{ key: 'past7', label: '过去7天' }, { key: 'recent7', label: '最近7天' }],
  [{ key: 'past30', label: '过去30天' }, { key: 'recent30', label: '最近30天' }],
  [{ key: 'pastYear', label: '过去一年' }]
]
const fixedPresets = [
  { key: 'fromToYesterday', label: '自某日至昨日' },
  { key: 'fromToToday', label: '自某日至今' }
]
const presetLabelMap = {}
;[...presetRows.flat(), ...fixedPresets].forEach((p) => (presetLabelMap[p.key] = p.label))

// single 模式仅保留单日预设（昨日/今日），隐藏多日与「自某日至…」
const visiblePresetRows = computed(() =>
  props.single ? [[{ key: 'yesterday', label: '昨日' }, { key: 'today', label: '今日' }]] : presetRows
)
const visibleFixedPresets = computed(() => (props.single ? [] : fixedPresets))

function presetRange(key) {
  switch (key) {
    case 'yesterday': { const y = addDays(TODAY, -1); return [y, y] }
    case 'today': return [TODAY, TODAY]
    case 'lastWeek': { const mon = startOfWeek(TODAY); return [addDays(mon, -7), addDays(mon, -1)] }
    case 'thisWeek': return [startOfWeek(TODAY), TODAY]
    case 'lastMonth': { const lm = addMonths(TODAY, -1); return [firstOfMonth(lm), lastOfMonth(lm)] }
    case 'thisMonth': return [firstOfMonth(TODAY), TODAY]
    case 'past7': return [addDays(TODAY, -7), addDays(TODAY, -1)]
    case 'recent7': return [addDays(TODAY, -6), TODAY]
    case 'past30': return [addDays(TODAY, -30), addDays(TODAY, -1)]
    case 'recent30': return [addDays(TODAY, -29), TODAY]
    case 'pastYear': return [addDays(TODAY, -365), addDays(TODAY, -1)]
    default: return [addDays(TODAY, -29), TODAY]
  }
}

// 已生效
const initRange = presetRange(props.defaultPreset)
const committedStart = ref(initRange[0])
const committedEnd = ref(initRange[1])
const committedLabel = ref(presetLabelMap[props.defaultPreset] || '最近30天')
// 草稿
const draftStart = ref(committedStart.value)
const draftEnd = ref(committedEnd.value)
const activePreset = ref(props.defaultPreset)
const pendingFixedEnd = ref(null)
const leftMonth = ref(firstOfMonth(committedStart.value))
const rightMonth = computed(() => addMonths(leftMonth.value, 1))

// 时间模式：static(日历点选具体日期) | dynamic(相对今天的偏移天数)
const timeMode = ref('static')
const startOffset = ref(7) // 开始：N 天前
const endOffset = ref(0) // 结束：M 天前（0=今日）
const committedMode = ref('static')
const committedStartOffset = ref(7)
const committedEndOffset = ref(0)
const endLabelText = computed(() => {
  if (endOffset.value === 0) return '今日'
  if (endOffset.value === 1) return '昨日'
  return slash(draftEnd.value)
})

const popRef = ref()
const draftLabel = computed(() => (activePreset.value ? presetLabelMap[activePreset.value] || '自定义' : '自定义'))

function applyPreset(key) {
  timeMode.value = 'static' // 预设属于静态选择
  if (key === 'fromToYesterday' || key === 'fromToToday') {
    pendingFixedEnd.value = key === 'fromToYesterday' ? addDays(TODAY, -1) : TODAY
    draftStart.value = null
    draftEnd.value = null
    activePreset.value = key
    leftMonth.value = firstOfMonth(pendingFixedEnd.value)
    return
  }
  const r = presetRange(key)
  pendingFixedEnd.value = null
  draftStart.value = r[0]
  draftEnd.value = r[1]
  activePreset.value = key
  leftMonth.value = firstOfMonth(r[0])
}

// 切换动态/静态时间模式
function setMode(m) {
  timeMode.value = m
  if (m === 'dynamic') {
    onDynamicChange()
  } else {
    leftMonth.value = firstOfMonth(draftStart.value || TODAY)
  }
}

// 动态偏移变化：开始 N 天前、结束 M 天前 → 具体日期（保证开始不晚于结束）
function onDynamicChange() {
  if (startOffset.value == null) startOffset.value = 0
  if (endOffset.value == null) endOffset.value = 0
  if (startOffset.value < endOffset.value) {
    endOffset.value = startOffset.value
  }
  draftStart.value = addDays(TODAY, -startOffset.value)
  draftEnd.value = addDays(TODAY, -endOffset.value)
  activePreset.value = ''
}

function clickDay(d) {
  // single 模式：点选即为单日（start=end）
  if (props.single) {
    draftStart.value = d
    draftEnd.value = d
    activePreset.value = ''
    return
  }
  if (pendingFixedEnd.value) {
    if (d.getTime() <= pendingFixedEnd.value.getTime()) {
      draftStart.value = d
      draftEnd.value = pendingFixedEnd.value
    }
    pendingFixedEnd.value = null
    return
  }
  if (!draftStart.value || draftEnd.value) {
    draftStart.value = d
    draftEnd.value = null
  } else if (d.getTime() < draftStart.value.getTime()) {
    draftStart.value = d
  } else {
    draftEnd.value = d
  }
  activePreset.value = ''
}

function shiftMonth(n) {
  leftMonth.value = addMonths(leftMonth.value, n)
}
function monthTitle(m) {
  return `${m.getMonth() + 1}月 ${m.getFullYear()}`
}
function monthCells(m) {
  const first = firstOfMonth(m)
  const lead = (first.getDay() + 6) % 7
  const days = lastOfMonth(m).getDate()
  const cells = []
  for (let i = 0; i < lead; i++) cells.push(null)
  for (let day = 1; day <= days; day++) cells.push({ day, date: new Date(m.getFullYear(), m.getMonth(), day) })
  return cells
}
function cellClass(c) {
  if (!c) return ''
  const d = c.date
  return {
    'is-start': sameDay(d, draftStart.value),
    'is-end': sameDay(d, draftEnd.value),
    'in-range': inRange(d, draftStart.value, draftEnd.value),
    'is-today': sameDay(d, TODAY)
  }
}

function onPanelShow() {
  timeMode.value = committedMode.value
  startOffset.value = committedStartOffset.value
  endOffset.value = committedEndOffset.value
  draftStart.value = committedStart.value
  draftEnd.value = committedEnd.value
  activePreset.value = labelToPreset(committedLabel.value)
  pendingFixedEnd.value = null
  leftMonth.value = firstOfMonth(committedStart.value)
}
function labelToPreset(label) {
  const hit = [...presetRows.flat(), ...fixedPresets].find((p) => p.label === label)
  return hit ? hit.key : ''
}
function cancel() {
  popRef.value && popRef.value.hide()
}
function apply() {
  if (!draftStart.value || !draftEnd.value) return
  // 保证 start <= end
  let s = draftStart.value
  let e = draftEnd.value
  if (s.getTime() > e.getTime()) {
    const t = s
    s = e
    e = t
  }
  committedStart.value = s
  committedEnd.value = e
  committedMode.value = timeMode.value
  committedStartOffset.value = startOffset.value
  committedEndOffset.value = endOffset.value
  if (activePreset.value) {
    committedLabel.value = presetLabelMap[activePreset.value]
  } else if (props.single || sameDay(s, e)) {
    committedLabel.value = slash(s) // 单日仅显示单个日期
  } else if (timeMode.value === 'dynamic') {
    committedLabel.value = `${slash(s)} → ${endLabelText.value}`
  } else {
    committedLabel.value = `${slash(s)} → ${slash(e)}`
  }
  popRef.value && popRef.value.hide()
  emitChange()
}
function emitChange() {
  emit('change', { start: ymd(committedStart.value), end: ymd(committedEnd.value), label: committedLabel.value })
}

// 供父组件按粒度默认选中某预设并立即生效（如按天默认「最近7天」）
function applyPresetAndEmit(key) {
  applyPreset(key)
  apply()
}
defineExpose({ applyPresetAndEmit })

// 切到 single（如按小时）时，若已生效的是多日范围，收敛为结束日单日（仅同步展示；
// 数据重新加载由父组件的粒度切换逻辑负责，避免重复请求）
watch(
  () => props.single,
  (isSingle) => {
    if (isSingle && !sameDay(committedStart.value, committedEnd.value)) {
      timeMode.value = 'static'
      committedStart.value = committedEnd.value
      draftStart.value = committedEnd.value
      draftEnd.value = committedEnd.value
      activePreset.value = ''
      committedLabel.value = slash(committedEnd.value)
    }
  }
)

// 切到 multiOnly（如按天/周/月）时，若已生效的是单日，清除单日选中态——
// 趋势模式按默认窗口展示，触发器提示重新选择多日范围（不改 committed 区间，故无需重复请求）
watch(
  () => props.multiOnly,
  (isMulti) => {
    if (isMulti && sameDay(committedStart.value, committedEnd.value)) {
      activePreset.value = ''
      committedLabel.value = ''
    }
  }
)

// 挂载即抛出默认范围，驱动父组件首次加载
onMounted(emitChange)
</script>

<style scoped>
.drp-trigger {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
}
.drp-trigger:hover {
  color: #409eff;
}
.drp-caret {
  margin-left: 2px;
  font-size: 10px;
  font-style: normal;
}
.drp-head {
  padding: 4px 4px 12px;
  border-bottom: 1px solid #f0f0f0;
}
.drp-head-title {
  font-size: 13px;
  color: #909399;
}
.drp-head-sub {
  margin-top: 4px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.drp-body {
  display: flex;
  gap: 16px;
  padding: 12px 4px;
}
.drp-presets {
  width: 150px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.drp-preset-row {
  display: flex;
  gap: 8px;
}
.drp-preset-btn {
  flex: 1;
  padding: 6px 0;
  font-size: 13px;
  color: #606266;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
}
.drp-preset-btn:hover {
  color: #409eff;
  border-color: #c6e2ff;
}
.drp-preset-btn.active {
  color: #fff;
  background: #409eff;
  border-color: #409eff;
}
.drp-preset-btn:disabled,
.drp-preset-btn.disabled,
.drp-preset-btn:disabled:hover,
.drp-preset-btn.disabled:hover {
  color: #c0c4cc;
  background: #f5f7fa;
  border-color: #e4e7ed;
  cursor: not-allowed;
}
.drp-preset-full {
  width: 100%;
}
.drp-right {
  flex: 1;
  min-width: 0;
}
.drp-mode-tabs {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
}
.drp-mode-tab {
  padding: 4px 14px;
  font-size: 13px;
  color: #606266;
  background: #f5f7fa;
  border-radius: 4px;
  cursor: pointer;
}
.drp-mode-tab:hover {
  color: #409eff;
}
.drp-mode-tab.active {
  color: #fff;
  background: #409eff;
}
.drp-dynamic {
  padding: 8px 0;
}
.drp-dyn-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.drp-dyn-row :deep(.el-input-number) {
  width: 96px;
}
.drp-dyn-unit {
  font-size: 13px;
  color: #606266;
}
.drp-dyn-arrow {
  margin: 0 4px;
  color: #909399;
}
.drp-dyn-hint {
  margin-top: 12px;
  font-size: 13px;
  color: #409eff;
}
.drp-cals {
  flex: 1;
  display: flex;
  gap: 16px;
}
.drp-cal {
  flex: 1;
}
.drp-cal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: #303133;
  margin-bottom: 8px;
}
.drp-cal-title {
  font-weight: 600;
}
.drp-nav {
  cursor: pointer;
  color: #909399;
  padding: 0 6px;
  user-select: none;
}
.drp-nav:hover {
  color: #409eff;
}
.drp-week,
.drp-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}
.drp-week span {
  text-align: center;
  font-size: 12px;
  color: #c0c4cc;
  padding: 4px 0;
}
.drp-day {
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #606266;
  cursor: pointer;
  border-radius: 4px;
}
.drp-day:hover {
  background: #ecf5ff;
}
.drp-day.is-today {
  color: #409eff;
  font-weight: 600;
}
.drp-day.in-range {
  background: #ecf5ff;
  border-radius: 0;
}
.drp-day.is-start,
.drp-day.is-end {
  background: #409eff !important;
  color: #fff;
}
.drp-foot {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 10px 4px 2px;
  border-top: 1px solid #f0f0f0;
}
</style>

<style>
.drp-pop.el-popover.el-popper {
  padding: 12px 16px;
}
</style>
