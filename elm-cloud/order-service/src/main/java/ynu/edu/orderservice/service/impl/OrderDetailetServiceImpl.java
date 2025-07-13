package ynu.edu.orderservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ynu.edu.orderservice.feign.FoodClient;
import ynu.edu.orderservice.mapper.OrderDetailetMapper;
import ynu.edu.orderservice.po.OrderDetailet;
import ynu.edu.orderservice.service.OrderDetailetService;

import java.util.List;

@Service
@Slf4j
public class OrderDetailetServiceImpl extends ServiceImpl<OrderDetailetMapper, OrderDetailet> implements OrderDetailetService {
    
    @Autowired
    private OrderDetailetMapper orderDetailetMapper;
    
    @Autowired
    private FoodClient foodClient;
    
    @Override
    public List<OrderDetailet> listOrderDetailetByOrderId(Integer orderId) {
        log.debug("根据订单ID查询订单明细: orderId={}", orderId);
        List<OrderDetailet> detailetList = orderDetailetMapper.listOrderDetailetByOrderId(orderId);
        
        // 填充食品信息
        for (OrderDetailet detailet : detailetList) {
            try {
                detailet.setFood(foodClient.getFoodById(detailet.getFoodId()));
                log.debug("获取到食品信息: foodId={}, food={}", detailet.getFoodId(), detailet.getFood());
            } catch (Exception e) {
                log.error("调用食品服务失败: foodId={}, error={}", detailet.getFoodId(), e.getMessage());
            }
        }
        
        return detailetList;
    }
    
    @Override
    public boolean saveBatchOrderDetailet(List<OrderDetailet> orderDetailetList) {
        log.debug("批量保存订单明细: {}", orderDetailetList);
        return saveBatch(orderDetailetList);
    }
} 