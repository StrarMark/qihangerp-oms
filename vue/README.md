> **注意：此目录（`vue/`）为旧版前端（Vue CLI 4 + Webpack 4 + Vue 2.6.12），已过时。请使用 `vue2/` 目录的升级版前端（Vue CLI 5 + Webpack 5 + Vue 2.7.16）。**

## 开发

```bash
# 克隆项目
# nodejs v20

# 进入项目目录
cd vue

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npmmirror.com

# 启动服务
npm run dev
```

浏览器访问 http://localhost:80

## 发布

```bash
# 构建测试环境
npm run build:stage

# 构建生产环境
npm run build:prod
```