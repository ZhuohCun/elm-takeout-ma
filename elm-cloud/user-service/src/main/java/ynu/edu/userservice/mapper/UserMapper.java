package ynu.edu.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ynu.edu.userservice.po.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
} 