"use strict";
const common_vendor = require("../../common/vendor.js");
const api_index = require("../../api/index.js");
const store_cart = require("../../store/cart.js");
const defaultImg = "/static/logo.png";
const _sfc_main = {
  __name: "detail",
  setup(__props) {
    const cartStore = store_cart.useCartStore();
    const product = common_vendor.ref({});
    const stock = common_vendor.ref(null);
    const quantity = common_vendor.ref(1);
    common_vendor.onLoad(async (options) => {
      const id = options.productId;
      if (id) {
        try {
          const [prodRes, stockRes] = await Promise.all([
            api_index.productApi.detail(id),
            api_index.inventoryApi.stock(id)
          ]);
          product.value = prodRes.data || {};
          stock.value = stockRes.data ?? 0;
        } catch (e) {
          common_vendor.index.showToast({ title: "加载失败", icon: "none" });
        }
      }
    });
    const decreaseQty = () => {
      if (quantity.value > 1)
        quantity.value--;
    };
    const increaseQty = () => {
      quantity.value++;
    };
    const addToCart = () => {
      cartStore.addItem(product.value, quantity.value);
      common_vendor.index.showToast({ title: "已加入购物车", icon: "success", duration: 1500 });
      setTimeout(() => {
        common_vendor.index.switchTab({ url: "/pages/cart/index" });
      }, 1200);
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: product.value.image || defaultImg,
        b: common_vendor.t(product.value.productName),
        c: common_vendor.t(product.value.description || "美味好喝，值得品尝"),
        d: common_vendor.t(product.value.price),
        e: stock.value !== null
      }, stock.value !== null ? {
        f: common_vendor.t(stock.value)
      } : {}, {
        g: common_vendor.o(decreaseQty, "dc"),
        h: common_vendor.t(quantity.value),
        i: common_vendor.o(increaseQty, "7f"),
        j: common_vendor.o(addToCart, "9d")
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-acf502d9"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/product/detail.js.map
