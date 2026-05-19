"use strict";
const common_vendor = require("../../common/vendor.js");
const api_index = require("../../api/index.js");
const store_cart = require("../../store/cart.js");
const defaultImg = "/static/logo.png";
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const cartStore = store_cart.useCartStore();
    const banners = ["/static/c1.png", "/static/c2.png", "/static/c3.png"];
    const categoryList = common_vendor.ref([]);
    const hotProducts = common_vendor.ref([]);
    const storeList = common_vendor.ref([]);
    const currentStoreId = common_vendor.ref(common_vendor.index.getStorageSync("storeId") || null);
    const currentStoreName = common_vendor.ref(common_vendor.index.getStorageSync("storeName") || "选择门店");
    const showStorePicker = common_vendor.ref(false);
    common_vendor.onMounted(async () => {
      try {
        const [catRes, prodRes, storeRes] = await Promise.all([
          api_index.categoryApi.list(),
          api_index.productApi.listAll(),
          api_index.storeApi.listAll()
        ]);
        categoryList.value = catRes.data || [];
        hotProducts.value = (prodRes.data || []).filter((p) => p.status === 1).slice(0, 6);
        storeList.value = storeRes.data || [];
        if (!currentStoreId.value && storeList.value.length > 0) {
          selectStore(storeList.value[0]);
        }
      } catch (e) {
      }
    });
    const openStorePicker = () => {
      showStorePicker.value = true;
    };
    const closeStorePicker = () => {
      showStorePicker.value = false;
    };
    const selectStore = (store) => {
      currentStoreId.value = store.storeId;
      currentStoreName.value = store.storeName;
      common_vendor.index.setStorageSync("storeId", store.storeId);
      common_vendor.index.setStorageSync("storeName", store.storeName);
      showStorePicker.value = false;
    };
    const goMenu = (categoryId) => {
      common_vendor.index.setStorageSync("selectedCategoryId", categoryId);
      common_vendor.index.switchTab({ url: "/pages/menu/index" });
    };
    const goDetail = (productId) => {
      common_vendor.index.navigateTo({ url: `/pages/product/detail?productId=${productId}` });
    };
    const addToCart = (product) => {
      cartStore.addItem(product);
      common_vendor.index.showToast({ title: "已加入购物车", icon: "success" });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.t(currentStoreName.value),
        b: common_vendor.o(openStorePicker, "56"),
        c: common_vendor.f(banners, (img, idx, i0) => {
          return {
            a: img,
            b: idx
          };
        }),
        d: common_vendor.f(categoryList.value, (cat, k0, i0) => {
          return {
            a: common_vendor.t(cat.categoryName.charAt(0)),
            b: common_vendor.t(cat.categoryName),
            c: cat.categoryId,
            d: common_vendor.o(($event) => goMenu(cat.categoryId), cat.categoryId)
          };
        }),
        e: common_vendor.f(hotProducts.value, (product, k0, i0) => {
          return {
            a: product.image || defaultImg,
            b: common_vendor.t(product.productName),
            c: common_vendor.t(product.price),
            d: common_vendor.o(($event) => addToCart(product), product.productId),
            e: product.productId,
            f: common_vendor.o(($event) => goDetail(product.productId), product.productId)
          };
        }),
        f: showStorePicker.value
      }, showStorePicker.value ? {
        g: common_vendor.f(storeList.value, (store, k0, i0) => {
          return common_vendor.e({
            a: common_vendor.t(store.storeName),
            b: store.status === 0
          }, store.status === 0 ? {} : {}, {
            c: store.storeId,
            d: currentStoreId.value === store.storeId ? 1 : "",
            e: common_vendor.o(($event) => selectStore(store), store.storeId)
          });
        }),
        h: common_vendor.o(closeStorePicker, "d5"),
        i: common_vendor.o(() => {
        }, "fb"),
        j: common_vendor.o(closeStorePicker, "14")
      } : {});
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-1cf27b2a"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
