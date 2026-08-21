<template>
  <div class="register-page">
    <div class="register-header">
      <div class="logo-box">
        <div class="logo-cn">简购</div>
        <div class="logo-en">SimpleMall</div>
      </div>

      <div class="page-title">用户注册</div>
    </div>

    <div class="register-content">
      <!-- 第一步：输入用户名 -->
      <div class="form-row">
        <label class="form-label">账号</label>
        <input
          v-model="form.username"
          class="form-input"
          type="text"
          placeholder="请输入你的账号"
          :disabled="usernameChecked"
        />
      </div>

      <div v-if="message" class="message" :class="{ success: messageType === 'success' }">
        {{ message }}
      </div>

      <button
        v-if="!usernameChecked"
        class="register-submit"
        @click="checkUsername"
      >
        下一步
      </button>

      <!-- 第二步：用户名不存在后，输入两次密码 -->
      <template v-if="usernameChecked">
        <div class="form-row">
          <label class="form-label">密码</label>
          <input
            v-model="form.password"
            class="form-input"
            type="password"
            placeholder="请输入密码"
          />
        </div>

        <div class="form-row">
          <label class="form-label">确认密码</label>
          <input
            v-model="form.confirmPassword"
            class="form-input"
            type="password"
            placeholder="请再次输入密码"
          />
        </div>

        <button class="register-submit" @click="handleRegister">
          同意并注册
        </button>

        <div class="change-username" @click="resetUsername">
          重新输入账号
        </div>
      </template>

      <div class="back-login" @click="goLogin">
        已有账号？返回登录
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { apiFetch } from '../utils/http'

const router = useRouter()

// 后端接口地址
// 如果你的 Spring Boot 端口不是 8080，就把这里改成你的后端端口
const API_BASE = 'http://localhost:8081/api/v1/user'

const CHECK_USERNAME_URL = `${API_BASE}/checkUsername`
const REGISTER_URL = `${API_BASE}/register`

const usernameChecked = ref(false)
const message = ref('')
const messageType = ref('error')

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

// 第一步：检查用户名是否存在
const checkUsername = async () => {
  if (!form.username.trim()) {
    showMessage('请输入账号', 'error')
    return
  }

  const url = `${CHECK_USERNAME_URL}?username=${encodeURIComponent(form.username.trim())}`

  try {
    const canRegister = await apiFetch(url)

    // 后端返回 true：数据库没有这个用户名，可以注册
    // 后端返回 false：数据库已有这个用户名，不能注册
    if (canRegister === true) {
      showMessage('账号可以使用，请继续设置密码', 'success')
      usernameChecked.value = true
    } else {
      showMessage('账号已存在，请换一个账号', 'error')
      usernameChecked.value = false
    }
  } catch (error) {
    console.error(error)
    showMessage(error.message || '无法连接后端服务', 'error')
  }
}
// 第二步：注册

 const handleRegister = async () => {
  if (!form.username.trim()) {
    showMessage('账号不能为空', 'error')
    return
  }

  if (!form.password) {
    showMessage('请输入密码', 'error')
    return
  }

  if (!form.confirmPassword) {
    showMessage('请再次输入密码', 'error')
    return
  }

  if (form.password !== form.confirmPassword) {
    showMessage('两次输入的密码不一致', 'error')
    return
  }

  try {
    await apiFetch(REGISTER_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: form.username.trim(),
        password: form.password
      })
    })

    alert('注册成功')
    router.push('/login')
  } catch (error) {
    console.error(error)
    showMessage(error.message || '注册失败，请稍后重试', 'error')
  }
}

const resetUsername = () => {
  usernameChecked.value = false
  form.password = ''
  form.confirmPassword = ''
  message.value = ''
}

const showMessage = (text, type) => {
  message.value = text
  messageType.value = type
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.register-page {
  width: 100%;
  min-height: 100vh;
  background-color: #ffffff;
  color: #1f2937;
}

.register-header {
  height: 110px;
  display: flex;
  align-items: center;
  padding-left: 44px;
}

.logo-box {
  width: 110px;
  margin-right: 40px;
  color: #ff5a00;
  line-height: 1;
}

.logo-cn {
  font-size: 34px;
  font-weight: 800;
  letter-spacing: 2px;
}

.logo-en {
  font-size: 22px;
  font-weight: 700;
  margin-top: 4px;
}

.page-title {
  font-size: 28px;
  color: #1f2937;
  font-weight: 500;
}

.register-content {
  width: 580px;
  margin: 110px auto 0;
}

.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.form-label {
  width: 90px;
  text-align: right;
  margin-right: 16px;
  font-size: 18px;
  color: #111827;
}

.form-input {
  width: 480px;
  height: 54px;
  border: 1px solid #d6d6d6;
  border-radius: 4px;
  outline: none;
  padding: 0 16px;
  font-size: 18px;
  color: #333333;
}

.form-input:disabled {
  background-color: #f5f5f5;
  color: #777777;
}

.form-input::placeholder {
  color: #b8bec8;
}

.message {
  width: 480px;
  margin-left: 106px;
  margin-top: -16px;
  margin-bottom: 22px;
  color: #ff4d4f;
  font-size: 15px;
}

.message.success {
  color: #16a34a;
}

.register-submit {
  width: 480px;
  height: 55px;
  border: none;
  border-radius: 4px;
  background: linear-gradient(to right, #ffd17a, #ff9d78);
  color: #ffffff;
  font-size: 20px;
  cursor: pointer;
  margin-left: 106px;
  margin-top: 24px;
}

.register-submit:active {
  opacity: 0.85;
}

.change-username {
  width: 480px;
  margin-left: 106px;
  margin-top: 18px;
  text-align: center;
  color: #666666;
  font-size: 15px;
  cursor: pointer;
}

.back-login {
  width: 480px;
  margin-left: 106px;
  margin-top: 22px;
  text-align: center;
  color: #ff5a00;
  font-size: 16px;
  cursor: pointer;
}
</style>
