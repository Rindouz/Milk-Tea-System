<template>
	<view class="container">
		<!-- 订单商品 -->
		<view class="section">
			<view class="section-title">订单详情</view>
			<view class="order-item" v-for="item in cartStore.items" :key="item.productId">
				<image :src="item.productImage || defaultImg" mode="aspectFill" class="item-img" />
				<view class="item-info">
					<text class="item-name">{{ item.productName }}</text>
					<text class="item-price">¥{{ item.price }} x {{ item.quantity }}</text>
				</view>
				<text class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</text>
			</view>
		</view>

		<!-- 取餐信息 -->
		<view class="section">
			<view class="section-title">取餐信息</view>
			<view class="form-item">
				<text class="label">取餐人</text>
				<input class="input" v-model="form.takeName" placeholder="请输入姓名" />
			</view>
			<view class="form-item">
				<text class="label">手机号</text>
				<input class="input" v-model="form.takePhone" type="number" maxlength="11" placeholder="请输入手机号" />
			</view>
			<view class="form-item">
				<text class="label">取餐门店</text>
				<input class="input" :value="storeName" disabled />
			</view>
			<view class="form-item">
				<text class="label">备注</text>
				<input class="input" v-model="form.remark" placeholder="如有特殊要求请备注" />
			</view>
		</view>

		<!-- 合计 -->
		<view class="section">
			<view class="total-row">
				<text class="total-label">合计</text>
				<text class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</text>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-bar">
			<button class="submit-btn" :loading="submitting" @click="submitOrder">提交订单</button>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useCartStore } from '@/store/cart'
import { orderApi } from '@/api/index'

const cartStore = useCartStore()
const defaultImg = '/static/logo.png'
const submitting = ref(false)
const storeName = ref(uni.getStorageSync('storeName') || '未知')

const form = reactive({
	takeName: '',
	takePhone: '',
	remark: ''
})

const submitOrder = async () => {
	if (!form.takeName.trim()) {
		uni.showToast({ title: '请输入取餐人姓名', icon: 'none' })
		return
	}
	if (!/^1[3-9]\d{9}$/.test(form.takePhone)) {
		uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
		return
	}

	submitting.value = true
	try {
		const orderItems = cartStore.items.map(item => ({
			productId: item.productId,
			productName: item.productName,
			productImage: item.productImage || '',
			price: item.price,
			quantity: item.quantity
		}))

		await orderApi.create({
			userId: uni.getStorageSync('userId') || 1,
			storeId: uni.getStorageSync('storeId'),
			takeName: form.takeName,
			takePhone: form.takePhone,
			remark: form.remark,
			orderItems
		})

		cartStore.clearCart()
		uni.showToast({ title: '下单成功', icon: 'success' })
		setTimeout(() => {
			uni.switchTab({ url: '/pages/order/list' })
		}, 1200)
	} catch (e) {
		uni.showToast({ title: '下单失败，请重试', icon: 'none' })
	} finally {
		submitting.value = false
	}
}
</script>

<style scoped>
.container { padding-bottom: 40px; }

.section { background: #fff; margin: 10px; border-radius: 10px; padding: 16px; }
.section-title { font-size: 16px; font-weight: 700; margin-bottom: 12px; }

.order-item { display: flex; align-items: center; padding: 10px 0; border-bottom: 1px solid #f5f5f5; }
.item-img { width: 48px; height: 48px; border-radius: 6px; flex-shrink: 0; }
.item-info { flex: 1; margin-left: 12px; }
.item-name { font-size: 14px; font-weight: 500; display: block; }
.item-price { font-size: 12px; color: #999; margin-top: 2px; display: block; }
.item-subtotal { font-size: 14px; font-weight: 600; }

.form-item { display: flex; align-items: center; padding: 12px 0; border-bottom: 1px solid #f5f5f5; }
.label { width: 72px; font-size: 14px; color: #666; flex-shrink: 0; }
.input { flex: 1; font-size: 14px; text-align: right; }

.total-row { display: flex; justify-content: space-between; align-items: center; }
.total-label { font-size: 16px; font-weight: 600; }
.total-price { font-size: 22px; font-weight: 700; color: #e74c3c; }

.submit-bar { padding: 16px; }
.submit-btn { width: 100%; height: 48px; background: #d4a574; color: #fff; border-radius: 24px; font-size: 16px; font-weight: 600; border: none; }
</style>
