## api/ai-agent 目录代码结构分析

### 一、模块概述

ai-agent 是一个基于 **LangChain4J** 的 AI 智能助手模块，用于实现对话式电商管理功能。

**核心技术栈：**
- LangChain4J (AI 框架)
- Ollama (本地大模型) / DeepSeek API (云端大模型)
- SSE (Server-Sent Events) 实时通信
- Spring Cloud 微服务

---

### 二、目录结构

```
api/ai-agent/
├── src/main/java/cn/qihangerp/erp/
│   ├── AiAgent.java                    # 启动类
│   ├── controller/
│   │   ├── SseController.java          # SSE实时通信控制器 ⭐核心
│   │   ├── HomeController.java         # 主页控制器
│   │   ├── OllamaController.java       # Ollama模型控制器
│   │   └── AiUserRoleController.java   # AI用户角色控制器
│   ├── service/
│   │   ├── OrderService.java           # 订单服务 (模拟数据)
│   │   └── OrderToolService.java       # AI订单工具 ⭐核心
│   ├── serviceImpl/
│   │   ├── AiService.java             # AI服务核心逻辑 ⭐⭐核心
│   │   ├── ConversationHistoryManager.java  # 对话历史管理
│   │   ├── SessionManager.java        # 会话管理
│   │   ├── InventorySalesAnalyzer.java # 库存销售分析
│   │   └── DeepSeekService.java       # DeepSeek API封装
│   ├── feign/
│   │   ├── OpenApiService.java        # 内部API调用
│   │   └── EchoService.java
│   └── config/
│       └── MybatisPlusConfig.java
└── src/main/resources/
    ├── application.yml                 # 配置文件
    └── page-rules.md                  # 页面跳转规则
```

---

### 三、核心业务逻辑

#### 1. SseController (SseController.java:30-240)

**职责：** 处理前端 SSE 实时通信

```
客户端连接 → 鉴权 → 创建会话 → 心跳保活 → AI处理 → 返回响应
```

**关键接口：**
- `GET /sse/connect` - 建立 SSE 连接
- `GET /sse/send` - 发送消息并获取 AI 回复
- `GET /sse/disconnect` - 断开连接
- `GET /sse/history` - 获取对话历史
- `GET /sse/status` - 获取服务状态

---

#### 2. AiService (AiService.java:24-302)

**职责：** AI 消息处理核心服务

```
用户消息 → 页面规则匹配 → 上下文增强 → 模型调用 → 返回响应
```

**核心流程：**

1. **页面规则匹配** (`checkPageRules`)
   - 从 `page-rules.md` 加载规则
   - 匹配关键词 → 返回导航指令 JSON

2. **上下文增强**
   - 将"今天"替换为实际日期
   - 限制历史上下文最长 2000 字符

3. **模型选择**
   - `deepseek` 前缀 → DeepSeek API
   - 其他 → Ollama 本地模型

4. **Tool 集成**
   - 绑定 `OrderToolService` 提供订单查询能力

---

#### 3. OrderToolService (OrderToolService.java:11-115)

**职责：** AI 可调用的订单查询工具

| 方法 | 功能 |
|------|------|
| `getOrderById` | 按订单号查询 |
| `getAllOrders` | 获取所有订单 |
| `getOrdersByStatus` | 按状态查询 |
| `getPendingOrders` | 待发货订单 |
| `getOrdersByDate` | 按日期查询 |

使用 `@Tool` 注解声明为 LangChain4J 工具

---

#### 4. ConversationHistoryManager (ConversationHistoryManager.java:17-145)

**职责：** 对话历史持久化管理

- 依赖 `IAiConversationHistoryService` 存储到数据库
- 支持获取历史、获取最近N条、清空历史

---

#### 5. SessionManager (SessionManager.java:16-101)

**职责：** 用户会话管理

- 内存缓存 + 数据库持久化
- `userId ↔ sessionId` 双向映射

---

#### 6. InventorySalesAnalyzer (InventorySalesAnalyzer.java:11-273)

**职责：** 库存销售分析（独立工具类）

- 解析库存和销售 JSON 数据
- 计算关键指标：日均销量、可售天数、库存状态
- 调用 DeepSeek API 生成分析报告

---

### 四、数据流图

```
┌─────────────┐     SSE      ┌──────────────────┐
│   前端 Vue   │ ──────────→ │   SseController  │
└─────────────┘              └────────┬─────────┘
                                       │
                      ┌────────────────┼────────────────┐
                      ▼                ▼                ▼
            ┌──────────────┐  ┌─────────────┐  ┌──────────────┐
            │ SessionManager│  │Conversation │  │  AiService   │
            │              │  │  HistoryMgr │  │              │
            └──────────────┘  └─────────────┘  └──────┬───────┘
                                                      │
                                   ┌──────────────────┼──────────────────┐
                                   ▼                  ▼                  ▼
                          ┌─────────────┐    ┌──────────────┐   ┌─────────────┐
                          │ PageRules   │    │OrderToolSvc  │   │ LLM Model   │
                          │ (页面导航)  │    │ (订单查询)   │   │(Ollama/DeepSeek)
                          └─────────────┘    └──────────────┘   └─────────────┘
```

---

### 五、配置说明 (application.yml)

```yaml
server.port: 8084                    # AI服务端口
spring.datasource: MySQL连接配置
spring.data.redis: Redis连接配置
deepseek.api:                        # DeepSeek API配置
  key: sk-xxxx
  endpoint: https://api.deepseek.com/v1
  model: deepseek-chat
```

---

### 六、待优化点

1. **OrderService.java** - 目前使用模拟数据，需对接真实 ERP 订单数据
2. **API Key 硬编码** - InventorySalesAnalyzer 中的 API Key 应配置化
3. **SessionManager** - 内存缓存无持久化，重启丢失
4. **错误处理** - AI 服务调用失败时需更完善的降级策略
