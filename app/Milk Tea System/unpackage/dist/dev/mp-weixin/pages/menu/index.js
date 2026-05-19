"use strict";
const common_vendor = require("../../common/vendor.js");
const api_index = require("../../api/index.js");
const store_cart = require("../../store/cart.js");
const defaultImg = "/static/logo.png";
const _sfc_main = {
  __name: "index",
  setup(__props) {
    const cartStore = store_cart.useCartStore();
    const categoryList = common_vendor.ref([]);
    const products = common_vendor.ref([]);
    const activeCategoryId = common_vendor.ref(null);
    const loading = common_vendor.ref(false);
    common_vendor.onMounted(async () => {
      try {
        const [catRes, prodRes] = await Promise.all([
          api_index.categoryApi.list(),
          api_index.productApi.listAll()
        ]);
        categoryList.value = catRes.data || [];
        products.value = (prodRes.data || []).filter((p) => p.status === 1);
        const selectedCategoryId = common_vendor.index.getStorageSync("selectedCategoryId");
        if (selectedCategoryId && categoryList.value.find((c) => c.categoryId === selectedCategoryId)) {
          activeCategoryId.value = selectedCategoryId;
        } else if (categoryList.value.length > 0) {
          activeCategoryId.value = categoryList.value[0].categoryId;
        }
      } catch (e) {
      }
    });
    const filteredProducts = common_vendor.computed(() => {
      if (!activeCategoryId.value)
        return products.value;
      return products.value.filter((p) => p.categoryId === activeCategoryId.value);
    });
    const loadMore = () => {
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
        a: common_vendor.f(categoryList.value, (cat, k0, i0) => {
          return {
            a: common_vendor.t(cat.categoryName),
            b: cat.categoryId,
            c: activeCategoryId.value === cat.categoryId ? 1 : "",
            d: common_vendor.o(($event) => activeCategoryId.value = cat.categoryId, cat.categoryId)
          };
        }),
        b: common_vendor.f(filteredProducts.value, (product, k0, i0) => {
          return {
            a: product.image || defaultImg,
            b: common_vendor.t(product.productName),
            c: common_vendor.t(product.description || "美味好喝，值得品尝"),
            d: common_vendor.t(product.price),
            e: common_vendor.o(($event) => addToCart(product), product.productId),
            f: product.productId,
            g: common_vendor.o(($event) => goDetail(product.productId), product.productId)
          };
        }),
        c: filteredProducts.value.length === 0
      }, filteredProducts.value.length === 0 ? {} : {}, {
        d: loading.value
      }, loading.value ? {} : {}, {
        e: common_vendor.o(loadMore, "04")
      });
    };
  }
};
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["__scopeId", "data-v-1f245baa"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/menu/index.js.map
