package ynu.edu.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ynu.edu.orderservice.dto.DeliveryAddressDTO;

@FeignClient(name = "address-service", path = "/address")
public interface DeliveryAddressClient {

    /**
     * 根据地址ID获取配送地址信息
     */
    @GetMapping("/{daId}")
    DeliveryAddressDTO getDeliveryAddressById(@PathVariable("daId") Integer daId);
} 