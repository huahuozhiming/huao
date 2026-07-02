module.exports = {
  devServer: {
    port: 8081,          // 前端开发服务器端口（可自定义）
    proxy: {
      '/api': {          // 拦截以 /api 开头的请求
        target: 'http://localhost:8080',   // 后端实际地址
        changeOrigin: true,
        pathRewrite: { '^/api': '' }       // 去掉 /api 前缀
      }
    }
  }
}