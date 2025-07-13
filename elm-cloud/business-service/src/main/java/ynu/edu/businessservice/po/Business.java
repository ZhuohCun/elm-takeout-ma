package ynu.edu.businessservice.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("business") // 指定数据库表名
public class Business {
    @TableId("business_id") // 指定主键字段
    private Integer businessId;

    @TableField("business_name") // 指定字段名
    private String businessName;

    @TableField("business_address") // 指定字段名
    private String businessAddress;

    @TableField("business_explain") // 指定字段名
    private String businessExplain;

    @TableField("business_img") // 指定字段名
    private String businessImg;

    @TableField("order_type_id") // 指定字段名
    private Integer orderTypeId;

    @TableField("star_price") // 指定字段名
    private double starPrice; // 起送费

    @TableField("delivery_price") // 指定字段名
    private double deliveryPrice; // 配送费

    @TableField("remarks") // 指定字段名
    private String remarks;
} 