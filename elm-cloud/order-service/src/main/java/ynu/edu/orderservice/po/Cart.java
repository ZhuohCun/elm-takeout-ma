package ynu.edu.orderservice.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ynu.edu.orderservice.dto.BusinessDTO;
import ynu.edu.orderservice.dto.FoodDTO;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("cart") // 指定数据库表名
public class Cart {
    @TableId("cart_id") // 指定主键字段
    private Integer cartId;

    @TableField("food_id") // 指定字段名
    private Integer foodId;

    @TableField("business_id") // 指定字段名
    private Integer businessId;

    @TableField("user_id") // 指定字段名
    private String userId;

    @TableField("quantity") // 指定字段名
    private Integer quantity;

    @TableField("order_id") // 指定字段名
    private Integer orderId;

    // 多对一：所属食品，通过feign远程调用获取
    @TableField(exist = false) // 表示该字段在数据库表中不存在
    private FoodDTO food;

    // 多对一：所属商家，通过feign远程调用获取
    @TableField(exist = false) // 表示该字段在数据库表中不存在
    private BusinessDTO business;
} 