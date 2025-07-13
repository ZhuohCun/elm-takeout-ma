package ynu.edu.businessservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ynu.edu.businessservice.po.Food;
import ynu.edu.businessservice.service.FoodService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/food")
@Slf4j
public class FoodController {

    @Autowired
    private FoodService foodService;

    /**
     * 根据商家ID获取食品列表
     */
    @PostMapping("/listFoodByBusinessId")
    public List<Food> listFoodByBusinessId(@RequestBody Map<String, Object> params) {
        Integer businessId = Integer.valueOf(params.get("businessId").toString());
        log.debug("根据商家ID获取食品列表: {}", businessId);
        return foodService.list(new QueryWrapper<Food>().eq("business_id", businessId));
    }
    
    /**
     * 根据商家ID获取食品列表 (处理表单数据)
     */
    @PostMapping(value = "/listFoodByBusinessId", consumes = "application/x-www-form-urlencoded")
    public List<Food> listFoodByBusinessIdForm(@RequestParam Integer businessId) {
        log.debug("根据商家ID获取食品列表(表单): {}", businessId);
        return foodService.list(new QueryWrapper<Food>().eq("business_id", businessId));
    }

    /**
     * 根据食品ID获取食品信息
     */
    @PostMapping("/getFoodById")
    public Food getFoodById(@RequestBody Map<String, Object> params) {
        Integer foodId = null;
        try {
            // 尝试从JSON中提取foodId
            foodId = Integer.valueOf(params.get("foodId").toString());
            log.debug("根据食品ID获取食品信息(JSON): {}", foodId);
            return foodService.getById(foodId);
        } catch (Exception e) {
            log.error("处理getFoodById请求出错: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 根据食品ID获取食品信息 (处理表单数据)
     */
    @PostMapping(value = "/getFoodById", consumes = "application/x-www-form-urlencoded")
    public Food getFoodByIdForm(@RequestParam Integer foodId) {
        log.debug("根据食品ID获取食品信息(表单): {}", foodId);
        return foodService.getById(foodId);
    }
    
    /**
     * 根据食品ID获取食品信息 (GET方法)
     */
    @GetMapping("/{foodId}")
    public Food getFoodById(@PathVariable Integer foodId) {
        log.debug("根据食品ID获取食品信息(GET): {}", foodId);
        return foodService.getById(foodId);
    }
} 