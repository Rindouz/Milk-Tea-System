<template>
  <div class="category-container">
    <el-card class="card">
      <template #header>
        <div class="card-header">
          <h2>商品分类管理</h2>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加分类
          </el-button>
        </div>
      </template>
      <el-table :data="categoryList" style="width: 100%">
        <el-table-column prop="categoryId" label="分类ID" width="120" />
        <el-table-column prop="categoryName" label="分类名称" width="200" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.categoryId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="400px"
    >
      <el-form :model="categoryForm" :rules="rules" ref="categoryFormRef">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" min="1" max="100" placeholder="请输入排序" />
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

const categoryList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const categoryFormRef = ref(null)
const categoryForm = reactive({
  categoryId: null,
  categoryName: '',
  sort: 1
})

const rules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 获取分类列表
const getCategoryList = async () => {
  try {
    const response = await request.get('/category/list')
    categoryList.value = response.data
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

// 处理添加分类
const handleAdd = () => {
  dialogTitle.value = '添加分类'
  categoryForm.categoryId = null
  categoryForm.categoryName = ''
  categoryForm.sort = 1
  dialogVisible.value = true
}

// 处理编辑分类
const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  categoryForm.categoryId = row.categoryId
  categoryForm.categoryName = row.categoryName
  categoryForm.sort = row.sort
  dialogVisible.value = true
}

// 处理删除分类
const handleDelete = async (categoryId) => {
  try {
    const response = await request.delete(`/category/delete/${categoryId}`)
    ElMessage.success('删除分类成功')
    getCategoryList()
  } catch (error) {
    ElMessage.error('删除分类失败')
  }
}

// 处理提交
const handleSubmit = async () => {
  try {
    await categoryFormRef.value.validate()
    let response
    if (categoryForm.categoryId) {
      // 编辑
      response = await request.put('/category/update', categoryForm)
    } else {
      // 添加
      response = await request.post('/category/add', categoryForm)
    }
    ElMessage.success(categoryForm.categoryId ? '编辑分类成功' : '添加分类成功')
    dialogVisible.value = false
    getCategoryList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 组件挂载时获取分类列表
onMounted(() => {
  getCategoryList()
})
</script>

<style scoped>
.category-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>