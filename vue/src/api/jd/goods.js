import request from '@/utils/request'

// 查询jd-goods列表
export function listGoods(query) {
  return request({
    url: '/api/open-api/jd/goods/list',
    method: 'get',
    params: query
  })
}


// 查询jd-goods-sku列表
export function listGoodsSku(query) {
  return request({
    url: '/api/open-api/jd/goods/skuList',
    method: 'get',
    params: query
  })
}


export function getGoodsSku(id) {
  return request({
    url: '/api/open-api/jd/goods/sku/'+id,
    method: 'get',
  })
}


export function linkErpGoodsSkuId(data) {
  return request({
    url: '/api/open-api/jd/goods/sku/linkErp',
    method: 'post',
    data: data
  })
}

// 接口拉取淘宝商品
export function pullGoodsList(data) {
  return request({
    url: '/api/open-api/jd/goods/pull_goods',
    method: 'post',
    data: data
  })
}
