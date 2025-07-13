package ynu.edu.orderservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ynu.edu.orderservice.po.Cart;
import ynu.edu.orderservice.service.CartService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 根据用户ID和商家ID查询购物车
     */
    @GetMapping("/user/{userId}/business/{businessId}")
    public List<Cart> listCartByUserIdAndBusinessId(
            @PathVariable String userId,
            @PathVariable Integer businessId) {
        log.debug("根据用户ID和商家ID查询购物车: userId={}, businessId={}", userId, businessId);
        return cartService.listCartByUserIdAndBusinessId(userId, businessId);
    }
    
    /**
     * 根据用户ID和商家ID查询所有购物车项（包括已加入订单的项）
     */
    @GetMapping("/user/{userId}/business/{businessId}/all")
    public List<Cart> listAllCartByUserIdAndBusinessId(
            @PathVariable String userId,
            @PathVariable Integer businessId) {
        log.debug("查询所有购物车项（包括已加入订单的项）: userId={}, businessId={}", userId, businessId);
        return cartService.listAllCartByUserIdAndBusinessId(userId, businessId);
    }
    
    /**
     * 根据用户ID和商家ID查询购物车 (适配前端)
     */
    @PostMapping("/listCartByBusinessId")
    public List<Cart> listCartByBusinessId(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        Integer businessId = Integer.valueOf(params.get("businessId").toString());
        log.debug("根据用户ID和商家ID查询购物车(JSON): userId={}, businessId={}", userId, businessId);
        return cartService.listCartByUserIdAndBusinessId(userId, businessId);
    }
    
    /**
     * 根据用户ID和商家ID查询所有购物车项（包括已加入订单的项）(适配前端)
     */
    @PostMapping("/listAllCartByBusinessId")
    public List<Cart> listAllCartByBusinessId(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        Integer businessId = Integer.valueOf(params.get("businessId").toString());
        log.debug("查询所有购物车项（包括已加入订单的项）(JSON): userId={}, businessId={}", userId, businessId);
        return cartService.listAllCartByUserIdAndBusinessId(userId, businessId);
    }
    
    /**
     * 根据用户ID和商家ID查询购物车 (适配前端表单)
     */
    @PostMapping(value = "/listCartByBusinessId", consumes = "application/x-www-form-urlencoded")
    public List<Cart> listCartByBusinessIdForm(@RequestParam String userId, @RequestParam Integer businessId) {
        log.debug("根据用户ID和商家ID查询购物车(表单): userId={}, businessId={}", userId, businessId);
        return cartService.listCartByUserIdAndBusinessId(userId, businessId);
    }
    
    /**
     * 根据用户ID和商家ID查询所有购物车项（包括已加入订单的项）(适配前端表单)
     */
    @PostMapping(value = "/listAllCartByBusinessId", consumes = "application/x-www-form-urlencoded")
    public List<Cart> listAllCartByBusinessIdForm(@RequestParam String userId, @RequestParam Integer businessId) {
        log.debug("查询所有购物车项（包括已加入订单的项）(表单): userId={}, businessId={}", userId, businessId);
        return cartService.listAllCartByUserIdAndBusinessId(userId, businessId);
    }
    
    /**
     * 根据用户ID查询购物车（适配前端）
     */
    @PostMapping("/listCart")
    public List<Cart> listCart(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        log.debug("根据用户ID查询购物车: userId={}", userId);
        return cartService.listCartByUserId(userId);
    }
    
    /**
     * 根据用户ID查询购物车（适配前端表单）
     */
    @PostMapping(value = "/listCart", consumes = "application/x-www-form-urlencoded")
    public List<Cart> listCartForm(@RequestParam String userId) {
        log.debug("根据用户ID查询购物车(表单): userId={}", userId);
        return cartService.listCartByUserId(userId);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping
    public Map<String, Object> saveCart(@RequestBody Cart cart) {
        log.debug("添加商品到购物车: {}", cart);
        Map<String, Object> result = new HashMap<>();
        boolean success = cartService.saveCart(cart);
        result.put("success", success);
        result.put("message", success ? "添加成功" : "添加失败");
        if (success) {
            result.put("cartId", cart.getCartId());
        }
        return result;
    }
    
    /**
     * 添加商品到购物车（适配前端）
     */
    @PostMapping("/saveCart")
    public Map<String, Object> saveCartApi(@RequestBody Cart cart) {
        log.debug("添加商品到购物车(API): {}", cart);
        return saveCart(cart);
    }
    
    /**
     * 添加商品到购物车（适配前端表单）
     */
    @PostMapping(value = "/saveCart", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> saveCartForm(Cart cart) {
        log.debug("添加商品到购物车(表单): {}", cart);
        return saveCart(cart);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping
    public Map<String, Object> updateCart(@RequestBody Cart cart) {
        log.debug("更新购物车商品数量: {}", cart);
        Map<String, Object> result = new HashMap<>();
        boolean success = cartService.updateCart(cart);
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        return result;
    }
    
    /**
     * 更新购物车商品数量（适配前端）
     */
    @PostMapping("/updateCart")
    public Map<String, Object> updateCartApi(@RequestBody Cart cart) {
        log.debug("更新购物车商品数量(API): {}", cart);
        return updateCart(cart);
    }
    
    /**
     * 更新购物车商品数量（适配前端表单）
     */
    @PostMapping(value = "/updateCart", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> updateCartForm(Cart cart) {
        log.debug("更新购物车商品数量(表单): {}", cart);
        return updateCart(cart);
    }

    /**
     * 删除购物车中的商品
     */
    @DeleteMapping("/{cartId}")
    public Map<String, Object> removeCart(@PathVariable Integer cartId) {
        log.debug("删除购物车中的商品: cartId={}", cartId);
        Map<String, Object> result = new HashMap<>();
        boolean success = cartService.removeCart(cartId);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
    
    /**
     * 删除购物车中的商品（适配前端）
     */
    @PostMapping("/removeCart")
    public Map<String, Object> removeCartApi(@RequestBody Map<String, Object> params) {
        Integer cartId = Integer.parseInt(params.get("cartId").toString());
        log.debug("删除购物车中的商品(API): cartId={}", cartId);
        return removeCart(cartId);
    }
    
    /**
     * 删除购物车中的商品（适配前端表单）
     */
    @PostMapping(value = "/removeCart", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> removeCartForm(@RequestParam Integer cartId) {
        log.debug("删除购物车中的商品(表单): cartId={}", cartId);
        return removeCart(cartId);
    }

    /**
     * 批量删除购物车中的商品
     */
    @DeleteMapping("/batch")
    public Map<String, Object> removeBatchCart(@RequestBody List<Integer> cartIds) {
        log.debug("批量删除购物车中的商品: cartIds={}", cartIds);
        Map<String, Object> result = new HashMap<>();
        boolean success = cartService.removeBatchCart(cartIds);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
} 