import { http } from '@/utils/http/axios';

/**
 * @description: ${table.comment!}列表
 */
export function get${entity}List(params?) {
  return http.request({
    url: '/${entity?lower_case}/page',
    method: 'GET',
    params,
  });
}

/**
 * 获取全部${table.comment!}列表
 * @param params 参数
 * @returns 返回结果
 */
export function get${entity}AllList(params?) {
  return http.request({
    url: '/${entity?lower_case}/list',
    method: 'GET',
    params,
  });
}

/**
 * @description: 根据ID获取详情
 */
export function get${entity}Detail(${entity?lower_case}Id) {
  return http.request({
    url: '/${entity?lower_case}/detail/' + ${entity?lower_case}Id,
    method: 'get',
  });
}

/**
 * @description: 添加${table.comment!}
 */
export function ${entity?lower_case}Add(data: any) {
  return http.request({
    url: '/${entity?lower_case}/add',
    method: 'POST',
    data,
  });
}

/**
 * @description: 更新${table.comment!}
 */
export function ${entity?lower_case}Update(data: any) {
  return http.request({
    url: '/${entity?lower_case}/update',
    method: 'PUT',
    data,
  });
}

/**
 * @description: 删除${table.comment!}
 */
export function ${entity?lower_case}Delete(${entity?lower_case}Id) {
  return http.request({
    url: '/${entity?lower_case}/delete/' + ${entity?lower_case}Id,
    method: 'DELETE',
  });
}

/**
 * @description: 批量删除${table.comment!}
 */
export function ${entity?lower_case}BatchDelete(data: any) {
  return http.request({
    url: '/${entity?lower_case}/batchDelete',
    method: 'DELETE',
    data,
  });
}
