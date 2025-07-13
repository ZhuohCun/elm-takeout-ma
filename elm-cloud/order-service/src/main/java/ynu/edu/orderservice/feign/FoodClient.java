package ynu.edu.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ynu.edu.orderservice.dto.FoodDTO;

@FeignClient(name = "business-service", path = "/food", fallback = FoodClient.FoodClientFallback.class)
public interface FoodClient {

    /**
     * 根据食品ID获取食品信息
     */
    @GetMapping("/{foodId}")
    FoodDTO getFoodById(@PathVariable("foodId") Integer foodId);
    
    /**
     * 降级处理类
     */
    @Component
    class FoodClientFallback implements FoodClient {
        @Override
        public FoodDTO getFoodById(Integer foodId) {
            // 返回一个基础的食品对象，避免NPE
            FoodDTO fallbackFood = new FoodDTO();
            fallbackFood.setFoodId(foodId);
            fallbackFood.setFoodName("暂无食品信息");
            fallbackFood.setFoodPrice(0.0);
            return fallbackFood;
        }
    }
} 