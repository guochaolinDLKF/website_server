<template>
  <div class="app-container">
    <el-card class="page-card rt-header-card" shadow="never">
      <div class="rt-title">实时数据</div>
      <div class="rt-subtitle">监控实时新增、在线、付费数据，若出现异常情况及时排查处理</div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <RealtimeOnlineCard />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import RealtimeOnlineCard from '../../dashboard/RealtimeOnlineCard.vue'
</script>

<style scoped>
.rt-header-card {
  padding: 4px 4px;
}
.rt-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2329;
}
.rt-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
