package ynu.edu.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.orderservice.po.OrderDetailet;

import java.util.List;

public interface OrderDetailetService extends IService<OrderDetailet> {
    
    /**
     * 根据订单ID查询订单明细列表
     */
    List<OrderDetailet> listOrderDetailetByOrderId(Integer orderId);
    
    /**
     * 批量保存订单明细
     */
    boolean saveBatchOrderDetailet(List<OrderDetailet> orderDetailetList);
} 