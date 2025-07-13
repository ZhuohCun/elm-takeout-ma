package ynu.edu.userservice.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("user") // 指定数据库表名
public class User {
    @TableId(value = "user_id", type = IdType.INPUT) // 指定主键字段和主键类型为输入
    private String userId;

    @TableField("password") // 指定字段名
    private String password;

    @TableField("user_name") // 指定字段名
    private String userName;

    @TableField("user_sex") // 指定字段名
    private Integer userSex;

    @TableField("user_img") // 指定字段名
    private String userImg;

    @TableField("del_tag") // 指定字段名
    private Integer delTag;
} 