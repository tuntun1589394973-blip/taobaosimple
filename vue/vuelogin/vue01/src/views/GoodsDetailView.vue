<template>
  <div class="detail-page">
    <header class="detail-header">
      <div class="brand" @click="goHome">
        <div class="brand-cn">简购</div>
        <div class="brand-en">SimpleMall</div>
      </div>

      <button class="back-btn" @click="goHome">
        返回首页
      </button>
    </header>

    <div v-if="loading" class="state-box">
      正在加载商品详情...
    </div>

    <div v-else-if="errorMessage" class="state-box error">
      {{ errorMessage }}
    </div>

    <main v-else class="detail-container">
      <!-- 左边商品图片 -->
      <section class="left-area">
        <div class="main-image">
          <img
            v-if="getGoodsImage(goods.photoUrl) && !imageError"
            :src="getGoodsImage(goods.photoUrl)"
            :alt="goods.name"
            @error="imageError = true"
          />

          <div v-else class="image-placeholder">
            <div class="placeholder-logo">简购</div>
            <div class="placeholder-text">商品图片</div>
          </div>
        </div>
      </section>

      <!-- 右边商品信息 -->
      <section class="right-area">
        <h1 class="goods-name">
          {{ goods.name }}
        </h1>

        <div class="sales-row">
          <span>已售 {{ goods.sales || 0 }}+</span>
          <span class="split">|</span>
          <span>库存 {{ goods.inventory || 0 }}</span>
        </div>

        <div class="price-card">
          <div class="price-label">平台价</div>
          <div class="price">
            ￥{{ formatPrice(goods.price) }}
          </div>
          <div class="activity">618 促销展示位</div>
        </div>

        <div class="info-row">
          <span class="label">商品分类</span>
          <span>{{ goods.typeName || getTypeName(goods.typeId) }}</span>
        </div>

        <div class="info-row">
          <span class="label">商品状态</span>
          <span>{{ goods.status === 1 ? '上架中' : '已下架' }}</span>
        </div>

        <div class="info-row">
          <span class="label">上架时间</span>
          <span>{{ formatTime(goods.createTime || goods.create_time) }}</span>
        </div>

        <div class="divider"></div>

        <div class="intro-title">商品介绍</div>
        <div class="intro-text">
          {{ goods.introduction || '暂无商品介绍' }}
        </div>

        <div class="action-bar">
          <button class="cart-btn" @click="addToCart">
  加入购物车
</button>

          <button class="buy-btn">
            立即购买
          </button>

          <button class="collect-btn">
            收藏
          </button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { isAuthenticated } from '../utils/auth'
import { apiFetch } from '../utils/http'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const imageError = ref(false)

const goods = reactive({
  id: '',
  name: '',
  price: '',
  typeId: '',
  typeName: '',
  introduction: '',
  photoUrl: '',
  inventory: 0,
  status: 1,
  sales: 0,
  deleted: 0,
  createTime: '',
  updateTime: ''
})

const CART_API_BASE = 'http://localhost:8081/api/v1/cart'

const ADD_CART_URL = `${CART_API_BASE}/addCartItem`

const addToCart = async () => {
  if (!isAuthenticated()) {
    alert('请先登录')
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return
  }

  try {
    await apiFetch(ADD_CART_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        goodsId: goods.id,
        quantity: 1
      })
    })

    alert('加入购物车成功')
  } catch (error) {
    console.error(error)
    alert(error.message || '加入购物车失败')
  }
}

const API_BASE = 'http://localhost:8081/api/v1/goods'
const SELECT_GOODS_BY_ID_URL = `${API_BASE}/selectGoodsById`

onMounted(() => {
  loadGoodsDetail()
})

const loadGoodsDetail = async () => {
  const id = route.params.id

  if (!id) {
    errorMessage.value = '缺少商品 id'
    return
  }

  loading.value = true
  errorMessage.value = ''
  imageError.value = false

  try {
    const url = `${SELECT_GOODS_BY_ID_URL}?id=${encodeURIComponent(id)}`

    console.log('商品详情请求地址：', url)

    const result = await apiFetch(url, {
      method: 'POST'
    })

    if (!result) {
      errorMessage.value = '没有查询到商品信息'
      return
    }

    Object.assign(goods, result)
  } catch (error) {
    console.error(error)
    errorMessage.value = error.message || '无法连接商品详情接口'
  } finally {
    loading.value = false
  }
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

const formatPrice = (price) => {
  if (price === null || price === undefined || price === '') {
    return '0.00'
  }

  return Number(price).toFixed(2)
}

const formatTime = (time) => {
  if (!time) {
    return '暂无'
  }

  return String(time).replace('T', ' ')
}

const getTypeName = (typeId) => {
  const typeMap = {
    1: '数码电器',
    2: '服饰鞋包',
    3: '家用电器',
    4: '图书教材',
    5: '美妆护理',
    6: '生活日用',
    7: '休闲食品',
    8: '运动户外',
    9: '家居用品',
    10: '手机配件'
  }

  return typeMap[typeId] || `分类 ${typeId || '未知'}`
}

const goHome = () => {
  router.push('/home')
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.detail-page {
  min-height: 100vh;
  background-color: #f6f7fb;
  color: #111827;
}

.detail-header {
  height: 82px;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  padding: 0 48px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
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
  font-size: 19px;
  font-weight: 700;
  margin-top: 4px;
}

.back-btn {
  margin-left: auto;
  border: 1px solid #ff5a00;
  background-color: #ffffff;
  color: #ff5a00;
  border-radius: 20px;
  padding: 8px 22px;
  cursor: pointer;
  font-size: 15px;
}

.detail-container {
  width: 1180px;
  margin: 42px auto;
  background-color: #ffffff;
  border-radius: 18px;
  padding: 34px;
  display: flex;
  gap: 42px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.06);
}

.left-area {
  width: 470px;
}

.main-image {
  width: 470px;
  height: 470px;
  background-color: #f3f4f6;
  border-radius: 16px;
  overflow: hidden;
}

.main-image img {
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
  gap: 14px;
}

.placeholder-logo {
  width: 96px;
  height: 96px;
  border-radius: 24px;
  background-color: #ff5a00;
  color: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  font-weight: 800;
}

.placeholder-text {
  font-size: 20px;
  font-weight: 700;
}

.right-area {
  flex: 1;
}

.goods-name {
  font-size: 30px;
  line-height: 42px;
  margin-bottom: 14px;
  color: #111827;
}

.sales-row {
  color: #6b7280;
  font-size: 16px;
  margin-bottom: 24px;
}

.sales-row span:first-child {
  color: #ff5a00;
}

.split {
  margin: 0 10px;
  color: #d1d5db;
}

.price-card {
  height: 112px;
  border-radius: 16px;
  background: linear-gradient(135deg, #ff3b5f, #ff2b7a);
  color: #ffffff;
  display: flex;
  align-items: center;
  padding: 0 28px;
  margin-bottom: 26px;
}

.price-label {
  font-size: 18px;
  font-weight: 700;
  margin-right: 12px;
}

.price {
  font-size: 46px;
  font-weight: 900;
  margin-right: auto;
}

.activity {
  font-size: 26px;
  font-weight: 800;
  font-style: italic;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 17px;
  color: #374151;
}

.label {
  width: 90px;
  color: #9ca3af;
}

.divider {
  height: 1px;
  background-color: #eeeeee;
  margin: 28px 0;
}

.intro-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 12px;
}

.intro-text {
  color: #4b5563;
  line-height: 28px;
  font-size: 16px;
  min-height: 88px;
}

.action-bar {
  margin-top: 36px;
  height: 64px;
  display: flex;
  gap: 0;
}

.cart-btn,
.buy-btn {
  width: 220px;
  border: none;
  color: #ffffff;
  font-size: 19px;
  font-weight: 700;
  cursor: pointer;
}

.cart-btn {
  background: linear-gradient(135deg, #8b5cf6, #9333ea);
  border-radius: 12px 0 0 12px;
}

.buy-btn {
  background: linear-gradient(135deg, #f43f5e, #ff2b7a);
  border-radius: 0 12px 12px 0;
}

.collect-btn {
  margin-left: 18px;
  width: 86px;
  border: 1px solid #e5e7eb;
  background-color: #ffffff;
  color: #374151;
  border-radius: 12px;
  cursor: pointer;
  font-size: 16px;
}

.state-box {
  width: 1180px;
  margin: 50px auto;
  background-color: #ffffff;
  border-radius: 14px;
  padding: 46px;
  text-align: center;
  color: #6b7280;
  font-size: 18px;
}

.state-box.error {
  color: #ff4d4f;
}
</style>
