package ynu.edu.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ynu.edu.orderservice.po.OrderDetailet;

import java.util.List;

@Mapper
public interface OrderDetailetMapper extends BaseMapper<OrderDetailet> {
    
    /**
     * 根据订单ID查询订单明细
     */
    List<OrderDetailet> listOrderDetailetByOrderId(Integer orderId);
} 