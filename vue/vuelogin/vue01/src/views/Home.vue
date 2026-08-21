<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header class="home-header">
      <div class="brand">
        <div class="brand-cn">简购</div>
        <div class="brand-en">SimpleMall</div>
      </div>

      <nav class="nav">
        <span class="nav-item active" @click="clearSearch">
          首页
        </span>

        <span class="nav-item">
          商品
        </span>

        <!-- 分类悬浮菜单 -->
        <div
          class="category-nav"
          @mouseenter="categoryPanelVisible = true"
          @mouseleave="categoryPanelVisible = false"
        >
          <span class="nav-item">
            分类
          </span>

          <div
            v-if="categoryPanelVisible"
            class="category-panel"
          >
            <div class="category-panel-title">
              商品分类
            </div>

            <div class="category-list">
              <div
                v-for="type in goodsTypes"
                :key="type.id"
                class="category-item"
                @click="selectTypeGoods(type)"
              >
                {{ type.typeName || type.type_name }}
              </div>
            </div>
          </div>
        </div>

        <span class="nav-item" @click="goCart">购物车</span>
      </nav>

      <div class="user-area">
        <!-- 已登录 -->
        <span
          v-if="loginUser"
          class="username"
          @click="goProfile"
        >
          欢迎，{{ loginUser }}
        </span>

        <!-- 未登录 -->
        <span
          v-else
          class="username"
          @click="goLogin"
        >
          登录
        </span>

        <button
          v-if="loginUser"
          class="logout-btn"
          @click="logout"
        >
          退出
        </button>
      </div>
    </header>

    <!-- 搜索区域 -->
    <section class="hero">
      <div class="hero-content">
        <h1>发现你想要的商品</h1>
        <p>输入商品名称进行搜索，例如：游戏鼠标、机械键盘、篮球</p>

        <div class="search-box">
          <input
            v-model="keyword"
            class="search-input"
            type="text"
            placeholder="请输入商品名称"
            @keyup.enter="searchGoods"
          />

          <button class="search-btn" @click="searchGoods">
            搜索
          </button>
        </div>
      </div>
    </section>

    <!-- 商品区域 -->
    <section class="goods-section">
      <div class="section-header">
        <div>
          <h2>{{ sectionTitle }}</h2>

          <p v-if="currentMode === 'search'">
            共找到 {{ goodsList.length }} 件相关商品
          </p>

          <p v-else-if="currentMode === 'type'">
            当前分类：{{ selectedTypeName }}，已加载 {{ goodsList.length }} 件商品
          </p>

          <p v-else>
            为你随机推荐商品，下滑可以继续加载
          </p>
        </div>

        <button
          v-if="currentMode !== 'random'"
          class="clear-btn"
          @click="clearSearch"
        >
          返回推荐
        </button>
      </div>

      <div v-if="loading" class="state-box">
        正在搜索商品，请稍候...
      </div>

      <div v-else-if="errorMessage" class="state-box error">
        {{ errorMessage }}
      </div>

      <div
        v-else-if="currentMode === 'search' && goodsList.length === 0"
        class="state-box"
      >
        没有找到相关商品，可以换个关键词试试。
      </div>

      <div
        v-else-if="goodsList.length === 0"
        class="state-box"
      >
        暂无商品数据
      </div>

      <div v-else class="goods-grid">
        <div
          v-for="item in goodsList"
          :key="item.id"
          class="goods-card"
          @click="goGoodsDetail(item.id)"
        >
          <div class="goods-image">
            <img
              v-if="getGoodsImage(item.photoUrl) && !item.imageError"
              :src="getGoodsImage(item.photoUrl)"
              :alt="item.name"
              @error="handleImageError(item)"
            />

            <div v-else class="goods-placeholder">
              <div class="placeholder-icon">简购</div>
              <div class="placeholder-text">商品图片</div>
            </div>
          </div>

          <div class="goods-info">
            <div class="goods-title">
              {{ item.name }}
            </div>

            <div class="goods-intro">
              {{ item.introduction }}
            </div>

            <div class="goods-meta">
              <span class="type-tag">
                {{ item.typeName || getTypeName(item.typeId) }}
              </span>

              <span class="inventory">
                库存：{{ item.inventory }}
              </span>
            </div>

            <div class="goods-bottom">
              <div class="price">
                ￥{{ formatPrice(item.price) }}
              </div>

              <button class="buy-btn" @click.stop="addToCart(item)">
  加入购物车
</button>
            </div>

            <div class="create-time">
              上架时间：{{ item.createTime || item.create_time || '暂无' }}
            </div>
          </div>
        </div>
      </div>

      <!-- 注意：这两个提示必须放在 goods-grid 外面 -->
      <div v-if="loadingMore" class="bottom-tip">
        正在加载更多商品...
      </div>

      <div v-if="finished && goodsList.length > 0" class="bottom-tip">
        已经到底了
      </div>
    </section>

    <!-- 左下角回到顶部 -->
    <button
      v-if="showBackTop"
      class="back-top-btn"
      @click="backToTop"
    >
      ↑
    </button>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { clearAuth, isAuthenticated } from '../utils/auth'
import { apiFetch } from '../utils/http'

const router = useRouter()

const goCart = () => {
  router.push('/cart')
}

const loginUser = ref(localStorage.getItem('loginUser'))

const keyword = ref('')
const goodsList = ref([])
const loadedIds = ref([])

const goodsTypes = ref([])
const categoryPanelVisible = ref(false)

const loading = ref(false)
const loadingMore = ref(false)
const finished = ref(false)
const showBackTop = ref(false)
const errorMessage = ref('')

// random: 随机推荐
// search: 搜索结果
// type: 分类商品
const currentMode = ref('random')

const selectedTypeId = ref(null)
const selectedTypeName = ref('')
const typePage = ref(1)

const API_BASE = 'http://localhost:8081/api/v1/goods'
const TYPE_API_BASE = 'http://localhost:8081/api/v1/goods-types'

const SEARCH_GOODS_URL = `${API_BASE}/selectGoodsByName`
const RANDOM_GOODS_URL = `${API_BASE}/selectRandomGoods`
const TYPE_GOODS_URL = `${API_BASE}/selectGoodsByTypeId`
const GOODS_TYPE_URL = `${TYPE_API_BASE}/getgoodstype`

const PAGE_SIZE = 8

const CART_API_BASE = 'http://localhost:8081/api/v1/cart'

const ADD_CART_URL = `${CART_API_BASE}/addCartItem`

const addToCart = async (item) => {
  if (!isAuthenticated()) {
    alert('请先登录')
    router.push({
      path: '/login',
      query: { redirect: '/home' }
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
        goodsId: item.id,
        quantity: 1
      })
    })

    alert('加入购物车成功')
  } catch (error) {
    console.error(error)
    alert(error.message || '加入购物车失败')
  }
}
const sectionTitle = computed(() => {
  if (currentMode.value === 'search') {
    return '搜索结果'
  }

  if (currentMode.value === 'type') {
    return '分类商品'
  }

  return '商品推荐'
})

onMounted(() => {
  loadGoodsTypes()
  loadRandomGoods(true)
  window.addEventListener('scroll', handleScroll)
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})

const goLogin = () => {
  router.push('/login')
}

const goProfile = () => {
  router.push('/profile')
}

const goGoodsDetail = (id) => {
  router.push(`/goods/${id}`)
}

const logout = () => {
  clearAuth()
  loginUser.value = null
}

// 查询所有商品分类
const loadGoodsTypes = async () => {
  try {
    const result = await apiFetch(GOODS_TYPE_URL, {
      method: 'POST'
    })

    if (Array.isArray(result)) {
      goodsTypes.value = result
    }
  } catch (error) {
    console.error(error)
  }
}

// 鼠标点击分类
const selectTypeGoods = (type) => {
  const typeId = type.id
  const typeName = type.typeName || type.type_name || '未知分类'

  categoryPanelVisible.value = false
  selectedTypeId.value = typeId
  selectedTypeName.value = typeName

  keyword.value = ''
  currentMode.value = 'type'
  typePage.value = 1
  finished.value = false
  errorMessage.value = ''
  goodsList.value = []

  loadGoodsByType(true)
}

// 按分类分页查询商品
const loadGoodsByType = async (reset = false) => {
  if (!selectedTypeId.value) {
    return
  }

  if (reset) {
    loading.value = true
    typePage.value = 1
    goodsList.value = []
    finished.value = false
  } else {
    loadingMore.value = true
  }

  errorMessage.value = ''

  try {
    const url =
      `${TYPE_GOODS_URL}?typeId=${encodeURIComponent(selectedTypeId.value)}` +
      `&page=${typePage.value}` +
      `&limit=${PAGE_SIZE}`

    console.log('分类商品请求地址：', url)

    const result = await apiFetch(url, {
      method: 'POST'
    })

    if (!Array.isArray(result)) {
      errorMessage.value = '后端返回数据格式不是数组'
      return
    }

    const newGoods = result.map(item => ({
      ...item,
      imageError: false
    }))

    goodsList.value.push(...newGoods)

    if (newGoods.length < PAGE_SIZE) {
      finished.value = true
    } else {
      typePage.value++
    }
  } catch (error) {
    console.error(error)
    errorMessage.value = error.message || '无法连接分类商品接口'
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 随机商品查询，继续沿用你之前的 ids 去重逻辑
const loadRandomGoods = async (reset = false) => {
  if (reset) {
    loading.value = true
    goodsList.value = []
    loadedIds.value = []
    finished.value = false
    currentMode.value = 'random'
  } else {
    loadingMore.value = true
  }

  errorMessage.value = ''

  try {
    const params = new URLSearchParams()
    params.append('limit', PAGE_SIZE)

    loadedIds.value.forEach(id => {
      params.append('ids', id)
    })

    const url = `${RANDOM_GOODS_URL}?${params.toString()}`

    console.log('随机商品请求地址：', url)

    const result = await apiFetch(url, {
      method: 'POST'
    })

    if (!Array.isArray(result)) {
      errorMessage.value = '后端返回数据格式不是数组'
      return
    }

    const newGoods = result.map(item => ({
      ...item,
      imageError: false
    }))

    goodsList.value.push(...newGoods)

    newGoods.forEach(item => {
      if (item.id !== undefined && item.id !== null) {
        loadedIds.value.push(item.id)
      }
    })

    if (newGoods.length < PAGE_SIZE) {
      finished.value = true
    }
  } catch (error) {
    console.error(error)
    errorMessage.value = error.message || '无法连接随机商品查询接口'
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 搜索商品
const searchGoods = async () => {
  const name = keyword.value.trim()

  if (!name) {
    errorMessage.value = '请输入商品名称'
    return
  }

  loading.value = true
  currentMode.value = 'search'
  finished.value = false
  errorMessage.value = ''
  goodsList.value = []
  loadedIds.value = []

  try {
    const url = `${SEARCH_GOODS_URL}?name=${encodeURIComponent(name)}`

    console.log('商品搜索请求地址：', url)

    const result = await apiFetch(url, {
      method: 'POST'
    })

    if (Array.isArray(result)) {
      goodsList.value = result.map(item => ({
        ...item,
        imageError: false
      }))

      finished.value = true
    } else {
      goodsList.value = []
      errorMessage.value = '后端返回数据格式不是数组'
    }
  } catch (error) {
    console.error(error)
    errorMessage.value = error.message || '无法连接商品查询接口'
  } finally {
    loading.value = false
  }
}

// 返回随机推荐
const clearSearch = () => {
  keyword.value = ''
  errorMessage.value = ''
  selectedTypeId.value = null
  selectedTypeName.value = ''
  typePage.value = 1
  loadRandomGoods(true)
}

// 滚动加载
const handleScroll = () => {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight

  showBackTop.value = scrollTop > windowHeight

  if (finished.value || loading.value || loadingMore.value) {
    return
  }

  if (scrollTop + windowHeight >= documentHeight - 120) {
    if (currentMode.value === 'random') {
      loadRandomGoods(false)
    }

    if (currentMode.value === 'type') {
      loadGoodsByType(false)
    }
  }
}

const backToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

const formatPrice = (price) => {
  if (price === null || price === undefined) {
    return '0.00'
  }

  return Number(price).toFixed(2)
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

  return typeMap[typeId] || `分类 ${typeId}`
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
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.home-page {
  min-height: 100vh;
  background-color: #f6f7fb;
  color: #111827;
}

.home-header {
  height: 82px;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  padding: 0 46px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.brand {
  width: 150px;
  color: #ff5a00;
  line-height: 1;
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

.nav {
  display: flex;
  align-items: center;
  gap: 34px;
  margin-left: 40px;
  flex: 1;
}

.nav-item {
  font-size: 17px;
  color: #374151;
  cursor: pointer;
}

.nav-item.active {
  color: #ff5a00;
  font-weight: 700;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.username {
  color: #374151;
  font-size: 15px;
  cursor: pointer;
}

.username:hover {
  color: #ff5a00;
}

.logout-btn {
  border: 1px solid #ff5a00;
  background-color: #ffffff;
  color: #ff5a00;
  border-radius: 18px;
  padding: 7px 18px;
  cursor: pointer;
}

.hero {
  width: 100%;
  padding: 58px 0 46px;
  background: linear-gradient(135deg, #fff2e8, #ffffff);
}

.hero-content {
  width: 980px;
  margin: 0 auto;
  text-align: center;
}

.hero-content h1 {
  font-size: 38px;
  color: #111827;
  margin-bottom: 14px;
}

.hero-content p {
  font-size: 17px;
  color: #6b7280;
  margin-bottom: 34px;
}

.search-box {
  width: 720px;
  height: 58px;
  margin: 0 auto;
  display: flex;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 28px rgba(255, 90, 0, 0.13);
  overflow: hidden;
}

.search-input {
  flex: 1;
  height: 100%;
  border: none;
  outline: none;
  padding: 0 24px;
  font-size: 18px;
  color: #111827;
}

.search-input::placeholder {
  color: #9ca3af;
}

.search-btn {
  width: 150px;
  height: 100%;
  border: none;
  background-color: #ff5a00;
  color: #ffffff;
  font-size: 19px;
  font-weight: 700;
  cursor: pointer;
}

.goods-section {
  width: 1180px;
  margin: 36px auto 0;
  padding-bottom: 60px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 26px;
}

.section-header h2 {
  font-size: 26px;
  margin-bottom: 6px;
}

.section-header p {
  color: #6b7280;
  font-size: 15px;
}

.clear-btn {
  border: none;
  background-color: #ffffff;
  color: #ff5a00;
  border-radius: 8px;
  padding: 10px 18px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
}

.state-box {
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



.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.goods-card {
  background-color: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06);
  transition: 0.2s;
}

.goods-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 26px rgba(0, 0, 0, 0.1);
}

.goods-image {
  width: 100%;
  height: 190px;
  background-color: #f3f4f6;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #9ca3af;
  font-size: 16px;
}

.goods-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  padding: 18px;
}

.goods-title {
  font-size: 19px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 10px;
}

.goods-intro {
  height: 44px;
  color: #6b7280;
  font-size: 14px;
  line-height: 22px;
  overflow: hidden;
}

.goods-meta {
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.type-tag {
  background-color: #fff2e8;
  color: #ff5a00;
  border-radius: 999px;
  padding: 5px 10px;
  font-size: 13px;
}

.inventory {
  color: #6b7280;
  font-size: 13px;
}

.goods-bottom {
  margin-top: 18px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ff3b00;
  font-size: 24px;
  font-weight: 800;
}

.buy-btn {
  border: none;
  background-color: #ff5a00;
  color: #ffffff;
  border-radius: 8px;
  padding: 9px 14px;
  cursor: pointer;
}

.create-time {
  margin-top: 12px;
  color: #9ca3af;
  font-size: 12px;
}

.goods-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #fff2e8, #ffe0cc);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #ff5a00;
}

.placeholder-icon {
  width: 68px;
  height: 68px;
  border-radius: 18px;
  background-color: #ff5a00;
  color: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  margin-bottom: 12px;
  font-weight: 700;
}

.placeholder-text {
  font-size: 15px;
  color: #ff5a00;
}

.bottom-tip {
  text-align: center;
  color: #9ca3af;
  font-size: 16px;
  padding: 28px 0 10px;
}

.back-top-btn {
  position: fixed;
  left: 34px;
  bottom: 34px;
  width: 52px;
  height: 52px;
  border: none;
  border-radius: 50%;
  background-color: #ff5a00;
  color: #ffffff;
  font-size: 30px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(255, 90, 0, 0.28);
  z-index: 999;
}

.back-top-btn:hover {
  opacity: 0.88;
}
.category-nav {
  position: relative;
  height: 82px;
  display: flex;
  align-items: center;
}

.category-nav::after {
  content: "";
  position: absolute;
  left: -30px;
  top: 40px;
  width: 140px;
  height: 40px;
}

.category-panel {
  position: absolute;
  top: 60px;
  left: -220px;
  width: 860px;
  min-height: 230px;
  background-color: #ffffff;
  border: 2px solid #ff5a00;
  border-radius: 14px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
  z-index: 999;
  padding: 24px 28px;
}

.category-panel-title {
  font-size: 22px;
  font-weight: 800;
  color: #ff5a00;
  margin-bottom: 20px;
}

.category-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px 24px;
}

.category-item {
  height: 44px;
  border-radius: 10px;
  background-color: #fff7f0;
  color: #374151;
  display: flex;
  align-items: center;
  padding: 0 16px;
  font-size: 16px;
  cursor: pointer;
}

.category-item:hover {
  background-color: #ff5a00;
  color: #ffffff;
}

.bottom-tip {
  text-align: center;
  color: #9ca3af;
  font-size: 16px;
  padding: 28px 0 10px;
}

.back-top-btn {
  position: fixed;
  left: 34px;
  bottom: 34px;
  width: 52px;
  height: 52px;
  border: none;
  border-radius: 50%;
  background-color: #ff5a00;
  color: #ffffff;
  font-size: 30px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(255, 90, 0, 0.28);
  z-index: 999;
}

.back-top-btn:hover {
  opacity: 0.88;
}
</style>
