"use strict";
const common_vendor = require("../common/vendor.js");
const useCartStore = common_vendor.defineStore("cart", {
  state: () => ({
    items: common_vendor.index.getStorageSync("cartItems") ? JSON.parse(common_vendor.index.getStorageSync("cartItems")) : []
  }),
  getters: {
    totalCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    totalPrice: (state) => state.items.reduce((sum, item) => sum + item.price * item.quantity, 0),
    selectedStoreId: (state) => state.items.length > 0 ? state.items[0].storeId : null
  },
  actions: {
    _save() {
      common_vendor.index.setStorageSync("cartItems", JSON.stringify(this.items));
      try {
        common_vendor.index.setTabBarBadge({
          index: 2,
          text: this.totalCount > 99 ? "99+" : String(this.totalCount)
        });
      } catch (e) {
      }
    },
    addItem(product, quantity = 1) {
      const existing = this.items.find((item) => item.productId === product.productId);
      if (existing) {
        existing.quantity += quantity;
      } else {
        this.items.push({
          productId: product.productId,
          productName: product.productName,
          productImage: product.productImage || product.image || "",
          price: product.price,
          quantity,
          storeId: product.storeId
        });
      }
      this._save();
    },
    removeItem(productId) {
      this.items = this.items.filter((item) => item.productId !== productId);
      this._save();
    },
    updateQuantity(productId, quantity) {
      const item = this.items.find((item2) => item2.productId === productId);
      if (item) {
        item.quantity = Math.max(1, quantity);
        this._save();
      }
    },
    clearCart() {
      this.items = [];
      this._save();
    }
  }
});
exports.useCartStore = useCartStore;
//# sourceMappingURL=../../.sourcemap/mp-weixin/store/cart.js.map
