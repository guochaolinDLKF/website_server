<template>
  <div class="layout-root">
    <div class="layout-sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
      <div class="logo">
        <span v-if="!appStore.sidebarCollapsed">易德自在运营管理</span>
        <span v-else>运营</span>
      </div>
      <Sidebar />
    </div>
    <div class="layout-main">
      <Navbar />
      <div class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import { useAppStore } from '@/store/app'

const appStore = useAppStore()
</script>

<style scoped>
.layout-root {
  display: flex;
  height: 100vh;
  width: 100%;
  overflow: hidden;
}
.layout-sidebar {
  width: 220px;
  height: 100%;
  background: #304156;
  transition: width 0.28s;
  overflow-x: hidden;
}
.layout-sidebar.collapsed {
  width: 64px;
}
.logo {
  height: 56px;
  line-height: 56px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  background: #2b3a4d;
  white-space: nowrap;
  overflow: hidden;
}
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}
.layout-content {
  flex: 1;
  overflow-y: auto;
  background: #f0f2f5;
}
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.25s;
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-12px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(12px);
}
</style>
