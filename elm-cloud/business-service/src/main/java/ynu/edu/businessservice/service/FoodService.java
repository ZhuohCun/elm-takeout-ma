package ynu.edu.businessservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.businessservice.po.Food;

import java.util.List;

public interface FoodService extends IService<Food> {
    
    /**
     * 根据商家ID查询食品列表
     */
    List<Food> getFoodsByBusinessId(Integer businessId);
} 