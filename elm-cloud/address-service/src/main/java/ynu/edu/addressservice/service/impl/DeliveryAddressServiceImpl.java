package ynu.edu.addressservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ynu.edu.addressservice.mapper.DeliveryAddressMapper;
import ynu.edu.addressservice.po.DeliveryAddress;
import ynu.edu.addressservice.service.DeliveryAddressService;

import java.util.List;

@Service
@Slf4j
public class DeliveryAddressServiceImpl extends ServiceImpl<DeliveryAddressMapper, DeliveryAddress> implements DeliveryAddressService {
    
    @Autowired
    private DeliveryAddressMapper deliveryAddressMapper;
    
    @Override
    public List<DeliveryAddress> listDeliveryAddressByUserId(String userId) {
        log.debug("根据用户ID查询配送地址列表: userId={}", userId);
        LambdaQueryWrapper<DeliveryAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeliveryAddress::getUserId, userId);
        return deliveryAddressMapper.selectList(queryWrapper);
    }
} 