import request from '@/utils/request'

// 列表
export function getWaybillAccountList(params) {
  return request({
    url: '/api/oms-api/ewaybill/get_waybill_account_list',
    method: 'get',
    params: params
  })
}

// 更新电子面单账户
export function pullWaybillAccount(data) {
  return request({
    url: '/api/oms-api/ewaybill/pull_waybill_account',
    method: 'post',
    data: data
  })
}

// 取号并发货
export function getWaybillCodeAndSend(data) {
  return request({
    url: '/api/oms-api/ewaybill/get_waybill_code_and_send',
    method: 'post',
    data: data
  })
}


