/**
 * 登录态管理（localStorage）
 */

// token 在 localStorage 中的 key
const TOKEN_KEY = 'authToken'
// 用户名在 localStorage 中的 key
const USERNAME_KEY = 'loginUser'

/**
 * 解码 JWT 的 payload 部分（Base64URL → JSON）
 * @param {string} token JWT 字符串
 * @returns {object|null} payload 对象
 */
const decodeTokenPayload = (token) => {
  try {
    const payload = token.split('.')[1]

    if (!payload) {
      return null
    }

    // Base64URL 转 Base64
    const normalized = payload
      .replace(/-/g, '+')
      .replace(/_/g, '/')
      .padEnd(Math.ceil(payload.length / 4) * 4, '=')

    return JSON.parse(window.atob(normalized))
  } catch (error) {
    return null
  }
}

/**
 * 清除所有登录态
 */
export const clearAuth = () => {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem('loginUserId')
  localStorage.removeItem(USERNAME_KEY)
}

/**
 * 获取有效 token（过期或无效时自动清除并返回空串）
 * @returns {string}
 */
export const getToken = () => {
  const token = localStorage.getItem(TOKEN_KEY)

  if (!token) {
    return ''
  }

  const payload = decodeTokenPayload(token)

  // 过期则清除登录态
  if (!payload || (payload.exp && payload.exp * 1000 <= Date.now())) {
    clearAuth()
    return ''
  }

  return token
}

/**
 * 保存登录态
 * @param {object} param0 登录响应
 * @param {string} param0.token JWT
 * @param {string} param0.username 用户名
 */
export const setAuth = ({ token, username }) => {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.removeItem('loginUserId')
  localStorage.setItem(USERNAME_KEY, username)
}

/**
 * 判断当前是否已登录
 * @returns {boolean}
 */
export const isAuthenticated = () => Boolean(getToken())
