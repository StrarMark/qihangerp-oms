import request from '@/utils/request'

// 查询店铺订单列表
export function listOrder(query) {
  return request({
    url: '/api/oms-api/order/list',
    method: 'get',
    params: query
  })
}

// 查询店铺订单详细
export function getOrder(id) {
  return request({
    url: '/api/oms-api/order/' + id,
    method: 'get'
  })
}

// 订单明细list
export function listOrderItem(query) {
  return request({
    url: '/api/oms-api/order/item_list',
    method: 'get',
    params: query
  })
}

export function updateErpSkuId(data) {
  return request({
    url: '/api/oms-api/order/updateErpSkuId',
    method: 'post',
    data: data
  })
}



// 分配供应商发货
export function allocateShipmentOrder(data) {
  return request({
    url: '/api/oms-api/order/allocateShipmentOrder',
    method: 'post',
    data: data
  })
}


// 手动发货
export function manualShipmentOrder(data) {
  return request({
    url: '/api/oms-api/order/manualShipment',
    method: 'post',
    data: data
  })
}
