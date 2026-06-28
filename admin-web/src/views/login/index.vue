<template>
  <div class="login-root">
    <div class="login-box">
      <div class="login-title">易德自在运营管理</div>
      <div class="login-subtitle">Operation Admin System</div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="onSubmit">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="captcha">
          <div class="captcha-row">
            <el-input v-model="form.captcha" placeholder="请输入计算结果" :prefix-icon="Picture" @keyup.enter="onSubmit" />
            <img
              class="captcha-img"
              :src="captchaImg"
              alt="验证码"
              title="点击刷新"
              @click="refreshCaptcha"
            />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="onSubmit">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Picture } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getCaptcha } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', captcha: '', captchaId: '' })
const captchaImg = ref('')
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入计算结果', trigger: 'blur' }]
}

async function refreshCaptcha() {
  try {
    const res = await getCaptcha()
    captchaImg.value = res.data.image
    form.captchaId = res.data.captchaId
    form.captcha = ''
  } catch (e) {
    /* 拦截器已提示 */
  }
}

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login({
      username: form.username,
      password: form.password,
      captcha: form.captcha,
      captchaId: form.captchaId
    })
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    // 登录失败（含验证码错误）：刷新验证码，错误信息已由拦截器提示
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(refreshCaptcha)
</script>

<style scoped>
.login-root {
  height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f2d3d 0%, #324157 60%, #409eff 100%);
}
.login-box {
  width: 380px;
  padding: 36px 36px 28px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}
.login-title {
  font-size: 22px;
  font-weight: 700;
  text-align: center;
  color: #303133;
}
.login-subtitle {
  font-size: 12px;
  text-align: center;
  color: #909399;
  margin: 6px 0 24px;
  letter-spacing: 1px;
}
.captcha-row {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}
.captcha-img {
  height: 40px;
  width: 140px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  flex-shrink: 0;
  object-fit: contain;
  background: #fff;
}
.login-btn {
  width: 100%;
}
</style>
