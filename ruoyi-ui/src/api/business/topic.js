import request from '@/utils/request'

export function listTopic(query) {
  return request({
    url: '/admin/topic/list',
    method: 'get',
    params: query
  })
}

export function createTopic(data) {
  return request({
    url: '/admin/topic/create',
    method: 'post',
    data
  })
}

export function readTopic(data) {
  return request({
    url: '/admin/topic/read',
    method: 'get',
    data
  })
}

export function updateTopic(data) {
  return request({
    url: '/admin/topic/update',
    method: 'post',
    data
  })
}

export function deleteTopic(data) {
  return request({
    url: '/admin/topic/delete',
    method: 'post',
    data
  })
}
