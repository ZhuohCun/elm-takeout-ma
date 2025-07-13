package ynu.edu.orderservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ynu.edu.orderservice.feign.BusinessClient;
import ynu.edu.orderservice.feign.DeliveryAddressClient;
import ynu.edu.orderservice.mapper.OrdersMapper;
import ynu.edu.orderservice.po.Cart;
import ynu.edu.orderservice.po.OrderDetailet;
import ynu.edu.orderservice.po.Orders;
import ynu.edu.orderservice.service.CartService;
import ynu.edu.orderservice.service.OrderDetailetService;
import ynu.edu.orderservice.service.OrdersService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    
    @Autowired
    private OrdersMapper ordersMapper;
    
    @Autowired
    private OrderDetailetService orderDetailetService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private BusinessClient businessClient;
    
    @Autowired
    private DeliveryAddressClient deliveryAddressClient;
    
    @Override
    public List<Orders> listOrdersWithDetailsByUserId(String userId) {
        log.debug("根据用户ID查询订单列表: userId={}", userId);
        List<Orders> ordersList = ordersMapper.listOrdersByUserId(userId);
        
        // 填充订单明细、商家和配送地址信息
        for (Orders orders : ordersList) {
            try {
                orders.setBusiness(businessClient.getBusinessById(orders.getBusinessId()));
            } catch (Exception e) {
                log.error("调用商家服务失败: {}", e.getMessage());
            }
            
            try {
                orders.setDeliveryAddress(deliveryAddressClient.getDeliveryAddressById(orders.getDaId()));
            } catch (Exception e) {
                log.error("调用配送地址服务失败: {}", e.getMessage());
            }
            
            orders.setOrderDetailetList(orderDetailetService.listOrderDetailetByOrderId(orders.getOrderId()));
        }
        
        return ordersList;
    }
    
    @Override
    public Orders getOrderWithDetailsById(Integer orderId) {
        log.debug("根据订单ID查询订单: orderId={}", orderId);
        Orders orders = getById(orderId);
        
        if (orders != null) {
            try {
                orders.setBusiness(businessClient.getBusinessById(orders.getBusinessId()));
            } catch (Exception e) {
                log.error("调用商家服务失败: {}", e.getMessage());
            }
            
            try {
                orders.setDeliveryAddress(deliveryAddressClient.getDeliveryAddressById(orders.getDaId()));
            } catch (Exception e) {
                log.error("调用配送地址服务失败: {}", e.getMessage());
            }
            
            orders.setOrderDetailetList(orderDetailetService.listOrderDetailetByOrderId(orderId));
        }
        
        return orders;
    }
    
    @Override
    @Transactional
    public Integer createOrderWithDetailets(Orders orders, List<OrderDetailet> orderDetailets) {
        log.debug("创建订单和订单明细: orders={}, orderDetailets={}", orders, orderDetailets);
        
        // 设置订单日期（如果未设置）
        if (orders.getOrderDate() == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            orders.setOrderDate(sdf.format(new Date()));
        }
        
        // 保存订单
        boolean saveResult = save(orders);
        if (!saveResult) {
            log.error("保存订单失败");
            return 0;
        }
        
        Integer orderId = orders.getOrderId();
        
        // 设置订单明细的订单ID，并保存
        for (OrderDetailet detailet : orderDetailets) {
            detailet.setOrderId(orderId);
        }
        
        // 批量保存订单明细
        boolean detailetResult = orderDetailetService.saveBatch(orderDetailets);
        if (!detailetResult) {
            log.error("保存订单明细失败");
            return 0;
        }
        
        // 获取用户的购物车，将相关项标记为已下单
        List<Cart> userCart = cartService.listCartByUserIdAndBusinessId(
                orders.getUserId(), orders.getBusinessId());
        
        for (Cart cart : userCart) {
            // 查找对应的订单明细
            for (OrderDetailet detailet : orderDetailets) {
                if (cart.getFoodId().equals(detailet.getFoodId())) {
                    // 更新购物车中的订单ID
                    cartService.updateOrderId(cart.getCartId(), orderId);
                    break;
                }
            }
        }
        
        log.debug("订单创建成功，订单ID: {}", orderId);
        return orderId;
    }
    
    @Override
    @Transactional
    public Integer createOrder(Orders orders, List<Integer> cartIds) {
        log.debug("创建订单: orders={}, cartIds={}", orders, cartIds);
        
        // 设置订单日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orders.setOrderDate(sdf.format(new Date()));
        
        // 设置订单状态（0：未支付）
        orders.setOrderState(0);
        
        // 保存订单
        save(orders);
        
        // 从购物车中获取商品信息
        List<Cart> cartList = cartService.listByIds(cartIds);
        
        // 创建订单明细
        List<OrderDetailet> orderDetailetList = new ArrayList<>();
        for (Cart cart : cartList) {
            OrderDetailet orderDetailet = new OrderDetailet();
            orderDetailet.setOrderId(orders.getOrderId());
            orderDetailet.setFoodId(cart.getFoodId());
            orderDetailet.setQuantity(cart.getQuantity());
            orderDetailetList.add(orderDetailet);
            
            // 更新购物车中的订单ID
            cartService.updateOrderId(cart.getCartId(), orders.getOrderId());
        }
        
        // 批量保存订单明细
        orderDetailetService.saveBatchOrderDetailet(orderDetailetList);
        
        return orders.getOrderId();
    }
    
    @Override
    public boolean updateOrderState(Integer orderId, Integer orderState) {
        log.debug("更新订单状态: orderId={}, orderState={}", orderId, orderState);
        return ordersMapper.updateOrderState(orderId, orderState) > 0;
    }
    
    @Override
    @Transactional
    public boolean removeOrder(Integer orderId) {
        log.debug("删除订单: orderId={}", orderId);
        return removeById(orderId);
    }
} 