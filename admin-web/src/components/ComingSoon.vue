<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <el-empty :description="`「${title}」模块前端已就绪，等待后端接口接入后开放`">
        <template #image>
          <el-icon style="font-size: 60px; color: #c0c4cc"><Tools /></el-icon>
        </template>
        <div class="cs-desc">
          <p>本页面已纳入菜单与权限体系（权限码：<b>{{ perm || '—' }}</b>）。</p>
          <p>对应业务接口规划在《运营后台系统策划案》中，后端接口补齐后即可联调真实数据，无需改动菜单结构。</p>
        </div>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '功能' },
  perm: { type: String, default: '' }
})
</script>

<style scoped>
.cs-desc {
  max-width: 560px;
  margin: 0 auto;
  color: #909399;
  font-size: 13px;
  line-height: 1.9;
  text-align: center;
}
</style>
