package cn.qihangerp.erp.serviceImpl;

import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class InventorySalesAnalyzer {

    // 配置你的 DeepSeek API 信息
    private static final String API_KEY = "sk-e1f3aecc45e44eca9451d5a659a4bc91";
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 你的数据
    private static final String INVENTORY_JSON = "[{\"id\":1,\"goods_title\":\"雷士照明led吸顶灯灯芯替换圆形灯板节能灯芯冷光高显护眼健康\",\"sku_name\":\"白光12W\",\"stock_num\":12},{\"id\":2,\"goods_title\":\"雷士照明led吸顶灯灯芯替换圆形灯板节能灯芯冷光高显护眼健康\",\"sku_name\":\"白光18W\",\"stock_num\":12},{\"id\":3,\"goods_title\":\"雷士照明led吸顶灯灯芯替换圆形灯板节能灯芯冷光高显护眼健康\",\"sku_name\":\"白光24W\",\"stock_num\":12},{\"id\":4,\"goods_title\":\"雷士照明led吸顶灯灯芯替换圆形灯板节能灯芯冷光高显护眼健康\",\"sku_name\":\"双色36W\",\"stock_num\":12}]";

    private static final String SALES_JSON = "[{\"order_num\":\"1\",\"sku_id\":1,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":3,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":1,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":2,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":4,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":1,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":1,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":3,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":1,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":1,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"},{\"order_num\":\"1\",\"sku_id\":2,\"count\":1,\"item_amount\":29.32,\"order_time\":\"2025-05-24 23:19:51\"}]";

    public static void main(String[] args) {
        try {
            System.out.println("开始分析库存与销售数据...\n");

            // 1. 解析数据
            List<InventoryItem> inventoryList = parseInventoryData();
            List<SalesOrder> salesList = parseSalesData();

            // 2. 分析数据并生成报告
            String analysisResult = analyzeInventoryAndSales(inventoryList, salesList);

            System.out.println("=== AI 分析报告 ===\n");
            System.out.println(analysisResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 核心分析方法
     */
    public static String analyzeInventoryAndSales(List<InventoryItem> inventory,
                                                  List<SalesOrder> sales) throws IOException {

        // 1. 数据预处理：按 SKU ID 关联库存和销售数据
        Map<Integer, SkuAnalysis> analysisMap = new HashMap<>();

        // 初始化库存数据
        for (InventoryItem item : inventory) {
            SkuAnalysis analysis = new SkuAnalysis();
            analysis.id = item.id;
            analysis.goodsTitle = item.goodsTitle;
            analysis.skuName = item.skuName;
            analysis.stockNum = item.stockNum;
            analysisMap.put(item.id, analysis);
        }

        // 统计销售数据
        for (SalesOrder order : sales) {
            if (analysisMap.containsKey(order.skuId)) {
                SkuAnalysis analysis = analysisMap.get(order.skuId);
                analysis.totalSales += order.count;
                analysis.totalRevenue += order.itemAmount;
                analysis.orderCount++;

                // 记录销售时间（用于趋势分析）
                analysis.salesTimes.add(order.orderTime);
            }
        }

        // 2. 计算关键指标
        for (SkuAnalysis analysis : analysisMap.values()) {
            // 计算日均销量（假设数据是最近30天的）
            analysis.dailyAvgSales = analysis.totalSales / 30.0;

            // 计算可售天数
            if (analysis.dailyAvgSales > 0) {
                analysis.daysOfSupply = analysis.stockNum / analysis.dailyAvgSales;
            } else {
                analysis.daysOfSupply = 999; // 无销售
            }

            // 判断库存状态
            analysis.stockStatus = determineStockStatus(analysis.stockNum, analysis.dailyAvgSales);
        }

        // 3. 构建 AI 分析提示词
        String prompt = buildAnalysisPrompt(analysisMap);

        // 4. 调用 DeepSeek API
        return callDeepSeekAPI(prompt);
    }

    /**
     * 构建 AI 分析提示词
     */
    private static String buildAnalysisPrompt(Map<Integer, SkuAnalysis> analysisMap) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("你是一名专业的电商库存管理专家。请分析以下 LED 灯具产品的库存与销售数据，并提供专业的分析报告和建议：\n\n");

        prompt.append("=== 数据概览 ===\n");
        prompt.append("产品名称：雷士照明 LED 吸顶灯灯芯\n");
        prompt.append("分析时间：").append(new Date()).append("\n\n");

        prompt.append("=== 详细数据 ===\n");
        prompt.append(String.format("%-8s %-12s %-8s %-8s %-12s %-10s %-15s\n",
                "SKU ID", "规格", "库存量", "总销量", "总销售额", "可售天数", "库存状态"));
        prompt.append("-".repeat(80)).append("\n");

        for (SkuAnalysis analysis : analysisMap.values()) {
            prompt.append(String.format("%-8d %-12s %-8d %-8d %-12.2f %-10.1f %-15s\n",
                    analysis.id,
                    analysis.skuName,
                    analysis.stockNum,
                    analysis.totalSales,
                    analysis.totalRevenue,
                    analysis.daysOfSupply,
                    analysis.stockStatus
            ));
        }

        prompt.append("\n=== 分析要求 ===\n");
        prompt.append("请基于以上数据，提供以下分析：\n");
        prompt.append("1. **库存健康度分析**：评估每个SKU的库存状况，识别缺货风险\n");
        prompt.append("2. **销售表现分析**：分析各规格产品的销售情况，找出畅销款和滞销款\n");
        prompt.append("3. **补货建议**：\n");
        prompt.append("   - 哪些SKU需要立即补货？建议补货数量？\n");
        prompt.append("   - 哪些SKU库存过多？建议如何清理？\n");
        prompt.append("   - 建议的安全库存水平\n");
        prompt.append("4. **运营建议**：基于销售模式，给出采购、促销或产品组合建议\n\n");

        prompt.append("请以专业报告格式回复，包含具体数据和理由。");

        return prompt.toString();
    }

    /**
     * 调用 DeepSeek API
     */
    private static String callDeepSeekAPI(String prompt) throws IOException {
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", Arrays.asList(
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.3);  // 降低随机性，使分析更稳定
        requestBody.put("max_tokens", 2000);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // 创建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, JSON))
                .build();

        // 发送请求（带重试机制）
        for (int attempt = 0; attempt < 3; attempt++) {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    return extractContentFromResponse(responseBody);
                } else if (response.code() == 429 || response.code() >= 500) {
                    // 频率限制或服务器错误，等待后重试
                    System.out.println("请求失败，状态码: " + response.code() + "，等待重试...");
                    Thread.sleep(2000 * (attempt + 1));
                    continue;
                } else {
                    throw new IOException("API请求失败: " + response.code() + " - " + response.message());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("请求被中断", e);
            }
        }

        throw new IOException("API请求失败，已重试3次");
    }

    /**
     * 从 API 响应中提取内容
     */
    private static String extractContentFromResponse(String responseBody) throws IOException {
        Map<String, Object> responseMap = objectMapper.readValue(responseBody,
                new TypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> choice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            return (String) message.get("content");
        }

        return "未获取到有效回复";
    }

    /**
     * 判断库存状态
     */
    private static String determineStockStatus(int stock, double dailySales) {
        if (stock == 0) return "缺货";
        if (dailySales == 0) return "滞销";

        double daysOfSupply = stock / dailySales;

        if (daysOfSupply < 7) return "急需补货";
        if (daysOfSupply < 14) return "需要补货";
        if (daysOfSupply < 30) return "库存正常";
        if (daysOfSupply < 60) return "库存偏高";
        return "库存积压";
    }

    // 数据解析方法
    private static List<InventoryItem> parseInventoryData() throws IOException {
        return objectMapper.readValue(INVENTORY_JSON,
                new TypeReference<List<InventoryItem>>() {});
    }

    private static List<SalesOrder> parseSalesData() throws IOException {
        return objectMapper.readValue(SALES_JSON,
                new TypeReference<List<SalesOrder>>() {});
    }

    // 数据类定义
    static class InventoryItem {
        public int id;
        @JsonProperty("goods_title")
        public String goodsTitle;
        @JsonProperty("sku_name")
        public String skuName;
        @JsonProperty("stock_num")
        public int stockNum;
    }

    static class SalesOrder {
        @JsonProperty("order_num")
        public String orderNum;
        @JsonProperty("sku_id")
        public int skuId;
        public int count;
        @JsonProperty("item_amount")
        public double itemAmount;
        @JsonProperty("order_time")
        public String orderTime;
    }

    static class SkuAnalysis {
        public int id;
        public String goodsTitle;
        public String skuName;
        public int stockNum;
        public int totalSales = 0;
        public double totalRevenue = 0.0;
        public int orderCount = 0;
        public double dailyAvgSales = 0.0;
        public double daysOfSupply = 0.0;
        public String stockStatus = "未知";
        public List<String> salesTimes = new ArrayList<>();
    }
}