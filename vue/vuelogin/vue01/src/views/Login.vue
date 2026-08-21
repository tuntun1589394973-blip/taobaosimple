<template>
  <div class="login-page">
    <button class="home-btn" type="button" @click="goHome">
      <span aria-hidden="true">←</span>
      返回首页
    </button>

    <div class="login-box">
      <!-- 顶部登录方式切换 -->
      <div class="login-tabs">
        <span
          class="tab-item"
          :class="{ active: loginType === 'password' }"
          @click="loginType = 'password'"
        >
          密码登录
        </span>

        <span class="tab-line"></span>

        <span
          class="tab-item"
          :class="{ active: loginType === 'sms' }"
          @click="loginType = 'sms'"
        >
          短信登录
        </span>
      </div>

      <!-- 表单区域 -->
      <div class="form-box">
        <input
          v-model="form.account"
          class="input-item"
          type="text"
          placeholder="账号名/邮箱/手机号"
        />

        <!-- 密码登录 -->
        <div v-if="loginType === 'password'" class="input-item password-row">
          <input
            v-model="form.password"
            class="inner-input"
            type="password"
            placeholder="请输入登录密码"
          />
          <span class="forget" @click="forgetPassword">忘记密码</span>
        </div>

        <!-- 短信登录 -->
        <div v-else class="input-item password-row">
          <input
            v-model="form.code"
            class="inner-input"
            type="text"
            placeholder="请输入短信验证码"
          />
          <span class="forget" @click="getCode">获取验证码</span>
        </div>

        <div
          v-if="message"
          class="message"
          :class="{ success: messageType === 'success' }"
        >
          {{ message }}
        </div>

        <button class="login-btn" @click="handleLogin">
          登录
        </button>

        <button class="register-btn" @click="handleRegister">
          注册
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { setAuth } from '../utils/auth'
import { apiFetch } from '../utils/http'

const router = useRouter()
const route = useRoute()

const loginType = ref('password')

const form = reactive({
  account: '',
  password: '',
  code: ''
})

const message = ref('')
const messageType = ref('error')

// 后端接口地址
const API_BASE = 'http://localhost:8081/api/v1/user'
const LOGIN_URL = `${API_BASE}/login`

const handleLogin = async () => {
  message.value = ''

  if (!form.account.trim()) {
    showMessage('请输入账号', 'error')
    return
  }

  if (loginType.value === 'sms') {
    showMessage('短信登录暂未接入，请使用密码登录', 'error')
    return
  }

  if (!form.password) {
    showMessage('请输入登录密码', 'error')
    return
  }

  try {
    const data = await apiFetch(LOGIN_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: form.account.trim(),
        password: form.password
      })
    })

    if (data?.token && data?.username) {
      showMessage('登录成功', 'success')

      setAuth(data)

      alert('登录成功')

      const redirect = typeof route.query.redirect === 'string'
        ? route.query.redirect
        : '/home'

      router.push(redirect)
    } else {
      showMessage('登录响应数据格式不正确', 'error')
    }
  } catch (error) {
    console.error(error)
    showMessage(error.message || '账号或密码错误', 'error')
  }
}

const handleRegister = () => {
  router.push('/register')
}

const goHome = () => {
  router.push('/home')
}

const forgetPassword = () => {
  alert('跳转到忘记密码页面')
}

const getCode = () => {
  if (!form.account.trim()) {
    alert('请先输入手机号')
    return
  }

  alert('验证码功能暂未接入')
}

const showMessage = (text, type) => {
  message.value = text
  messageType.value = type
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.login-page {
  position: relative;
  width: 100%;
  min-height: 100vh;
  background-color: #ffffff;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 70px;
}

.home-btn {
  position: absolute;
  top: 28px;
  left: 36px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #e2e5ea;
  border-radius: 22px;
  padding: 9px 18px;
  background-color: #ffffff;
  color: #3c4e70;
  font-size: 16px;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s, background-color 0.2s;
}

.home-btn:hover {
  border-color: #ff5a00;
  background-color: #fff8f3;
  color: #ff5a00;
}

.login-box {
  width: 555px;
}

.login-tabs {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 62px;
}

.tab-item {
  font-size: 28px;
  font-weight: 700;
  color: #071633;
  cursor: pointer;
  user-select: none;
}

.tab-item.active {
  color: #ff5a00;
}

.tab-line {
  width: 1px;
  height: 22px;
  background-color: #e2e5ea;
  margin: 0 28px;
}

.form-box {
  width: 100%;
}

.input-item {
  width: 100%;
  height: 72px;
  border: none;
  outline: none;
  border-radius: 10px;
  background-color: #f3f6f9;
  margin-bottom: 30px;
  padding: 0 30px;
  font-size: 22px;
  font-weight: 600;
  color: #25385c;
}

.input-item::placeholder {
  color: #3c4e70;
}

.password-row {
  display: flex;
  align-items: center;
  padding-right: 28px;
}

.inner-input {
  flex: 1;
  height: 100%;
  border: none;
  outline: none;
  background-color: transparent;
  font-size: 22px;
  font-weight: 600;
  color: #25385c;
}

.inner-input::placeholder {
  color: #3c4e70;
}

.forget {
  font-size: 19px;
  color: #3c4e70;
  cursor: pointer;
  white-space: nowrap;
}

.message {
  color: #ff4d4f;
  font-size: 16px;
  margin-top: -12px;
  margin-bottom: 18px;
}

.message.success {
  color: #16a34a;
}

.login-btn {
  width: 100%;
  height: 72px;
  border: none;
  border-radius: 10px;
  background-color: #ff5a00;
  color: #ffffff;
  font-size: 22px;
  font-weight: 700;
  cursor: pointer;
  margin-top: 30px;
}

.login-btn:active {
  opacity: 0.85;
}

.register-btn {
  width: 100%;
  height: 72px;
  border: 1px solid #ff5a00;
  border-radius: 10px;
  background-color: #ffffff;
  color: #ff5a00;
  font-size: 22px;
  font-weight: 700;
  cursor: pointer;
  margin-top: 18px;
}

.register-btn:active {
  opacity: 0.85;
}

@media (max-width: 680px) {
  .login-page {
    padding: 88px 20px 30px;
  }

  .home-btn {
    top: 22px;
    left: 20px;
  }

  .login-box {
    width: 100%;
  }
}
</style>
