package ynu.edu.addressservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.addressservice.po.DeliveryAddress;

import java.util.List;

public interface DeliveryAddressService extends IService<DeliveryAddress> {
    
    /**
     * 根据用户ID查询配送地址列表
     */
    List<DeliveryAddress> listDeliveryAddressByUserId(String userId);
} 