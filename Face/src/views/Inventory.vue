<template>
  <div class="inventory-container">
    <el-card class="card">
      <template #header>
        <div class="card-header">
          <h2>库存管理</h2>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getInventoryList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="inventoryList" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" width="120" />
        <el-table-column prop="productName" label="商品名称" width="200" />
        <el-table-column prop="stock" label="当前库存" width="120" />
        <el-table-column prop="soldCount" label="已售数量" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleAdjustStock(scope.row)">
              调整库存
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 调整库存对话框 -->
    <el-dialog
      v-model="adjustDialogVisible"
      title="调整库存"
      width="400px"
    >
      <el-form :model="adjustForm" :rules="adjustRules" ref="adjustFormRef">
        <el-form-item label="商品名称" disabled>
          <el-input v-model="adjustForm.productName" />
        </el-form-item>
        <el-form-item label="当前库存" disabled>
          <el-input v-model="adjustForm.currentStock" />
        </el-form-item>
        <el-form-item label="调整数量" prop="adjustQuantity">
          <el-input-number 
            v-model="adjustForm.adjustQuantity" 
            placeholder="请输入调整数量" 
            :min="-adjustForm.currentStock" 
          />
          <span class="adjust-hint">负数表示减少库存</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="adjustDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAdjustSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const inventoryList = ref([])
const adjustDialogVisible = ref(false)
const adjustFormRef = ref(null)
const searchForm = reactive({
  productName: ''
})
const adjustForm = reactive({
  productId: null,
  productName: '',
  currentStock: 0,
  adjustQuantity: 0
})

const adjustRules = {
  adjustQuantity: [
    { required: true, message: '请输入调整数量', trigger: 'blur' }
  ]
}

// 获取库存列表
const getInventoryList = async () => {
  try {
    // 先获取所有商品列表（包括下架的）
    const productResponse = await request.get('/product/all')
    const products = productResponse.data || []
    
    // 遍历商品获取库存信息
    const inventoryPromises = products.map(async (product) => {
      try {
        // 获取库存
        const stockResponse = await request.get(`/inventory/stock/${product.productId}`)
        const stock = stockResponse.data || 0
        
        // 获取已售数量
        const soldResponse = await request.get(`/inventory/sold/${product.productId}`)
        const soldCount = soldResponse.data || 0
        
        return {
          productId: product.productId,
          productName: product.productName,
          stock,
          soldCount
        }
      } catch (error) {
        return {
          productId: product.productId,
          productName: product.productName,
          stock: 0,
          soldCount: 0
        }
      }
    })
    
    inventoryList.value = await Promise.all(inventoryPromises)
    
    // 过滤搜索
    if (searchForm.productName) {
      inventoryList.value = inventoryList.value.filter(item => 
        item.productName.includes(searchForm.productName)
      )
    }
  } catch (error) {
    ElMessage.error('获取库存列表失败')
    inventoryList.value = []
  }
}

// 处理调整库存
const handleAdjustStock = (row) => {
  adjustForm.productId = row.productId
  adjustForm.productName = row.productName
  adjustForm.currentStock = row.stock
  adjustForm.soldCount = row.soldCount
  adjustForm.adjustQuantity = 0
  adjustDialogVisible.value = true
}

// 处理调整库存提交
const handleAdjustSubmit = async () => {
  try {
    await adjustFormRef.value.validate()
    
    const newStock = adjustForm.currentStock + adjustForm.adjustQuantity
    
    // 使用正确的saveOrUpdate接口来调整库存
    await request.post('/inventory/saveOrUpdate', {
      productId: adjustForm.productId,
      stock: newStock,
      sold: adjustForm.soldCount
    })
    
    ElMessage.success('库存调整成功')
    adjustDialogVisible.value = false
    getInventoryList()
  } catch (error) {
    ElMessage.error('库存调整失败')
  }
}

// 组件挂载时获取数据
onMounted(() => {
  getInventoryList()
})
</script>

<style scoped>
.inventory-container {
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

.adjust-hint {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style>