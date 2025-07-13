package ynu.edu.businessservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ynu.edu.businessservice.po.Business;

@Mapper
public interface BusinessMapper extends BaseMapper<Business> {
    /**
     * 分页查询商家列表
     */
    IPage<Business> selectBusinessPage(Page<Business> page, @Param("businessName") String businessName, @Param("orderTypeId") Integer orderTypeId);
    
    /**
     * 根据商家类型分页查询商家
     */
    IPage<Business> selectBusinessPageByOrderType(Page<Business> page, @Param("orderTypeId") Integer orderTypeId);
} 