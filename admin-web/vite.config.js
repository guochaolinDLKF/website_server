import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 运营后台前端构建配置
// - 开发态(serve)：base 为 '/'，通过 proxy 将 /api 转发到后端（默认 8660），保留 HMR。
// - 生产构建(build)：默认 base 为 '/'，产物可直接由 nginx 在站点根(/)下托管。
// - jar 单体部署(build:jar)：通过 npm run build:jar 把 base 临时改为 '/admin/'，
//   产物由后端 Spring Boot 在 /admin/ 下托管（单 jar 部署）。
export default defineConfig(({ command }) => ({
  base: '/',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    port: 9660,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8660',
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: 'dist',
    chunkSizeWarningLimit: 1500
  }
}))
