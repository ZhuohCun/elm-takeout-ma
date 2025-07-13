package ynu.edu.businessservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ynu.edu.businessservice.po.Food;

@Mapper
public interface FoodMapper extends BaseMapper<Food> {
} 