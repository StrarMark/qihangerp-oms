<template>
  <el-dialog
    v-model="visible"
    title="新手引导"
    width="840px"
    align-center
    :close-on-click-modal="false"
    class="onboarding-dialog"
    @close="handleClose"
  >
    <el-carousel
      ref="carouselRef"
      height="460px"
      :autoplay="false"
      trigger="click"
      indicator-position="outside"
      @change="onChange"
    >
      <el-carousel-item v-for="(step, i) in steps" :key="i">
        <div class="step">
          <div class="step-icon">
            <el-icon :size="44"><component :is="step.icon" /></el-icon>
          </div>
          <h2 class="step-title">{{ step.title }}</h2>
          <p v-if="step.desc" class="step-desc">{{ step.desc }}</p>
          <div class="step-body" v-html="step.body"></div>
          <div v-if="i === 3" class="step-actions">
            <el-button type="primary" @click="openHelp">打开在线帮助</el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <template #footer>
      <div class="onboarding-footer">
        <el-checkbox v-model="dontShowAgain">下次登录不再自动显示</el-checkbox>
        <div class="footer-actions">
          <el-button v-if="current > 0" @click="prev">上一步</el-button>
          <el-button v-if="current < steps.length - 1" type="primary" @click="next">下一步</el-button>
          <el-button v-else type="primary" @click="finish">开始使用</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { MagicStick, DataAnalysis, Connection, QuestionFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useGuideStore } from '@/store/modules/guide'

const guideStore = useGuideStore()
const router = useRouter()

const visible = computed({
  get: () => guideStore.onboardingVisible,
  set: (v: boolean) => guideStore.setOnboardingVisible(v),
})

const carouselRef = ref<{ prev: () => void; next: () => void } | null>(null)
const current = ref(0)
const dontShowAgain = ref(false)

// 弹窗打开时重置"不再显示"勾选（手动重新查看时保持默认未勾选）
watch(visible, (v) => {
  if (v) dontShowAgain.value = false
})

const steps = [
  {
    icon: MagicStick,
    title: '欢迎使用启航电商ERP',
    desc: '多平台店铺统一管理 · 开源免费',
    body: `
      <div class="intro">
        <p>把多个平台的店铺，装进一个系统统一管理。</p>
        <ul>
          <li>多平台订单统一处理、统一发货</li>
          <li>商品库 / 采购 / 仓库 / 库存 / 售后 一体化</li>
          <li>开源免费，可自部署、可二次开发</li>
        </ul>
      </div>`,
  },
  {
    icon: DataAnalysis,
    title: '核心业务流程',
    desc: '两条主线：订单履约 + 进销存',
    body: `
      <div class="flow-section">
        <div class="flow-section-title">① 平台订单处理流程</div>
        <div class="flow">
          <span class="flow-node">配置 appkey</span><span class="flow-arrow">→</span>
          <span class="flow-node">添加店铺</span><span class="flow-arrow">→</span>
          <span class="flow-node">拉取店铺商品</span><span class="flow-arrow">→</span>
          <span class="flow-node">拉取订单</span><span class="flow-arrow">→</span>
          <span class="flow-node">打单发货(电子面单)</span><span class="flow-arrow">→</span>
          <span class="flow-node">售后处理</span>
        </div>
      </div>
      <div class="flow-section">
        <div class="flow-section-title">② 库存管理流程</div>
        <div class="flow">
          <span class="flow-node">建立商品库</span><span class="flow-arrow">→</span>
          <span class="flow-node">采购入库</span><span class="flow-arrow">→</span>
          <span class="flow-node">关联店铺商品</span><span class="flow-arrow">→</span>
          <span class="flow-node">库存管理</span><span class="flow-arrow">→</span>
          <span class="flow-node">发货出库</span>
        </div>
      </div>
      <p class="flow-tip">两条流程在「发货」处交汇：订单触发发货、库存扣减出库；「关联店铺商品」是两段的桥梁。</p>`,
  },
  {
    icon: Connection,
    title: '两种方式跑通系统',
    desc: '不管有没有平台 appkey，都能跑通',
    body: `
      <div class="paths">
        <div class="path-item">
          <div class="path-tag">方式一</div>
          <div class="path-name">小平台直连</div>
          <div class="path-desc">微信小店 / 小红书 / 快手，拿到 appkey 即可开箱直连。</div>
        </div>
        <div class="path-item">
          <div class="path-tag">方式二</div>
          <div class="path-name">大平台订单导入</div>
          <div class="path-desc">淘宝 / 京东 / 拼多多 / 抖店，后台导出订单 Excel 导入即可，无需 appkey。</div>
        </div>
      </div>
      <p class="biz-tip">需要多平台 API 自动直连？关注商业版（支持多平台自动同步与托管）</p>`,
  },
  {
    icon: QuestionFilled,
    title: '随时找到帮助',
    desc: '遇到问题别卡住',
    body: `
      <div class="entries">
        <div class="entry">
          <div class="entry-name">① 在线帮助</div>
          <div class="entry-desc">查看系统内操作手册、平台接入教程与常见问题解答。</div>
        </div>
        <div class="entry">
          <div class="entry-name">② 提交工单 / 反馈</div>
          <div class="entry-desc">遇到问题或建议，推荐在代码仓库提 Issue（免登录），也可到官网反馈（扫码登录）。</div>
          <div class="entry-links">
            <a href="https://gitee.com/qiliping/qihang-erp-open/issues" target="_blank">Gitee Issue</a>
            <a href="https://github.com/zeasin/qihang-erp-open/issues" target="_blank">GitHub Issue</a>
            <a href="https://qihangerp.cn/community/feedback?source=erp-open" target="_blank">官网反馈</a>
          </div>
        </div>
      </div>
      <p class="entry-tip">系统内导航栏右上角❓也可随时打开帮助中心。</p>`,
  },
]

function onChange(index: number) {
  current.value = index
}

function prev() {
  carouselRef.value?.prev()
}

function next() {
  carouselRef.value?.next()
}

function finish() {
  visible.value = false
}

// 跳转帮助中心页面并关闭引导
function openHelp() {
  visible.value = false
  router.push('/help')
}

// 弹窗关闭：标记本会话已弹过；若用户勾选"不再显示"则永久禁用
function handleClose() {
  guideStore.markShownThisSession()
  if (dontShowAgain.value) {
    guideStore.disableOnboarding()
    ElMessage.success('已关闭自动引导，后续可在帮助中心重新查看')
  }
}
</script>

<style lang="scss" scoped>
.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 8px 24px 0;
  box-sizing: border-box;
  height: 100%;
}

.step-icon {
  width: 76px;
  height: 76px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #6db3ff 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.step-title {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.step-desc {
  margin: 0 0 18px;
  color: #909399;
  font-size: 14px;
}

.step-body {
  width: 100%;
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
}

.step-body :deep(.intro) {
  text-align: left;
  max-width: 480px;
  margin: 0 auto;
}

.step-body :deep(.intro ul) {
  margin: 8px 0 0;
  padding-left: 20px;
}

.step-body :deep(.intro li) {
  margin: 4px 0;
}

.step-body :deep(.flow-section) {
  margin-bottom: 14px;
}

.step-body :deep(.flow-section:last-of-type) {
  margin-bottom: 0;
}

.step-body :deep(.flow-section-title) {
  font-weight: 600;
  color: #303133;
  font-size: 13px;
  margin-bottom: 8px;
}

.step-body :deep(.flow) {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  gap: 6px;
  margin-bottom: 16px;
}

.step-body :deep(.flow-node) {
  background: #f0f5ff;
  color: #409eff;
  border: 1px solid #d6e4ff;
  padding: 6px 12px;
  border-radius: 14px;
  font-size: 13px;
  white-space: nowrap;
}

.step-body :deep(.flow-arrow) {
  color: #c0c4cc;
  font-size: 14px;
}

.step-body :deep(.flow-tip) {
  color: #909399;
  font-size: 13px;
  margin: 0;
}

.step-body :deep(.paths) {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 520px;
  margin: 0 auto;
}

.step-body :deep(.path-item) {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fafbfc;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 10px 14px;
  text-align: left;
}

.step-body :deep(.path-tag) {
  flex-shrink: 0;
  background: #409eff;
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.step-body :deep(.path-name) {
  font-weight: 600;
  color: #303133;
  margin-right: 8px;
  white-space: nowrap;
}

.step-body :deep(.path-desc) {
  color: #606266;
  font-size: 13px;
}

.step-body :deep(.biz-tip) {
  margin: 14px 0 0;
  font-size: 13px;
  color: #909399;
}

.step-body :deep(.entries) {
  display: flex;
  flex-direction: column;
  gap: 14px;
  max-width: 460px;
  margin: 0 auto;
}

.step-body :deep(.entry) {
  text-align: left;
  background: #fafbfc;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px 16px;
}

.step-body :deep(.entry-name) {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.step-body :deep(.entry-desc) {
  color: #606266;
  font-size: 13px;
}

.step-body :deep(.entry-links) {
  display: flex;
  gap: 14px;
  margin-top: 6px;
}

.step-body :deep(.entry-links a) {
  color: #409eff;
  text-decoration: none;
  font-size: 13px;
}

.step-body :deep(.entry-tip) {
  margin: 16px 0 0;
  color: #909399;
  font-size: 13px;
}

.step-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.onboarding-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.footer-actions {
  display: flex;
  gap: 8px;
}
</style>
