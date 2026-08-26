<template>
  <el-drawer v-model="visible" title="帮助中心" direction="rtl" size="540px">
    <div class="help-top">
      <el-button size="small" plain @click="reopenOnboarding">
        <el-icon><RefreshLeft /></el-icon>&nbsp;重新查看新手引导
      </el-button>
    </div>

    <el-tabs v-model="activeTab" class="help-tabs">
      <el-tab-pane label="快速上手" name="quick">
        <div class="doc">
          <h3>5 步跑通系统</h3>
          <ol>
            <li><b>配置平台 appkey</b>：小平台(微信小店/小红书/快手)拿到即用</li>
            <li><b>添加店铺</b>：填写店铺信息并授权</li>
            <li><b>拉取商品/订单</b>：或对大平台用「订单 Excel 导入」</li>
            <li><b>打单发货</b>：电子面单出库，回传平台</li>
            <li><b>售后处理</b>：跟进退款/换货</li>
          </ol>
          <el-alert type="info" :closable="false" show-icon>
            <template #title>两条主线</template>
            ①平台订单处理(平台→发货) ②库存管理(商品→出库)，在「发货」处交汇
          </el-alert>
        </div>
      </el-tab-pane>

      <el-tab-pane label="平台接入" name="platform">
        <div class="doc">
          <h3>方式一：小平台直连</h3>
          <p>微信小店 / 小红书 / 快手：在开放平台拿到 appkey 后，进入「店铺管理」填写即可直连，自动同步商品与订单。</p>
          <h3>方式二：大平台订单导入（无需 appkey）</h3>
          <p>淘宝 / 京东 / 拼多多 / 抖店：在商家后台导出订单 Excel，进入「订单管理 - 导入」上传，半自动管理。</p>
          <h3>方式三：自用型应用（可选）</h3>
          <p>部分平台支持「店铺自用型应用」，门槛低于对外 ISV，可申请后走 API 直连。</p>
          <el-alert type="warning" :closable="false" show-icon>
            <template #title>拿不到 appkey？</template>
            大平台(淘宝/京东/拼多多/抖店)用「订单 Excel 导入」即可，不耽误使用
          </el-alert>
        </div>
      </el-tab-pane>

      <el-tab-pane label="功能模块" name="module">
        <div class="doc">
          <ul class="module-list">
            <li><b>商品库</b>：本地商品 SPU/SKU 主数据</li>
            <li><b>店铺商品</b>：平台同步/导入的店铺商品</li>
            <li><b>采购入库</b>：采购进货、增加库存</li>
            <li><b>库存管理</b>：库存查询与调整</li>
            <li><b>关联店铺商品</b>：本地 SKU ↔ 店铺商品映射（发货扣库存的桥梁）</li>
            <li><b>订单处理</b>：拉取/导入订单、打单发货</li>
            <li><b>售后</b>：退款/换货跟进</li>
          </ul>
        </div>
      </el-tab-pane>

      <el-tab-pane label="常见问题" name="faq">
        <div class="doc">
          <el-collapse>
            <el-collapse-item title="拿不到平台 appkey 怎么办？" name="1">
              <p>小平台(微信小店/小红书/快手)appkey 易获取；大平台(淘宝/京东/拼多多/抖店)用「订单 Excel 导入」，无需 appkey。</p>
            </el-collapse-item>
            <el-collapse-item title="订单 Excel 导入支持哪些格式？" name="2">
              <p>支持各平台商家后台导出的订单 Excel，导入时选择对应平台模板即可。</p>
            </el-collapse-item>
            <el-collapse-item title="如何重新查看新手引导？" name="3">
              <p>点击本页顶部「重新查看新手引导」按钮即可。</p>
            </el-collapse-item>
            <el-collapse-item title="需要多平台自动直连/技术支持？" name="4">
              <p>商业版支持多平台 API 自动同步、托管部署与技术支持，详见官网。</p>
            </el-collapse-item>
          </el-collapse>
        </div>
      </el-tab-pane>

      <el-tab-pane label="关于" name="about">
        <div class="doc about">
          <h3>启航电商ERP</h3>
          <p>开源电商 ERP · 多平台店铺统一管理</p>
          <p>官网：<a href="https://qihangerp.cn" target="_blank">qihangerp.cn</a></p>
          <el-divider />
          <h4>商业版</h4>
          <p>支持多平台 API 自动直连、托管部署、技术支持与去品牌，适合需要全自动同步与专业服务的商家。</p>
          <a href="https://qihangerp.cn" target="_blank" class="biz-link">了解商业版 →</a>
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { RefreshLeft } from '@element-plus/icons-vue'
import { useGuideStore } from '@/store/modules/guide'

const guideStore = useGuideStore()
const visible = computed({
  get: () => guideStore.helpVisible,
  set: (v: boolean) => guideStore.setHelpVisible(v),
})
const activeTab = ref('quick')

// 重新查看新手引导：清除禁用状态并弹出 Onboarding
function reopenOnboarding() {
  guideStore.enableOnboarding()
  guideStore.setOnboardingVisible(true)
  visible.value = false
}
</script>

<style lang="scss" scoped>
.help-top {
  margin-bottom: 12px;
}

.help-tabs {
  :deep(.el-tabs__content) {
    padding-right: 4px;
  }
}

.doc {
  color: #303133;
  font-size: 14px;
  line-height: 1.8;

  h3 {
    font-size: 15px;
    margin: 16px 0 8px;
    color: #303133;
  }
  h3:first-child {
    margin-top: 0;
  }
  h4 {
    font-size: 14px;
    margin: 12px 0 6px;
    color: #303133;
  }
  p {
    margin: 6px 0;
    color: #606266;
  }
  ol {
    padding-left: 20px;
    margin: 8px 0;
  }
  ol li {
    margin: 6px 0;
  }
  ul.module-list {
    padding-left: 4px;
    list-style: none;
    li {
      margin: 8px 0;
      padding-left: 12px;
      position: relative;
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 10px;
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: #409eff;
      }
    }
  }
  a {
    color: #409eff;
    text-decoration: none;
  }
  .biz-link {
    display: inline-block;
    margin-top: 8px;
    font-weight: 600;
  }
}
</style>
