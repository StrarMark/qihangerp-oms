<template>
  <div class="help-doc">
    <aside class="help-aside">
      <div class="help-aside-title">帮助中心</div>
      <el-menu
        :default-active="activeId"
        :default-openeds="openeds"
        class="help-menu"
        @select="onSelect"
      >
        <el-sub-menu v-for="cat in categories" :key="cat.id" :index="cat.id">
          <template #title>{{ cat.title }}</template>
          <el-menu-item v-for="art in cat.articles" :key="art.id" :index="art.id">
            {{ art.title }}
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
      <div class="help-aside-footer">
        <el-button size="small" plain @click="reopenOnboarding">重新查看新手引导</el-button>
      </div>
    </aside>

    <main class="help-content">
      <article v-if="currentArticle" class="doc-article">
        <h1 class="doc-h1">{{ currentArticle.title }}</h1>
        <div v-if="loading" class="doc-loading">加载中…</div>
        <div v-else-if="error" class="doc-empty">该文章内容待补充。</div>
        <div v-else class="doc-text" v-html="html" @click="onContentClick"></div>
      </article>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { marked } from 'marked'
import { useGuideStore } from '@/store/modules/guide'

const router = useRouter()
const guideStore = useGuideStore()

interface Article {
  id: string
  title: string
  file: string
}
interface Category {
  id: string
  title: string
  articles: Article[]
}

const categories = ref<Category[]>([])
const activeId = ref('')
const html = ref('')
const loading = ref(false)
const error = ref(false)

const openeds = computed(() => categories.value.map((c) => c.id))

const currentArticle = computed<Article | null>(() => {
  for (const c of categories.value) {
    const a = c.articles.find((x) => x.id === activeId.value)
    if (a) return a
  }
  return null
})

// 加载菜单清单
onMounted(async () => {
  try {
    const res = await fetch('/help/manifest.json')
    const data = await res.json()
    categories.value = data.categories || []
    const first = categories.value[0]?.articles[0]
    if (first) {
      activeId.value = first.id
      await loadArticle(first)
    }
  } catch {
    /* 清单加载失败，菜单为空 */
  }
})

// 加载文章 md 并渲染
async function loadArticle(art: Article) {
  loading.value = true
  error.value = false
  html.value = ''
  try {
    const res = await fetch(`/help/${art.file}.md`)
    if (!res.ok) {
      error.value = true
      return
    }
    const md = await res.text()
    html.value = marked.parse(md) as string
  } catch {
    error.value = true
  } finally {
    loading.value = false
  }
}

async function onSelect(index: string) {
  activeId.value = index
  const art = findArticle(index)
  if (art) await loadArticle(art)
}

function findArticle(id: string): Article | null {
  for (const c of categories.value) {
    const a = c.articles.find((x) => x.id === id)
    if (a) return a
  }
  return null
}

// 拦截 v-html 内的站内链接，走 SPA 路由跳转（外部链接带 target 的不拦截）
function onContentClick(e: MouseEvent) {
  const a = (e.target as HTMLElement)?.closest('a')
  if (!a) return
  const href = a.getAttribute('href')
  if (!href || a.getAttribute('target')) return
  if (href.startsWith('/')) {
    e.preventDefault()
    router.push(href)
  }
}

function reopenOnboarding() {
  guideStore.enableOnboarding()
  guideStore.setOnboardingVisible(true)
}
</script>

<style lang="scss" scoped>
.help-doc {
  display: flex;
  height: 100%;
  background: #fff;
}

.help-aside {
  width: 240px;
  flex-shrink: 0;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  background: #fafbfc;

  .help-aside-title {
    padding: 16px 20px 12px;
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }

  .help-menu {
    flex: 1;
    border-right: none;
    background: transparent;
    overflow-y: auto;
  }

  .help-aside-footer {
    padding: 12px 16px;
    border-top: 1px solid #ebeef5;
  }
}

.help-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px 36px;
}

.doc-article {
  max-width: 880px;
}

.doc-h1 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid #ebeef5;
}

.doc-loading,
.doc-empty {
  color: #909399;
  font-size: 14px;
  padding: 24px 0;
}

.doc-text {
  color: #444;
  font-size: 14px;
  line-height: 1.8;

  :deep(h2),
  :deep(h3) {
    color: #303133;
    margin: 20px 0 8px;
  }
  :deep(h2) {
    font-size: 18px;
  }
  :deep(h3) {
    font-size: 16px;
  }
  :deep(p) {
    margin: 8px 0;
  }
  :deep(ol),
  :deep(ul) {
    padding-left: 20px;
  }
  :deep(li) {
    margin: 6px 0;
  }
  :deep(a) {
    color: #409eff;
    text-decoration: none;
  }
  :deep(strong) {
    color: #303133;
  }
  :deep(blockquote) {
    margin: 12px 0;
    padding: 8px 12px;
    border-left: 3px solid #409eff;
    background: #f5f7fa;
    color: #606266;
  }
  :deep(img) {
    max-width: 100%;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    display: block;
    margin: 12px 0;
  }
}
</style>
