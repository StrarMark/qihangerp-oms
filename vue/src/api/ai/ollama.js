import request from '@/utils/request'

// 获取Ollama模型列表
export function getOllamaModels() {
  return request({
    url: '/api/ai-agent/ollama/models',
    method: 'get'
  })
}
