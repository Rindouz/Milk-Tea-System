<template>
  <div class="store-container">
    <el-card class="card">
      <template #header>
        <div class="card-header">
          <h2>门店管理</h2>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加门店
          </el-button>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getStoreList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="storeList" style="width: 100%">
        <el-table-column prop="storeId" label="门店ID" width="120" />
        <el-table-column prop="storeName" label="门店名称" width="200" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.storeId)">
              删除
            </el-button>
            <el-button 
              :type="scope.row.status === 1 ? 'warning' : 'success'" 
              size="small" 
              @click="handleStatusChange(scope.row.storeId, scope.row.status === 1 ? 0 : 1)"
            >
              {{ scope.row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑门店对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="storeForm" :rules="rules" ref="storeFormRef">
        <el-form-item label="门店名称" prop="storeName">
          <el-input v-model="storeForm.storeName" placeholder="请输入门店名称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="storeForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="storeForm.address" type="textarea" placeholder="请输入门店地址" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="storeForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
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

const storeList = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('添加门店')
const storeFormRef = ref(null)
const searchForm = reactive({
  phone: ''
})
const storeForm = reactive({
  storeId: null,
  storeName: '',
  phone: '',
  address: '',
  status: 1
})

const rules = {
  storeName: [
    { required: true, message: '请输入门店名称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' }
  ]
}

// 获取门店列表
const getStoreList = async () => {
  try {
    const response = await request.get('/store/page', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        phone: searchForm.phone
      }
    })
    storeList.value = response.data.records || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取门店列表失败')
    storeList.value = []
    total.value = 0
  }
}

// 处理添加门店
const handleAdd = () => {
  dialogTitle.value = '添加门店'
  storeForm.storeId = null
  storeForm.storeName = ''
  storeForm.phone = ''
  storeForm.address = ''
  storeForm.status = 1
  dialogVisible.value = true
}

// 处理编辑门店
const handleEdit = (row) => {
  dialogTitle.value = '编辑门店'
  storeForm.storeId = row.storeId
  storeForm.storeName = row.storeName
  storeForm.phone = row.phone
  storeForm.address = row.address
  storeForm.status = row.status
  dialogVisible.value = true
}

// 处理删除门店
const handleDelete = async (storeId) => {
  try {
    const response = await request.delete(`/store/${storeId}`)
    ElMessage.success('删除门店成功')
    getStoreList()
  } catch (error) {
    ElMessage.error('删除门店失败')
  }
}

// 处理门店状态变更
const handleStatusChange = async (storeId, status) => {
  try {
    const response = await request.put(`/store/${storeId}/status`, null, {
      params: {
        status
      }
    })
    ElMessage.success(status === 1 ? '启用成功' : '停用成功')
    getStoreList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 处理提交
const handleSubmit = async () => {
  try {
    await storeFormRef.value.validate()
    let response
    if (storeForm.storeId) {
      // 编辑
      response = await request.put('/store', storeForm)
    } else {
      // 添加
      response = await request.post('/store', storeForm)
    }
    ElMessage.success(storeForm.storeId ? '编辑门店成功' : '添加门店成功')
    dialogVisible.value = false
    getStoreList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getStoreList()
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getStoreList()
}

// 组件挂载时获取数据
onMounted(() => {
  getStoreList()
})
</script>

<style scoped>
.store-container {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>