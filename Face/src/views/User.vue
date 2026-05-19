<template>
  <div class="user-container">
    <el-card class="card">
      <template #header>
        <div class="card-header">
          <h2>用户管理</h2>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getUserList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="userList" style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.userId)">
              删除
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

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="400px"
    >
      <el-form :model="userForm" :rules="rules" ref="userFormRef">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
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

const userList = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const userFormRef = ref(null)
const searchForm = reactive({
  phone: ''
})
const userForm = reactive({
  userId: null,
  nickname: '',
  phone: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' }
  ]
}

// 获取用户列表
const getUserList = async () => {
  try {
    const response = await request.get('/user/page', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        phone: searchForm.phone
      }
    })
    userList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
}

// 处理添加用户
const handleAdd = () => {
  dialogTitle.value = '添加用户'
  userForm.userId = null
  userForm.nickname = ''
  userForm.phone = ''
  dialogVisible.value = true
}

// 处理编辑用户
const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  userForm.userId = row.userId
  userForm.nickname = row.nickname
  userForm.phone = row.phone
  dialogVisible.value = true
}

// 处理删除用户
const handleDelete = async (userId) => {
  try {
    const response = await request.delete(`/user/${userId}`)
    ElMessage.success('删除用户成功')
    getUserList()
  } catch (error) {
    ElMessage.error('删除用户失败')
  }
}

// 处理提交
const handleSubmit = async () => {
  try {
    await userFormRef.value.validate()
    let response
    if (userForm.userId) {
      // 编辑
      response = await request.put(`/user/${userForm.userId}`, {
        nickname: userForm.nickname,
        phone: userForm.phone
      })
    } else {
      // 添加
      response = await request.post('/user', {
        nickname: userForm.nickname,
        phone: userForm.phone
      })
    }
    ElMessage.success(userForm.userId ? '编辑用户成功' : '添加用户成功')
    dialogVisible.value = false
    getUserList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getUserList()
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  getUserList()
}

// 组件挂载时获取用户列表
onMounted(() => {
  getUserList()
})
</script>

<style scoped>
.user-container {
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