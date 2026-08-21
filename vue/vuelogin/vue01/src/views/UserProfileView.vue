<template>
  <div class="profile-page">
    <header class="profile-header">
      <div class="brand" @click="goHome">
        <div class="brand-cn">简购</div>
        <div class="brand-en">SimpleMall</div>
      </div>

      <div class="header-title">账号管理</div>

      <button class="back-btn" @click="goHome">返回首页</button>
    </header>

    <div class="profile-container">
      <aside class="side-menu">
        <div
          class="menu-item"
          :class="{ active: activeMenu === 'profile' }"
          @click="activeMenu = 'profile'"
        >
          个人资料
        </div>

        <div
          class="menu-item"
          :class="{ active: activeMenu === 'address' }"
          @click="goAddressManage"
        >
          收货信息
        </div>
      </aside>

      <main class="profile-main">
        <!-- 个人资料 -->
        <template v-if="activeMenu === 'profile'">
          <div class="tabs">
            <div class="tab title-tab">个人资料</div>

            <div
              class="sub-tab"
              :class="{ active: profileTab === 'basic' }"
              @click="profileTab = 'basic'"
            >
              基本资料
            </div>

            <div
              class="sub-tab"
              :class="{ active: profileTab === 'avatar' }"
              @click="profileTab = 'avatar'"
            >
              头像照片
            </div>
          </div>

          <div v-if="profileTab === 'basic'" class="content-card">
            <div class="tip-text">
              亲爱的 {{ form.userName || '用户' }}，完善个人资料，方便后续使用。
            </div>

            <div class="form-row avatar-row">
              <label class="form-label">当前头像</label>

              <div class="avatar-box" @click="profileTab = 'avatar'">
                <img
                  v-if="avatarSrc"
                  class="avatar-img"
                  :src="avatarSrc"
                  alt="头像"
                />

                <div v-else class="avatar-placeholder">头像</div>

                <div class="avatar-mask">修改头像</div>
              </div>
            </div>

            <div class="form-row">
              <label class="form-label">账号</label>
              <input
                v-model="form.userName"
                class="form-input disabled"
                type="text"
                disabled
              />
            </div>

            <div class="form-row">
              <label class="form-label">昵称</label>
              <input
                v-model="form.virtualName"
                class="form-input"
                type="text"
                placeholder="请输入昵称"
              />
            </div>

            <div class="form-row">
              <label class="form-label">手机号码</label>
              <input
                v-model="form.phone"
                class="form-input"
                type="text"
                placeholder="请输入手机号码"
              />
            </div>

            <div class="form-row">
              <label class="form-label">性别</label>

              <div class="radio-group">
                <label class="radio-item">
                  <input v-model="form.sex" type="radio" value="男" />
                  男
                </label>

                <label class="radio-item">
                  <input v-model="form.sex" type="radio" value="女" />
                  女
                </label>

                <label class="radio-item">
                  <input v-model="form.sex" type="radio" value="保密" />
                  保密
                </label>
              </div>
            </div>

            <div
              v-if="message"
              class="message"
              :class="{ success: messageType === 'success' }"
            >
              {{ message }}
            </div>

            <div class="button-row">
              <button class="save-btn" @click="updateUserInfo">保存修改</button>

              <button class="delete-btn" @click="deleteAccount">注销账号</button>
            </div>
          </div>

          <div v-if="profileTab === 'avatar'" class="content-card avatar-upload-card">
            <div class="upload-area">
              <button class="upload-btn" @click="chooseAvatar">上传图片</button>

              <input
                ref="avatarInput"
                type="file"
                accept="image/jpeg,image/jpg,image/png"
                class="file-input"
                @change="handleAvatarChange"
              />

              <div class="upload-desc">支持 jpg、jpeg、png 格式的图片</div>

              <div class="upload-warning">
                请不要上传公司、店铺、商品的图片，系统会保留处理违规头像的权力
              </div>

              <div class="preview-box">
                <img
                  v-if="avatarPreview || avatarSrc"
                  class="preview-img"
                  :src="avatarPreview || avatarSrc"
                  alt="头像预览"
                />

                <div v-else class="preview-empty">暂无头像</div>
              </div>

              <div
                v-if="message"
                class="message avatar-message"
                :class="{ success: messageType === 'success' }"
              >
                {{ message }}
              </div>

              <button class="save-btn avatar-save-btn" @click="uploadAvatar">
                保存
              </button>
            </div>
          </div>
        </template>

        <!-- 收货信息：仍然在当前页面右侧，不会新增路由 -->
        <template v-else-if="activeMenu === 'address'">
          <div class="tabs">
            <div class="tab title-tab">收货信息</div>
          </div>

          <div class="address-content">
            <div class="address-header">
              <div>
                <h2>我的收货地址</h2>
                <p>默认地址或置顶地址会优先显示。</p>
              </div>

              <button class="add-address-btn" @click="openAddAddressDialog">
                添加地址
              </button>
            </div>

            <div
              v-if="addressMessage"
              class="address-message"
              :class="{ success: addressMessageType === 'success' }"
            >
              {{ addressMessage }}
            </div>

            <div v-if="addressLoading" class="address-state">
              正在加载收货地址...
            </div>

            <div v-else class="address-table-wrap">
              <table class="address-table">
                <thead>
                  <tr>
                    <th>收货人</th>
                    <th>电话/手机</th>
                    <th>所在地区</th>
                    <th>详细地址</th>
                    <th>邮编</th>
                    <th>操作</th>
                    <th>移动设置</th>
                  </tr>
                </thead>

                <tbody v-if="addressList.length > 0">
                  <tr v-for="address in addressList" :key="address.id">
                    <td class="receiver-cell">
  <div class="receiver-name">
    {{ address.receiverName || '未填写收货人' }}
  </div>

  <span
    v-if="isOne(address.isDefault)"
    class="address-tag default-tag"
  >
    默认
  </span>
</td>

                    <td>{{ address.receiverPhone }}</td>
                    <td>{{ address.region }}</td>
                    <td class="detail-cell">{{ address.detailAddress }}</td>
                    <td>{{ address.postalCode || '000000' }}</td>

                    <td class="address-actions">
                      <button @click="openEditAddressDialog(address)">修改</button>
                      <button class="danger-text" @click="deleteAddress(address)">删除</button>

                      <button @click="toggleTopAddress(address)">
  {{ isEffectiveTop(address) ? '取消置顶' : '置顶' }}
</button>
                    </td>

                    <td>
                      <button class="default-action" @click="toggleDefaultAddress(address)">
                        {{ isOne(address.isDefault) ? '取消默认' : '设为默认' }}
                      </button>
                    </td>
                  </tr>
                </tbody>

                <tbody v-else>
                  <tr>
                    <td colspan="7" class="empty-address-row">
                      暂无收货地址，点击“添加地址”创建。
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </template>
      </main>
    </div>

    <!-- 添加 / 编辑地址弹窗 -->
    <div
      v-if="addressDialogVisible"
      class="dialog-mask"
      @click.self="closeAddressDialog"
    >
      <div class="address-dialog">
        <div class="dialog-header">
          <h2>{{ addressEditing ? '编辑收货地址' : '添加收货地址' }}</h2>

          <button class="close-btn" @click="closeAddressDialog">×</button>
        </div>

        <div class="delivery-tip">
          <span>当前配送至</span>
          <strong>中国大陆</strong>
        </div>

        <div class="address-form-row">
          <label>
            <span>*</span>
            地址信息：
          </label>

          <input
            v-model.trim="addressForm.region"
            type="text"
            placeholder="请输入省 / 市 / 区 / 街道"
          />
        </div>

        <div class="address-form-row detail-row">
          <label>
            <span>*</span>
            详细地址：
          </label>

          <textarea
            v-model.trim="addressForm.detailAddress"
            placeholder="请输入详细地址信息，如道路、门牌号、小区、楼栋号、单元等信息"
          ></textarea>
        </div>

        <div class="address-form-row">
          <label>
            <span>*</span>
            收货人姓名：
          </label>

          <input
            v-model.trim="addressForm.receiverName"
            type="text"
            maxlength="25"
            placeholder="长度不超过25个字符"
          />
        </div>

        <div class="address-form-row">
          <label>
            <span>*</span>
            手机号码：
          </label>

          <div class="phone-row">
            <div class="phone-prefix">中国大陆 +86</div>

            <input
              v-model.trim="addressForm.receiverPhone"
              type="text"
              maxlength="20"
              placeholder="请输入手机号码"
            />
          </div>
        </div>

        <div class="address-form-row">
          <label>邮编：</label>

          <input
            v-model.trim="addressForm.postalCode"
            type="text"
            maxlength="20"
            placeholder="默认 000000"
          />
        </div>

        <div class="default-check">
          <input
            id="defaultAddress"
            v-model="addressForm.isDefault"
            type="checkbox"
          />

          <label for="defaultAddress">设置为默认收货地址</label>
        </div>

        <div class="dialog-footer">
          <button class="cancel-btn" @click="closeAddressDialog">取消</button>
          <button class="confirm-btn" @click="saveAddress">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { clearAuth } from '../utils/auth'
import { apiFetch } from '../utils/http'

const router = useRouter()

const API_BASE = 'http://localhost:8081/api/v1/user'
const ADDRESS_API_BASE = 'http://localhost:8081/api/v1/addresses'

const SELECT_USER_URL = `${API_BASE}/select`
const UPDATE_USER_URL = `${API_BASE}/update`
const LOGOUT_USER_URL = `${API_BASE}/logout`
const UPLOAD_AVATAR_URL = `${API_BASE}/uploadAvatar`

const SELECT_ADDRESS_URL = `${ADDRESS_API_BASE}/selectUserAddressByUserId`
const ADD_ADDRESS_URL = `${ADDRESS_API_BASE}/addUserAddress`
const UPDATE_ADDRESS_URL = `${ADDRESS_API_BASE}/updateUserAddress`
const SET_DEFAULT_ADDRESS_URL = `${ADDRESS_API_BASE}/setDefaultAddress`
const SET_TOP_ADDRESS_URL = `${ADDRESS_API_BASE}/setIsTopById`
const DELETE_ADDRESS_URL = `${ADDRESS_API_BASE}/deleteUserAddressById`

const loginUser = localStorage.getItem('loginUser')

const activeMenu = ref('profile')
const profileTab = ref('basic')

const message = ref('')
const messageType = ref('error')

const avatarInput = ref(null)
const avatarPreview = ref('')
const selectedAvatarFile = ref(null)

const form = reactive({
  id: null,
  userName: '',
  virtualName: '',
  phone: '',
  sex: '保密',
  avatar: ''
})

const addressList = ref([])
const addressLoading = ref(false)
const addressMessage = ref('')
const addressMessageType = ref('error')
const addressDialogVisible = ref(false)
const addressEditing = ref(false)

const createEmptyAddressForm = () => ({
  id: null,
  receiverName: '',
  receiverPhone: '',
  region: '',
  detailAddress: '',
  postalCode: '000000',
  isDefault: false,
  isTop: 0
})

const addressForm = reactive(createEmptyAddressForm())

const avatarSrc = computed(() => {
  if (!form.avatar) {
    return ''
  }

  if (form.avatar.startsWith('http')) {
    return form.avatar
  }

  return `http://localhost:8081${form.avatar}`
})

onMounted(async () => {
  if (!loginUser) {
    alert('请先登录')
    router.push('/login')
    return
  }

  form.userName = loginUser
  await selectUserInfo()
})

const showMessage = (text, type = 'error') => {
  message.value = text
  messageType.value = type
}

const showAddressMessage = (text, type = 'error') => {
  addressMessage.value = text
  addressMessageType.value = type

  window.setTimeout(() => {
    if (addressMessage.value === text) {
      addressMessage.value = ''
    }
  }, 2500)
}

const isOne = (value) => Number(value) === 1

const isEffectiveTop = (address) => {
  return isOne(address.isTop) || isOne(address.isDefault)
}

const selectUserInfo = async () => {
  if (!loginUser) {
    showMessage('未获取到当前登录账号', 'error')
    return
  }

  try {
    const result = await apiFetch(SELECT_USER_URL, {
      method: 'POST'
    })

    if (!result) {
      showMessage('没有查询到用户信息', 'error')
      return
    }

    form.id = result.id || null
    form.userName = result.username || result.userName || loginUser
    form.virtualName = result.virtualName || ''
    form.phone = result.phone || ''
    form.sex = result.sex || '保密'
    form.avatar = result.avatar || ''
  } catch (error) {
    console.error(error)
    showMessage(error.message || '无法连接用户查询接口', 'error')
  }
}

const updateUserInfo = async () => {
  message.value = ''

  if (!form.userName) {
    showMessage('缺少账号信息，无法修改', 'error')
    return
  }

  try {
    await apiFetch(UPDATE_USER_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        virtualName: form.virtualName,
        phone: form.phone,
        sex: form.sex
      })
    })

    await selectUserInfo()
    showMessage('修改成功', 'success')
  } catch (error) {
    console.error(error)
    showMessage(error.message || '无法连接用户修改接口', 'error')
  }
}

const chooseAvatar = () => {
  avatarInput.value?.click()
}

const handleAvatarChange = (event) => {
  const file = event.target.files?.[0]

  if (!file) {
    return
  }

  const allowTypes = ['image/jpeg', 'image/jpg', 'image/png']

  if (!allowTypes.includes(file.type)) {
    showMessage('只能上传 jpg、jpeg、png 格式的图片', 'error')
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    showMessage('图片大小不能超过 5 MB', 'error')
    return
  }

  selectedAvatarFile.value = file
  avatarPreview.value = URL.createObjectURL(file)
}

const uploadAvatar = async () => {
  message.value = ''

  if (!form.userName) {
    showMessage('缺少账号信息，无法上传头像', 'error')
    return
  }

  if (!selectedAvatarFile.value) {
    showMessage('请先选择图片', 'error')
    return
  }

  const formData = new FormData()
  formData.append('file', selectedAvatarFile.value)

  try {
    await apiFetch(UPLOAD_AVATAR_URL, {
      method: 'POST',
      body: formData
    })

    await selectUserInfo()
    selectedAvatarFile.value = null
    avatarPreview.value = ''
    profileTab.value = 'basic'
    showMessage('头像修改成功', 'success')
  } catch (error) {
    console.error(error)
    showMessage(error.message || '无法连接头像上传接口', 'error')
  }
}

const deleteAccount = async () => {
  if (!form.userName) {
    showMessage('缺少账号信息，无法注销', 'error')
    return
  }

  const password = window.prompt('请输入当前账号密码，确认注销账号')

  if (!password) {
    showMessage('已取消注销', 'error')
    return
  }

  const confirmed = window.confirm(
    '确认要注销当前账号吗？注销后将无法继续使用该账号登录。'
  )

  if (!confirmed) {
    showMessage('已取消注销', 'error')
    return
  }

  try {
    const url = `${LOGOUT_USER_URL}?password=${encodeURIComponent(password)}`

    await apiFetch(url, {
      method: 'POST'
    })

    alert('账号注销成功')
    clearAuth()
    router.push('/home')
  } catch (error) {
    console.error(error)
    showMessage(error.message || '注销失败，密码可能不正确', 'error')
  }
}

const goAddressManage = async () => {
  activeMenu.value = 'address'
  await loadAddressList()
}

const loadAddressList = async () => {
  addressLoading.value = true
  addressMessage.value = ''

  try {
    const result = await apiFetch(SELECT_ADDRESS_URL, {
      method: 'POST'
    })

    if (!Array.isArray(result)) {
      showAddressMessage('后端返回地址数据格式不正确')
      return
    }

    addressList.value = result
  } catch (error) {
    console.error(error)
    showAddressMessage(error.message || '无法连接收货地址查询接口')
  } finally {
    addressLoading.value = false
  }
}

const resetAddressForm = () => {
  Object.assign(addressForm, createEmptyAddressForm())
}

const openAddAddressDialog = () => {
  addressEditing.value = false
  resetAddressForm()
  addressDialogVisible.value = true
}

const openEditAddressDialog = (address) => {
  addressEditing.value = true

  Object.assign(addressForm, {
    id: address.id,
    receiverName: address.receiverName || '',
    receiverPhone: address.receiverPhone || '',
    region: address.region || '',
    detailAddress: address.detailAddress || '',
    postalCode: address.postalCode || '000000',
    isDefault: isOne(address.isDefault),
    isTop: Number(address.isTop || 0)
  })

  addressDialogVisible.value = true
}

const closeAddressDialog = () => {
  addressDialogVisible.value = false
  resetAddressForm()
}

const validateAddressForm = () => {
  if (!addressForm.region) {
    showAddressMessage('请输入所在地区')
    return false
  }

  if (!addressForm.detailAddress) {
    showAddressMessage('请输入详细地址')
    return false
  }

  if (!addressForm.receiverName) {
    showAddressMessage('请输入收货人姓名')
    return false
  }

  if (!addressForm.receiverPhone) {
    showAddressMessage('请输入手机号码')
    return false
  }

  return true
}

const saveAddress = async () => {
  if (!validateAddressForm()) {
    return
  }

  const payload = {
    id: addressForm.id,
    receiverName: addressForm.receiverName,
    receiverPhone: addressForm.receiverPhone,
    region: addressForm.region,
    detailAddress: addressForm.detailAddress,
    postalCode: addressForm.postalCode || '000000',
    isDefault: addressForm.isDefault ? 1 : 0,
    isTop: Number(addressForm.isTop || 0)
  }

  const url = addressEditing.value
    ? UPDATE_ADDRESS_URL
    : ADD_ADDRESS_URL

  try {
    await apiFetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    })

    const successText = addressEditing.value ? '地址修改成功' : '地址添加成功'
    closeAddressDialog()
    await loadAddressList()
    showAddressMessage(successText, 'success')
  } catch (error) {
    console.error(error)
    showAddressMessage(error.message || '无法连接地址保存接口')
  }
}

const toggleTopAddress = async (address) => {
  const currentlyDefault = isOne(address.isDefault)
  const currentlyTop = isOne(address.isTop)

  try {
    // 情况一：
    // 默认地址本身也算“置顶”
    // 点击取消置顶时，要同时取消默认和置顶
    if (currentlyDefault) {
      const cancelDefaultUrl =
        `${SET_DEFAULT_ADDRESS_URL}?id=${encodeURIComponent(address.id)}` +
        `&isDefault=0`

      await apiFetch(cancelDefaultUrl, {
        method: 'POST'
      })

      // 无论原来 isTop 是 0 还是 1，都再明确设为 0
      const cancelTopUrl =
        `${SET_TOP_ADDRESS_URL}?id=${encodeURIComponent(address.id)}` +
        `&isTop=0`

      await apiFetch(cancelTopUrl, {
        method: 'POST'
      })

      await loadAddressList()
      showAddressMessage('已取消默认地址和置顶状态', 'success')
      return
    }

    // 情况二：
    // 普通地址已经手动置顶，点击后只取消置顶
    if (currentlyTop) {
      const cancelTopUrl =
        `${SET_TOP_ADDRESS_URL}?id=${encodeURIComponent(address.id)}` +
        `&isTop=0`

      await apiFetch(cancelTopUrl, {
        method: 'POST'
      })

      await loadAddressList()
      showAddressMessage('已取消置顶', 'success')
      return
    }

    // 情况三：
    // 普通地址没有置顶，设置为置顶
    const setTopUrl =
      `${SET_TOP_ADDRESS_URL}?id=${encodeURIComponent(address.id)}` +
      `&isTop=1`

    await apiFetch(setTopUrl, {
      method: 'POST'
    })

    await loadAddressList()
    showAddressMessage('地址已置顶', 'success')
  } catch (error) {
    console.error(error)
    showAddressMessage(error.message || '无法连接置顶接口')
  }
}

const toggleDefaultAddress = async (address) => {
  const nextIsDefault = isOne(address.isDefault) ? 0 : 1

  try {
    const url =
      `${SET_DEFAULT_ADDRESS_URL}?id=${encodeURIComponent(address.id)}` +
      `&isDefault=${nextIsDefault}`

    await apiFetch(url, {
      method: 'POST'
    })

    await loadAddressList()

    showAddressMessage(
      nextIsDefault === 1 ? '默认地址设置成功' : '已取消默认地址',
      'success'
    )
  } catch (error) {
    console.error(error)
    showAddressMessage(error.message || '无法连接默认地址接口')
  }
}

const deleteAddress = async (address) => {
  const confirmed = window.confirm(
    `确认删除“${address.receiverName}”这条收货地址吗？删除后无法恢复。`
  )

  if (!confirmed) {
    return
  }

  try {
    const url =
      `${DELETE_ADDRESS_URL}?id=${encodeURIComponent(address.id)}`

    await apiFetch(url, {
      method: 'POST'
    })

    await loadAddressList()
    showAddressMessage('地址已删除', 'success')
  } catch (error) {
    console.error(error)
    showAddressMessage(error.message || '无法连接删除地址接口')
  }
}

const goHome = () => {
  router.push('/home')
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.profile-page {
  min-height: 100vh;
  background-color: #ffffff;
  color: #111827;
}

.profile-header {
  height: 92px;
  display: flex;
  align-items: center;
  padding: 0 48px;
  border-bottom: 1px solid #f1f1f1;
}

.brand {
  width: 150px;
  color: #ff5a00;
  line-height: 1;
  cursor: pointer;
}

.brand-cn {
  font-size: 30px;
  font-weight: 800;
  letter-spacing: 2px;
}

.brand-en {
  margin-top: 4px;
  font-size: 19px;
  font-weight: 700;
}

.header-title {
  flex: 1;
  margin-left: 30px;
  font-size: 26px;
  font-weight: 600;
}

.back-btn {
  border: 1px solid #ff5a00;
  border-radius: 20px;
  padding: 8px 22px;
  background-color: #ffffff;
  color: #ff5a00;
  cursor: pointer;
  font-size: 15px;
}

.profile-container {
  display: flex;
  width: 1180px;
  margin: 36px auto 0;
}

.side-menu {
  width: 170px;
  padding-top: 10px;
}

.menu-item {
  margin-bottom: 22px;
  color: #111827;
  cursor: pointer;
  font-size: 17px;
}

.menu-item.active {
  color: #ff5a00;
  font-weight: 700;
}

.profile-main {
  flex: 1;
  min-height: 660px;
  border: 1px solid #dcdcdc;
  background-color: #ffffff;
}

.tabs {
  display: flex;
  align-items: flex-end;
  height: 54px;
  border-bottom: 1px solid #dcdcdc;
  padding-left: 34px;
}

.tab,
.sub-tab {
  display: flex;
  align-items: center;
  height: 54px;
  padding: 0 22px;
  font-size: 16px;
}

.tab {
  border: 1px solid transparent;
  border-bottom: none;
}

.title-tab {
  transform: translateY(1px);
  border-color: #dcdcdc;
  background-color: #ffffff;
  font-weight: 700;
  font-size: 17px;
}

.sub-tab {
  color: #333333;
  cursor: pointer;
}

.sub-tab.active {
  color: #ff5a00;
  font-weight: 700;
}

.content-card {
  padding: 52px 90px;
}

.tip-text {
  margin-bottom: 34px;
  color: #333333;
  font-size: 17px;
}

.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.form-label {
  width: 110px;
  margin-right: 26px;
  color: #333333;
  text-align: right;
  font-size: 16px;
}

.form-input {
  width: 620px;
  height: 48px;
  border: 1px solid #d6d6d6;
  border-radius: 7px;
  outline: none;
  padding: 0 15px;
  font-size: 16px;
}

.form-input.disabled {
  background-color: #f5f5f5;
  color: #777777;
}

.avatar-row {
  align-items: flex-start;
}

.avatar-box {
  position: relative;
  width: 120px;
  height: 120px;
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
}

.avatar-img,
.avatar-placeholder {
  width: 120px;
  height: 120px;
}

.avatar-img {
  display: block;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: linear-gradient(135deg, #ffd28a, #ff8a4c);
  color: #ffffff;
  font-size: 22px;
  font-weight: 700;
}

.avatar-mask {
  position: absolute;
  inset: 0;
  display: none;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.45);
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
}

.avatar-box:hover .avatar-mask {
  display: flex;
}

.radio-group {
  display: flex;
  align-items: center;
  gap: 34px;
}

.radio-item {
  cursor: pointer;
  font-size: 16px;
}

.radio-item input {
  margin-right: 8px;
}

.message {
  margin: 8px 0 22px 136px;
  color: #ff4d4f;
  font-size: 15px;
}

.message.success {
  color: #16a34a;
}

.button-row {
  display: flex;
  gap: 18px;
  margin: 34px 0 0 136px;
}

.save-btn {
  border: none;
  border-radius: 8px;
  padding: 12px 30px;
  background-color: #ff5a00;
  color: #ffffff;
  cursor: pointer;
  font-size: 17px;
}

.delete-btn {
  border: 1px solid #ff4d4f;
  border-radius: 8px;
  padding: 12px 30px;
  background-color: #ffffff;
  color: #ff4d4f;
  cursor: pointer;
  font-size: 17px;
}

.delete-btn:hover {
  background-color: #fff1f0;
}

.avatar-upload-card {
  padding: 72px 90px;
}

.upload-area {
  margin-left: 60px;
}

.upload-btn {
  border: 1px solid #d6d6d6;
  border-radius: 8px;
  padding: 12px 28px;
  background-color: #ffffff;
  color: #111827;
  cursor: pointer;
  font-size: 17px;
}

.upload-btn:hover {
  border-color: #ff5a00;
  color: #ff5a00;
}

.file-input {
  display: none;
}

.upload-desc {
  margin-top: 14px;
  color: #333333;
  font-size: 15px;
}

.upload-warning {
  margin-top: 8px;
  color: #111827;
  font-size: 15px;
  font-weight: 700;
}

.preview-box {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 620px;
  height: 330px;
  margin-top: 34px;
  background-color: #f3f3f3;
}

.preview-img {
  max-width: 300px;
  max-height: 300px;
  object-fit: cover;
}

.preview-empty {
  color: #999999;
  font-size: 18px;
}

.avatar-message {
  margin-left: 0;
  margin-top: 18px;
}

.avatar-save-btn {
  margin-top: 34px;
}

/* 收货地址 */
.address-content {
  padding: 30px 34px 42px;
}

.address-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.address-header h2 {
  margin: 0;
  color: #111827;
  font-size: 23px;
}

.address-header p {
  margin: 8px 0 0;
  color: #94a3b8;
  font-size: 14px;
}

.add-address-btn {
  border: 1px solid #d9dee7;
  border-radius: 7px;
  padding: 10px 18px;
  background-color: #ffffff;
  color: #111827;
  cursor: pointer;
  font-size: 15px;
}

.add-address-btn:hover {
  border-color: #ff5a00;
  color: #ff5a00;
}

.address-message {
  margin-bottom: 16px;
  color: #ff4d4f;
  font-size: 15px;
}

.address-message.success {
  color: #16a34a;
}

.address-state {
  border-radius: 8px;
  padding: 48px;
  background-color: #f8fafc;
  color: #94a3b8;
  text-align: center;
}

.address-table-wrap {
  width: 100%;
  overflow-x: auto;
  border: 1px solid #edf0f5;
  border-radius: 8px;
}

.address-table {
  width: 100%;
  min-width: 1000px;
  border-collapse: collapse;
  table-layout: fixed;
}

.address-table th {
  padding: 18px 16px;
  background-color: #f4f6f9;
  color: #1f2937;
  text-align: left;
  font-size: 15px;
  font-weight: 600;
}

.address-table td {
  padding: 20px 16px;
  border-top: 1px solid #edf0f5;
  color: #526581;
  vertical-align: middle;
  word-break: break-word;
  font-size: 14px;
}

.address-table th:nth-child(1),
.address-table td:nth-child(1) {
  width: 11%;
}

.address-table th:nth-child(2),
.address-table td:nth-child(2) {
  width: 13%;
}

.address-table th:nth-child(3),
.address-table td:nth-child(3) {
  width: 18%;
}

.address-table th:nth-child(4),
.address-table td:nth-child(4) {
  width: 22%;
}

.address-table th:nth-child(5),
.address-table td:nth-child(5) {
  width: 8%;
}

.address-table th:nth-child(6),
.address-table td:nth-child(6) {
  width: 18%;
}

.address-table th:nth-child(7),
.address-table td:nth-child(7) {
  width: 10%;
}

.receiver-cell {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 7px;
}

.address-tag {
  display: inline-flex;
  align-items: center;
  border-radius: 4px;
  padding: 2px 6px;
  white-space: nowrap;
  font-size: 12px;
}

.default-tag {
  background-color: #fff1e8;
  color: #ff5a00;
}

.top-tag {
  background-color: #eff6ff;
  color: #2563eb;
}

.detail-cell {
  line-height: 21px;
}

.address-actions {
  white-space: nowrap;
}

.address-actions button,
.default-action {
  margin-right: 12px;
  border: none;
  padding: 0;
  background-color: transparent;
  color: #526581;
  cursor: pointer;
  font-size: 14px;
}

.address-actions button:hover,
.default-action:hover {
  color: #ff5a00;
}

.danger-text:hover {
  color: #ef4444 !important;
}

.empty-address-row {
  padding: 44px !important;
  color: #94a3b8 !important;
  text-align: center;
}

/* 地址弹窗 */
.dialog-mask {
  position: fixed;
  inset: 0;
  z-index: 3000;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(15, 23, 42, 0.42);
}

.address-dialog {
  width: 680px;
  max-width: calc(100vw - 36px);
  max-height: calc(100vh - 36px);
  overflow-y: auto;
  border-radius: 12px;
  padding: 26px 34px 28px;
  background-color: #ffffff;
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 26px;
}

.dialog-header h2 {
  margin: 0;
  font-size: 24px;
}

.close-btn {
  border: none;
  background-color: transparent;
  color: #111827;
  cursor: pointer;
  font-size: 34px;
  line-height: 1;
}

.delivery-tip {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  padding: 14px 20px;
  background-color: #f5f5f5;
  color: #8b95a5;
  font-size: 15px;
}

.delivery-tip strong {
  color: #111827;
}

.address-form-row {
  display: flex;
  align-items: center;
  margin-bottom: 22px;
}

.address-form-row > label {
  width: 118px;
  padding-right: 12px;
  color: #4b5563;
  text-align: right;
  white-space: nowrap;
  font-size: 16px;
}

.address-form-row > label span {
  margin-right: 5px;
  color: #ff4d4f;
}

.address-form-row input,
.address-form-row textarea {
  flex: 1;
  border: 1px solid #cfd6e1;
  border-radius: 5px;
  outline: none;
  padding: 0 13px;
  color: #111827;
  font-size: 16px;
}

.address-form-row input {
  height: 46px;
}

.address-form-row textarea {
  height: 74px;
  padding-top: 12px;
  resize: vertical;
  line-height: 22px;
}

.address-form-row input:focus,
.address-form-row textarea:focus {
  border-color: #ff5a00;
}

.phone-row {
  display: flex;
  flex: 1;
  gap: 8px;
}

.phone-prefix {
  display: flex;
  align-items: center;
  width: 180px;
  height: 46px;
  border: 1px solid #cfd6e1;
  border-radius: 5px;
  padding: 0 13px;
  background-color: #fafafa;
  color: #374151;
}

.phone-row input {
  min-width: 0;
}

.default-check {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 30px 118px;
  color: #4b5563;
  font-size: 16px;
}

.default-check input {
  width: 22px;
  height: 22px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 16px;
}

.cancel-btn,
.confirm-btn {
  width: 156px;
  height: 54px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 20px;
  font-weight: 700;
}

.cancel-btn {
  border: 1px solid #d9dee7;
  background-color: #ffffff;
  color: #111827;
}

.confirm-btn {
  border: none;
  background-color: #ff5a00;
  color: #ffffff;
}

.receiver-cell {
  min-width: 120px;
  vertical-align: middle;
  white-space: nowrap;
  word-break: keep-all !important;
  overflow-wrap: normal !important;
}

.receiver-name {
  display: block;
  width: 100%;
  min-width: 100px;
  margin-bottom: 8px;
  color: #526581;
  font-size: 14px;
  line-height: 20px;

  white-space: nowrap;
  word-break: keep-all !important;
  overflow-wrap: normal !important;
}

.receiver-cell .address-tag {
  margin-left: 0;
  white-space: nowrap;
}

.address-table td:nth-child(1) {
  white-space: nowrap;
  word-break: keep-all !important;
  overflow-wrap: normal !important;
}
</style>
