import request from '@/utils/request'

// 获取Ollama模型列表
export function getOllamaModels() {
  return request({
    url: '/api/ai-agent/ollama/models',
    method: 'get'
  })
}

// 获取对话历史
export function getConversationHistory(token) {
  return request({
    url: '/api/ai-agent/sse/history',
    method: 'get',
    params: { token }
  })
}
