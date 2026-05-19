"use strict";
const common_vendor = require("../common/vendor.js");
const BASE_URL = "http://localhost:8080/milkteasystem";
const cleanData = (data) => {
  if (!data)
    return data;
  const cleaned = {};
  for (const key of Object.keys(data)) {
    if (data[key] !== null && data[key] !== void 0) {
      cleaned[key] = data[key];
    }
  }
  return cleaned;
};
const request = (url, options = {}) => {
  return new Promise((resolve, reject) => {
    const token = common_vendor.index.getStorageSync("satoken");
    common_vendor.index.request({
      url: BASE_URL + url,
      method: options.method || "GET",
      data: cleanData(options.data),
      header: {
        "Content-Type": "application/json",
        "satoken": token || ""
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const body = res.data;
          if (body.code === 200) {
            resolve(body);
          } else {
            common_vendor.index.showToast({ title: body.msg || "请求失败", icon: "none" });
            reject(body);
          }
        } else if (res.statusCode === 401) {
          common_vendor.index.removeStorageSync("satoken");
          common_vendor.index.removeStorageSync("userInfo");
          common_vendor.index.showToast({ title: "请先登录", icon: "none" });
          reject(res);
        } else {
          common_vendor.index.showToast({ title: "服务器错误", icon: "none" });
          reject(res);
        }
      },
      fail: (err) => {
        common_vendor.index.showToast({ title: "网络异常，请检查连接", icon: "none" });
        reject(err);
      }
    });
  });
};
request.get = (url, data) => request(url, { method: "GET", data });
request.post = (url, data) => request(url, { method: "POST", data });
request.put = (url, data) => request(url, { method: "PUT", data });
request.delete = (url, data) => request(url, { method: "DELETE", data });
exports.request = request;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/request.js.map
