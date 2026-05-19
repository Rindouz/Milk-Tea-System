<template>
	<view class="container">
		<!-- 分类侧边栏 + 商品列表 -->
		<view class="menu-layout">
			<!-- 左侧分类 -->
			<scroll-view scroll-y class="category-sidebar">
				<view class="cat-item" v-for="cat in categoryList" :key="cat.categoryId"
					:class="{ active: activeCategoryId === cat.categoryId }"
					@click="activeCategoryId = cat.categoryId">
					<text>{{ cat.categoryName }}</text>
				</view>
			</scroll-view>

			<!-- 右侧商品 -->
			<scroll-view scroll-y class="product-list" @scrolltolower="loadMore">
				<view class="product-item" v-for="product in filteredProducts" :key="product.productId"
					@click="goDetail(product.productId)">
					<image :src="product.image || defaultImg" mode="aspectFill" class="product-img" />
					<view class="product-content">
						<text class="product-name">{{ product.productName }}</text>
						<text class="product-desc">{{ product.description || '美味好喝，值得品尝' }}</text>
						<view class="product-bottom">
							<text class="product-price">¥{{ product.price }}</text>
							<view class="add-btn" @click.stop="addToCart(product)">
								<text>+</text>
							</view>
						</view>
					</view>
				</view>
				<view v-if="filteredProducts.length === 0" class="empty">暂无商品</view>
				<view v-if="loading" class="loading">加载中...</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { categoryApi, productApi } from '@/api/index'
import { useCartStore } from '@/store/cart'

const cartStore = useCartStore()
const defaultImg = '/static/logo.png'

const categoryList = ref([])
const products = ref([])
const activeCategoryId = ref(null)
const loading = ref(false)

onMounted(async () => {
	try {
		const [catRes, prodRes] = await Promise.all([
			categoryApi.list(),
			productApi.listAll()
		])
		categoryList.value = catRes.data || []
		products.value = (prodRes.data || []).filter(p => p.status === 1)

		// 从首页传入的分类ID
		const selectedCategoryId = uni.getStorageSync('selectedCategoryId')
		if (selectedCategoryId && categoryList.value.find(c => c.categoryId === selectedCategoryId)) {
			activeCategoryId.value = selectedCategoryId
		} else if (categoryList.value.length > 0) {
			activeCategoryId.value = categoryList.value[0].categoryId
		}
	} catch (e) {
		// 静默处理
	}
})

const filteredProducts = computed(() => {
	if (!activeCategoryId.value) return products.value
	return products.value.filter(p => p.categoryId === activeCategoryId.value)
})

const loadMore = () => {
	// 分页加载预留
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
.container { height: 100vh; display: flex; flex-direction: column; }

.menu-layout { display: flex; flex: 1; overflow: hidden; }

.category-sidebar {
	width: 90px; background: #f8f8f8; height: 100%;
}
.cat-item {
	padding: 16px 10px; text-align: center; font-size: 13px; color: #666;
	border-left: 3px solid transparent;
}
.cat-item.active {
	background: #fff; color: #d4a574; font-weight: 600; border-left-color: #d4a574;
}

.product-list { flex: 1; padding: 10px; background: #fff; }
.product-item {
	display: flex; padding: 12px 0; border-bottom: 1px solid #f5f5f5;
}
.product-img { width: 80px; height: 80px; border-radius: 8px; flex-shrink: 0; }
.product-content { flex: 1; margin-left: 12px; display: flex; flex-direction: column; }
.product-name { font-size: 15px; font-weight: 600; }
.product-desc { font-size: 12px; color: #999; margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-bottom { display: flex; align-items: center; justify-content: space-between; margin-top: auto; }
.product-price { font-size: 16px; font-weight: 700; color: #e74c3c; }
.add-btn {
	width: 28px; height: 28px; border-radius: 50%; background: #d4a574;
	color: #fff; display: flex; align-items: center; justify-content: center; font-size: 18px; font-weight: 700;
}
.empty, .loading { text-align: center; padding: 40px; color: #999; font-size: 14px; }
</style>
