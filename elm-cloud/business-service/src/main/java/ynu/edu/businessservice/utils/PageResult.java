package ynu.edu.businessservice.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装对象
 */
@Data
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 每页记录数
     */
    private long size;
    
    /**
     * 当前页码
     */
    private long current;
    
    /**
     * 总页数
     */
    private long pages;
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 是否有下一页
     */
    private boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private boolean hasPrevious;
    
    public PageResult() {
    }
    
    public PageResult(IPage<T> page) {
        this.total = page.getTotal();
        this.size = page.getSize();
        this.current = page.getCurrent();
        this.pages = page.getPages();
        this.records = page.getRecords();
        this.hasNext = this.current < this.pages;
        this.hasPrevious = this.current > 1;
    }
    
    /**
     * 将MyBatis-Plus分页结果转为通用分页结果
     */
    public static <T> PageResult<T> fromIPage(IPage<T> page) {
        return new PageResult<>(page);
    }
} 