package cn.qihangerp.service.impl;

import cn.qihangerp.model.dto.ImportResult;
import cn.qihangerp.model.entity.FmsExpense;
import cn.qihangerp.model.entity.FmsExpenseItem;
import cn.qihangerp.service.FmsExpenseService;
import cn.qihangerp.service.PlatformBillImportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class PlatformBillImportServiceImpl implements PlatformBillImportService {

    @Autowired
    private FmsExpenseService expenseService;

    @Override
    public List<String> queryPlatforms() {
        return Arrays.asList("taobao", "pdd", "douyin", "jd");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResult saveExpensesFromBillImport(List<FmsExpense> expenses, String operator) {
        return expenseService.batchImportExpenses(expenses, operator);
    }

    public static List<Map<String, Object>> parseExcel(MultipartFile file, String platform) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IOException("文件名不能为空");
        }

        if (filename.endsWith(".csv") || filename.endsWith(".CSV")) {
            return parseCsv(file, platform);
        } else {
            return parseExcelFile(file, platform);
        }
    }

    private static List<Map<String, Object>> parseCsv(MultipartFile file, String platform) throws IOException {
        List<Map<String, Object>> details = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "GBK"))) {
            String headerLine = findHeaderLine(reader);
            if (headerLine == null) {
                throw new IOException("CSV文件没有找到有效表头");
            }

            String[] headers = splitLine(headerLine);
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                String trimmed = headers[i].trim();
                if (!trimmed.isEmpty()) {
                    headerMap.put(trimmed.toLowerCase(), i);
                }
            }

            boolean isPddFinanceFormat = false;
            for (String key : headerMap.keySet()) {
                if (key.contains("收入金额") || key.contains("支出金额") || key.contains("账务类型") || key.contains("商户订单号")) {
                    isPddFinanceFormat = true;
                    break;
                }
            }

            if ("pdd".equalsIgnoreCase(platform) && isPddFinanceFormat) {
                return parsePddFinanceFormat(reader, headers, headerMap);
            }

            int orderNoIndex = findColumnIndex(headerMap, "订单号", "order_no", "订单编号", "商户订单号");
            int goodsNameIndex = findColumnIndex(headerMap, "商品名称", "goods_name", "商品");
            int goodsNumIndex = findColumnIndex(headerMap, "商品数量", "goods_num", "数量");
            int orderAmountIndex = findColumnIndex(headerMap, "订单金额", "order_amount", "金额");
            int platformFeeIndex = findColumnIndex(headerMap, "平台服务费", "platform_service_fee", "服务费");
            int promotionFeeIndex = findColumnIndex(headerMap, "推广费", "promotion_fee", "推广服务费");
            int freightIndex = findColumnIndex(headerMap, "运费", "freight");
            int otherFeeIndex = findColumnIndex(headerMap, "其他扣费", "other_fee", "其他费用");
            int actualAmountIndex = findColumnIndex(headerMap, "实收金额", "actual_amount", "实际金额");
            int orderDateIndex = findColumnIndex(headerMap, "订单日期", "order_date", "日期", "发生时间");

            String line;
            while ((line = reader.readLine()) != null) {
                if (isEmptyOrSeparatorLine(line)) continue;

                String[] values = splitLine(line);
                if (values.length == 0) continue;

                String orderNo = getValue(values, orderNoIndex);
                if (orderNo == null || orderNo.isEmpty()) continue;

                Map<String, Object> detail = new HashMap<>();

                String goodsName = getValue(values, goodsNameIndex);
                Integer goodsNum = parseInteger(getValue(values, goodsNumIndex));
                BigDecimal orderAmount = parseDecimal(getValue(values, orderAmountIndex));
                BigDecimal platformServiceFee = parseDecimal(getValue(values, platformFeeIndex));
                BigDecimal promotionFee = parseDecimal(getValue(values, promotionFeeIndex));
                BigDecimal freight = parseDecimal(getValue(values, freightIndex));
                BigDecimal otherFee = parseDecimal(getValue(values, otherFeeIndex));
                BigDecimal actualAmount = parseDecimal(getValue(values, actualAmountIndex));
                String orderDate = getValue(values, orderDateIndex);

                if (actualAmount == null || actualAmount.compareTo(BigDecimal.ZERO) == 0) {
                    actualAmount = orderAmount.subtract(platformServiceFee).subtract(promotionFee).subtract(freight).subtract(otherFee);
                }

                detail.put("orderNo", orderNo);
                detail.put("goodsName", goodsName != null ? goodsName : "");
                detail.put("goodsNum", goodsNum != null ? goodsNum : 1);
                detail.put("orderAmount", orderAmount);
                detail.put("platformServiceFee", platformServiceFee);
                detail.put("promotionFee", promotionFee != null ? promotionFee : BigDecimal.ZERO);
                detail.put("freight", freight != null ? freight : BigDecimal.ZERO);
                detail.put("otherFee", otherFee != null ? otherFee : BigDecimal.ZERO);
                detail.put("actualAmount", actualAmount);
                detail.put("orderDate", orderDate);

                details.add(detail);
            }
        }

        return details;
    }

    private static List<Map<String, Object>> parsePddFinanceFormat(BufferedReader reader, String[] headers, Map<String, Integer> headerMap) throws IOException {
        List<Map<String, Object>> details = new ArrayList<>();

        int orderNoIndex = findColumnIndex(headerMap, "商户订单号", "订单号", "订单编号");
        int incomeAmountIndex = findColumnIndex(headerMap, "收入金额（+元）", "收入金额", "income_amount");
        int expenseAmountIndex = findColumnIndex(headerMap, "支出金额（-元）", "支出金额", "expense_amount");
        int financeTypeIndex = findColumnIndex(headerMap, "账务类型", "type");
        int remarkIndex = findColumnIndex(headerMap, "备注", "remark");
        int businessDescIndex = findColumnIndex(headerMap, "业务描述", "business_desc");
        int timeIndex = findColumnIndex(headerMap, "发生时间", "时间", "date");

        String line;
        while ((line = reader.readLine()) != null) {
            if (isEmptyOrSeparatorLine(line)) continue;

            String[] values = splitLine(line);
            if (values.length == 0) continue;

            String orderNo = getValue(values, orderNoIndex);
            if (orderNo != null && (orderNo.startsWith("#") || orderNo.contains("合计") || orderNo.contains("支出"))) continue;

            BigDecimal incomeAmount = parseDecimal(getValue(values, incomeAmountIndex));
            BigDecimal expenseAmount = parseDecimal(getValue(values, expenseAmountIndex));
            String financeType = getValue(values, financeTypeIndex);
            String remark = getValue(values, remarkIndex);
            String businessDesc = getValue(values, businessDescIndex);
            String time = getValue(values, timeIndex);

            Map<String, Object> detail = new HashMap<>();
            detail.put("orderNo", orderNo != null ? orderNo : "");
            detail.put("incomeAmount", incomeAmount);
            detail.put("expenseAmount", expenseAmount);
            detail.put("financeType", financeType);
            detail.put("remark", remark);
            detail.put("businessDesc", businessDesc);
            detail.put("orderDate", time);

            details.add(detail);
        }

        return details;
    }

    private static List<Map<String, Object>> parseExcelFile(MultipartFile file, String platform) throws IOException {
        List<Map<String, Object>> details = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IOException("Excel文件没有表头");
            }

            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String headerValue = getCellValueAsString(cell);
                    if (headerValue != null && !headerValue.isEmpty()) {
                        headerMap.put(headerValue.toLowerCase(), i);
                    }
                }
            }

            int orderNoIndex = findColumnIndex(headerMap, "订单号", "order_no", "订单编号");
            int goodsNameIndex = findColumnIndex(headerMap, "商品名称", "goods_name", "商品");
            int goodsNumIndex = findColumnIndex(headerMap, "商品数量", "goods_num", "数量");
            int orderAmountIndex = findColumnIndex(headerMap, "订单金额", "order_amount", "金额");
            int platformFeeIndex = findColumnIndex(headerMap, "平台服务费", "platform_service_fee", "服务费");
            int promotionFeeIndex = findColumnIndex(headerMap, "推广费", "promotion_fee", "推广服务费");
            int freightIndex = findColumnIndex(headerMap, "运费", "freight");
            int otherFeeIndex = findColumnIndex(headerMap, "其他扣费", "other_fee", "其他费用");
            int actualAmountIndex = findColumnIndex(headerMap, "实收金额", "actual_amount", "实际金额");
            int orderDateIndex = findColumnIndex(headerMap, "订单日期", "order_date", "日期");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String orderNo = getCellValueAsString(row.getCell(orderNoIndex));
                if (orderNo == null || orderNo.isEmpty()) continue;

                Map<String, Object> detail = new HashMap<>();

                String goodsName = getCellValueAsString(row.getCell(goodsNameIndex));
                Integer goodsNum = parseInteger(getCellValueAsString(row.getCell(goodsNumIndex)));
                BigDecimal orderAmount = parseDecimal(getCellValueAsString(row.getCell(orderAmountIndex)));
                BigDecimal platformServiceFee = parseDecimal(getCellValueAsString(row.getCell(platformFeeIndex)));
                BigDecimal promotionFee = parseDecimal(getCellValueAsString(row.getCell(promotionFeeIndex)));
                BigDecimal freight = parseDecimal(getCellValueAsString(row.getCell(freightIndex)));
                BigDecimal otherFee = parseDecimal(getCellValueAsString(row.getCell(otherFeeIndex)));
                BigDecimal actualAmount = parseDecimal(getCellValueAsString(row.getCell(actualAmountIndex)));
                String orderDate = getCellValueAsString(row.getCell(orderDateIndex));

                if (actualAmount == null || actualAmount.compareTo(BigDecimal.ZERO) == 0) {
                    actualAmount = orderAmount.subtract(platformServiceFee).subtract(promotionFee).subtract(freight).subtract(otherFee);
                }

                detail.put("orderNo", orderNo);
                detail.put("goodsName", goodsName != null ? goodsName : "");
                detail.put("goodsNum", goodsNum != null ? goodsNum : 1);
                detail.put("orderAmount", orderAmount);
                detail.put("platformServiceFee", platformServiceFee);
                detail.put("promotionFee", promotionFee != null ? promotionFee : BigDecimal.ZERO);
                detail.put("freight", freight != null ? freight : BigDecimal.ZERO);
                detail.put("otherFee", otherFee != null ? otherFee : BigDecimal.ZERO);
                detail.put("actualAmount", actualAmount);
                detail.put("orderDate", orderDate);

                details.add(detail);
            }
        }

        return details;
    }

    private static String findHeaderLine(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            if (line.startsWith("-") || line.startsWith("=") || line.startsWith("起始时间")) continue;
            if (line.contains("\t") && (line.contains("商户订单号") || line.contains("订单号"))) {
                return line;
            }
            if (line.contains("商户订单号") || line.contains("商品名称")) {
                return line;
            }
        }
        return null;
    }

    private static boolean isEmptyOrSeparatorLine(String line) {
        if (line == null || line.trim().isEmpty()) return true;
        String trimmed = line.trim();
        if (trimmed.startsWith("-") || trimmed.startsWith("=") || trimmed.startsWith("起始时间")) {
            return true;
        }
        return false;
    }

    private static String[] splitLine(String line) {
        if (line == null) return new String[0];
        if (line.contains("\t")) {
            return line.split("\t", -1);
        }
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result.toArray(new String[0]);
    }

    private static String getValue(String[] values, int index) {
        if (index < 0 || index >= values.length) return null;
        String value = values[index].trim();
        return value.isEmpty() ? null : value;
    }

    private static int findColumnIndex(Map<String, Integer> headerMap, String... names) {
        for (String name : names) {
            if (headerMap.containsKey(name.toLowerCase())) {
                return headerMap.get(name.toLowerCase());
            }
        }
        return -1;
    }

    private static BigDecimal parseDecimal(String value) {
        if (value == null || value.isEmpty()) return BigDecimal.ZERO;
        try {
            String cleaned = value.replace(",", "").replace(" ", "");
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private static Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) return 1;
        try {
            String cleaned = value.replace(",", "").replace(" ", "");
            return (int) Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                }
                return new DecimalFormat("#.##").format(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return new DecimalFormat("#.##").format(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            default:
                return null;
        }
    }
}
