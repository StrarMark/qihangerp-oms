import request from '@/utils/request'


export function getPddOAuthUrl(query) {
  return request({
    url: '/api/oms-api/pdd/getOauthUrl',
    method: 'get',
    params: query
  })
}
export function getPddToken(data) {
  return request({
    url: '/api/oms-api/pdd/getToken',
    method: 'post',
    data: data
  })
}
