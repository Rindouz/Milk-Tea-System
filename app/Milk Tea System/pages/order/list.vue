<template>
	<view class="container">
		<!-- 状态筛选 -->
		<scroll-view scroll-x class="status-tabs">
			<view class="tab-item" v-for="tab in statusTabs" :key="tab.value"
				:class="{ active: activeStatus === tab.value }"
				@click="selectStatus(tab.value)">
				<text>{{ tab.label }}</text>
			</view>
		</scroll-view>

		<!-- 订单列表 -->
		<scroll-view scroll-y class="order-list" refresher-enabled @refresherrefresh="onRefresh">
			<view v-if="orderList.length === 0 && !loading" class="empty">
				<text>暂无订单</text>
			</view>

			<view class="order-card" v-for="order in orderList" :key="order.orderNo"
				@click="showDetail(order)">
				<view class="order-header">
					<text class="order-no">订单号：{{ order.orderNo }}</text>
					<text class="order-status" :style="{ color: getStatusColor(order.orderStatus) }">
						{{ getStatusText(order.orderStatus) }}
					</text>
				</view>
				<view class="order-body">
					<text>总金额：¥{{ order.totalAmount }}</text>
					<text>创建时间：{{ order.createTime }}</text>
				</view>
				<view class="order-actions" v-if="order.orderStatus === 0">
					<button class="action-btn pay-btn" @click.stop="payOrder(order.orderNo)">去支付</button>
					<button class="action-btn cancel-btn" @click.stop="cancelOrder(order.orderNo)">取消订单</button>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api/index'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()

const statusTabs = [
	{ label: '全部', value: null },
	{ label: '待支付', value: 0 },
	{ label: '待取餐', value: 1 },
	{ label: '制作中', value: 3 },
	{ label: '已完成', value: 2 },
	{ label: '已取消', value: 4 }
]

const activeStatus = ref(null)
const orderList = ref([])
const loading = ref(false)

const getStatusText = (status) => {
	const map = { 0: '待支付', 1: '待取餐', 2: '已完成', 3: '制作中', 4: '已取消' }
	return map[status] || '未知'
}

const getStatusColor = (status) => {
	const map = { 0: '#f39c12', 1: '#3498db', 2: '#2ecc71', 3: '#9b59b6', 4: '#95a5a6' }
	return map[status] || '#333'
}

const selectStatus = (value) => {
	activeStatus.value = value
	loadOrders()
}

const loadOrders = async () => {
	loading.value = true
	try {
		const userId = authStore.userId
		if (!userId) {
			orderList.value = []
			return
		}
		const res = await orderApi.list(userId, activeStatus.value)
		orderList.value = res.data || []
	} catch (e) {
		orderList.value = []
	} finally {
		loading.value = false
	}
}

const onRefresh = async () => {
	await loadOrders()
}

const showDetail = async (order) => {
	try {
		const res = await orderApi.detail(order.orderNo)
		const detail = res.data
		if (detail && detail.orderItems) {
			const itemsText = detail.orderItems
				.map(i => `${i.productName} x${i.quantity} ¥${i.price}`)
				.join('\n')
			uni.showModal({
				title: '订单详情',
				content: `订单号：${detail.orderNo}\n状态：${getStatusText(detail.orderStatus)}\n总金额：¥${detail.totalAmount}\n\n${itemsText}`,
				showCancel: false
			})
		}
	} catch (e) {
		// 静默处理
	}
}

const cancelOrder = async (orderNo) => {
	const res = await uni.showModal({ title: '提示', content: '确定要取消该订单吗？' })
	if (res.confirm) {
		try {
			await orderApi.cancel(orderNo)
			uni.showToast({ title: '已取消', icon: 'success' })
			loadOrders()
		} catch (e) {
			uni.showToast({ title: '取消失败', icon: 'none' })
		}
	}
}

const payOrder = async (orderNo) => {
	try {
		await orderApi.pay(orderNo)
		uni.showToast({ title: '支付成功', icon: 'success' })
		loadOrders()
	} catch (e) {
		uni.showToast({ title: '支付失败', icon: 'none' })
	}
}

onMounted(() => {
	loadOrders()
})
</script>

<style scoped>
.container { padding-top: 0; }

.status-tabs { white-space: nowrap; background: #fff; padding: 10px 0; }
.tab-item {
	display: inline-block; padding: 8px 16px; font-size: 13px; color: #666;
	border-bottom: 2px solid transparent; margin: 0 4px;
}
.tab-item.active { color: #d4a574; border-bottom-color: #d4a574; font-weight: 600; }

.order-list { padding: 10px; }
.empty { text-align: center; padding: 60px 0; color: #999; font-size: 14px; }

.order-card {
	background: #fff; border-radius: 10px; padding: 14px; margin-bottom: 10px;
	box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.order-no { font-size: 13px; color: #666; }
.order-status { font-size: 13px; font-weight: 600; }
.order-body { font-size: 13px; color: #999; }
.order-body text { display: block; margin-top: 2px; }
.order-actions { margin-top: 10px; display: flex; justify-content: flex-end; }
.action-btn { font-size: 12px; padding: 4px 16px; border-radius: 14px; height: 30px; line-height: 30px; }
.pay-btn { background: #d4a574; color: #fff; border: none; }
.cancel-btn { background: #f5f5f5; color: #e74c3c; border: 1px solid #ddd; }
</style>
