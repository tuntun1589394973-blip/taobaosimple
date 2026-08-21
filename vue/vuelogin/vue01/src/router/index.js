import { createRouter, createWebHistory } from 'vue-router'

import CartView from '../views/CartView.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import UserProfile from '../views/UserProfileView.vue'
import GoodsDetail from '../views/GoodsDetailView.vue'
import { isAuthenticated } from '../utils/auth'

const routes = [
  {
  path: '/goods/:id',
  component: GoodsDetail
},
  
{
  path: '/cart',
  component: CartView,
  meta: { requiresAuth: true }
},
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/register',
    component: Register
  },
  {
    path: '/home',
    component: Home
  },
   {
    path: '/profile',
    component: UserProfile,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !isAuthenticated()) {
    return {
      path: '/login',
      query: { redirect: to.fullPath }
    }
  }

  if (to.path === '/login' && isAuthenticated()) {
    return '/home'
  }

  return true
})

export default router
