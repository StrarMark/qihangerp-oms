import { defineStore } from 'pinia'

const STORAGE_KEY = 'qihang-guide-state'
const SESSION_KEY = 'qihang-onboarding-shown'

interface GuideState {
  // 用户主动勾选"下次登录不再自动显示"，永久生效
  onboardingDisabled: boolean
  // 各业务页指引是否已看过
  pageGuides: Record<string, boolean>
  // Onboarding 弹窗运行时显示状态（非持久化，供跨组件触发）
  onboardingVisible: boolean
}

function persist(disabled: boolean, pageGuides: Record<string, boolean>) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ onboardingDisabled: disabled, pageGuides }))
}

export const useGuideStore = defineStore('guide', {
  state: (): GuideState => {
    try {
      const raw = JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}')
      return {
        onboardingDisabled: !!raw.onboardingDisabled,
        pageGuides: raw.pageGuides || {},
        onboardingVisible: false,
      }
    } catch {
      return { onboardingDisabled: false, pageGuides: {}, onboardingVisible: false }
    }
  },
  getters: {
    // 是否应自动弹出新手引导：未禁用 + 本会话未弹过
    shouldShowOnboarding(): boolean {
      if (this.onboardingDisabled) return false
      return sessionStorage.getItem(SESSION_KEY) !== '1'
    },
  },
  actions: {
    // 标记本次会话已弹过（防止切换页面重复弹）
    markShownThisSession() {
      sessionStorage.setItem(SESSION_KEY, '1')
    },
    // 设置 Onboarding 弹窗显示状态（运行时，供跨组件触发/关闭）
    setOnboardingVisible(v: boolean) {
      this.onboardingVisible = v
    },
    // 用户勾选"下次不再显示"，永久关闭自动弹窗
    disableOnboarding() {
      this.onboardingDisabled = true
      persist(this.onboardingDisabled, this.pageGuides)
    },
    // 重新启用自动弹窗（帮助页"重新查看引导"重置用）
    enableOnboarding() {
      this.onboardingDisabled = false
      sessionStorage.removeItem(SESSION_KEY)
      persist(this.onboardingDisabled, this.pageGuides)
    },
    isPageGuideSeen(code: string): boolean {
      return !!this.pageGuides[code]
    },
    markPageGuide(code: string) {
      this.pageGuides[code] = true
      persist(this.onboardingDisabled, this.pageGuides)
    },
    // 重置全部引导（演示数据重置场景）
    resetAll() {
      this.onboardingDisabled = false
      this.pageGuides = {}
      sessionStorage.removeItem(SESSION_KEY)
      persist(this.onboardingDisabled, this.pageGuides)
    },
  },
})
