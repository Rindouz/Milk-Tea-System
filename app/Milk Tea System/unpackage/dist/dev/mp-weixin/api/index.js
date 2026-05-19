"use strict";
const utils_request = require("../utils/request.js");
const userApi = {
  login: (username, password) => utils_request.request.get("/user/doLogin", { username, password }),
  wechatLogin: (code) => utils_request.request.post(`/user/wechatLogin?code=${code}`),
  updateWechatInfo: (data) => utils_request.request.post("/user/updateWechatInfo", data),
  getUserOrders: (pageNum = 1, pageSize = 10) => utils_request.request.get("/user/orders", { pageNum, pageSize })
};
const categoryApi = {
  list: () => utils_request.request.get("/category/list")
};
const productApi = {
  list: (params) => utils_request.request.get("/product/list", params),
  listAll: () => utils_request.request.get("/product/all"),
  detail: (productId) => utils_request.request.get(`/product/detail/${productId}`),
  stock: (productId) => utils_request.request.get(`/product/stock/${productId}`)
};
const inventoryApi = {
  stock: (productId) => utils_request.request.get(`/inventory/stock/${productId}`)
};
const storeApi = {
  listAll: () => utils_request.request.get("/store"),
  detail: (id) => utils_request.request.get(`/store/${id}`),
  waitingCount: (id) => utils_request.request.get(`/store/${id}/orders/Waiting`)
};
const orderApi = {
  create: (data) => utils_request.request.post("/orders/create", data),
  pay: (orderNo) => utils_request.request.post(`/orders/pay/${orderNo}`),
  cancel: (orderNo) => utils_request.request.post(`/orders/cancel/${orderNo}`),
  confirm: (orderNo) => utils_request.request.post(`/orders/confirm/${orderNo}`),
  list: (userId, status) => utils_request.request.get(`/orders/user/${userId}`, { status }),
  listPage: (userId, page = 1, size = 10, status) => utils_request.request.get(`/orders/user/page/${userId}`, { page, size, status }),
  detail: (orderNo) => utils_request.request.get(`/orders/detail/${orderNo}`)
};
exports.categoryApi = categoryApi;
exports.inventoryApi = inventoryApi;
exports.orderApi = orderApi;
exports.productApi = productApi;
exports.storeApi = storeApi;
exports.userApi = userApi;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/index.js.map
