import request from '@/utils/request'

export function info(query) {
  return request({
    url: '/admin/dashboard',
    method: 'get',
    params: query
  })
}

export function chart(query) {
  return request({
    url: '/admin/dashboard/chart',
    method: 'get',
    params: query
  })
}
