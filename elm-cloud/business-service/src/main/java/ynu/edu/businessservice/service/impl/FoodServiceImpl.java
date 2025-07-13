package ynu.edu.businessservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ynu.edu.businessservice.mapper.FoodMapper;
import ynu.edu.businessservice.po.Food;
import ynu.edu.businessservice.service.FoodService;

import java.util.List;

@Service
@Slf4j
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {
    
    @Autowired
    private FoodMapper foodMapper;
    
    @Override
    public List<Food> getFoodsByBusinessId(Integer businessId) {
        log.debug("根据商家ID查询食品列表: businessId={}", businessId);
        LambdaQueryWrapper<Food> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Food::getBusinessId, businessId);
        return foodMapper.selectList(queryWrapper);
    }
} 