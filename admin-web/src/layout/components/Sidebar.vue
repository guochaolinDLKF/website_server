<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="appStore.sidebarCollapsed"
    :collapse-transition="false"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409eff"
    router
    unique-opened
  >
    <SidebarItem v-for="menu in menuTree" :key="menu.id" :item="menu" />
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useAppStore } from '@/store/app'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

// 仅渲染菜单类型(type=1)节点；按 sortOrder 排序
function normalize(nodes) {
  return (nodes || [])
    .filter((n) => n.type === 1)
    .sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
    .map((n) => ({ ...n, children: normalize(n.children) }))
}

const menuTree = computed(() => normalize(userStore.menus))
const activeMenu = computed(() => route.path)
</script>
