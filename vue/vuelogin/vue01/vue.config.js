const { defineConfig } = require('@vue/cli-service')

/**
 * Vue CLI 配置
 * - devServer 代理将 /api 请求转发到后端 8081 端口
 */
module.exports = defineConfig({
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
