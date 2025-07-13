package ynu.edu.addressservice.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("delivery_address") // 指定数据库表名
public class DeliveryAddress {
    @TableId("da_id") // 指定主键字段
    private Integer daId;

    @TableField("contact_name") // 指定字段名
    private String contactName;

    @TableField("contact_sex") // 指定字段名
    private Integer contactSex;

    @TableField("contact_tel") // 指定字段名
    private String contactTel;

    @TableField("address") // 指定字段名
    private String address;

    @TableField("user_id") // 指定字段名
    private String userId;
} 