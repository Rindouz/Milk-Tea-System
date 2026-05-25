import { defineStore } from 'pinia'

function getClientId() {
  let clientId = uni.getStorageSync('clientId')
  if (!clientId) {
    clientId = 'mp_' + Date.now().toString(36) + '_' + Math.random().toString(36).substring(2, 10)
    uni.setStorageSync('clientId', clientId)
  }
  return clientId
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: uni.getStorageSync('satoken') || '',
    userInfo: uni.getStorageSync('userInfo') ? JSON.parse(uni.getStorageSync('userInfo')) : null,
    isLogin: !!uni.getStorageSync('satoken'),
    clientId: getClientId()
  }),

  getters: {
    userId: (state) => state.userInfo?.userId || null,
    nickname: (state) => state.userInfo?.nickname || '未登录',
    avatar: (state) => state.userInfo?.avatar || '/static/logo.png',
    phone: (state) => state.userInfo?.phone || ''
  },

  actions: {
    setLogin(token, userInfo) {
      this.token = token
      this.userInfo = userInfo
      this.isLogin = true
      uni.setStorageSync('satoken', token)
      uni.setStorageSync('userInfo', JSON.stringify(userInfo))
      uni.setStorageSync('userId', userInfo.userId)
    },

    setUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      uni.setStorageSync('userInfo', JSON.stringify(this.userInfo))
    },

    updatePhone(phone) {
      if (this.userInfo) {
        this.userInfo.phone = phone
        uni.setStorageSync('userInfo', JSON.stringify(this.userInfo))
      }
    },

    logout() {
      this.token = ''
      this.userInfo = null
      this.isLogin = false
      uni.removeStorageSync('satoken')
      uni.removeStorageSync('userInfo')
      uni.removeStorageSync('userId')
    }
  }
})