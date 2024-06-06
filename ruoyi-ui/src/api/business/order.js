import request from '@/utils/request'
import Qs from 'qs'

export function listOrder(query) {
  return request({
    url: '/admin/order/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function detailOrder(id) {
  return request({
    url: '/admin/order/detail',
    method: 'get',
    params: { id }
  })
}

export function shipOrder(data) {
  return request({
    url: '/admin/order/ship',
    method: 'post',
    data
  })
}

export function refundOrder(data) {
  return request({
    url: '/admin/order/refund',
    method: 'post',
    data
  })
}

export function replyComment(data) {
  return request({
    url: '/admin/order/reply',
    method: 'post',
    data
  })
}

export function listShipChannel() {
  return request({
    url: '/admin/order/listShipChannel',
    method: 'get'
  })
}
