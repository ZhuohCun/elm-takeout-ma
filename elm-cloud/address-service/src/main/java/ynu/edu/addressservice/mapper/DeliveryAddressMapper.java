package ynu.edu.addressservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ynu.edu.addressservice.po.DeliveryAddress;

@Mapper
public interface DeliveryAddressMapper extends BaseMapper<DeliveryAddress> {
} 