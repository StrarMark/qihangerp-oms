import request from '@/utils/request'

// 查询出库单列表
export function listStockOut(query) {
  return request({
    url: '/api/oms-api/stockOut/list',
    method: 'get',
    params: query
  })
}

// 查询出库单详细
export function getStockOutEntry(id) {
  return request({
    url: '/api/oms-api/stockOut/' + id,
    method: 'get'
  })
}



// 出库
export function stockOut(data) {
  return request({
    url: '/api/oms-api/stockOut/out',
    method: 'post',
    data: data
  })
}

// 打印
export function stockOutPrint(id) {
  return request({
    url: '/api/oms-api/stockOut/print/'+id,
    method: 'get'
  })
}

