<template>
  <div class="product-container">
    <el-card class="card">
      <template #header>
        <div class="card-header">
          <h2>商品管理</h2>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加商品
          </el-button>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类">
            <el-option
              v-for="category in categoryList"
              :key="category.categoryId"
              :label="category.categoryName"
              :value="category.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-switch v-model="showAllProducts" active-text="显示所有商品" inactive-text="仅显示上架商品" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getProductList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="productList" style="width: 100%">
        <el-table-column prop="productId" label="商品ID" width="120" />
        <el-table-column prop="productName" label="商品名称" width="200" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.productId)">
              删除
            </el-button>
            <el-button 
              :type="scope.row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="handleStatusChange(scope.row.productId, scope.row.status === 1 ? 0 : 1)"
            >
              {{ scope.row.status === 1 ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="productForm" :rules="rules" ref="productFormRef">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="productForm.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="productForm.categoryId" placeholder="请选择分类">
            <el-option
              v-for="category in categoryList"
              :key="category.categoryId"
              :label="category.categoryName"
              :value="category.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="门店" prop="storeId">
          <el-select v-model="productForm.storeId" placeholder="请选择门店">
            <el-option
              v-for="store in storeList"
              :key="store.storeId"
              :label="store.storeName"
              :value="store.storeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="productForm.price" min="0.01" step="0.01" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="productForm.stock" min="0" placeholder="请输入库存" />
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
            name="file"
          >
            <img v-if="productForm.image" :src="productForm.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="productForm.description" type="textarea" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="productForm.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const productList = ref([])
const categoryList = ref([])
const storeList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const productFormRef = ref(null)
const showAllProducts = ref(false)
const searchForm = reactive({
  categoryId: null
})
const productForm = reactive({
  productId: null,
  productName: '',
  categoryId: null,
  storeId: null,
  price: 0,
  stock: 0,
  image: '',
  description: '',
  status: 1
})

const uploadUrl = '/api/product/uploadImage'

const handleImageSuccess = (response) => {
  if (response.code === 200) {
    productForm.image = response.data
  } else {
    ElMessage.error('图片上传失败')
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
  }
  return isImage && isLt10M
}

const rules = {
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'blur' }
  ],
  storeId: [
    { required: true, message: '请选择门店', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存', trigger: 'blur' }
  ]
}

// 获取分类列表
const getCategoryList = async () => {
  try {
    const response = await request.get('/category/list')
    categoryList.value = response.data || []
  } catch (error) {
    ElMessage.error('获取分类列表失败')
    categoryList.value = []
  }
}

// 获取门店列表
const getStoreList = async () => {
  try {
    const response = await request.get('/store')
    storeList.value = response.data || []
  } catch (error) {
    ElMessage.error('获取门店列表失败')
    storeList.value = []
  }
}

// 获取商品列表
const getProductList = async () => {
  try {
    let response
    if (showAllProducts.value) {
      // 调用获取所有商品的接口
      response = await request.get('/product/all')
    } else {
      // 调用原来的获取上架商品的接口
      response = await request.get('/product/list', {
        params: {
          categoryId: searchForm.categoryId
        }
      })
    }
    
    const products = response.data || []
    
    // 为每个商品添加分类名称和库存信息
    const productWithDetails = await Promise.all(products.map(async (product) => {
      // 查找分类名称
      const category = categoryList.value.find(c => c.categoryId === product.categoryId)
      const categoryName = category ? category.categoryName : '未知分类'
      
      // 获取库存信息
      try {
        const stockResponse = await request.get(`/inventory/stock/${product.productId}`)
        const stock = stockResponse.data || 0
        return {
          ...product,
          categoryName,
          stock
        }
      } catch (error) {
        return {
          ...product,
          categoryName,
          stock: 0
        }
      }
    }))
    
    productList.value = productWithDetails
  } catch (error) {
    ElMessage.error('获取商品列表失败')
    productList.value = []
  }
}

// 处理添加商品
const handleAdd = () => {
  dialogTitle.value = '添加商品'
  productForm.productId = null
  productForm.productName = ''
  productForm.categoryId = null
  productForm.storeId = null
  productForm.price = 0
  productForm.stock = 0
  productForm.image = ''
  productForm.description = ''
  productForm.status = 1
  dialogVisible.value = true
}

// 处理编辑商品
const handleEdit = (row) => {
  dialogTitle.value = '编辑商品'
  productForm.productId = row.productId
  productForm.productName = row.productName
  productForm.categoryId = row.categoryId
  productForm.storeId = row.storeId
  productForm.price = row.price
  productForm.stock = row.stock
  productForm.image = row.image || ''
  productForm.description = row.description
  productForm.status = row.status
  dialogVisible.value = true
}

// 处理删除商品
const handleDelete = async (productId) => {
  try {
    const response = await request.delete(`/product/delete/${productId}`)
    ElMessage.success('删除商品成功')
    getProductList()
  } catch (error) {
    ElMessage.error('删除商品失败')
  }
}

// 处理商品上下架
const handleStatusChange = async (productId, status) => {
  try {
    const response = await request.put('/product/status', null, {
      params: {
        productId,
        status
      }
    })
    ElMessage.success(status === 1 ? '上架成功' : '下架成功')
    getProductList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 处理提交
const handleSubmit = async () => {
  try {
    await productFormRef.value.validate()
    let response
    if (productForm.productId) {
      // 编辑
      response = await request.put('/product/update', productForm)
    } else {
      // 添加
      response = await request.post('/product/add', productForm)
    }
    ElMessage.success(productForm.productId ? '编辑商品成功' : '添加商品成功')
    dialogVisible.value = false
    getProductList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 组件挂载时获取数据
onMounted(() => {
  getCategoryList()
  getStoreList()
  getProductList()
})
</script>

<style scoped>
.product-container {
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

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}
</style>