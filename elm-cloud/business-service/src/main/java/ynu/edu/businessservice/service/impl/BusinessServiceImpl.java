package ynu.edu.businessservice.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ynu.edu.businessservice.mapper.BusinessMapper;
import ynu.edu.businessservice.po.Business;
import ynu.edu.businessservice.service.BusinessService;

@Service
@Slf4j
public class BusinessServiceImpl extends ServiceImpl<BusinessMapper, Business> implements BusinessService {
    
    @Autowired
    private BusinessMapper businessMapper;
    
    @Override
    public IPage<Business> getBusinessPage(Page<Business> page, String businessName, Integer orderTypeId) {
        log.debug("分页查询商家列表: page={}, businessName={}, orderTypeId={}", page, businessName, orderTypeId);
        return businessMapper.selectBusinessPage(page, businessName, orderTypeId);
    }
    
    @Override
    public IPage<Business> getBusinessPageByOrderType(Page<Business> page, Integer orderTypeId) {
        log.debug("根据商家类型分页查询商家: page={}, orderTypeId={}", page, orderTypeId);
        return businessMapper.selectBusinessPageByOrderType(page, orderTypeId);
    }
} 