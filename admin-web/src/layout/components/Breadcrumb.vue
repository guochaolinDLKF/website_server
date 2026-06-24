<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item v-for="(item, idx) in matchedTitles" :key="idx">
      {{ item }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 取当前路由 meta.title 作为面包屑（首页恒在最前）
const matchedTitles = computed(() => {
  const titles = route.matched
    .filter((r) => r.meta && r.meta.title && r.path !== '/dashboard')
    .map((r) => r.meta.title)
  return titles
})
</script>
