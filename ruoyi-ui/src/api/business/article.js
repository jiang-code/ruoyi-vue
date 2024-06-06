import request from '@/utils/request'

export function listArticle(query) {
  return request({
    url: '/admin/article/list',
    method: 'get',
    params: query
  })
}

export function deleteArticle(data) {
  return request({
    url: '/admin/article/delete',
    method: 'post',
    data
  })
}

export function publishArticle(data) {
  return request({
    url: '/admin/article/create',
    method: 'post',
    data
  })
}

export function detailArticle(id) {
  return request({
    url: '/admin/article/detail',
    method: 'get',
    params: { id }
  })
}

export function editArticle(data) {
  return request({
    url: '/admin/article/update',
    method: 'post',
    data
  })
}

