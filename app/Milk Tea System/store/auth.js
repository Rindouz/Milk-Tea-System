import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: uni.getStorageSync('satoken') || '',
    userInfo: uni.getStorageSync('userInfo') ? JSON.parse(uni.getStorageSync('userInfo')) : null,
    isLogin: !!uni.getStorageSync('satoken')
  }),

  getters: {
    userId: (state) => state.userInfo?.userId || null,
    nickname: (state) => state.userInfo?.nickname || '未登录',
    avatar: (state) => state.userInfo?.avatar || '/static/logo.png'
  },

  actions: {
    setLogin(token, userInfo) {
      this.token = token
      this.userInfo = userInfo
      this.isLogin = true
      uni.setStorageSync('satoken', token)
      uni.setStorageSync('userInfo', JSON.stringify(userInfo))
    },

    setUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      uni.setStorageSync('userInfo', JSON.stringify(this.userInfo))
    },

    logout() {
      this.token = ''
      this.userInfo = null
      this.isLogin = false
      uni.removeStorageSync('satoken')
      uni.removeStorageSync('userInfo')
    }
  }
})
