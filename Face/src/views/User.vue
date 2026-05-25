<template>
  <div class="user-container">
    <el-card class="card">
      <template #header>
        <div class="card-header">
          <h2>用户管理</h2>
          <span class="card-tip">用户在小程序端首次登录后自动录入</span>
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
        <el-table-column label="手机号" width="180">
          <template #default="scope">
            <span v-if="phoneVisible[scope.row.userId]">{{ scope.row.phone }}</span>
            <span v-else style="color: #999; font-style: italic;">加密手机号</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号操作" width="120">
          <template #default="scope">
            <el-button size="small" text @click="togglePhone(scope.row.userId)">
              {{ phoneVisible[scope.row.userId] ? '隐藏' : '显示' }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button
              v-if="scope.row.status === 1"
              type="warning"
              size="small"
              @click="handleToggleStatus(scope.row, 0)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="handleToggleStatus(scope.row, 1)"
            >
              启用
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

    <el-dialog v-model="dialogVisible" title="编辑用户" width="400px">
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
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref([])
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const dialogVisible = ref(false)
const userFormRef = ref(null)
const phoneVisible = reactive({})
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

    userList.value.forEach(u => {
      if (!(u.userId in phoneVisible)) {
        phoneVisible[u.userId] = true
      }
    })
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
}

const togglePhone = (userId) => {
  phoneVisible[userId] = !phoneVisible[userId]
}

const handleEdit = (row) => {
  userForm.userId = row.userId
  userForm.nickname = row.nickname
  userForm.phone = row.phone
  dialogVisible.value = true
}

const handleToggleStatus = async (row, status) => {
  const label = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${label}该用户吗？`, '确认操作')
    await request.put(`/user/${row.userId}/status`, null, {
      params: { status }
    })
    ElMessage.success(`${label}用户成功`)
    getUserList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${label}用户失败`)
    }
  }
}

const handleSubmit = async () => {
  try {
    await userFormRef.value.validate()
    await request.put(`/user/${userForm.userId}`, {
      nickname: userForm.nickname,
      phone: userForm.phone
    })
    ElMessage.success('编辑用户成功')
    dialogVisible.value = false
    getUserList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
  getUserList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getUserList()
}

onMounted(() => {
  getUserList()
})
</script>

<style scoped>
.user-container { width: 100%; }
.card { margin-bottom: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { font-size: 18px; font-weight: bold; margin: 0; }
.card-tip { font-size: 12px; color: #999; }
.search-form { margin-bottom: 20px; }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
</style>