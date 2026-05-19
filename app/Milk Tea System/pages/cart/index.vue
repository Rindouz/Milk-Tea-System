<template>
	<view class="container">
		<!-- 空购物车 -->
		<view v-if="cartStore.items.length === 0" class="empty-cart">
			<text class="empty-icon">🛒</text>
			<text class="empty-text">购物车是空的</text>
			<button class="go-menu-btn" @click="goMenu">去逛逛</button>
		</view>

		<!-- 购物车列表 -->
		<template v-else>
			<scroll-view scroll-y class="cart-list">
				<view class="cart-item" v-for="item in cartStore.items" :key="item.productId">
					<image :src="item.productImage || defaultImg" mode="aspectFill" class="item-img" />
					<view class="item-content">
						<text class="item-name">{{ item.productName }}</text>
						<text class="item-price">¥{{ item.price }}</text>
					</view>
					<view class="item-qty">
						<view class="qty-btn" @click="cartStore.updateQuantity(item.productId, item.quantity - 1)">
							<text>-</text>
						</view>
						<text class="qty-value">{{ item.quantity }}</text>
						<view class="qty-btn" @click="cartStore.updateQuantity(item.productId, item.quantity + 1)">
							<text>+</text>
						</view>
					</view>
					<view class="item-delete" @click="cartStore.removeItem(item.productId)">
						<text>✕</text>
					</view>
				</view>
			</scroll-view>

			<!-- 底部结算栏 -->
			<view class="bottom-bar">
				<view class="total-row">
					<text class="total-label">合计：</text>
					<text class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</text>
				</view>
				<button class="checkout-btn" @click="goCheckout">去结算 ({{ cartStore.totalCount }})</button>
			</view>
		</template>
	</view>
</template>

<script setup>
import { useCartStore } from '@/store/cart'
import { storeApi } from '@/api/index'

const cartStore = useCartStore()
const defaultImg = '/static/logo.png'

const goMenu = () => {
	uni.switchTab({ url: '/pages/menu/index' })
}

const goCheckout = async () => {
	if (cartStore.items.length === 0) {
		uni.showToast({ title: '购物车为空', icon: 'none' })
		return
	}
	const storeId = uni.getStorageSync('storeId')
	if (!storeId) {
		uni.showToast({ title: '请先在首页选择门店', icon: 'none' })
		return
	}
	try {
		const res = await storeApi.detail(storeId)
		if (res.data && res.data.status === 0) {
			uni.showToast({ title: '该门店已休息，请选择其他门店', icon: 'none' })
			return
		}
	} catch (e) {
		// 继续
	}
	uni.navigateTo({ url: '/pages/order/create' })
}
</script>

<style scoped>
.container { padding-bottom: 80px; min-height: 100vh; }

.empty-cart { display: flex; flex-direction: column; align-items: center; padding-top: 120px; }
.empty-icon { font-size: 64px; }
.empty-text { font-size: 15px; color: #999; margin-top: 12px; }
.go-menu-btn { margin-top: 20px; background: #d4a574; color: #fff; border-radius: 24px; font-size: 14px; border: none; }

.cart-list { padding: 10px; }
.cart-item {
	display: flex; align-items: center; background: #fff; padding: 12px;
	border-radius: 10px; margin-bottom: 10px; box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.item-img { width: 64px; height: 64px; border-radius: 8px; flex-shrink: 0; }
.item-content { flex: 1; margin-left: 12px; }
.item-name { font-size: 14px; font-weight: 600; display: block; }
.item-price { font-size: 15px; font-weight: 700; color: #e74c3c; margin-top: 4px; display: block; }
.item-qty { display: flex; align-items: center; gap: 8px; }
.qty-btn {
	width: 24px; height: 24px; border-radius: 50%; border: 1px solid #ddd;
	display: flex; align-items: center; justify-content: center; font-size: 14px; color: #333;
}
.qty-value { font-size: 14px; font-weight: 600; min-width: 24px; text-align: center; }
.item-delete { padding: 8px; color: #ccc; font-size: 14px; }

.bottom-bar {
	position: fixed; bottom: 0; left: 0; right: 0; padding: 12px 16px;
	background: #fff; display: flex; align-items: center; justify-content: space-between;
	box-shadow: 0 -1px 6px rgba(0,0,0,0.06);
}
.total-label { font-size: 14px; color: #333; }
.total-price { font-size: 20px; font-weight: 700; color: #e74c3c; }
.checkout-btn { background: #d4a574; color: #fff; border-radius: 24px; font-size: 15px; font-weight: 600; padding: 0 24px; height: 42px; line-height: 42px; border: none; }
</style>
