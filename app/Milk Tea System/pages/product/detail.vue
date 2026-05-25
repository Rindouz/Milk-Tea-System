<template>
	<view class="container">
		<!-- 商品图片 -->
		<image :src="product.image || defaultImg" mode="aspectFill" class="detail-img" />

		<!-- 商品信息 -->
		<view class="detail-info">
			<text class="product-name">{{ product.productName }}</text>
			<text class="product-desc">{{ product.description || '美味好喝，值得品尝' }}</text>
			<view class="price-row">
				<text class="product-price">¥{{ product.price }}</text>
				<text class="stock-text" v-if="stock !== null">库存：{{ stock }}</text>
			</view>
		</view>

		<!-- 数量选择 -->
		<view class="quantity-row">
			<text class="qty-label">数量</text>
			<view class="qty-control">
				<view class="qty-btn" @click="decreaseQty">
					<text>-</text>
				</view>
				<text class="qty-value">{{ quantity }}</text>
				<view class="qty-btn" @click="increaseQty">
					<text>+</text>
				</view>
			</view>
		</view>

		<!-- 加入购物车 -->
		<view class="bottom-bar">
			<button class="cart-btn" @click="addToCart">加入购物车</button>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { productApi, inventoryApi } from '@/api/index'
import { useCartStore } from '@/store/cart'

const cartStore = useCartStore()
const defaultImg = '/static/logo.png'

const product = ref({})
const stock = ref(null)
const quantity = ref(1)

onLoad(async (options) => {
	const id = options.productId
	if (id) {
		try {
			const [prodRes, stockRes] = await Promise.all([
				productApi.detail(id),
				inventoryApi.stock(id)
			])
			product.value = prodRes.data || {}
			stock.value = stockRes.data ?? 0
		} catch (e) {
			uni.showToast({ title: '加载失败', icon: 'none' })
		}
	}
})

const decreaseQty = () => { if (quantity.value > 1) quantity.value-- }
const increaseQty = () => { quantity.value++ }

const addToCart = () => {
	cartStore.addItem(product.value, quantity.value)
	uni.showToast({ title: '已加入购物车', icon: 'success', duration: 1500 })
}
</script>

<style scoped>
.container { padding-bottom: 80px; }

.detail-img { width: 100%; height: 320px; }

.detail-info { background: #fff; padding: 16px; margin: 10px; border-radius: 10px; }
.product-name { font-size: 18px; font-weight: 700; display: block; }
.product-desc { font-size: 13px; color: #999; margin-top: 6px; display: block; }
.price-row { display: flex; align-items: center; justify-content: space-between; margin-top: 10px; }
.product-price { font-size: 22px; font-weight: 700; color: #e74c3c; }
.stock-text { font-size: 13px; color: #999; }

.quantity-row {
	display: flex; align-items: center; justify-content: space-between;
	background: #fff; padding: 16px; margin: 10px; border-radius: 10px;
}
.qty-label { font-size: 15px; }
.qty-control { display: flex; align-items: center; gap: 16px; }
.qty-btn {
	width: 32px; height: 32px; border-radius: 50%; border: 1px solid #ddd;
	display: flex; align-items: center; justify-content: center; font-size: 18px; color: #333;
}
.qty-value { font-size: 17px; font-weight: 600; min-width: 32px; text-align: center; }

.bottom-bar { position: fixed; bottom: 0; left: 0; right: 0; padding: 12px 16px; background: #fff; box-shadow: 0 -1px 6px rgba(0,0,0,0.06); }
.cart-btn { width: 100%; height: 48px; background: #d4a574; color: #fff; border-radius: 24px; font-size: 16px; font-weight: 600; border: none; }
</style>
