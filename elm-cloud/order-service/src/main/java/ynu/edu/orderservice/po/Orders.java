package ynu.edu.orderservice.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ynu.edu.orderservice.dto.BusinessDTO;
import ynu.edu.orderservice.dto.DeliveryAddressDTO;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@TableName("orders") // 指定数据库表名
public class Orders {
    @TableId("order_id") // 指定主键字段
    private Integer orderId;

    @TableField("user_id") // 指定字段名
    private String userId;

    @TableField("business_id") // 指定字段名
    private Integer businessId;

    @TableField("order_date") // 指定字段名
    private String orderDate;

    @TableField("order_total") // 指定字段名
    private Double orderTotal;

    @TableField("da_id") // 指定字段名
    private Integer daId; // 送货地址编号

    @TableField("order_state") // 指定字段名
    private Integer orderState; // 订单状态（0：未支付； 1：已支付）

    // 多对一：所属商家，通过feign远程调用获取
    @TableField(exist = false) // 表示该字段在数据库表中不存在
    private BusinessDTO business;

    // 多对一：配送地址，通过feign远程调用获取
    @TableField(exist = false) // 表示该字段在数据库表中不存在
    private DeliveryAddressDTO deliveryAddress;

    // 一对多：订单明细
    @TableField(exist = false) // 表示该字段在数据库表中不存在
    private List<OrderDetailet> orderDetailetList;
} 