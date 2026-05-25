"use strict";
const common_vendor = require("../common/vendor.js");
function getClientId() {
  let clientId = common_vendor.index.getStorageSync("clientId");
  if (!clientId) {
    clientId = "mp_" + Date.now().toString(36) + "_" + Math.random().toString(36).substring(2, 10);
    common_vendor.index.setStorageSync("clientId", clientId);
  }
  return clientId;
}
const useAuthStore = common_vendor.defineStore("auth", {
  state: () => ({
    token: common_vendor.index.getStorageSync("satoken") || "",
    userInfo: common_vendor.index.getStorageSync("userInfo") ? JSON.parse(common_vendor.index.getStorageSync("userInfo")) : null,
    isLogin: !!common_vendor.index.getStorageSync("satoken"),
    clientId: getClientId()
  }),
  getters: {
    userId: (state) => {
      var _a;
      return ((_a = state.userInfo) == null ? void 0 : _a.userId) || null;
    },
    nickname: (state) => {
      var _a;
      return ((_a = state.userInfo) == null ? void 0 : _a.nickname) || "未登录";
    },
    avatar: (state) => {
      var _a;
      return ((_a = state.userInfo) == null ? void 0 : _a.avatar) || "/static/logo.png";
    },
    phone: (state) => {
      var _a;
      return ((_a = state.userInfo) == null ? void 0 : _a.phone) || "";
    }
  },
  actions: {
    setLogin(token, userInfo) {
      this.token = token;
      this.userInfo = userInfo;
      this.isLogin = true;
      common_vendor.index.setStorageSync("satoken", token);
      common_vendor.index.setStorageSync("userInfo", JSON.stringify(userInfo));
      common_vendor.index.setStorageSync("userId", userInfo.userId);
    },
    setUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo };
      common_vendor.index.setStorageSync("userInfo", JSON.stringify(this.userInfo));
    },
    updatePhone(phone) {
      if (this.userInfo) {
        this.userInfo.phone = phone;
        common_vendor.index.setStorageSync("userInfo", JSON.stringify(this.userInfo));
      }
    },
    logout() {
      this.token = "";
      this.userInfo = null;
      this.isLogin = false;
      common_vendor.index.removeStorageSync("satoken");
      common_vendor.index.removeStorageSync("userInfo");
      common_vendor.index.removeStorageSync("userId");
    }
  }
});
exports.useAuthStore = useAuthStore;
//# sourceMappingURL=../../.sourcemap/mp-weixin/store/auth.js.map
