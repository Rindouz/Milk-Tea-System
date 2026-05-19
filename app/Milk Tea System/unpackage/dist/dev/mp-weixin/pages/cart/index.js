"use strict";
const common_vendor = require("../../common/vendor.js");
const store_cart = require("../../store/cart.js");
const api_index = require("../../api/index.js");
const defaultImg = "/static/logo.png";
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const cartStore = store_cart.useCartStore();
    const goMenu = () => {
      common_vendor.index.switchTab({ url: "/pages/menu/index" });
    };
    const goCheckout = async () => {
      if (cartStore.items.length === 0) {
        common_vendor.index.showToast({ title: "购物车为空", icon: "none" });
        return;
      }
      const storeId = common_vendor.index.getStorageSync("storeId");
      if (!storeId) {
        common_vendor.index.showToast({ title: "请先在首页选择门店", icon: "none" });
        return;
      }
      try {
        const res = await api_index.storeApi.detail(storeId);
        if (res.data && res.data.status === 0) {
          common_vendor.index.showToast({ title: "该门店已休息，请选择其他门店", icon: "none" });
          return;
        }
      } catch (e) {
      }
      common_vendor.index.navigateTo({ url: "/pages/order/create" });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.unref(cartStore).items.length === 0
      }, common_vendor.unref(cartStore).items.length === 0 ? {
        b: common_vendor.o(goMenu, "ce")
      } : {
        c: common_vendor.f(common_vendor.unref(cartStore).items, (item, k0, i0) => {
          return {
            a: item.productImage || defaultImg,
            b: common_vendor.t(item.productName),
            c: common_vendor.t(item.price),
            d: common_vendor.o(($event) => common_vendor.unref(cartStore).updateQuantity(item.productId, item.quantity - 1), item.productId),
            e: common_vendor.t(item.quantity),
            f: common_vendor.o(($event) => common_vendor.unref(cartStore).updateQuantity(item.productId, item.quantity + 1), item.productId),
            g: common_vendor.o(($event) => common_vendor.unref(cartStore).removeItem(item.productId), item.productId),
            h: item.productId
          };
        }),
        d: common_vendor.t(common_vendor.unref(cartStore).totalPrice.toFixed(2)),
        e: common_vendor.t(common_vendor.unref(cartStore).totalCount),
        f: common_vendor.o(goCheckout, "b1")
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-8039fbf1"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/cart/index.js.map
