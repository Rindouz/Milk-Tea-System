"use strict";
const common_vendor = require("../../common/vendor.js");
const store_auth = require("../../store/auth.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const authStore = store_auth.useAuthStore();
    const orderStats = common_vendor.ref({});
    const goOrders = () => {
      common_vendor.index.switchTab({ url: "/pages/order/list" });
    };
    const goIndex = () => {
      common_vendor.index.switchTab({ url: "/pages/index/index" });
    };
    const goCart = () => {
      common_vendor.index.switchTab({ url: "/pages/cart/index" });
    };
    common_vendor.onMounted(async () => {
      var _a;
      if (authStore.isLogin) {
        try {
          const res = await api_index.userApi.getUserOrders(1, 100);
          const records = ((_a = res.data) == null ? void 0 : _a.records) || [];
          orderStats.value = {
            pending: records.filter((o) => o.orderStatus === 0).length,
            waiting: records.filter((o) => o.orderStatus === 1).length,
            making: records.filter((o) => o.orderStatus === 3).length,
            completed: records.filter((o) => o.orderStatus === 2).length
          };
        } catch (e) {
        }
      }
    });
    const handleWechatLogin = () => {
      common_vendor.index.login({
        provider: "weixin",
        success: async (res) => {
          try {
            const apiRes = await api_index.userApi.wechatLogin(res.code);
            const tokenInfo = apiRes.data;
            authStore.setLogin(tokenInfo.tokenValue, {
              userId: tokenInfo.loginId,
              nickname: "微信用户",
              avatar: "/static/logo.png"
            });
            common_vendor.index.showToast({ title: "登录成功", icon: "success" });
          } catch (e) {
            common_vendor.index.showToast({ title: "登录失败", icon: "none" });
          }
        },
        fail: () => {
          common_vendor.index.showToast({ title: "微信授权失败", icon: "none" });
        }
      });
    };
    const handleLogout = () => {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            authStore.logout();
            orderStats.value = {};
            common_vendor.index.showToast({ title: "已退出登录", icon: "success" });
          }
        }
      });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.unref(authStore).avatar,
        b: common_vendor.t(common_vendor.unref(authStore).nickname),
        c: common_vendor.unref(authStore).isLogin
      }, common_vendor.unref(authStore).isLogin ? {} : {}, {
        d: !common_vendor.unref(authStore).isLogin
      }, !common_vendor.unref(authStore).isLogin ? {
        e: common_vendor.o(handleWechatLogin, "de")
      } : {}, {
        f: common_vendor.t(orderStats.value.pending || 0),
        g: common_vendor.t(orderStats.value.waiting || 0),
        h: common_vendor.t(orderStats.value.making || 0),
        i: common_vendor.t(orderStats.value.completed || 0),
        j: common_vendor.o(goOrders, "5d"),
        k: common_vendor.o(goOrders, "1f"),
        l: common_vendor.o(goIndex, "95"),
        m: common_vendor.o(goCart, "63"),
        n: common_vendor.unref(authStore).isLogin
      }, common_vendor.unref(authStore).isLogin ? {
        o: common_vendor.o(handleLogout, "80")
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-79e6a490"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/user/index.js.map
