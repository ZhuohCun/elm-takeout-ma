package ynu.edu.orderservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ynu.edu.orderservice.po.OrderDetailet;
import ynu.edu.orderservice.po.Orders;
import ynu.edu.orderservice.service.OrderDetailetService;
import ynu.edu.orderservice.service.OrdersService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    
    @Autowired
    private OrderDetailetService orderDetailetService;

    /**
     * 根据用户ID查询订单列表
     */
    @GetMapping("/user/{userId}")
    public List<Orders> listOrdersByUserId(@PathVariable String userId) {
        log.debug("根据用户ID查询订单列表: userId={}", userId);
        return ordersService.listOrdersWithDetailsByUserId(userId);
    }
    
    /**
     * 根据用户ID查询订单列表（适配前端）
     */
    @PostMapping("/listOrdersByUserId")
    public List<Orders> listOrdersByUserIdPost(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        log.debug("根据用户ID查询订单列表(POST): userId={}", userId);
        return ordersService.listOrdersWithDetailsByUserId(userId);
    }
    
    /**
     * 根据用户ID查询订单列表（适配前端表单）
     */
    @PostMapping(value = "/listOrdersByUserId", consumes = "application/x-www-form-urlencoded")
    public List<Orders> listOrdersByUserIdForm(@RequestParam String userId) {
        log.debug("根据用户ID查询订单列表(表单): userId={}", userId);
        return ordersService.listOrdersWithDetailsByUserId(userId);
    }

    /**
     * 根据订单ID查询订单详情
     */
    @GetMapping("/{orderId}")
    public Orders getOrderById(@PathVariable Integer orderId) {
        log.debug("根据订单ID查询订单详情: orderId={}", orderId);
        return ordersService.getOrderWithDetailsById(orderId);
    }
    
    /**
     * 根据订单ID查询订单详情（适配前端）
     */
    @PostMapping("/getOrdersById")
    public Orders getOrderByIdPost(@RequestBody Map<String, Object> params) {
        Integer orderId = Integer.parseInt(params.get("orderId").toString());
        log.debug("根据订单ID查询订单详情(POST): orderId={}", orderId);
        return ordersService.getOrderWithDetailsById(orderId);
    }
    
    /**
     * 根据订单ID查询订单详情（适配前端表单）
     */
    @PostMapping(value = "/getOrdersById", consumes = "application/x-www-form-urlencoded")
    public Orders getOrderByIdForm(@RequestParam Integer orderId) {
        log.debug("根据订单ID查询订单详情(表单): orderId={}", orderId);
        return ordersService.getOrderWithDetailsById(orderId);
    }
    
    /**
     * 根据订单ID查询订单明细（包含食品信息）
     */
    @PostMapping("/getOrderDetailetByOrderId")
    public List<OrderDetailet> getOrderDetailetByOrderId(@RequestBody Map<String, Object> params) {
        Integer orderId = Integer.parseInt(params.get("orderId").toString());
        log.debug("根据订单ID查询订单明细: orderId={}", orderId);
        // 确保返回带有食品信息的订单明细
        return orderDetailetService.listOrderDetailetByOrderId(orderId);
    }
    
    /**
     * 根据订单ID查询订单明细（适配前端表单）
     */
    @PostMapping(value = "/getOrderDetailetByOrderId", consumes = "application/x-www-form-urlencoded")
    public List<OrderDetailet> getOrderDetailetByOrderIdForm(@RequestParam Integer orderId) {
        log.debug("根据订单ID查询订单明细(表单): orderId={}", orderId);
        // 确保返回带有食品信息的订单明细
        return orderDetailetService.listOrderDetailetByOrderId(orderId);
    }

    /**
     * 创建订单 (适配前端)
     */
    @PostMapping("/createOrders")
    public Integer createOrders(@RequestBody Map<String, Object> orderData) {
        log.debug("创建订单: orderData={}", orderData);
        
        try {
            // 创建订单对象
            Orders orders = new Orders();
            orders.setUserId(orderData.get("userId").toString());
            orders.setBusinessId(Integer.parseInt(orderData.get("businessId").toString()));
            orders.setOrderTotal(Double.parseDouble(orderData.get("orderTotal").toString()));
            orders.setDaId(Integer.parseInt(orderData.get("daId").toString()));
            orders.setOrderDate(LocalDate.now().toString());
            orders.setOrderState(0); // 初始状态：未支付
            
            // 获取订单项
            List<Map<String, Object>> orderItems = (List<Map<String, Object>>) orderData.get("orderItems");
            List<OrderDetailet> orderDetailets = new ArrayList<>();
            
            for (Map<String, Object> item : orderItems) {
                OrderDetailet detailet = new OrderDetailet();
                detailet.setFoodId(Integer.parseInt(item.get("foodId").toString()));
                detailet.setQuantity(Integer.parseInt(item.get("quantity").toString()));
                orderDetailets.add(detailet);
            }
            
            // 调用service层创建订单
            Integer orderId = ordersService.createOrderWithDetailets(orders, orderDetailets);
            log.debug("创建订单成功，订单ID: {}", orderId);
            return orderId;
        } catch (Exception e) {
            log.error("创建订单失败: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * 创建订单 (直接传入订单和订单项数据)
     */
    @PostMapping("/createOrdersWithDetailets")
    public Map<String, Object> createOrdersWithDetailets(@RequestBody Map<String, Object> requestMap) {
        log.debug("创建订单: requestMap={}", requestMap);
        Map<String, Object> result = new HashMap<>();
        
        try {
            Orders orders = new Orders();
            Map<String, Object> ordersMap = (Map<String, Object>) requestMap.get("orders");
            orders.setUserId((String) ordersMap.get("userId"));
            orders.setBusinessId((Integer) ordersMap.get("businessId"));
            orders.setOrderTotal(Double.parseDouble(ordersMap.get("orderTotal").toString()));
            orders.setDaId((Integer) ordersMap.get("daId"));
            
            @SuppressWarnings("unchecked")
            List<Integer> cartIds = (List<Integer>) requestMap.get("cartIds");
            
            Integer orderId = ordersService.createOrder(orders, cartIds);
            
            result.put("success", true);
            result.put("message", "订单创建成功");
            result.put("orderId", orderId);
        } catch (Exception e) {
            log.error("创建订单失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "订单创建失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 更新订单状态（适配前端）
     */
    @PostMapping("/updateOrderState")
    public Map<String, Object> updateOrderStatePost(@RequestBody Map<String, Object> params) {
        Integer orderId = Integer.parseInt(params.get("orderId").toString());
        Integer orderState = Integer.parseInt(params.get("orderState").toString());
        log.debug("更新订单状态(POST): orderId={}, orderState={}", orderId, orderState);
        return updateOrderState(orderId, orderState);
    }
    
    /**
     * 更新订单状态（适配前端表单）
     */
    @PostMapping(value = "/updateOrderState", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> updateOrderStateForm(@RequestParam Integer orderId, @RequestParam Integer orderState) {
        log.debug("更新订单状态(表单): orderId={}, orderState={}", orderId, orderState);
        return updateOrderState(orderId, orderState);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{orderId}/state/{orderState}")
    public Map<String, Object> updateOrderState(
            @PathVariable Integer orderId,
            @PathVariable Integer orderState) {
        log.debug("更新订单状态: orderId={}, orderState={}", orderId, orderState);
        Map<String, Object> result = new HashMap<>();
        boolean success = ordersService.updateOrderState(orderId, orderState);
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        return result;
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{orderId}")
    public Map<String, Object> removeOrder(@PathVariable Integer orderId) {
        log.debug("删除订单: orderId={}", orderId);
        Map<String, Object> result = new HashMap<>();
        boolean success = ordersService.removeOrder(orderId);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
} 