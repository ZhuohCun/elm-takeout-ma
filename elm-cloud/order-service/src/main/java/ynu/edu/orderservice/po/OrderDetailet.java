package ynu.edu.orderservice.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ynu.edu.orderservice.dto.FoodDTO;

@NoArgsConstructor
@Data
@AllArgsConstructor
@TableName("order_detailet") // 指定数据库表名
public class OrderDetailet {
    @TableId("od_id") // 指定主键字段
    private Integer odId;

    @TableField("order_id") // 指定字段名
    private Integer orderId;

    @TableField("food_id") // 指定字段名
    private Integer foodId;

    @TableField("quantity") // 指定字段名
    private Integer quantity;

    // 多对一：所属食品，通过feign远程调用获取
    @TableField(exist = false) // 表示该字段在数据库表中不存在
    private FoodDTO food;
} 