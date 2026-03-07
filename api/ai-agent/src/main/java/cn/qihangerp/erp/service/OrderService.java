package cn.qihangerp.erp.service;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单服务类，用于查询订单信息
 */
@Slf4j
public class OrderService {
    
    // 模拟订单数据
    private static final Map<String, Order> orderMap = new ConcurrentHashMap<>();
    
    static {
        // 初始化一些模拟订单数据
        orderMap.put("ORDER001", new Order("ORDER001", "2026-03-01", "张三", 1299.99, "待发货"));
        orderMap.put("ORDER002", new Order("ORDER002", "2026-03-02", "李四", 899.99, "已发货"));
        orderMap.put("ORDER003", new Order("ORDER003", "2026-03-03", "王五", 599.99, "已完成"));
        orderMap.put("ORDER004", new Order("ORDER004", "2026-03-04", "赵六", 1999.99, "待发货"));
        orderMap.put("ORDER005", new Order("ORDER005", "2026-03-05", "钱七", 399.99, "已发货"));
        orderMap.put("ORDER006", new Order("ORDER006", "2026-03-06", "齐", 399.99, "已发货"));
        orderMap.put("ORDER007", new Order("ORDER007", "2026-03-07", "钱", 399.99, "未发货"));
    }
    
    /**
     * 根据订单号查询订单
     * @param orderId 订单号
     * @return 订单信息
     */
    public Order getOrderById(String orderId) {
        return orderMap.get(orderId);
    }
    
    /**
     * 获取所有订单
     * @return 订单列表
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(orderMap.values());
    }
    
    /**
     * 根据状态查询订单
     * @param status 订单状态
     * @return 订单列表
     */
    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        for (Order order : orderMap.values()) {
            if (order.getStatus().equals(status)) {
                orders.add(order);
            }
        }
        return orders;
    }
    
    /**
     * 获取待发货订单
     * @return 待发货订单列表
     */
    public List<Order> getPendingOrders() {
        return getOrdersByStatus("待发货");
    }
    
    /**
     * 根据日期查询订单
     * @param date 订单日期
     * @return 订单列表
     */
    public List<Order> getOrdersByDate(String date) {
        log.info("=========根据日期查询订单：{}",date);
        List<Order> orders = new ArrayList<>();
        for (Order order : orderMap.values()) {
            if (order.getOrderDate().equals(date)) {
                orders.add(order);
            }
        }
        return orders;
    }
    
    /**
     * 订单实体类
     */
    public static class Order {
        private String orderId;
        private String orderDate;
        private String customerName;
        private double totalAmount;
        private String status;
        
        public Order(String orderId, String orderDate, String customerName, double totalAmount, String status) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.customerName = customerName;
            this.totalAmount = totalAmount;
            this.status = status;
        }
        
        public String getOrderId() {
            return orderId;
        }
        
        public String getOrderDate() {
            return orderDate;
        }
        
        public String getCustomerName() {
            return customerName;
        }
        
        public double getTotalAmount() {
            return totalAmount;
        }
        
        public String getStatus() {
            return status;
        }
        
        @Override
        public String toString() {
            return "订单号: " + orderId + ", 日期: " + orderDate + ", 客户: " + customerName + ", 金额: " + totalAmount + ", 状态: " + status;
        }
    }
}