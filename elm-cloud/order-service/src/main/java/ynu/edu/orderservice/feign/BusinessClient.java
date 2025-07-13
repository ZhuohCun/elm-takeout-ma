package ynu.edu.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ynu.edu.orderservice.dto.BusinessDTO;

@FeignClient(name = "business-service", path = "/business", fallback = BusinessClient.BusinessClientFallback.class)
public interface BusinessClient {

    /**
     * 根据商家ID获取商家信息
     */
    @GetMapping("/{businessId}")
    BusinessDTO getBusinessById(@PathVariable("businessId") Integer businessId);
    
    /**
     * 降级处理类
     */
    @Component
    class BusinessClientFallback implements BusinessClient {
        @Override
        public BusinessDTO getBusinessById(Integer businessId) {
            // 返回一个基础的商家对象，避免NPE
            BusinessDTO fallbackBusiness = new BusinessDTO();
            fallbackBusiness.setBusinessId(businessId);
            fallbackBusiness.setBusinessName("暂无商家信息");
            return fallbackBusiness;
        }
    }
} 