<template>
  <div class="order-container">
    <el-card class="card">
      <template #header>
        <div class="card-header">
          <h2>订单管理</h2>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value="" />
            <el-option label="待支付" value="0" />
            <el-option label="待取餐" value="1" />
            <el-option label="已完成" value="2" />
            <el-option label="制作中" value="3" />
            <el-option label="已取消" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getOrderList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="orderList" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="100" />
        <el-table-column prop="orderStatus" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.orderStatus)">
              {{ getStatusText(scope.row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="400">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleDetail(scope.row.orderNo)">
              详情
            </el-button>
            <el-button 
              v-if="scope.row.orderStatus === 0" 
              type="success" 
              size="small" 
              @click="handlePay(scope.row.orderNo)"
            >
              支付
            </el-button>
            <el-button 
              v-if="scope.row.orderStatus === 1" 
              type="warning" 
              size="small" 
              @click="handleCancel(scope.row.orderNo)"
            >
              取消
            </el-button>
            <el-button 
              v-if="scope.row.orderStatus === 1" 
              type="info" 
              size="small" 
              @click="handleConfirm(scope.row.orderNo)"
            >
              确认取餐
            </el-button>
            <el-button 
              type="primary" 
              size="small" 
              @click="handleUpdateStatus(scope.row)"
            >
              修改状态
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="orderDetail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">{{ orderDetail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ orderDetail.userId }}</el-descriptions-item>
          <el-descriptions-item label="总金额">{{ orderDetail.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ getStatusText(orderDetail.orderStatus) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ orderDetail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ orderDetail.payTime }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ orderDetail.completeTime }}</el-descriptions-item>
        </el-descriptions>
        <h3 style="margin-top: 20px; margin-bottom: 10px;">订单商品</h3>
        <el-table :data="orderDetail.orderItems" style="width: 100%">
          <el-table-column prop="productId" label="商品ID" width="120" />
          <el-table-column prop="productName" label="商品名称" width="200" />
          <el-table-column prop="quantity" label="数量" width="100" />
          <el-table-column prop="price" label="价格" width="100" />
        </el-table>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改订单状态对话框 -->
    <el-dialog
      v-model="updateStatusVisible"
      title="修改订单状态"
      width="400px"
    >
      <el-form :model="updateStatusForm" ref="updateStatusFormRef">
        <el-form-item label="订单号" prop="orderNo">
          <el-input v-model="updateStatusForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="当前状态" prop="currentStatus">
          <el-input v-model="updateStatusForm.currentStatus" disabled />
        </el-form-item>
        <el-form-item label="新状态" prop="newStatus" required>
          <el-select v-model="updateStatusForm.newStatus" placeholder="请选择新状态">
            <el-option label="待支付" value="0" />
            <el-option label="待取餐" value="1" />
            <el-option label="已完成" value="2" />
            <el-option label="制作中" value="3" />
            <el-option label="已取消" value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="updateStatusVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStatusSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const orderList = ref([])
const orderDetail = ref(null)
const detailVisible = ref(false)
const updateStatusVisible = ref(false)
const updateStatusFormRef = ref(null)
const updateStatusForm = reactive({
  orderNo: '',
  currentStatus: '',
  newStatus: ''
})
const searchForm = reactive({
  orderNo: '',
  status: ''
})

// 获取订单列表
const getOrderList = async () => {
  try {
    // 这里使用后端的订单列表API，暂时使用userId=1作为默认值
    // 实际项目中应该从登录状态获取userId
    const response = await request.get('/orders/user/page/1', {
      params: {
        status: searchForm.status || undefined
      }
    })
    orderList.value = response.data.records || []
  } catch (error) {
    ElMessage.error('获取订单列表失败')
    orderList.value = []
  }
}

// 获取订单详情
const handleDetail = async (orderNo) => {
  try {
    const response = await request.get(`/orders/detail/${orderNo}`)
    orderDetail.value = response.data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

// 处理订单支付
const handlePay = async (orderNo) => {
  try {
    const response = await request.post(`/orders/pay/${orderNo}`)
    ElMessage.success('支付成功')
    getOrderList()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

// 处理订单取消
const handleCancel = async (orderNo) => {
  try {
    const response = await request.post(`/orders/cancel/${orderNo}`)
    ElMessage.success('取消成功')
    getOrderList()
  } catch (error) {
    ElMessage.error('取消失败')
  }
}

// 处理确认取餐
const handleConfirm = async (orderNo) => {
  try {
    const response = await request.post(`/orders/confirm/${orderNo}`)
    ElMessage.success('取餐成功')
    getOrderList()
  } catch (error) {
    ElMessage.error('取餐失败')
  }
}

// 处理修改订单状态
const handleUpdateStatus = (row) => {
  updateStatusForm.orderNo = row.orderNo
  updateStatusForm.currentStatus = getStatusText(row.orderStatus)
  updateStatusForm.newStatus = row.orderStatus.toString()
  updateStatusVisible.value = true
}

// 处理状态提交
const handleStatusSubmit = async () => {
  try {
    // 调用后端的更新状态接口
    await request.post(`/orders/updateStatus/${updateStatusForm.orderNo}`, null, {
      params: {
        status: parseInt(updateStatusForm.newStatus)
      }
    })
    
    ElMessage.success('状态更新成功')
    updateStatusVisible.value = false
    getOrderList()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '待取餐',
    2: '已完成',
    3: '制作中',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'primary',
    4: 'danger'
  }
  return typeMap[status] || 'info'
}

// 组件挂载时获取订单列表
onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.order-container {
  width: 100%;
}

.card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  font-size: 18px;
  font-weight: bold;
  margin: 0;
}

.search-form {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>