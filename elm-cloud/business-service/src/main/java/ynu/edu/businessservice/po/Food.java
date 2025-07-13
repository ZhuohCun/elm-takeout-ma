package ynu.edu.businessservice.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("food") // 指定数据库表名
public class Food {
    @TableId("food_id") // 指定主键字段
    private Integer foodId;

    @TableField("food_name") // 指定字段名
    private String foodName;

    @TableField("food_explain") // 指定字段名
    private String foodExplain;

    @TableField("food_img") // 指定字段名
    private String foodImg;

    @TableField("food_price") // 指定字段名
    private Double foodPrice;

    @TableField("business_id") // 指定字段名
    private Integer businessId;

    @TableField("remarks") // 指定字段名
    private String remarks;
} 