package ynu.edu.addressservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ynu.edu.addressservice.po.DeliveryAddress;
import ynu.edu.addressservice.service.DeliveryAddressService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
@Slf4j
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    /**
     * 根据用户ID查询配送地址列表
     */
    @GetMapping("/user/{userId}")
    public List<DeliveryAddress> listDeliveryAddressByUserId(@PathVariable String userId) {
        log.debug("根据用户ID查询配送地址列表: userId={}", userId);
        return deliveryAddressService.listDeliveryAddressByUserId(userId);
    }
    
    /**
     * 根据用户ID查询配送地址列表（适配前端表单）
     */
    @PostMapping(value = "/user", consumes = "application/x-www-form-urlencoded")
    public List<DeliveryAddress> listDeliveryAddressByUserIdForm(@RequestParam String userId) {
        log.debug("根据用户ID查询配送地址列表(表单): userId={}", userId);
        return deliveryAddressService.listDeliveryAddressByUserId(userId);
    }

    /**
     * 根据地址ID查询配送地址
     */
    @GetMapping("/{daId}")
    public DeliveryAddress getDeliveryAddressById(@PathVariable Integer daId) {
        log.debug("根据地址ID查询配送地址: daId={}", daId);
        return deliveryAddressService.getById(daId);
    }
    
    /**
     * 根据地址ID查询配送地址（适配前端表单）
     */
    @PostMapping(value = "/getById", consumes = "application/x-www-form-urlencoded")
    public DeliveryAddress getDeliveryAddressByIdForm(@RequestParam Integer daId) {
        log.debug("根据地址ID查询配送地址(表单): daId={}", daId);
        return deliveryAddressService.getById(daId);
    }

    /**
     * 新增配送地址
     */
    @PostMapping
    public Map<String, Object> saveDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress) {
        log.debug("新增配送地址: {}", deliveryAddress);
        Map<String, Object> result = new HashMap<>();
        boolean success = deliveryAddressService.save(deliveryAddress);
        result.put("success", success);
        result.put("message", success ? "添加成功" : "添加失败");
        if (success) {
            result.put("daId", deliveryAddress.getDaId());
        }
        return result;
    }
    
    /**
     * 新增配送地址（适配前端表单）
     */
    @PostMapping(value = "/save", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> saveDeliveryAddressForm(DeliveryAddress deliveryAddress) {
        log.debug("新增配送地址(表单): {}", deliveryAddress);
        return saveDeliveryAddress(deliveryAddress);
    }

    /**
     * 修改配送地址
     */
    @PutMapping
    public Map<String, Object> updateDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress) {
        log.debug("修改配送地址: {}", deliveryAddress);
        Map<String, Object> result = new HashMap<>();
        boolean success = deliveryAddressService.updateById(deliveryAddress);
        result.put("success", success);
        result.put("message", success ? "修改成功" : "修改失败");
        return result;
    }
    
    /**
     * 修改配送地址（适配前端表单）
     */
    @PostMapping(value = "/update", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> updateDeliveryAddressForm(DeliveryAddress deliveryAddress) {
        log.debug("修改配送地址(表单): {}", deliveryAddress);
        return updateDeliveryAddress(deliveryAddress);
    }

    /**
     * 删除配送地址
     */
    @DeleteMapping("/{daId}")
    public Map<String, Object> deleteDeliveryAddress(@PathVariable Integer daId) {
        log.debug("删除配送地址: daId={}", daId);
        Map<String, Object> result = new HashMap<>();
        boolean success = deliveryAddressService.removeById(daId);
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
    
    /**
     * 删除配送地址（适配前端表单）
     */
    @PostMapping(value = "/delete", consumes = "application/x-www-form-urlencoded")
    public Map<String, Object> deleteDeliveryAddressForm(@RequestParam Integer daId) {
        log.debug("删除配送地址(表单): daId={}", daId);
        return deleteDeliveryAddress(daId);
    }
} 