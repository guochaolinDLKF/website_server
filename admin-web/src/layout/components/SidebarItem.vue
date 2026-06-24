<template>
  <!-- 含子菜单：渲染为可展开的 sub-menu -->
  <el-sub-menu v-if="hasChildren" :index="item.path || String(item.id)">
    <template #title>
      <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
      <span>{{ item.name }}</span>
    </template>
    <SidebarItem v-for="child in item.children" :key="child.id" :item="child" />
  </el-sub-menu>

  <!-- 叶子菜单：渲染为可点击项 -->
  <el-menu-item v-else :index="item.path">
    <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
    <template #title>{{ item.name }}</template>
  </el-menu-item>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  item: { type: Object, required: true }
})

const hasChildren = computed(() => Array.isArray(props.item.children) && props.item.children.length > 0)
</script>
