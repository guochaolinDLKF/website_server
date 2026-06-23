<template>
  <el-dialog
    v-model="props.visible"
    :title="props.${entity?lower_case}Id ? '编辑' : '新增'"
    width="500"
    :close-on-click-modal="false"
    :before-close="dialogClose"
  >
    <el-form class="ls-form" ref="formRef" :model="formData" label-width="80px">
  <#list table.fields as field>
    <#if field.propertyName != 'id' 
      && field.propertyName != 'createUser' 
      && field.propertyName != 'createTime' 
      && field.propertyName != 'updateUser' 
      && field.propertyName != 'updateTime' 
      && field.propertyName != 'delFlag' 
      && field.propertyName != 'tenantId'
    >
    <#if (field.customMap?? && field.customMap?size > 0)>
      <#if (field.customMap?size > 2)>
      <el-form-item label="${field.comment}">
        <el-select v-model="formData.${field.propertyName}" placeholder="请选择${field.comment}">
        <#list field.customMap?keys as key>
          <el-option label="${field.customMap[key]!}" :value="${key}" />
        </#list>
        </el-select>
      </el-form-item>
      <#else>
      <el-form-item label="${field.comment}" prop="${field.propertyName}">
        <el-radio-group v-model="formData.${field.propertyName}" name="${field.propertyName}">
        <#list field.customMap?keys as key>
          <el-radio :value="${key}">${field.customMap[key]!}</el-radio>
        </#list>
        </el-radio-group>
      </el-form-item>
      </#if>
    <#elseif field.propertyName == 'cover' || field.propertyName == 'logo' || field.propertyName == 'image'>
      <el-form-item
        label="${field.comment}"
        prop="${field.propertyName}"
        :rules="{ required: true, message: '请上传${field.comment}', trigger: 'change' }"
      >
        <UploadImg
          @changeFileName="(name) => (formData.imageImgName = name)"
          :fileType="['image/jpeg', 'image/png', 'image/jpg', 'image/gif']"
          name="${entity?lower_case}"
          :fileSize="20"
          v-model:image-url="formData.${field.propertyName}"
        >
          <template #tip>支持扩展名: jpg png jpeg;文件大小不超过20M</template>
        </UploadImg>
      </el-form-item>
    <#elseif field.columnType?string == 'INTEGER'>
      <el-form-item label="${field.comment}" prop="${field.propertyName}">
        <el-input-number v-model="formData.${field.propertyName}" />
      </el-form-item>
    <#elseif field.columnType?string == 'LOCAL_DATE_TIME'>
      <el-form-item
        label="${field.comment}"
        prop="${field.propertyName}"
        :rules="{ required: true, message: '请选择${field.comment}', trigger: 'blur' }"
      >
        <el-date-picker
          type="datetime"
          placeholder="请选择${field.comment}"
          v-model="formData.${field.propertyName}"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
    <#elseif field.columnType?string == 'LOCAL_DATE'>
      <el-form-item
        label="${field.comment}"
        prop="${field.propertyName}"
        :rules="{ required: true, message: '请选择${field.comment}', trigger: 'blur' }"
      >
        <el-date-picker
          type="date"
          placeholder="请选择${field.comment}"
          v-model="formData.${field.propertyName}"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
    <#else>
      <el-form-item
        label="${field.comment}"
        prop="${field.propertyName}"
        :rules="{ required: true, message: '请输入${field.comment}', trigger: 'blur' }"
      >
        <el-input class="ls-input" v-model="formData.${field.propertyName}" placeholder="请输入${field.comment}" clearable />
      </el-form-item>
    </#if>
    </#if>
  </#list>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogClose">取消</el-button>
        <el-button :loading="subLoading" type="primary" @click="submit"> 确定 </el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
  import type { FormInstance } from 'element-plus';
  import { get${entity}Detail, ${entity?lower_case}Add, ${entity?lower_case}Update } from '@/api/tool/${entity?lower_case}';
  import { onMounted, reactive, shallowRef } from 'vue';
  import UploadImg from '@/components/Upload/Image.vue';
  import { message } from '@/utils/auth';
  import { useLockFn } from '@/utils/useLockFn';

  const emit = defineEmits(['success', 'update:visible']);
  const formRef = shallowRef<FormInstance>();

  /**
   * 定义表单参数
   */
  const formData = reactive({
    id: '',
  <#list table.fields as field>
    <#if field.propertyName != 'id' 
      && field.propertyName != 'createUser' 
      && field.propertyName != 'createTime' 
      && field.propertyName != 'updateUser' 
      && field.propertyName != 'updateTime' 
      && field.propertyName != 'delFlag' 
      && field.propertyName != 'tenantId'
    >
    <#if (field.customMap?? && field.customMap?size > 0)>
    ${field.propertyName}: ${field.metaInfo.defaultValue},
    <#elseif field.columnType?string == 'INTEGER'>
    ${field.propertyName}: ${field.metaInfo.defaultValue!0},
    <#else>
    ${field.propertyName}: '',
    </#if>
    </#if>
  </#list>
  });

  /**
   * 定义接收的参数
   */
  const props = defineProps({
    visible: {
      type: Boolean,
      required: true,
      default: false,
    },
    ${entity?lower_case}Id: {
      type: Number,
      required: true,
      default: 0,
    },
  });

  /**
   * 执行提交表单
   */
  const handleSubmit = async () => {
    await formRef.value?.validate();
    props.${entity?lower_case}Id ? await ${entity?lower_case}Update(formData) : await ${entity?lower_case}Add(formData);
    message('操作成功');
    emit('update:visible', false);
    emit('success');
  };

  /**
   * 关闭窗体
   */
  const dialogClose = () => {
    emit('update:visible', false);
  };

  const { isLock: subLoading, lockFn: submit } = useLockFn(handleSubmit);

  /**
   * 设置表单数据
   */
  const setFormData = async () => {
    const data = await get${entity}Detail(props.${entity?lower_case}Id);
    for (const key in formData) {
      if (data[key] != null && data[key] != undefined) {
        //@ts-ignore
        formData[key] = data[key];
      }
    }
  };

  /**
   * 钩子函数
   */
  onMounted(() => {
    if (props.${entity?lower_case}Id) {
      setFormData();
    }
  });
</script>
