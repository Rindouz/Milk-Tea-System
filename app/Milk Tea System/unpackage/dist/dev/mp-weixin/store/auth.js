"use strict";
const common_vendor = require("../common/vendor.js");
const useAuthStore = common_vendor.defineStore("auth", {
  state: () => ({
    token: common_vendor.index.getStorageSync("satoken") || "",
    userInfo: common_vendor.index.getStorageSync("userInfo") ? JSON.parse(common_vendor.index.getStorageSync("userInfo")) : null,
    isLogin: !!common_vendor.index.getStorageSync("satoken")
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
    }
  },
  actions: {
    setLogin(token, userInfo) {
      this.token = token;
      this.userInfo = userInfo;
      this.isLogin = true;
      common_vendor.index.setStorageSync("satoken", token);
      common_vendor.index.setStorageSync("userInfo", JSON.stringify(userInfo));
    },
    setUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo };
      common_vendor.index.setStorageSync("userInfo", JSON.stringify(this.userInfo));
    },
    logout() {
      this.token = "";
      this.userInfo = null;
      this.isLogin = false;
      common_vendor.index.removeStorageSync("satoken");
      common_vendor.index.removeStorageSync("userInfo");
    }
  }
});
exports.useAuthStore = useAuthStore;
//# sourceMappingURL=../../.sourcemap/mp-weixin/store/auth.js.map
