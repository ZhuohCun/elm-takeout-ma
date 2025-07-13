package ynu.edu.orderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ynu.edu.orderservice.feign.BusinessClient;
import ynu.edu.orderservice.feign.FoodClient;
import ynu.edu.orderservice.mapper.CartMapper;
import ynu.edu.orderservice.po.Cart;
import ynu.edu.orderservice.service.CartService;

import java.util.List;

@Service
@Slf4j
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private FoodClient foodClient;
    
    @Autowired
    private BusinessClient businessClient;
    
    @Override
    public List<Cart> listCartByUserId(String userId) {
        log.debug("根据用户ID查询购物车: userId={}", userId);
        List<Cart> cartList = list(new QueryWrapper<Cart>().eq("user_id", userId));
        
        // 填充食品和商家信息，如果服务调用失败，仍返回基本购物车信息
        for (Cart cart : cartList) {
            enrichCartWithFoodInfo(cart);
            enrichCartWithBusinessInfo(cart);
        }
        
        return cartList;
    }
    
    @Override
    public List<Cart> listCartByUserIdAndBusinessId(String userId, Integer businessId) {
        log.debug("根据用户ID和商家ID查询购物车: userId={}, businessId={}", userId, businessId);
        List<Cart> cartList = cartMapper.listCartByUserIdAndBusinessId(userId, businessId);
        
        // 填充食品和商家信息，如果服务调用失败，仍返回基本购物车信息
        for (Cart cart : cartList) {
            enrichCartWithFoodInfo(cart);
            enrichCartWithBusinessInfo(cart);
        }
        
        return cartList;
    }
    
    @Override
    public List<Cart> listAllCartByUserIdAndBusinessId(String userId, Integer businessId) {
        log.debug("查询所有购物车项（包括已加入订单的项）: userId={}, businessId={}", userId, businessId);
        List<Cart> cartList = cartMapper.listAllCartByUserIdAndBusinessId(userId, businessId);
        
        // 填充食品和商家信息，如果服务调用失败，仍返回基本购物车信息
        for (Cart cart : cartList) {
            enrichCartWithFoodInfo(cart);
            enrichCartWithBusinessInfo(cart);
        }
        
        return cartList;
    }
    
    /**
     * 填充购物车项的食品信息，包含错误处理
     */
    private void enrichCartWithFoodInfo(Cart cart) {
        try {
            cart.setFood(foodClient.getFoodById(cart.getFoodId()));
        } catch (Exception e) {
            log.error("调用食品服务失败，食品ID: {}, 错误: {}", cart.getFoodId(), e.getMessage());
            // 保持cart.food为null，不影响整体结果返回
        }
    }
    
    /**
     * 填充购物车项的商家信息，包含错误处理
     */
    private void enrichCartWithBusinessInfo(Cart cart) {
        try {
            cart.setBusiness(businessClient.getBusinessById(cart.getBusinessId()));
        } catch (Exception e) {
            log.error("调用商家服务失败，商家ID: {}, 错误: {}", cart.getBusinessId(), e.getMessage());
            // 保持cart.business为null，不影响整体结果返回
        }
    }
    
    @Override
    public boolean saveCart(Cart cart) {
        log.debug("添加商品到购物车: {}", cart);
        return save(cart);
    }
    
    @Override
    public boolean updateCart(Cart cart) {
        log.debug("更新购物车商品数量: {}", cart);
        return updateById(cart);
    }
    
    @Override
    public boolean removeCart(Integer cartId) {
        log.debug("删除购物车中的商品: cartId={}", cartId);
        return removeById(cartId);
    }
    
    @Override
    public boolean removeBatchCart(List<Integer> cartIds) {
        log.debug("批量删除购物车中的商品: cartIds={}", cartIds);
        return removeByIds(cartIds);
    }
    
    @Override
    public boolean updateOrderId(Integer cartId, Integer orderId) {
        log.debug("更新购物车中的订单ID: cartId={}, orderId={}", cartId, orderId);
        return cartMapper.updateOrderIdByCartId(cartId, orderId) > 0;
    }
} 