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

// 取消订单
export function cancelOrder(data) {
  return request({
    url: '/api/oms-api/order/cancelOrder',
    method: 'post',
    data: data
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

// 查询待自己发货的订单列表（待发货的）
export function waitSelfShipmentList(query) {
  return request({
    url: '/api/oms-api/order/waitShipmentList',
    method: 'get',
    params: query
  })
}
// 查询已分配给供应商发货的订单
export function assignedShipmentList(query) {
  return request({
    url: '/api/oms-api/order/assignedShipmentList',
    method: 'get',
    params: query
  })
}
// 查询己发货的订单列表(已发货的)
export function selfShippedList(query) {
  return request({
    url: '/api/oms-api/order/shippedList',
    method: 'get',
    params: query
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
//修改订单item skuId
export function orderItemSpecIdUpdate(data) {
  return request({
    url: '/api/oms-api/order/order_item_sku_id_update',
    method: 'post',
    data: data
  })
}
