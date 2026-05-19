<template>
	<view class="container">
		<!-- 门店选择 -->
		<view class="store-bar" @click="openStorePicker">
			<text class="store-icon">🏠</text>
			<text class="store-name">{{ currentStoreName }}</text>
			<text class="arrow">▼</text>
		</view>

		<!-- 轮播图 -->
		<swiper class="banner" indicator-dots autoplay circular>
			<swiper-item v-for="(img, idx) in banners" :key="idx">
				<image :src="img" mode="aspectFill" class="banner-img" />
			</swiper-item>
		</swiper>

		<!-- 分类导航 -->
		<view class="section">
			<view class="section-title">商品分类</view>
			<scroll-view scroll-x class="category-scroll">
				<view class="category-item" v-for="cat in categoryList" :key="cat.categoryId"
					@click="goMenu(cat.categoryId)">
					<view class="category-icon">{{ cat.categoryName.charAt(0) }}</view>
					<text class="category-name">{{ cat.categoryName }}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 推荐商品 -->
		<view class="section">
			<view class="section-title">🔥 热销推荐</view>
			<view class="product-grid">
				<view class="product-card" v-for="product in hotProducts" :key="product.productId"
					@click="goDetail(product.productId)">
					<image :src="product.image || defaultImg" mode="aspectFill" class="product-img" />
					<view class="product-info">
						<text class="product-name">{{ product.productName }}</text>
						<view class="product-bottom">
							<text class="product-price">¥{{ product.price }}</text>
							<text class="add-btn" @click.stop="addToCart(product)">+</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 门店选择弹窗 -->
		<view class="picker-mask" v-if="showStorePicker" @click="closeStorePicker">
			<view class="picker-popup" @click.stop>
				<view class="picker-title">选择门店</view>
				<scroll-view scroll-y class="picker-list">
					<view class="picker-item" v-for="store in storeList" :key="store.storeId"
						:class="{ active: currentStoreId === store.storeId }"
						@click="selectStore(store)">
						<text>{{ store.storeName }}</text>
						<text v-if="store.status === 0" class="store-closed">休息中</text>
					</view>
				</scroll-view>
				<button class="picker-close" @click="closeStorePicker">关闭</button>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { categoryApi, productApi, storeApi } from '@/api/index'
import { useCartStore } from '@/store/cart'

const cartStore = useCartStore()
const defaultImg = '/static/logo.png'
const banners = ['/static/c1.png', '/static/c2.png', '/static/c3.png']

const categoryList = ref([])
const hotProducts = ref([])
const storeList = ref([])
const currentStoreId = ref(uni.getStorageSync('storeId') || null)
const currentStoreName = ref(uni.getStorageSync('storeName') || '选择门店')
const showStorePicker = ref(false)

onMounted(async () => {
	try {
		const [catRes, prodRes, storeRes] = await Promise.all([
			categoryApi.list(),
			productApi.listAll(),
			storeApi.listAll()
		])
		categoryList.value = catRes.data || []
		hotProducts.value = (prodRes.data || []).filter(p => p.status === 1).slice(0, 6)
		storeList.value = storeRes.data || []
		if (!currentStoreId.value && storeList.value.length > 0) {
			selectStore(storeList.value[0])
		}
	} catch (e) {
		// 静默处理
	}
})

const openStorePicker = () => { showStorePicker.value = true }
const closeStorePicker = () => { showStorePicker.value = false }

const selectStore = (store) => {
	currentStoreId.value = store.storeId
	currentStoreName.value = store.storeName
	uni.setStorageSync('storeId', store.storeId)
	uni.setStorageSync('storeName', store.storeName)
	showStorePicker.value = false
}

const goMenu = (categoryId) => {
	uni.setStorageSync('selectedCategoryId', categoryId)
	uni.switchTab({ url: '/pages/menu/index' })
}

const goDetail = (productId) => {
	uni.navigateTo({ url: `/pages/product/detail?productId=${productId}` })
}

const addToCart = (product) => {
	cartStore.addItem(product)
	uni.showToast({ title: '已加入购物车', icon: 'success' })
}
</script>

<style scoped>
.container { padding-bottom: 20px; }

.store-bar {
	display: flex; align-items: center; padding: 12px 16px;
	background: #fff; margin: 10px; border-radius: 10px; box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.store-icon { font-size: 20px; margin-right: 8px; }
.store-name { flex: 1; font-size: 15px; font-weight: 600; }
.arrow { font-size: 12px; color: #999; }

.banner { height: 180px; margin: 0 10px; border-radius: 10px; overflow: hidden; }
.banner-img { width: 100%; height: 100%; }

.section { margin: 15px 10px; }
.section-title { font-size: 17px; font-weight: 700; margin-bottom: 10px; }

.category-scroll { white-space: nowrap; }
.category-item {
	display: inline-flex; flex-direction: column; align-items: center;
	width: 80px; padding: 10px 0;
}
.category-icon {
	width: 48px; height: 48px; border-radius: 50%;
	background: linear-gradient(135deg, #fce4d6, #f8c291); color: #d4a574;
	display: flex; align-items: center; justify-content: center;
	font-size: 18px; font-weight: 700; margin-bottom: 6px;
}
.category-name { font-size: 12px; color: #333; }

.product-grid { display: flex; flex-wrap: wrap; gap: 10px; }
.product-card {
	width: calc(50% - 5px); background: #fff; border-radius: 10px;
	overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.product-img { width: 100%; height: 160px; }
.product-info { padding: 10px; }
.product-name { font-size: 14px; font-weight: 500; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-bottom { display: flex; align-items: center; justify-content: space-between; margin-top: 6px; }
.product-price { font-size: 16px; font-weight: 700; color: #e74c3c; }
.add-btn {
	width: 26px; height: 26px; border-radius: 50%; background: #d4a574;
	color: #fff; text-align: center; line-height: 26px; font-size: 18px; font-weight: 700;
}

.picker-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 999; display: flex; align-items: flex-end; }
.picker-popup { width: 100%; background: #fff; border-radius: 20px 20px 0 0; padding: 20px 16px; max-height: 60vh; }
.picker-title { font-size: 17px; font-weight: 700; text-align: center; margin-bottom: 12px; }
.picker-list { max-height: 40vh; }
.picker-item { padding: 14px 12px; border-bottom: 1px solid #f0f0f0; display: flex; justify-content: space-between; }
.picker-item.active { color: #d4a574; font-weight: 600; }
.store-closed { color: #e74c3c; font-size: 12px; }
.picker-close { margin-top: 12px; background: #f0f0f0; font-size: 14px; }
</style>
