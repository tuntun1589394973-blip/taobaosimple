/**
 * 统一的 HTTP 请求封装
 * - 自动注入 Authorization: Bearer <token>
 * - 自动识别后端 RestResp 响应结构，成功返回 data
 * - 业务失败抛 Error（message/code）
 * - HTTP 401 清除登录态并跳转登录页
 */
import { clearAuth, getToken } from './auth'

/**
 * 判断响应体是否为后端 RestResp 格式
 * @param {any} payload 响应体
 * @returns {boolean}
 */
const isRestResp = (payload) =>
  payload && typeof payload === 'object' && 'code' in payload && 'message' in payload

/**
 * 构造带错误码的 Error 对象
 * @param {string} message 错误消息
 * @param {string} code 错误码
 * @returns {Error}
 */
const buildError = (message, code) => {
  const err = new Error(message || '请求失败')
  err.code = code
  return err
}

/**
 * 统一请求入口
 * @param {string|Request} input URL 或 Request 对象
 * @param {object} init fetch 配置
 * @returns {Promise<any>} 成功时返回 data 字段
 * @throws {Error} 业务失败时抛出，message 为后端提示
 */
export const apiFetch = async (input, init = {}) => {
  const headers = new Headers(init.headers || {})
  const token = getToken()

  // 自动注入 token
  if (token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(input, {
    ...init,
    headers
  })

  // 处理 401 未登录
  if (response.status === 401) {
    clearAuth()

    const currentPath = window.location.pathname

    if (currentPath !== '/login' && currentPath !== '/register') {
      const redirect = encodeURIComponent(
        `${window.location.pathname}${window.location.search}`
      )

      window.location.assign(`/login?redirect=${redirect}`)
    }

    throw buildError('未登录或登录已过期', '4010')
  }

  // 解析响应体
  const text = await response.text()
  let payload = null

  if (text) {
    try {
      payload = JSON.parse(text)
    } catch (e) {
      payload = null
    }
  }

  // HTTP 状态非 2xx
  if (!response.ok) {
    throw buildError(
      (isRestResp(payload) && payload.message) || `请求失败，状态码：${response.status}`,
      isRestResp(payload) ? payload.code : String(response.status)
    )
  }

  // 非 RestResp 格式直接返回（兼容裸数据接口）
  if (!isRestResp(payload)) {
    return payload
  }

  // RestResp 格式：code=0 代表成功，返回 data
  if (payload.code === '0' || payload.code === 0) {
    return payload.data
  }

  // 业务失败，抛错
  throw buildError(payload.message, payload.code)
}
