<template>
  <div class="home-container">
    <el-container class="container">
      <el-aside width="200px" class="aside">
        <div class="logo">奶茶系统管理后台</div>
        <el-menu
          :default-active="activePath"
          class="el-menu-vertical-demo"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/category">
            <el-icon><Folder /></el-icon>
            <span>商品分类</span>
          </el-menu-item>
          <el-menu-item index="/product">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/inventory">
            <el-icon><List /></el-icon>
            <span>库存管理</span>
          </el-menu-item>
          <el-menu-item index="/store">
            <el-icon><House /></el-icon>
            <span>门店管理</span>
          </el-menu-item>
          <el-menu-item index="/order">
            <el-icon><Tickets /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-right">
            <el-dropdown>
              <span class="user-info">
                <el-icon><UserFilled /></el-icon>
                <span>管理员</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Folder, 
  Goods, 
  Tickets, 
  User, 
  UserFilled, 
  ArrowDown,
  List,
  House 
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activePath = computed(() => {
  return route.path
})

const handleMenuSelect = (key) => {
  router.push(key)
}

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
  ElMessage.success('退出登录成功')
}
</script>

<style scoped>
.home-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.container {
  height: 100%;
}

.aside {
  background-color: #2c3e50;
  color: white;
  overflow: hidden;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid #34495e;
  margin-bottom: 20px;
}

.el-menu-vertical-demo {
  background-color: #2c3e50;
  border-right: none;
}

.el-menu-item {
  color: white;
  height: 60px;
  line-height: 60px;
  margin: 0 20px;
  border-radius: 8px;
}

.el-menu-item:hover {
  background-color: #34495e;
}

.el-menu-item.is-active {
  background-color: #409eff;
}

.header {
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.main {
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style>