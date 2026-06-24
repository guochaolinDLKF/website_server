<template>
  <div class="app-container">
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="table-toolbar" style="margin: 0">
          <span style="font-weight: 600">权限菜单（只读）</span>
          <el-input v-model="filterText" placeholder="筛选权限名称/编码" clearable style="width: 240px" />
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="tree"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="name" label="名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限码" min-width="160" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 1" type="primary">菜单</el-tag>
            <el-tag v-else-if="row.type === 2" type="success">按钮</el-tag>
            <el-tag v-else type="info">接口</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由/路径" min-width="180" />
        <el-table-column prop="icon" label="图标" width="120" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getPermissionTree } from '@/api/permission'

const loading = ref(false)
const fullTree = ref([])
const tree = ref([])
const filterText = ref('')

function filterTree(nodes, kw) {
  const res = []
  for (const n of nodes) {
    const children = n.children ? filterTree(n.children, kw) : []
    const hit = (n.name && n.name.includes(kw)) || (n.permissionCode && n.permissionCode.includes(kw))
    if (hit || children.length) {
      res.push({ ...n, children })
    }
  }
  return res
}

watch(filterText, (v) => {
  tree.value = v ? filterTree(fullTree.value, v) : fullTree.value
})

async function load() {
  loading.value = true
  try {
    const res = await getPermissionTree()
    fullTree.value = res.data || []
    tree.value = fullTree.value
  } finally {
    loading.value = false
  }
}
load()
</script>
