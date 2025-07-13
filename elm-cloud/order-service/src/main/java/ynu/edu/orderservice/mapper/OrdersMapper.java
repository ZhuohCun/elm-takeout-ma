package ynu.edu.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ynu.edu.orderservice.po.Orders;

import java.util.List;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    
    /**
     * 根据用户ID查询订单
     */
    List<Orders> listOrdersByUserId(String userId);
    
    /**
     * 更新订单状态
     */
    int updateOrderState(@Param("orderId") Integer orderId, @Param("orderState") Integer orderState);
} 