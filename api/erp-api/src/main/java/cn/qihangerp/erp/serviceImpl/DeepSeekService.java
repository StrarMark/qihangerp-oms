//package cn.qihangerp.erp.serviceImpl;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import okhttp3.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class DeepSeekService {
//
//    private static final Logger log = LoggerFactory.getLogger(DeepSeekService.class);
//    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
//
//    @Value("${deepseek.api.key}")
//    private String apiKey;
//
//    @Value("${deepseek.api.endpoint:https://api.deepseek.com/v1/chat/completions}")
//    private String apiEndpoint;
//
//    @Value("${deepseek.api.model:deepseek-chat}")
//    private String model;
//
//    private OkHttpClient okHttpClient;
//    private final ObjectMapper objectMapper;
//
//    // 缓存最近一次成功的分析结果
//    private Map<String, Object> cachedAnalysis = new HashMap<>();
//
//    public DeepSeekService(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }
//
//    @PostConstruct
//    public void init() {
//        // 配置具有重试和连接池功能的OkHttpClient
//        this.okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(15, TimeUnit.SECONDS)     // 连接超时
//                .readTimeout(30, TimeUnit.SECONDS)        // 读取超时
//                .writeTimeout(15, TimeUnit.SECONDS)       // 写入超时
//                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES)) // 连接池
//                .addInterceptor(new RetryInterceptor(3))  // 自定义重试拦截器
//                .addInterceptor(new LoggingInterceptor()) // 日志拦截器
//                .build();
//    }
//
//    /**
//     * 调用DeepSeek API - 带有Spring Retry重试机制
//     */
////    @Retryable(
////            value = {IOException.class, RuntimeException.class},
////            maxAttempts = 3,
////            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 10000)
////    )
//    public Map<String, Object> analyzeData(Map<String, Object> formattedData, String analysisType) {
//        String cacheKey = generateCacheKey(formattedData, analysisType);
//
//        try {
//            // 1. 构建请求体
//            String requestBody = buildRequestBody(formattedData, analysisType);
//            RequestBody body = RequestBody.create(requestBody, JSON);
//
//            // 2. 构建请求
//            Request request = new Request.Builder()
//                    .url(apiEndpoint)
//                    .header("Authorization", "Bearer " + apiKey)
//                    .header("Content-Type", "application/json")
//                    .post(body)
//                    .build();
//
//            // 3. 执行请求并处理响应
//            try (Response response = okHttpClient.newCall(request).execute()) {
//                if (!response.isSuccessful()) {
//                    handleErrorResponse(response, cacheKey);
//                }
//
//                String responseBody = response.body().string();
//                Map<String, Object> result = parseResponse(responseBody, analysisType);
//
//                // 缓存成功的结果
//                cacheSuccessfulResult(cacheKey, result);
//                return result;
//            }
//
//        } catch (Exception e) {
//            log.error("调用DeepSeek API失败，尝试使用缓存或降级方案", e);
//            return getFallbackAnalysis(cacheKey, analysisType);
//        }
//    }
//
//    /**
//     * 为补货建议优化的专用方法
//     */
//    public Map<String, Object> generateReplenishmentSuggestions(Map<String, Object> inventoryData) {
//        try {
//            String prompt = buildReplenishmentPrompt(inventoryData);
//
//            Map<String, Object> requestBody = Map.of(
//                    "model", model,
//                    "messages", List.of(
//                            Map.of("role", "system", "content",
//                                    "你是一个经验丰富的电商库存管理专家，擅长制定补货策略。"),
//                            Map.of("role", "user", "content", prompt)
//                    ),
//                    "temperature", 0.2,
//                    "max_tokens", 1500,
//                    "response_format", Map.of("type", "json_object")
//            );
//
//            String jsonBody = objectMapper.writeValueAsString(requestBody);
//
//            Request request = new Request.Builder()
//                    .url(apiEndpoint)
//                    .header("Authorization", "Bearer " + apiKey)
//                    .post(RequestBody.create(jsonBody, JSON))
//                    .build();
//
//            // 设置更短的超时时间，因为补货建议需要快速响应
//            OkHttpClient quickClient = okHttpClient.newBuilder()
//                    .readTimeout(15, TimeUnit.SECONDS)
//                    .build();
//
//            try (Response response = quickClient.newCall(request).execute()) {
//                if (response.isSuccessful()) {
//                    String responseBody = response.body().string();
//                    return parseReplenishmentResponse(responseBody);
//                } else {
//                    // 如果API失败，使用本地算法生成补货建议
//                    return generateLocalReplenishmentSuggestions(inventoryData);
//                }
//            }
//
//        } catch (Exception e) {
//            log.warn("AI补货建议失败，使用本地算法", e);
//            return generateLocalReplenishmentSuggestions(inventoryData);
//        }
//    }
//
//    /**
//     * 本地补货算法 - 服务降级方案
//     */
//    private Map<String, Object> generateLocalReplenishmentSuggestions(Map<String, Object> inventoryData) {
//        List<Map<String, Object>> products = (List<Map<String, Object>>) inventoryData.get("data");
//        List<Map<String, Object>> suggestions = new ArrayList<>();
//        int totalQuantity = 0;
//        double estimatedCost = 0.0;
//
//        for (Map<String, Object> product : products) {
//            String status = (String) product.get("inventoryStatus");
//
//            // 只处理需要补货的产品
//            if ("HEALTHY".equals(status) || "OVERSTOCK".equals(status)) {
//                continue;
//            }
//
//            int currentStock = (int) product.getOrDefault("currentStock", 0);
//            int safetyStock = (int) product.getOrDefault("safetyStock", 100);
//            double avgDailySales = ((Integer) product.getOrDefault("avgDailySales", 10)).doubleValue();
//            double coverDays = (double) product.getOrDefault("coverDays", 0.0);
//
//            Map<String, Object> suggestion = new HashMap<>();
//            suggestion.put("product_id", product.get("productId"));
//            suggestion.put("product_name", product.get("productName"));
//
//            // 本地补货逻辑
//            int suggestedQty;
//            String urgency;
//
//            if (currentStock <= 0) {
//                suggestedQty = (int) (avgDailySales * 30);
//                urgency = "紧急";
//            } else if (coverDays < 3) {
//                suggestedQty = (int) (avgDailySales * 15 - currentStock);
//                urgency = "高";
//            } else if (currentStock < safetyStock) {
//                suggestedQty = safetyStock * 2 - currentStock;
//                urgency = "中";
//            } else {
//                suggestedQty = (int) (avgDailySales * 7);
//                urgency = "低";
//            }
//
//            suggestedQty = Math.max(suggestedQty, 10);
//            totalQuantity += suggestedQty;
//
//            suggestion.put("suggested_quantity", suggestedQty);
//            suggestion.put("urgency", urgency);
//            suggestion.put("reason", "本地算法计算");
//            suggestion.put("expected_cover_days", suggestedQty / Math.max(avgDailySales, 1));
//
//            suggestions.add(suggestion);
//        }
//
//        return Map.of(
//                "success", true,
//                "source", "local_algorithm",
//                "replenishment_list", suggestions,
//                "total_replenishment_quantity", totalQuantity,
//                "analysis_summary", "基于本地规则生成的补货建议",
//                "recommendations", List.of(
//                        "建议优先处理标记为'紧急'的商品",
//                        "此为本地降级方案，AI分析恢复后将提供更精确建议"
//                )
//        );
//    }
//
//    /**
//     * 自定义重试拦截器
//     */
//    private static class RetryInterceptor implements Interceptor {
//        private final int maxRetries;
//
//        public RetryInterceptor(int maxRetries) {
//            this.maxRetries = maxRetries;
//        }
//
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            Response response = null;
//            IOException exception = null;
//
//            // 重试逻辑
//            for (int retryCount = 0; retryCount <= maxRetries; retryCount++) {
//                try {
//                    response = chain.proceed(request);
//
//                    // 只有服务器错误(5xx)或特定客户端错误才重试
//                    if (response.isSuccessful() ||
//                            (response.code() != 503 && response.code() != 429 && response.code() != 408)) {
//                        return response;
//                    }
//
//                    log.warn("API请求失败，状态码: {}, 重试: {}/{}",
//                            response.code(), retryCount, maxRetries);
//
//                    // 关闭响应体
//                    response.close();
//
//                } catch (IOException e) {
//                    exception = e;
//                    log.warn("网络异常，重试: {}/{}", retryCount, maxRetries, e);
//                }
//
//                // 如果不是最后一次重试，等待一段时间
//                if (retryCount < maxRetries) {
//                    try {
//                        // 指数退避：1s, 2s, 4s...
//                        long waitTime = (long) Math.pow(2, retryCount) * 1000;
//                        Thread.sleep(waitTime);
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                        throw new IOException("重试被中断", e);
//                    }
//                }
//            }
//
//            if (exception != null) {
//                throw exception;
//            }
//
//            return response;
//        }
//    }
//
//    /**
//     * 日志拦截器
//     */
//    private static class LoggingInterceptor implements Interceptor {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            long startTime = System.currentTimeMillis();
//
//            log.debug("发送请求: {} {}", request.method(), request.url());
//
//            Response response;
//            try {
//                response = chain.proceed(request);
//            } catch (IOException e) {
//                long duration = System.currentTimeMillis() - startTime;
//                log.error("请求失败: {} {} - 耗时: {}ms",
//                        request.method(), request.url(), duration, e);
//                throw e;
//            }
//
//            long duration = System.currentTimeMillis() - startTime;
//            log.info("收到响应: {} {} - 状态: {} - 耗时: {}ms",
//                    request.method(), request.url(), response.code(), duration);
//
//            return response;
//        }
//    }
//
//    /**
//     * 错误处理
//     */
//    private void handleErrorResponse(Response response, String cacheKey) throws IOException {
//        int code = response.code();
//        String errorBody = response.body() != null ? response.body().string() : "无错误详情";
//
//        log.error("DeepSeek API错误响应: 状态码={}, 错误信息={}", code, errorBody);
//
//        // 根据错误类型处理
//        if (code == 401) {
//            throw new RuntimeException("API密钥无效或已过期");
//        } else if (code == 429) {
//            throw new RuntimeException("请求频率超限，请稍后重试");
//        } else if (code == 503) {
//            // 服务不可用，尝试使用缓存
//            if (cachedAnalysis.containsKey(cacheKey)) {
//                log.info("服务不可用，使用缓存结果");
//                throw new ServiceUnavailableException("服务不可用，已返回缓存结果");
//            }
//            throw new RuntimeException("DeepSeek服务暂时不可用，请稍后重试");
//        } else {
//            throw new RuntimeException(String.format("API请求失败: %d - %s", code, errorBody));
//        }
//    }
//
//    /**
//     * 服务降级：获取缓存或基础分析
//     */
//    private Map<String, Object> getFallbackAnalysis(String cacheKey, String analysisType) {
//        // 1. 首先尝试缓存
//        if (cachedAnalysis.containsKey(cacheKey)) {
//            log.info("使用缓存的AI分析结果");
//            Map<String, Object> cached = (Map<String, Object>) cachedAnalysis.get(cacheKey);
//            cached.put("source", "cached");
//            return cached;
//        }
//
//        // 2. 返回基础分析模板
//        log.info("返回基础分析模板");
//        return Map.of(
//                "success", false,
//                "source", "fallback_template",
//                "message", "AI分析服务暂时不可用",
//                "basic_analysis", Map.of(
//                        "suggestion", "建议检查库存水平，重点关注缺货商品",
//                        "generated_at", new Date()
//                ),
//                "recommendations", List.of(
//                        "1. 优先处理库存为0的商品",
//                        "2. 检查日销量高但库存低的商品",
//                        "3. AI服务恢复后重新获取详细分析"
//                )
//        );
//    }
//
//    // 其他辅助方法（buildRequestBody, parseResponse等）保持原有逻辑
//    // ...
//}
//
//// 自定义异常类
//class ServiceUnavailableException extends RuntimeException {
//    public ServiceUnavailableException(String message) {
//        super(message);
//    }
//}