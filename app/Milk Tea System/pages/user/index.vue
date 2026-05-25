<template>
	<view class="container">
		<view class="user-header">
			<image :src="authStore.avatar" mode="aspectFill" class="avatar" />
			<view class="user-info">
				<text class="nickname">{{ authStore.nickname }}</text>
				<text class="sub-text" v-if="authStore.isLogin">欢迎回来</text>
				<text class="sub-text" v-else>登录后享受更多服务</text>
			</view>
			<button v-if="!authStore.isLogin" class="login-btn" @click="handleWechatLogin">
				微信一键登录
			</button>
		</view>

		<view v-if="authStore.isLogin" class="section">
			<view class="section-title">个人信息</view>
			<view class="info-row">
				<text class="info-label">手机号</text>
				<input class="info-input" v-model="phoneForm.phone" type="number" maxlength="11" placeholder="请输入手机号" />
				<button class="save-btn" size="mini" @click="savePhone">保存</button>
			</view>
		</view>

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
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import { userApi } from '@/api/index'

const authStore = useAuthStore()
const orderStats = ref({})
const phoneForm = reactive({
	phone: authStore.phone || ''
})

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
		phoneForm.phone = authStore.phone || ''
	}
})

const handleWechatLogin = () => {
	uni.showLoading({ title: '登录中...' })
	uni.login({
		provider: 'weixin',
		success: async (res) => {
			try {
				const apiRes = await userApi.wechatLogin(res.code, authStore.clientId)
				const data = apiRes.data
				authStore.setLogin(data.tokenValue, {
					userId: data.loginId,
					nickname: data.nickname || '微信用户',
					avatar: data.avatar || '/static/logo.png',
					phone: ''
				})
				phoneForm.phone = ''
				uni.hideLoading()
				uni.showToast({ title: '登录成功', icon: 'success' })
			} catch (e) {
				uni.hideLoading()
				const errMsg = e.msg || e.message || '登录失败'
				uni.showToast({ title: errMsg, icon: 'none' })
			}
		},
		fail: (err) => {
			uni.hideLoading()
			uni.showToast({ title: '微信授权失败，请在微信中打开', icon: 'none' })
		}
	})
}

const savePhone = async () => {
	const phone = phoneForm.phone.trim()
	if (!/^1[3-9]\d{9}$/.test(phone)) {
		uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
		return
	}

	try {
		await userApi.updateWechatInfo({ phone })
		authStore.updatePhone(phone)
		uni.showToast({ title: '手机号保存成功', icon: 'success' })
	} catch (e) {
		uni.showToast({ title: '保存失败', icon: 'none' })
	}
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

.section { background: #fff; margin: 12px; border-radius: 10px; padding: 16px; }
.section-title { font-size: 15px; font-weight: 700; margin-bottom: 12px; }
.info-row { display: flex; align-items: center; }
.info-label { width: 60px; font-size: 14px; color: #666; flex-shrink: 0; }
.info-input { flex: 1; font-size: 14px; text-align: right; border: 1px solid #eee; border-radius: 6px; padding: 8px 12px; margin: 0 8px; }
.save-btn { flex-shrink: 0; background: #d4a574; color: #fff; border: none; border-radius: 14px; font-size: 12px; }

.stats-card {
	display: flex; background: #fff; margin: 12px; padding: 16px 0;
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