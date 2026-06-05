<template>
  <div class="mock-order-container">
    <!-- 步骤一：选择商品 -->
    <el-card class="card">
      <template #header>
        <div class="card-header">
          <h2>模拟小程序下单</h2>
          <el-tag type="warning" size="large">管理后台模拟端</el-tag>
        </div>
      </template>

      <div class="step-section">
        <h3 class="step-title">步骤一：选择商品</h3>
        <el-form :inline="true" class="filter-form">
          <el-form-item label="商品筛选">
            <el-input v-model="productKeyword" placeholder="输入商品名称搜索" clearable />
          </el-form-item>
          <el-form-item label="门店">
            <el-select v-model="selectedStoreId" placeholder="请选择门店" @change="loadProducts">
              <el-option v-for="store in storeList" :key="store.storeId" :label="store.storeName" :value="store.storeId" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadProducts">刷新商品</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="filteredProducts" style="width: 100%" max-height="400">
          <el-table-column prop="productId" label="ID" width="80" />
          <el-table-column prop="productName" label="商品名称" width="200" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="scope">¥{{ scope.row.price.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="description" label="描述" min-width="200" />
          <el-table-column label="数量" width="180">
            <template #default="scope">
              <el-input-number
                v-model="selectedQuantities[scope.row.productId]"
                :min="0"
                :max="99"
                size="small"
                @change="onQuantityChange(scope.row.productId, $event)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                :disabled="!selectedQuantities[scope.row.productId]"
                @click="addToCart(scope.row)"
              >
                加入清单
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 步骤二：订单清单 -->
    <el-card class="card" v-if="cartItems.length > 0">
      <template #header>
        <div class="card-header">
          <h3 class="step-title">步骤二：订单清单</h3>
          <el-button type="danger" size="small" @click="clearCart">清空清单</el-button>
        </div>
      </template>
      <el-table :data="cartItems" style="width: 100%">
        <el-table-column prop="productName" label="商品名称" width="200" />
        <el-table-column prop="price" label="单价" width="120">
          <template #default="scope">¥{{ scope.row.price.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column label="小计" width="120">
          <template #default="scope">
            ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="danger" size="small" @click="removeFromCart(scope.$index)">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="total-row">
        <span class="total-label">合计金额：</span>
        <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
      </div>
    </el-card>

    <!-- 步骤三：填写取餐信息 -->
    <el-card class="card" v-if="cartItems.length > 0">
      <template #header>
        <h3 class="step-title">步骤三：填写取餐信息</h3>
      </template>
      <el-form :model="orderForm" :rules="orderRules" ref="orderFormRef" label-width="100px" class="order-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户ID" prop="userId">
              <el-input-number v-model="orderForm.userId" :min="1" placeholder="输入用户ID" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="取餐门店" prop="storeId">
              <el-select v-model="orderForm.storeId" placeholder="请选择门店" style="width: 100%">
                <el-option v-for="store in storeList" :key="store.storeId" :label="store.storeName" :value="store.storeId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="取餐人姓名" prop="takeName">
              <el-input v-model="orderForm.takeName" placeholder="请输入取餐人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="takePhone">
              <el-input v-model="orderForm.takePhone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="orderForm.remark" type="textarea" :rows="2" placeholder="如有特殊要求请备注（如少糖、加冰等）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="submitOrder" :loading="submitting" class="submit-btn">
            模拟下单
          </el-button>
          <el-button size="large" @click="resetForm">重置表单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 下单结果 -->
    <el-card class="card" v-if="orderResult">
      <template #header>
        <h3 class="step-title">下单结果</h3>
      </template>
      <el-alert
        :title="orderResult.success ? '下单请求已提交' : '下单失败'"
        :type="orderResult.success ? 'success' : 'error'"
        :description="orderResult.message"
        show-icon
        :closable="false"
      />
      <div v-if="orderResult.success" class="result-tip">
        <el-icon><InfoFilled /></el-icon>
        <span>订单已通过 RocketMQ 异步处理，可在「订单管理」页面查看最新状态。</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'

// 门店列表
const storeList = ref([])
const selectedStoreId = ref(null)

// 商品列表
const allProducts = ref([])
const productKeyword = ref('')
const selectedQuantities = reactive({})

// 购物车
const cartItems = ref([])

// 订单表单
const orderFormRef = ref(null)
const submitting = ref(false)
const orderResult = ref(null)

const orderForm = reactive({
  userId: null,
  storeId: null,
  takeName: '',
  takePhone: '',
  remark: ''
})

const orderRules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  storeId: [{ required: true, message: '请选择取餐门店', trigger: 'change' }],
  takeName: [{ required: true, message: '请输入取餐人姓名', trigger: 'blur' }],
  takePhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 过滤商品
const filteredProducts = computed(() => {
  if (!productKeyword.value) return allProducts.value
  return allProducts.value.filter(p =>
    p.productName.includes(productKeyword.value)
  )
})

// 计算总价
const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 加载门店
const loadStores = async () => {
  try {
    const res = await request.get('/store')
    storeList.value = res.data || []
  } catch {
    storeList.value = []
  }
}

// 加载商品
const loadProducts = async () => {
  try {
    const res = await request.get('/product/all')
    allProducts.value = res.data || []
    // 初始化数量
    allProducts.value.forEach(p => {
      if (!(p.productId in selectedQuantities)) {
        selectedQuantities[p.productId] = 0
      }
    })
  } catch {
    ElMessage.error('加载商品列表失败')
  }
}

// 数量变更
const onQuantityChange = (productId, val) => {
  selectedQuantities[productId] = val || 0
}

// 加入购物车
const addToCart = (product) => {
  const qty = selectedQuantities[product.productId] || 0
  if (qty <= 0) return

  const existing = cartItems.value.find(i => i.productId === product.productId)
  if (existing) {
    existing.quantity += qty
  } else {
    cartItems.value.push({
      productId: product.productId,
      productName: product.productName,
      productImage: product.image || '',
      price: product.price,
      quantity: qty
    })
  }
  selectedQuantities[product.productId] = 0
  ElMessage.success(`已添加 ${product.productName} x${qty}`)
}

// 从购物车移除
const removeFromCart = (index) => {
  cartItems.value.splice(index, 1)
}

// 清空购物车
const clearCart = () => {
  cartItems.value = []
  orderResult.value = null
}

// 重置表单
const resetForm = () => {
  orderFormRef.value?.resetFields()
  orderForm.userId = null
  orderForm.storeId = null
  orderForm.takeName = ''
  orderForm.takePhone = ''
  orderForm.remark = ''
  cartItems.value = []
  orderResult.value = null
}

// 提交订单
const submitOrder = async () => {
  try {
    await orderFormRef.value.validate()
  } catch {
    return
  }

  if (cartItems.value.length === 0) {
    ElMessage.warning('请先选择商品')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认提交订单？合计金额：¥${totalPrice.value.toFixed(2)}`,
      '确认下单',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }

  submitting.value = true
  orderResult.value = null

  try {
    const orderData = {
      userId: orderForm.userId,
      storeId: orderForm.storeId,
      takeName: orderForm.takeName,
      takePhone: orderForm.takePhone,
      remark: orderForm.remark,
      orderItems: cartItems.value.map(item => ({
        productId: item.productId,
        productName: item.productName,
        productImage: item.productImage,
        price: item.price,
        quantity: item.quantity
      }))
    }

    const res = await request.post('/orders/create', orderData)
    orderResult.value = {
      success: true,
      message: res.data || res.msg || '订单创建请求已提交，正在通过 RocketMQ 异步处理'
    }
    ElMessage.success('下单成功！')
    // 清空购物车
    cartItems.value = []
    orderFormRef.value?.resetFields()
    orderForm.userId = null
    orderForm.storeId = null
  } catch (error) {
    orderResult.value = {
      success: false,
      message: error.message || '下单失败，请重试'
    }
    ElMessage.error('下单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadStores()
  loadProducts()
})
</script>

<style scoped>
.mock-order-container { width: 100%; }

.card { margin-bottom: 20px; }

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

.step-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #303133;
}

.filter-form { margin-bottom: 16px; }

.total-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 16px 0 0;
  border-top: 1px solid #ebeef5;
  margin-top: 16px;
}

.total-label {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: #e74c3c;
  margin-left: 12px;
}

.order-form {
  max-width: 800px;
}

.submit-btn {
  min-width: 160px;
}

.result-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding: 10px 16px;
  background: #ecf5ff;
  border-radius: 4px;
  color: #409eff;
  font-size: 13px;
}
</style>