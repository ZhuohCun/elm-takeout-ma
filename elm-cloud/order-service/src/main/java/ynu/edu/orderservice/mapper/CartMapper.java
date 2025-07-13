package ynu.edu.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ynu.edu.orderservice.po.Cart;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
    
    /**
     * 根据用户ID和商家ID查询购物车
     */
    List<Cart> listCartByUserIdAndBusinessId(@Param("userId") String userId, @Param("businessId") Integer businessId);
    
    /**
     * 根据用户ID和商家ID查询所有购物车项（包括已加入订单的项）
     */
    List<Cart> listAllCartByUserIdAndBusinessId(@Param("userId") String userId, @Param("businessId") Integer businessId);
    
    /**
     * 更新购物车中的订单ID
     */
    int updateOrderIdByCartId(@Param("cartId") Integer cartId, @Param("orderId") Integer orderId);
} 