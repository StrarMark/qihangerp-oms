import request from '@/utils/request'


// 查询淘宝商品列表
export function listGoods(query) {
  return request({
    url: '/api/open-api/tao/goods/list',
    method: 'get',
    params: query
  })
}

// 查询淘宝商品sku列表
export function listGoodsSku(query) {
  return request({
    url: '/api/open-api/tao/goods/skuList',
    method: 'get',
    params: query
  })
}


export function getGoodsSku(id) {
  return request({
    url: '/api/open-api/tao/goods/sku/'+id,
    method: 'get',
  })
}


export function linkErpGoodsSkuId(data) {
  return request({
    url: '/api/open-api/tao/goods/sku/linkErp',
    method: 'post',
    data: data
  })
}

// 接口拉取淘宝商品
export function pullGoodsList(data) {
  return request({
    url: '/api/open-api/tao/goods/pull_goods',
    method: 'post',
    data: data
  })
}
//推送商品到商品库
export function pushToOms(data) {
  return request({
    url: '/api/open-api/tao/goods/push_oms',
    method: 'post',
    data: data
  })
}
