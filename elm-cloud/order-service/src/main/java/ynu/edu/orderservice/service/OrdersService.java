package ynu.edu.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.orderservice.po.OrderDetailet;
import ynu.edu.orderservice.po.Orders;

import java.util.List;

public interface OrdersService extends IService<Orders> {
    
    /**
     * 根据用户ID查询订单列表（包含订单明细）
     */
    List<Orders> listOrdersWithDetailsByUserId(String userId);
    
    /**
     * 根据订单ID查询订单（包含订单明细）
     */
    Orders getOrderWithDetailsById(Integer orderId);
    
    /**
     * 创建订单
     * 
     * @param orders 订单信息
     * @param cartIds 购物车ID列表
     * @return 订单ID
     */
    Integer createOrder(Orders orders, List<Integer> cartIds);
    
    /**
     * 创建订单并添加订单明细
     * 
     * @param orders 订单信息
     * @param orderDetailets 订单明细列表
     * @return 订单ID
     */
    Integer createOrderWithDetailets(Orders orders, List<OrderDetailet> orderDetailets);
    
    /**
     * 更新订单状态
     */
    boolean updateOrderState(Integer orderId, Integer orderState);
    
    /**
     * 删除订单
     */
    boolean removeOrder(Integer orderId);
} 