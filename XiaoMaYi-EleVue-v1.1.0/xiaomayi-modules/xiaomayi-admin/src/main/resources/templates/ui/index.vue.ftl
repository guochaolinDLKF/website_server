<template>
  <PageWrapper>
    <el-card :bordered="false" class="pt-3 mb-3 proCard">
      <BasicForm @register="register" @submit="handleSubmit" @reset="handleReset" />
    </el-card>
    <el-card :bordered="false" class="proCard">
      <BasicTable
        :columns="columns"
        :request="loadDataTable"
        :row-key="(row) => row.id"
        ref="tableRef"
        :actionColumn="actionColumn"
        @selection-change="onSelectionChange"
      >
        <template #tableTitle>
          <el-button type="primary" @click="handleAdd" v-perm="['sys:${entity?lower_case}:add']">
            <template #icon>
              <el-icon class="el-input__icon">
                <PlusOutlined />
              </el-icon>
            </template>
            添加${table.comment!}
          </el-button>
          <el-button
            type="danger"
            @click="handleDelete()"
            :disabled="!selectionData.length"
            v-perm="['sys:${entity?lower_case}:batchDelete']"
          >
            <template #icon>
              <el-icon class="el-input__icon">
                <Delete />
              </el-icon>
            </template>
            删除
          </el-button>
        </template>
      </BasicTable>
    </el-card>

    <editDialog
      v-if="editVisible"
      :${entity?lower_case}Id="${entity?lower_case}Id"
      v-model:visible="editVisible"
      @success="reloadTable('noRefresh')"
    />
  </PageWrapper>
</template>

<script lang="ts" setup>
  import { reactive, ref, h, nextTick, defineAsyncComponent } from 'vue';
  import { ColProps } from 'element-plus';
  import { schemas } from './querySchemas';
  import { useForm } from '@/components/Form/index';
  import { TableAction } from '@/components/Table';
  import { get${entity}List, ${entity?lower_case}Delete, ${entity?lower_case}BatchDelete } from '@/api/tool/${entity?lower_case}';
  import { columns } from './columns';
  import { PlusOutlined } from '@vicons/antd';
  import { message, confirm } from '@/utils/auth';

  /**
   * 导入组件
   */
  const editDialog = defineAsyncComponent(() => import('./edit.vue'));

  /**
   * 定义参数变量
   */
  const ${entity?lower_case}Id = ref(0);
  const editVisible = ref(false);
  const selectionData = ref([]);
  const tableRef = ref();

  /**
   * 定义查询参数
   */
  const formParams = reactive({
<#list table.fields as field>
  <#if field.propertyName != 'id' 
    && field.propertyName != 'createUser' 
    && field.propertyName != 'createTime' 
    && field.propertyName != 'updateUser' 
    && field.propertyName != 'updateTime' 
    && field.propertyName != 'delFlag' 
    && field.propertyName != 'tenantId'
  >
  <#if field.comment?contains('名称')
    || field.comment?contains('标题')
    || field.comment?contains('状态')
    || field.comment?contains('类型')
  >
    ${field.propertyName}: '',
  </#if>
  </#if>
</#list>
  });

  /**
   * 定义操作栏
   */
  const actionColumn = reactive({
    width: 200,
    label: '操作',
    prop: 'action',
    fixed: 'right',
    render(record) {
      return h(TableAction, {
        style: 'button',
        actions: [
          {
            label: '编辑',
            icon: 'Edit',
            type: 'warning',
            onClick: handleEdit.bind(null, record),
            auth: ['sys:${entity?lower_case}:update'],
          },
          {
            label: '删除',
            icon: 'Delete',
            type: 'danger',
            onClick: handleDelete.bind(null, record),
            auth: ['sys:${entity?lower_case}:delete'],
          },
        ],
      });
    },
  });

  /**
   * 加载数据列表
   * @param res 参数
   */
  const loadDataTable = async (res: any) => {
    const result = await get${entity}List({ ...formParams, ...res });
    return result;
  };

  /**
   * 刷新数据列表
   * @param noRefresh 参数
   */
  function reloadTable(noRefresh = '') {
    tableRef.value.reload(noRefresh ? {} : { pageNo: 1 });
  }

  /**
   * 注册
   */
  const [register, {}] = useForm({
    labelWidth: 80,
    layout: 'horizontal',
    colProps: { span: 6 } as ColProps,
    submitOnReset: true,
    schemas,
  });

  /**
   * 执行提交表单
   */
  function handleSubmit(values: Recordable) {
    handleReset();
    for (const key in values) {
      formParams[key] = values[key];
    }
    reloadTable();
  }

  /**
   * 执行重置
   */
  function handleReset() {
    for (const key in formParams) {
      formParams[key] = '';
    }
  }

  /**
   * 执行添加
   */
  const handleAdd = async () => {
    ${entity?lower_case}Id.value = 0;
    await nextTick();
    editVisible.value = true;
  };

  /**
   * 执行编辑
   * @param record 参数
   */
  const handleEdit = async (record: Recordable) => {
    ${entity?lower_case}Id.value = record.row.id;
    await nextTick();
    editVisible.value = true;
  };

  /**
   * 执行删除
   * @param record 参数
   */
  async function handleDelete(record: Recordable) {
    let ids = [];
    if (!record) {
      ids = selectionData.value.map(({ id }) => id);
    }
    await confirm('确定要删除？');
    record ? await ${entity?lower_case}Delete(record.row.id) : await ${entity?lower_case}BatchDelete(ids);
    message('删除成功');
    reloadTable();
  }

  /**
   * 选项发生变化
   * @param value 参数
   */
  function onSelectionChange(value) {
    selectionData.value = value;
  }
</script>

<style lang="scss" scoped></style>
