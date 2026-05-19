"use strict";
const common_vendor = require("../../common/vendor.js");
const store_cart = require("../../store/cart.js");
const api_index = require("../../api/index.js");
const defaultImg = "/static/logo.png";
const _sfc_main = {
  __name: "create",
  setup(__props) {
    const cartStore = store_cart.useCartStore();
    const submitting = common_vendor.ref(false);
    const storeName = common_vendor.ref(common_vendor.index.getStorageSync("storeName") || "未知");
    const form = common_vendor.reactive({
      takeName: "",
      takePhone: "",
      remark: ""
    });
    const submitOrder = async () => {
      if (!form.takeName.trim()) {
        common_vendor.index.showToast({ title: "请输入取餐人姓名", icon: "none" });
        return;
      }
      if (!/^1[3-9]\d{9}$/.test(form.takePhone)) {
        common_vendor.index.showToast({ title: "请输入正确的手机号", icon: "none" });
        return;
      }
      submitting.value = true;
      try {
        const orderItems = cartStore.items.map((item) => ({
          productId: item.productId,
          productName: item.productName,
          productImage: item.productImage || "",
          price: item.price,
          quantity: item.quantity
        }));
        await api_index.orderApi.create({
          userId: common_vendor.index.getStorageSync("userId") || 1,
          storeId: common_vendor.index.getStorageSync("storeId"),
          takeName: form.takeName,
          takePhone: form.takePhone,
          remark: form.remark,
          orderItems
        });
        cartStore.clearCart();
        common_vendor.index.showToast({ title: "下单成功", icon: "success" });
        setTimeout(() => {
          common_vendor.index.switchTab({ url: "/pages/order/list" });
        }, 1200);
      } catch (e) {
        common_vendor.index.showToast({ title: "下单失败，请重试", icon: "none" });
      } finally {
        submitting.value = false;
      }
    };
    return (_ctx, _cache) => {
      return {
        a: common_vendor.f(common_vendor.unref(cartStore).items, (item, k0, i0) => {
          return {
            a: item.productImage || defaultImg,
            b: common_vendor.t(item.productName),
            c: common_vendor.t(item.price),
            d: common_vendor.t(item.quantity),
            e: common_vendor.t((item.price * item.quantity).toFixed(2)),
            f: item.productId
          };
        }),
        b: form.takeName,
        c: common_vendor.o(($event) => form.takeName = $event.detail.value, "ae"),
        d: form.takePhone,
        e: common_vendor.o(($event) => form.takePhone = $event.detail.value, "6b"),
        f: storeName.value,
        g: form.remark,
        h: common_vendor.o(($event) => form.remark = $event.detail.value, "10"),
        i: common_vendor.t(common_vendor.unref(cartStore).totalPrice.toFixed(2)),
        j: submitting.value,
        k: common_vendor.o(submitOrder, "60")
      };
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-8837ac90"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order/create.js.map
