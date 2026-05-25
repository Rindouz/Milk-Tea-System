"use strict";
const common_vendor = require("../../common/vendor.js");
const store_auth = require("../../store/auth.js");
const api_index = require("../../api/index.js");
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const authStore = store_auth.useAuthStore();
    const orderStats = common_vendor.ref({});
    const phoneForm = common_vendor.reactive({
      phone: authStore.phone || ""
    });
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
        phoneForm.phone = authStore.phone || "";
      }
    });
    const handleWechatLogin = () => {
      common_vendor.index.showLoading({ title: "登录中..." });
      common_vendor.index.login({
        provider: "weixin",
        success: async (res) => {
          try {
            const apiRes = await api_index.userApi.wechatLogin(res.code, authStore.clientId);
            const data = apiRes.data;
            authStore.setLogin(data.tokenValue, {
              userId: data.loginId,
              nickname: data.nickname || "微信用户",
              avatar: data.avatar || "/static/logo.png",
              phone: ""
            });
            phoneForm.phone = "";
            common_vendor.index.hideLoading();
            common_vendor.index.showToast({ title: "登录成功", icon: "success" });
          } catch (e) {
            common_vendor.index.hideLoading();
            const errMsg = e.msg || e.message || "登录失败";
            common_vendor.index.showToast({ title: errMsg, icon: "none" });
          }
        },
        fail: (err) => {
          common_vendor.index.hideLoading();
          common_vendor.index.showToast({ title: "微信授权失败，请在微信中打开", icon: "none" });
        }
      });
    };
    const savePhone = async () => {
      const phone = phoneForm.phone.trim();
      if (!/^1[3-9]\d{9}$/.test(phone)) {
        common_vendor.index.showToast({ title: "请输入正确的手机号", icon: "none" });
        return;
      }
      try {
        await api_index.userApi.updateWechatInfo({ phone });
        authStore.updatePhone(phone);
        common_vendor.index.showToast({ title: "手机号保存成功", icon: "success" });
      } catch (e) {
        common_vendor.index.showToast({ title: "保存失败", icon: "none" });
      }
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
        e: common_vendor.o(handleWechatLogin, "b2")
      } : {}, {
        f: common_vendor.unref(authStore).isLogin
      }, common_vendor.unref(authStore).isLogin ? {
        g: phoneForm.phone,
        h: common_vendor.o(($event) => phoneForm.phone = $event.detail.value, "86"),
        i: common_vendor.o(savePhone, "b8")
      } : {}, {
        j: common_vendor.t(orderStats.value.pending || 0),
        k: common_vendor.t(orderStats.value.waiting || 0),
        l: common_vendor.t(orderStats.value.making || 0),
        m: common_vendor.t(orderStats.value.completed || 0),
        n: common_vendor.o(goOrders, "2c"),
        o: common_vendor.o(goOrders, "93"),
        p: common_vendor.o(goIndex, "6c"),
        q: common_vendor.o(goCart, "7e"),
        r: common_vendor.unref(authStore).isLogin
      }, common_vendor.unref(authStore).isLogin ? {
        s: common_vendor.o(handleLogout, "fc")
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-79e6a490"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/user/index.js.map
