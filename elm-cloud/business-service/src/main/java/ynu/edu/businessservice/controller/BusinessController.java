package ynu.edu.businessservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ynu.edu.businessservice.po.Business;
import ynu.edu.businessservice.service.BusinessService;
import ynu.edu.businessservice.utils.PageResult;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/business")
@Slf4j
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    /**
     * 根据订单类型id查询商家信息
     */
    @PostMapping("/listBusinessByOrderTypeId")
    public List<Business> listBusinessByOrderTypeId(@RequestBody Map<String, Object> params) {
        Integer orderTypeId = Integer.valueOf(params.get("orderTypeId").toString());
        log.debug("根据订单类型id查询商家信息: {}", orderTypeId);
        return businessService.list(new QueryWrapper<Business>().eq("order_type_id", orderTypeId));
    }
    
    /**
     * 根据订单类型id查询商家信息 (处理表单数据)
     */
    @PostMapping(value = "/listBusinessByOrderTypeId", consumes = "application/x-www-form-urlencoded")
    public List<Business> listBusinessByOrderTypeIdForm(@RequestParam Integer orderTypeId) {
        log.debug("根据订单类型id查询商家信息(表单): {}", orderTypeId);
        return businessService.list(new QueryWrapper<Business>().eq("order_type_id", orderTypeId));
    }

    /**
     * 根据商家id查询商家信息 (GET方法)
     */
    @GetMapping("/{businessId}")
    public Business getBusinessById(@PathVariable Integer businessId) {
        log.debug("根据商家id查询商家信息(GET): {}", businessId);
        return businessService.getById(businessId);
    }
    
    /**
     * 根据商家id查询商家信息 (POST方法，兼容前端)
     */
    @PostMapping("/getBusinessById")
    public Business getBusinessByIdPost(@RequestBody Map<String, Object> params) {
        try {
            Integer businessId = Integer.valueOf(params.get("businessId").toString());
            log.debug("根据商家id查询商家信息(POST): {}", businessId);
            return businessService.getById(businessId);
        } catch (Exception e) {
            log.error("处理getBusinessById请求出错: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 根据商家id查询商家信息 (POST方法，处理表单数据)
     */
    @PostMapping(value = "/getBusinessById", consumes = "application/x-www-form-urlencoded")
    public Business getBusinessByIdForm(@RequestParam Integer businessId) {
        log.debug("根据商家id查询商家信息(表单): {}", businessId);
        return businessService.getById(businessId);
    }

    /**
     * 分页查询商家列表
     */
    @GetMapping("/page")
    public PageResult<Business> getBusinessPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String businessName,
            @RequestParam(required = false) Integer orderTypeId) {
        
        log.debug("分页查询商家列表: current={}, size={}, businessName={}, orderTypeId={}", 
                current, size, businessName, orderTypeId);
        Page<Business> page = new Page<>(current, size);
        IPage<Business> result = businessService.getBusinessPage(page, businessName, orderTypeId);
        return PageResult.fromIPage(result);
    }

    /**
     * 根据商家类型分页查询商家
     */
    @GetMapping("/page/type/{orderTypeId}")
    public PageResult<Business> getBusinessPageByOrderType(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @PathVariable Integer orderTypeId) {
        
        log.debug("根据商家类型分页查询商家: current={}, size={}, orderTypeId={}", 
                current, size, orderTypeId);
        Page<Business> page = new Page<>(current, size);
        IPage<Business> result = businessService.getBusinessPageByOrderType(page, orderTypeId);
        return PageResult.fromIPage(result);
    }
}