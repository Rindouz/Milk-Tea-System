<template>
	<view class="container">
		<!-- 用户信息头部 -->
		<view class="user-header">
			<image :src="authStore.avatar" mode="aspectFill" class="avatar" />
			<view class="user-info">
				<text class="nickname">{{ authStore.nickname }}</text>
				<text class="sub-text" v-if="authStore.isLogin">欢迎回来</text>
			</view>
			<button v-if="!authStore.isLogin" class="login-btn" @click="handleWechatLogin">
				微信登录
			</button>
		</view>

		<!-- 订单统计 -->
		<view class="stats-card" @click="goOrders">
			<view class="stat-item">
				<text class="stat-value">{{ orderStats.pending || 0 }}</text>
				<text class="stat-label">待支付</text>
			</view>
			<view class="stat-item">
				<text class="stat-value">{{ orderStats.waiting || 0 }}</text>
				<text class="stat-label">待取餐</text>
			</view>
			<view class="stat-item">
				<text class="stat-value">{{ orderStats.making || 0 }}</text>
				<text class="stat-label">制作中</text>
			</view>
			<view class="stat-item">
				<text class="stat-value">{{ orderStats.completed || 0 }}</text>
				<text class="stat-label">已完成</text>
			</view>
		</view>

		<!-- 菜单列表 -->
		<view class="menu-card">
			<view class="menu-item" @click="goOrders">
				<text class="menu-icon">📋</text>
				<text class="menu-label">我的订单</text>
				<text class="menu-arrow">›</text>
			</view>
			<view class="menu-item" @click="goIndex">
				<text class="menu-icon">🏠</text>
				<text class="menu-label">切换门店</text>
				<text class="menu-arrow">›</text>
			</view>
			<view class="menu-item" @click="goCart">
				<text class="menu-icon">🛒</text>
				<text class="menu-label">购物车</text>
				<text class="menu-arrow">›</text>
			</view>
			<view v-if="authStore.isLogin" class="menu-item" @click="handleLogout">
				<text class="menu-icon">🚪</text>
				<text class="menu-label" style="color: #e74c3c">退出登录</text>
				<text class="menu-arrow">›</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import { userApi } from '@/api/index'

const authStore = useAuthStore()
const orderStats = ref({})

const goOrders = () => { uni.switchTab({ url: '/pages/order/list' }) }
const goIndex = () => { uni.switchTab({ url: '/pages/index/index' }) }
const goCart = () => { uni.switchTab({ url: '/pages/cart/index' }) }

onMounted(async () => {
	if (authStore.isLogin) {
		try {
			const res = await userApi.getUserOrders(1, 100)
			const records = res.data?.records || []
			orderStats.value = {
				pending: records.filter(o => o.orderStatus === 0).length,
				waiting: records.filter(o => o.orderStatus === 1).length,
				making: records.filter(o => o.orderStatus === 3).length,
				completed: records.filter(o => o.orderStatus === 2).length
			}
		} catch (e) {
			// 静默处理
		}
	}
})

const handleWechatLogin = () => {
	uni.login({
		provider: 'weixin',
		success: async (res) => {
			try {
				const apiRes = await userApi.wechatLogin(res.code)
				const tokenInfo = apiRes.data
				authStore.setLogin(tokenInfo.tokenValue, {
					userId: tokenInfo.loginId,
					nickname: '微信用户',
					avatar: '/static/logo.png'
				})
				uni.showToast({ title: '登录成功', icon: 'success' })
			} catch (e) {
				uni.showToast({ title: '登录失败', icon: 'none' })
			}
		},
		fail: () => {
			uni.showToast({ title: '微信授权失败', icon: 'none' })
		}
	})
}

const handleLogout = () => {
	uni.showModal({
		title: '提示',
		content: '确定要退出登录吗？',
		success: (res) => {
			if (res.confirm) {
				authStore.logout()
				orderStats.value = {}
				uni.showToast({ title: '已退出登录', icon: 'success' })
			}
		}
	})
}
</script>

<style scoped>
.container { padding-bottom: 40px; }

.user-header {
	display: flex; align-items: center; padding: 30px 20px;
	background: linear-gradient(135deg, #fce4d6, #f8c291);
}
.avatar { width: 64px; height: 64px; border-radius: 50%; border: 3px solid #fff; }
.user-info { flex: 1; margin-left: 16px; }
.nickname { font-size: 18px; font-weight: 700; color: #333; display: block; }
.sub-text { font-size: 13px; color: #666; margin-top: 4px; display: block; }
.login-btn { background: #fff; color: #d4a574; border-radius: 20px; font-size: 13px; height: 34px; line-height: 34px; padding: 0 16px; border: none; }

.stats-card {
	display: flex; background: #fff; margin: -10px 12px 12px; padding: 16px 0;
	border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.stat-item { flex: 1; text-align: center; }
.stat-value { font-size: 20px; font-weight: 700; color: #333; display: block; }
.stat-label { font-size: 12px; color: #999; margin-top: 4px; display: block; }

.menu-card { background: #fff; margin: 12px; border-radius: 10px; overflow: hidden; }
.menu-item {
	display: flex; align-items: center; padding: 16px; border-bottom: 1px solid #f5f5f5;
}
.menu-icon { font-size: 20px; width: 32px; }
.menu-label { flex: 1; font-size: 15px; }
.menu-arrow { font-size: 18px; color: #ccc; }
</style>
