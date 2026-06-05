import request from '@/utils/request'

// 用户相关
export const userApi = {
  login: (username, password) => request.get('/user/doLogin', { username, password }),
  wechatLogin: (code, clientId) => {
    let url = `/user/wechatLogin?code=${code}`
    if (clientId) url += `&clientId=${clientId}`
    return request.post(url, {})
  },
  updateWechatInfo: (data) => request.post('/user/updateWechatInfo', data),
  getUserOrders: (pageNum = 1, pageSize = 10) => request.get('/user/orders', { pageNum, pageSize })
}

// 分类相关
export const categoryApi = {
  list: () => request.get('/category/list')
}

// 商品相关
export const productApi = {
  list: (params) => request.get('/product/list', params),
  listAll: () => request.get('/product/all'),
  detail: (productId) => request.get(`/product/detail/${productId}`),
  stock: (productId) => request.get(`/product/stock/${productId}`)
}

// 库存相关
export const inventoryApi = {
  stock: (productId) => request.get(`/inventory/stock/${productId}`)
}

// 门店相关
export const storeApi = {
  listAll: () => request.get('/store'),
  detail: (id) => request.get(`/store/${id}`),
  waitingCount: (id) => request.get(`/store/${id}/orders/Waiting`)
}

// 订单相关
export const orderApi = {
  create: (data) => request.post('/orders/create', data),
  pay: (orderNo) => request.post(`/orders/pay/${orderNo}`),
  cancel: (orderNo) => request.post(`/orders/cancel/${orderNo}`),
  confirm: (orderNo) => request.post(`/orders/confirm/${orderNo}`),
  list: (userId, status) => request.get(`/orders/user/${userId}`, { status }),
  listPage: (userId, page = 1, size = 10, status) =>
    request.get(`/orders/user/page/${userId}`, { page, size, status }),
  detail: (orderNo) => request.get(`/orders/detail/${orderNo}`)
}
