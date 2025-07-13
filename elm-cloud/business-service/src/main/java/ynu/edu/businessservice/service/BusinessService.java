package ynu.edu.businessservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.businessservice.po.Business;

public interface BusinessService extends IService<Business> {
    
    /**
     * 分页查询商家列表
     */
    IPage<Business> getBusinessPage(Page<Business> page, String businessName, Integer orderTypeId);
    
    /**
     * 根据商家类型分页查询商家
     */
    IPage<Business> getBusinessPageByOrderType(Page<Business> page, Integer orderTypeId);
} 