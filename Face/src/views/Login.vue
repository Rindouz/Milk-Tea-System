<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2>奶茶系统管理后台</h2>
          <p>请登录系统</p>
        </div>
      </template>
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="密码登录" name="password">
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-position="top">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="loginForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="扫码登录" name="qrcode">
          <div class="qrcode-box">
            <div class="qrcode-placeholder">
              <el-icon :size="64"><PictureFilled /></el-icon>
              <p class="qrcode-tip">请使用微信扫描二维码</p>
              <p class="qrcode-subtip">需要先在微信小程序中绑定管理员账号</p>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { PictureFilled } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const activeTab = ref('password')
const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    const response = await request.get('/user/doLogin', {
      params: {
        username: loginForm.username,
        password: loginForm.password
      }
    })
    if (response.code === 200) {
      localStorage.setItem('token', response.data.tokenValue)
      localStorage.setItem('adminInfo', JSON.stringify({ username: loginForm.username }))
      router.push('/category')
    } else {
      ElMessage.error(response.msg)
    }
  } catch (error) {
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  width: 420px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-header h2 {
  color: #409eff;
  margin-bottom: 10px;
}

.login-header p {
  color: #999;
  font-size: 14px;
}

.login-tabs :deep(.el-tabs__header) {
  margin-bottom: 10px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}

.qrcode-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.qrcode-placeholder {
  width: 220px;
  height: 220px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  color: #999;
}

.qrcode-tip {
  margin-top: 12px;
  font-size: 13px;
}

.qrcode-subtip {
  margin-top: 6px;
  font-size: 11px;
  color: #bbb;
}
</style>