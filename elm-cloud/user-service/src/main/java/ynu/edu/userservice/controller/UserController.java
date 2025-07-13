package ynu.edu.userservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ynu.edu.userservice.po.User;
import ynu.edu.userservice.service.UserService;
import ynu.edu.userservice.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        User existingUser = userService.getOne(new QueryWrapper<User>()
                .eq("user_id", user.getUserId())
                .eq("password", user.getPassword()));
        
        Map<String, String> result = new HashMap<>();
        
        if (existingUser != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", existingUser.getUserId());
            claims.put("userName", existingUser.getUserName());

            String jwtToken = JwtUtil.generateJwt(claims);
            result.put("token", jwtToken);
            result.put("userId", existingUser.getUserId());
            result.put("success", "true");
            
            log.info("用户 {} 登录成功, JWT Token: {}", existingUser.getUserId(), jwtToken);
            return result;
        } else {
            result.put("success", "false");
            result.put("message", "用户名或密码错误");
            return result;
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        Map<String, String> result = new HashMap<>();
        
        // 检查用户是否已存在
        boolean exists = userService.count(new QueryWrapper<User>().eq("user_id", user.getUserId())) > 0;
        if (exists) {
            result.put("success", "false");
            result.put("message", "用户已存在");
            return result;
        }
        
        // 设置默认值
        if (user.getDelTag() == null) {
            user.setDelTag(1);  // 1表示有效用户
        }
        
        boolean saved = userService.save(user);
        if (saved) {
            log.info("用户 {} 注册成功", user.getUserId());
            result.put("success", "true");
            result.put("message", "注册成功");
        } else {
            result.put("success", "false");
            result.put("message", "注册失败");
        }
        
        return result;
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public User getUserInfo(@RequestParam String userId) {
        return userService.getOne(new QueryWrapper<User>().eq("user_id", userId));
    }

    /**
     * 验证用户是否存在
     */
    @GetMapping("/exists")
    public boolean userExists(@RequestParam String userId) {
        return userService.count(new QueryWrapper<User>().eq("user_id", userId)) > 0;
    }
    
    /**
     * 根据用户ID查询用户信息 (POST方法，兼容前端)
     */
    @PostMapping("/getUserById")
    public int getUserById(@RequestBody Map<String, Object> params) {
        String userId = params.get("userId").toString();
        log.debug("根据用户ID查询用户信息(POST): {}", userId);
        return (int) userService.count(new QueryWrapper<User>().eq("user_id", userId));
    }
    
    /**
     * 获取用户信息 (GET方法，兼容前端)
     */
    @GetMapping("/getUserInfo")
    public User getUserInfoGet(@RequestParam String userId) {
        log.debug("获取用户信息(GET): {}", userId);
        return userService.getOne(new QueryWrapper<User>().eq("user_id", userId));
    }
} 