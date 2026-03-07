package cn.qihangerp.erp.service;

import dev.langchain4j.agent.tool.Tool;
import java.util.List;

/**
 * 订单工具服务，用于AI查询订单信息
 */
public class OrderToolService {
    
    private final OrderService orderService;
    
    public OrderToolService() {
        this.orderService = new OrderService();
    }
    
    /**
     * 查询指定订单号的订单信息
     * @param orderId 订单号
     * @return 订单信息
     */
    @Tool("根据订单号查询订单信息")
    public String getOrderById(String orderId) {
        OrderService.Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "未找到订单号为" + orderId + "的订单";
        }
        return order.toString().replace("\n", "<br>");
    }
    
    /**
     * 查询所有订单
     * @return 所有订单信息
     */
    @Tool("获取所有订单信息")
    public String getAllOrders() {
        List<OrderService.Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            return "暂无订单数据";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("所有订单信息：<br>");
        for (OrderService.Order order : orders) {
            sb.append(order.toString()).append("<br>");
        }
        return sb.toString();
    }
    
    /**
     * 查询指定状态的订单
     * @param status 订单状态
     * @return 订单信息
     */
    @Tool("根据状态查询订单信息")
    public String getOrdersByStatus(String status) {
        List<OrderService.Order> orders = orderService.getOrdersByStatus(status);
        if (orders.isEmpty()) {
            return "暂无状态为" + status + "的订单";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("状态为").append(status).append("的订单：<br>");
        for (OrderService.Order order : orders) {
            sb.append(order.toString()).append("<br>");
        }
        return sb.toString();
    }
    
    /**
     * 查询待发货订单
     * @return 待发货订单信息
     */
    @Tool("获取待发货订单信息")
    public String getPendingOrders() {
        List<OrderService.Order> orders = orderService.getPendingOrders();
        if (orders.isEmpty()) {
            return "暂无待发货订单";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("待发货订单：<br>");
        for (OrderService.Order order : orders) {
            sb.append(order.toString()).append("<br>");
        }
        return sb.toString();
    }
}