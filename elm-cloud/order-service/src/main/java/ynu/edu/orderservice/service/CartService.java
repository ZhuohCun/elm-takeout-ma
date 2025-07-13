package ynu.edu.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.orderservice.po.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {
    
    /**
     * 根据用户ID查询购物车
     */
    List<Cart> listCartByUserId(String userId);
    
    /**
     * 根据用户ID和商家ID查询购物车
     */
    List<Cart> listCartByUserIdAndBusinessId(String userId, Integer businessId);
    
    /**
     * 根据用户ID和商家ID查询所有购物车项（包括已加入订单的项）
     */
    List<Cart> listAllCartByUserIdAndBusinessId(String userId, Integer businessId);
    
    /**
     * 添加商品到购物车
     */
    boolean saveCart(Cart cart);
    
    /**
     * 更新购物车商品数量
     */
    boolean updateCart(Cart cart);
    
    /**
     * 删除购物车中的商品
     */
    boolean removeCart(Integer cartId);
    
    /**
     * 批量删除购物车中的商品
     */
    boolean removeBatchCart(List<Integer> cartIds);
    
    /**
     * 更新购物车中的订单ID
     */
    boolean updateOrderId(Integer cartId, Integer orderId);
} 