import { defineStore } from 'pinia'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: uni.getStorageSync('cartItems') ? JSON.parse(uni.getStorageSync('cartItems')) : []
  }),

  getters: {
    totalCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    totalPrice: (state) => state.items.reduce((sum, item) => sum + (item.price * item.quantity), 0),
    selectedStoreId: (state) => state.items.length > 0 ? state.items[0].storeId : null
  },

  actions: {
    _save() {
      uni.setStorageSync('cartItems', JSON.stringify(this.items))
      try {
        uni.setTabBarBadge({
          index: 2,
          text: this.totalCount > 99 ? '99+' : String(this.totalCount)
        })
      } catch (e) {
        // setTabBarBadge only works on TabBar pages; ignore on non-TabBar pages
      }
    },

    addItem(product, quantity = 1) {
      const existing = this.items.find(item => item.productId === product.productId)
      if (existing) {
        existing.quantity += quantity
      } else {
        this.items.push({
          productId: product.productId,
          productName: product.productName,
          productImage: product.productImage || product.image || '',
          price: product.price,
          quantity,
          storeId: product.storeId
        })
      }
      this._save()
    },

    removeItem(productId) {
      this.items = this.items.filter(item => item.productId !== productId)
      this._save()
    },

    updateQuantity(productId, quantity) {
      const item = this.items.find(item => item.productId === productId)
      if (item) {
        item.quantity = Math.max(1, quantity)
        this._save()
      }
    },

    clearCart() {
      this.items = []
      this._save()
    }
  }
})
