import { FormSchema } from '@/components/Form/index';
export const schemas: FormSchema[] = [
<#list table.fields as field>
  <#if field.propertyName == 'name' || field.propertyName == 'title'> 
  {
    field: '${field.propertyName}',
    component: 'Input',
    label: '${field.comment}',
    componentProps: {
      placeholder: '请输入${field.comment}',
    },
  },
  <#elseif (field.customMap?? && field.customMap?size > 0)>
  {
    field: '${field.propertyName}',
    component: 'Select',
    label: '${field.comment}',
    componentProps: {
      placeholder: '请选择${field.comment}',
      clearable: true,
      options: [
      <#list field.customMap?keys as key>
        {
          label: '${field.customMap[key]!}',
          value: '${key}',
        },
      </#list>
      ],
    },
  },
  </#if>
</#list>
];
