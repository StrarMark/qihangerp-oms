import request from '@/utils/request'

// 查询用户AI角色列表
export function listAiUserRole(query) {
  return request({
    url: '/api/ai-agent/aiUserRole/list',
    method: 'get',
    params: query
  })
}

// 查询用户AI角色详细
export function getAiUserRole(id) {
  return request({
    url: '/api/ai-agent/aiUserRole/' + id,
    method: 'get'
  })
}

// 新增用户AI角色
export function addAiUserRole(data) {
  return request({
    url: '/api/ai-agent/aiUserRole',
    method: 'post',
    data: data
  })
}

// 修改用户AI角色
export function updateAiUserRole(data) {
  return request({
    url: '/api/ai-agent/aiUserRole',
    method: 'put',
    data: data
  })
}

// 删除用户AI角色
export function delAiUserRole(id) {
  return request({
    url: '/api/ai-agent/aiUserRole/' + id,
    method: 'delete'
  })
}

// 批量删除用户AI角色
export function delAiUserRoles(ids) {
  return request({
    url: '/api/ai-agent/aiUserRole/' + ids,
    method: 'delete'
  })
}

// 设置默认角色
export function setDefaultRole(id) {
  return request({
    url: '/api/ai-agent/aiUserRole/setDefault/' + id,
    method: 'put'
  })
}