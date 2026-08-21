<template>
  <div class="cart-page">
    <header class="cart-header">
      <div class="brand" @click="goHome">
        <div class="brand-cn">简购</div>
        <div class="brand-en">SimpleMall</div>
      </div>

      <div class="cart-title">购物车</div>

      <div class="header-right">
        <span v-if="loginUser" class="welcome">欢迎，{{ loginUser }}</span>
        <button class="home-btn" @click="goHome">返回首页</button>
      </div>
    </header>

    <main class="cart-container">
      <section class="cart-left">
        <div class="cart-tabs">
          <div class="tab active">
            全部商品({{ cartList.length }})
          </div>
        </div>

        <div class="cart-tools">
          <label class="check-wrap">
            <input
              type="checkbox"
              :checked="isAllChecked"
              @change="toggleAll"
            />
            <span>全选</span>
          </label>

          <button class="tool-btn" @click="deleteSelected">
            删除
          </button>

          <div class="cart-search">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索购物车内商品"
            />
          </div>
        </div>

        <div v-if="loading" class="state-box">
          正在加载购物车...
        </div>

        <div v-else-if="errorMessage" class="state-box error">
          {{ errorMessage }}
        </div>

        <div v-else-if="filteredCartList.length === 0" class="empty-cart">
          <div class="empty-icon">🛒</div>
          <div class="empty-text">购物车还是空的</div>
          <button class="go-shopping-btn" @click="goHome">
            去逛逛
          </button>
        </div>

        <div v-else class="cart-list">
          <div
            v-for="item in filteredCartList"
            :key="item.cartId"
            class="cart-item"
          >
            <div class="item-check">
              <input
                type="checkbox"
                :checked="selectedCartIds.includes(item.cartId)"
                @change="toggleOne(item)"
              />
            </div>

            <div class="item-image" @click="goGoodsDetail(item.goodsId)">
              <img
                v-if="getGoodsImage(item.photoUrl) && !item.imageError"
                :src="getGoodsImage(item.photoUrl)"
                :alt="item.goodsName"
                @error="handleImageError(item)"
              />

              <div v-else class="image-placeholder">
                <div class="placeholder-logo">简购</div>
                <div class="placeholder-text">商品图片</div>
              </div>
            </div>

            <div class="item-info" @click="goGoodsDetail(item.goodsId)">
              <div class="goods-name">
                {{ item.goodsName }}
              </div>

              <div class="goods-extra">
                库存：{{ item.inventory }}
              </div>

              <div class="goods-service">
                支持配送　极速退款　正品保障
              </div>
            </div>

            <div class="item-price">
              <div class="price-label">平台价</div>
              <div class="price">￥{{ formatPrice(item.price) }}</div>
            </div>

            <div class="quantity-box">
              <button
                class="quantity-btn"
                @click="changeQuantity(item, item.quantity - 1)"
              >
                -
              </button>

              <input
                class="quantity-input"
                type="number"
                :value="item.quantity"
                @change="inputQuantity(item, $event)"
              />

              <button
                class="quantity-btn"
                @click="changeQuantity(item, item.quantity + 1)"
              >
                +
              </button>
            </div>

            <div class="item-subtotal">
              ￥{{ formatPrice(item.price * item.quantity) }}
            </div>

            <div class="item-actions">
              <button @click="deleteCartItem(item)">
                删除
              </button>
            </div>
          </div>
        </div>
      </section>

      <aside class="cart-summary">
        <div class="summary-title">结算明细</div>

        <div class="summary-empty" v-if="checkedItems.length === 0">
          <div class="empty-small-icon">🛒</div>
          <div>选择商品查看实际支付价格</div>
        </div>

        <div v-else class="summary-list">
          <div
            v-for="item in checkedItems"
            :key="item.cartId"
            class="summary-item"
          >
            <span>{{ item.goodsName }}</span>
            <strong>￥{{ formatPrice(item.price * item.quantity) }}</strong>
          </div>
        </div>

        <div class="summary-total">
          <span>合计：</span>
          <strong>￥{{ formatPrice(totalPrice) }}</strong>
        </div>

        <button class="settle-btn" @click="settleCart">
          结算
        </button>
      </aside>
    </main>

    <div
      v-if="toastMessage"
      class="cart-toast"
      :class="{ success: toastType === 'success', error: toastType !== 'success' }"
    >
      <span class="toast-icon">
        {{ toastType === 'success' ? '✓' : '!' }}
      </span>
      <span>{{ toastMessage }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { apiFetch } from '../utils/http'

const router = useRouter()

const CART_API_BASE = 'http://localhost:8081/api/v1/cart'

const SELECT_CART_URL = `${CART_API_BASE}/selectCartItemByUserId`
const DELETE_CART_URL = `${CART_API_BASE}/deleteCartItemByGoodsById`
const UPDATE_QUANTITY_URL = `${CART_API_BASE}/updateQuantityById`

const loginUser = localStorage.getItem('loginUser')

const cartList = ref([])
const selectedCartIds = ref([])

const loading = ref(false)
const errorMessage = ref('')
const searchKeyword = ref('')

const toastMessage = ref('')
const toastType = ref('success')

onMounted(async () => {
  if (!loginUser) {
    alert('请先登录')
    router.push('/login')
    return
  }

  await loadCartList()
})

const loadCartList = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const result = await apiFetch(SELECT_CART_URL, {
      method: 'GET'
    })

    if (!Array.isArray(result)) {
      errorMessage.value = '后端返回购物车数据格式不正确'
      return
    }

    cartList.value = result.map(item => ({
      ...item,
      imageError: false,
      quantity: Number(item.quantity || 1)
    }))

    selectedCartIds.value = cartList.value
      .filter(item => Number(item.selected) === 1)
      .map(item => item.cartId)
  } catch (error) {
    console.error(error)
    errorMessage.value = error.message || '无法连接购物车查询接口'
  } finally {
    loading.value = false
  }
}

const filteredCartList = computed(() => {
  const keyword = searchKeyword.value.trim()

  if (!keyword) {
    return cartList.value
  }

  return cartList.value.filter(item => {
    return String(item.goodsName || '').includes(keyword)
  })
})

const checkedItems = computed(() => {
  return cartList.value.filter(item => selectedCartIds.value.includes(item.cartId))
})

const totalPrice = computed(() => {
  return checkedItems.value.reduce((sum, item) => {
    return sum + Number(item.price || 0) * Number(item.quantity || 1)
  }, 0)
})

const isAllChecked = computed(() => {
  if (filteredCartList.value.length === 0) {
    return false
  }

  return filteredCartList.value.every(item => {
    return selectedCartIds.value.includes(item.cartId)
  })
})

const toggleAll = () => {
  if (isAllChecked.value) {
    const visibleIds = filteredCartList.value.map(item => item.cartId)

    selectedCartIds.value = selectedCartIds.value.filter(id => {
      return !visibleIds.includes(id)
    })

    return
  }

  const idSet = new Set(selectedCartIds.value)

  filteredCartList.value.forEach(item => {
    idSet.add(item.cartId)
  })

  selectedCartIds.value = Array.from(idSet)
}

const toggleOne = (item) => {
  const index = selectedCartIds.value.indexOf(item.cartId)

  if (index >= 0) {
    selectedCartIds.value.splice(index, 1)
  } else {
    selectedCartIds.value.push(item.cartId)
  }
}

const changeQuantity = async (item, quantity) => {
  let newQuantity = Number(quantity)

  if (!newQuantity || newQuantity <= 0) {
    newQuantity = 1
  }

  if (item.inventory !== null && item.inventory !== undefined) {
    if (newQuantity > Number(item.inventory)) {
      newQuantity = Number(item.inventory)
    }
  }

  try {
    const url =
      `${UPDATE_QUANTITY_URL}?goodsId=${encodeURIComponent(item.goodsId)}` +
      `&quantity=${encodeURIComponent(newQuantity)}`

    await apiFetch(url, {
      method: 'POST'
    })

    item.quantity = newQuantity
  } catch (error) {
    console.error(error)
    showToast(error.message || '修改数量失败', 'error')
  }
}

const inputQuantity = (item, event) => {
  changeQuantity(item, Number(event.target.value))
}

const deleteCartItem = async (item) => {
  const confirmed = window.confirm(`确认删除“${item.goodsName}”吗？`)

  if (!confirmed) {
    return
  }

  try {
    const url =
      `${DELETE_CART_URL}?goodsId=${encodeURIComponent(item.goodsId)}`

    await apiFetch(url, {
      method: 'POST'
    })

    await loadCartList()
    showToast('删除成功', 'success')
  } catch (error) {
    console.error(error)
    showToast(error.message || '删除失败', 'error')
  }
}

const deleteSelected = async () => {
  if (selectedCartIds.value.length === 0) {
    showToast('请先选择商品', 'error')
    return
  }

  const confirmed = window.confirm('确认删除选中的商品吗？')

  if (!confirmed) {
    return
  }

  const selectedItems = cartList.value.filter(item => {
    return selectedCartIds.value.includes(item.cartId)
  })

  let lastError = null

  for (const item of selectedItems) {
    try {
      const url =
        `${DELETE_CART_URL}?goodsId=${encodeURIComponent(item.goodsId)}`

      await apiFetch(url, {
        method: 'POST'
      })
    } catch (error) {
      console.error(error)
      lastError = error
    }
  }

  await loadCartList()

  if (lastError) {
    showToast(lastError.message || '部分商品删除失败', 'error')
  } else {
    showToast('删除成功', 'success')
  }
}

const settleCart = () => {
  if (checkedItems.value.length === 0) {
    showToast('请先选择要结算的商品', 'error')
    return
  }

  showToast('结算功能后续接入订单模块', 'success')
}

const getGoodsImage = (photoUrl) => {
  if (!photoUrl) {
    return ''
  }

  if (photoUrl.startsWith('http')) {
    return photoUrl
  }

  return `http://localhost:8081${photoUrl}`
}

const handleImageError = (item) => {
  item.imageError = true
}

const formatPrice = (price) => {
  if (price === null || price === undefined || price === '') {
    return '0.00'
  }

  return Number(price).toFixed(2)
}

const showToast = (text, type = 'success') => {
  toastMessage.value = text
  toastType.value = type

  window.setTimeout(() => {
    if (toastMessage.value === text) {
      toastMessage.value = ''
    }
  }, 1600)
}

const goHome = () => {
  router.push('/home')
}

const goGoodsDetail = (goodsId) => {
  router.push(`/goods/${goodsId}`)
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.cart-page {
  min-height: 100vh;
  background-color: #ffffff;
  color: #111827;
}

.cart-header {
  height: 82px;
  display: flex;
  align-items: center;
  padding: 0 56px;
  border-bottom: 1px solid #f1f1f1;
}

.brand {
  width: 130px;
  color: #ff5a00;
  line-height: 1;
  cursor: pointer;
}

.brand-cn {
  font-size: 34px;
  font-weight: 900;
  letter-spacing: 2px;
}

.brand-en {
  font-size: 18px;
  font-weight: 700;
  margin-top: 3px;
}

.cart-title {
  margin-left: 18px;
  color: #ff5a00;
  font-size: 28px;
  font-weight: 800;
}

.header-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 18px;
}

.welcome {
  color: #374151;
  font-size: 15px;
}

.home-btn {
  border: 1px solid #ff5a00;
  background-color: #ffffff;
  color: #ff5a00;
  border-radius: 20px;
  padding: 8px 20px;
  cursor: pointer;
}

.cart-container {
  display: flex;
  gap: 36px;
  width: 1400px;
  margin: 34px auto 0;
  align-items: flex-start;
}

.cart-left {
  flex: 1;
}

.cart-tabs {
  display: flex;
  align-items: center;
  height: 50px;
  border-bottom: 1px solid #eeeeee;
}

.tab {
  height: 50px;
  padding: 0 4px;
  margin-right: 34px;
  color: #111827;
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
}

.tab.active {
  color: #ff5a00;
  border-bottom: 2px solid #ff5a00;
}

.cart-tools {
  height: 74px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.check-wrap {
  display: flex;
  align-items: center;
  gap: 9px;
  color: #374151;
  font-size: 15px;
}

.check-wrap input,
.item-check input {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.tool-btn {
  height: 36px;
  border: 1px solid #e5e7eb;
  background-color: #ffffff;
  color: #374151;
  border-radius: 8px;
  padding: 0 18px;
  cursor: pointer;
  font-size: 14px;
}

.tool-btn:hover {
  color: #ff5a00;
  border-color: #ff5a00;
}

.cart-search {
  margin-left: auto;
  width: 260px;
  height: 38px;
  border: 1px solid #ff5a00;
  border-radius: 8px;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.cart-search input {
  width: 100%;
  height: 100%;
  border: none;
  outline: none;
  padding: 0 14px;
  font-size: 14px;
}

.state-box,
.empty-cart {
  margin-top: 24px;
  padding: 80px 0;
  border-radius: 12px;
  text-align: center;
  background-color: #fafafa;
  color: #6b7280;
  font-size: 18px;
}

.state-box.error {
  color: #ff4d4f;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 14px;
}

.empty-text {
  margin-bottom: 20px;
}

.go-shopping-btn {
  border: none;
  background-color: #ff5a00;
  color: #ffffff;
  border-radius: 22px;
  padding: 10px 28px;
  font-size: 16px;
  cursor: pointer;
}

.cart-list {
  border-top: 1px solid #eeeeee;
}

.cart-item {
  min-height: 178px;
  display: grid;
  grid-template-columns: 42px 120px 1fr 140px 150px 130px 90px;
  gap: 18px;
  align-items: center;
  padding: 30px 0;
  border-bottom: 1px solid #eeeeee;
}

.item-check {
  display: flex;
  justify-content: center;
}

.item-image {
  width: 110px;
  height: 110px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #f5f5f5;
  cursor: pointer;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #fff2e8, #ffe0cc);
  color: #ff5a00;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.placeholder-logo {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background-color: #ff5a00;
  color: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 800;
}

.placeholder-text {
  font-size: 12px;
}

.item-info {
  cursor: pointer;
}

.goods-name {
  color: #111827;
  font-size: 17px;
  line-height: 25px;
  font-weight: 600;
  margin-bottom: 12px;
}

.goods-name:hover {
  color: #ff5a00;
}

.goods-extra {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 10px;
}

.goods-service {
  color: #ff5a00;
  font-size: 14px;
}

.item-price {
  color: #374151;
}

.price-label {
  color: #ff5a00;
  font-size: 14px;
  margin-bottom: 4px;
}

.price {
  color: #ff5a00;
  font-size: 18px;
  font-weight: 800;
}

.quantity-box {
  width: 130px;
  height: 38px;
  display: flex;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.quantity-btn {
  width: 38px;
  border: none;
  background-color: #ffffff;
  color: #374151;
  font-size: 20px;
  cursor: pointer;
}

.quantity-btn:hover {
  color: #ff5a00;
  background-color: #fff7f0;
}

.quantity-input {
  width: 54px;
  border: none;
  border-left: 1px solid #e5e7eb;
  border-right: 1px solid #e5e7eb;
  text-align: center;
  outline: none;
  font-size: 16px;
}

.item-subtotal {
  color: #ff5a00;
  font-size: 18px;
  font-weight: 800;
}

.item-actions button {
  display: block;
  border: none;
  background-color: transparent;
  color: #6b7280;
  margin-bottom: 8px;
  cursor: pointer;
  font-size: 14px;
}

.item-actions button:hover {
  color: #ff5a00;
}

.cart-summary {
  width: 340px;
  border: 1px solid #eeeeee;
  border-radius: 18px;
  padding: 22px;
  position: sticky;
  top: 24px;
  background-color: #ffffff;
}

.summary-title {
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 24px;
}

.summary-empty {
  height: 150px;
  color: #ff5a00;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 14px;
  font-weight: 700;
}

.empty-small-icon {
  font-size: 52px;
  opacity: 0.35;
}

.summary-list {
  max-height: 220px;
  overflow-y: auto;
  margin-bottom: 20px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  color: #6b7280;
  font-size: 14px;
}

.summary-item span {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.summary-item strong {
  color: #ff5a00;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 26px 0;
  font-size: 18px;
  font-weight: 800;
}

.summary-total strong {
  color: #ff5a00;
  font-size: 24px;
}

.settle-btn {
  width: 100%;
  height: 58px;
  border: none;
  border-radius: 12px;
  background-color: #ff5a00;
  color: #ffffff;
  font-size: 22px;
  font-weight: 800;
  cursor: pointer;
}

.settle-btn:hover {
  opacity: 0.9;
}

.cart-toast {
  position: fixed;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 5000;
  min-width: 170px;
  height: 60px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.16);
  color: #333333;
  font-size: 18px;
}

.toast-icon {
  width: 23px;
  height: 23px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 15px;
  font-weight: 800;
}

.cart-toast.success .toast-icon {
  background-color: #52c41a;
}

.cart-toast.error .toast-icon {
  background-color: #ff4d4f;
}
</style>
