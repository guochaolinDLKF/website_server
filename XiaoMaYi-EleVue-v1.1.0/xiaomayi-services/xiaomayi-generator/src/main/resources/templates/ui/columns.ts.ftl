import { h } from 'vue';
import { ElAvatar, ElTag } from 'element-plus';

export const columns = [
  {
    type: 'selection',
  },
  {
    label: 'ID',
    prop: 'id',
    width: 100,
  },
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
  {
    label: '${field.comment}',
    prop: '${field.propertyName}',
    render(record) {
      let ${field.propertyName}Text = '';
      switch (record.row.${field.propertyName}) {
      <#list field.customMap?keys as key>
        case ${key}:
          ${field.propertyName}Text = '${field.customMap[key]!}';
          break;
      </#list>
        default:
          break;
      }
      return h('span', ${field.propertyName}Text || '-');
    },
  },
  <#else>
  {
    label: '${field.comment}',
    prop: '${field.propertyName}',
    render(record) {
      return h(
        ElTag,
        {
          type: record.row.${field.propertyName} == ${field.metaInfo.defaultValue} ? 'success' : 'danger',
        },
        {
          default: () => (record.row.${field.propertyName} == ${field.metaInfo.defaultValue} ? '${field.customMap["1"]!}' : '${field.customMap["2"]!}'),
        },
      );
    },
  },
  </#if>
  <#elseif field.propertyName == 'cover' || field.propertyName == 'logo' || field.propertyName == 'image'>
  {
    label: '${field.comment}',
    prop: '${field.propertyName}',
    render(record) {
      return h(ElAvatar, {
        size: 48,
        src: record.row.${field.propertyName},
        shape: 'square',
        fit: 'fill',
      });
    },
    width: 100,
  },
  <#else>
  {
    label: '${field.comment}',
    prop: '${field.propertyName}',
  },
  </#if>
</#if>
</#list>
  {
    label: '创建人',
    prop: 'createUser',
  },
  {
    label: '创建时间',
    prop: 'createTime',
    width: 180,
  },
];
