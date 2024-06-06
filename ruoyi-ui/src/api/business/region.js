import request from '@/utils/request'

export function listRegion(query) {
  return request({
    url: '/admin/region/list',
    method: 'get',
    params: query
  })
}

export function listSubRegion(query) {
  return request({
    url: '/admin/region/clist',
    method: 'get',
    params: query
  })
}
