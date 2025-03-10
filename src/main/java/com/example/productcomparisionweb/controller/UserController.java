package com.example.productcomparisionweb.controller;

import com.example.productcomparisionweb.entity.User;
import com.example.productcomparisionweb.mapper.UserMapper;
import com.example.productcomparisionweb.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.DigestUtils;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserMapper userMapper;
    @GetMapping("")
    public Result query() {
        System.out.println("query");
        log.info("Hello world");
        List<User> users = userMapper.selectList(null);
        Map<String, Object> data = new HashMap<>();
        data.put("users", users);
        return Result.success(data);
    }

    // 用户注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            // 验证用户名和密码长度
            if (user.getUsername() == null || user.getUsername().length() < 6) {
                return Result.error("用户名长度必须大于6个字符");
            }
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                return Result.error("密码长度必须大于6个字符");
            }
            
            // 检查用户名是否已存在
            if (userMapper.findByUsername(user.getUsername()) != null) {
                return Result.error("用户名已存在");
            }
            
            // 检查邮箱是否已存在
            if (userMapper.findByEmail(user.getEmail()) != null) {
                return Result.error("邮箱已被注册");
            }

            // 插入用户数据
            int result = userMapper.insertUser(user);
            if (result > 0) {
                return Result.success();
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    // 用户注销
    @DeleteMapping("/delete")
    public Result deleteUser(@RequestParam String email) {
        try {
            int result = userMapper.deleteByEmail(email);
            if (result > 0) {
                return Result.success();
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("注销失败：" + e.getMessage());
        }
    }

    // 修改密码
    @PostMapping("/reset-password")
    public Result resetPassword(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String username = params.get("username");
        String newPassword = params.get("newPassword");

        // 验证用户是否存在
        User user = userMapper.findByEmail(email);
        if (user == null || !user.getUsername().equals(username)) {
            return Result.error("用户名或邮箱���正确");
        }

        try {
            int result = userMapper.updatePassword(email, username, newPassword);
            if (result > 0) {
                return Result.success();
            } else {
                return Result.error("修改密码失败");
            }
        } catch (Exception e) {
            return Result.error("修改密码失败：" + e.getMessage());
        }
    }

    // 更新用户名
    @PutMapping("/update-username")
    public Result updateUsername(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String newUsername = params.get("newUsername");

        try {
            int result = userMapper.updateUsername(email, newUsername);
            if (result > 0) {
                return Result.success();
            } else {
                return Result.error("更新用户名失败");
            }
        } catch (Exception e) {
            return Result.error("更新用户名失败：" + e.getMessage());
        }
    }

    // 更新邮箱
    @PutMapping("/update-email")
    public Result updateEmail(@RequestBody Map<String, String> params) {
        String oldEmail = params.get("oldEmail");
        String newEmail = params.get("newEmail");

        // 检查新邮箱是否被使用
        User existUser = userMapper.findByEmail(newEmail);
        if (existUser != null) {
            return Result.error("该邮箱已被使用");
        }

        try {
            int result = userMapper.updateEmail(oldEmail, newEmail);
            if (result > 0) {
                return Result.success();
            } else {
                return Result.error("更新邮箱失败");
            }
        } catch (Exception e) {
            return Result.error("更新邮箱失败：" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        System.out.println("Received login request for username: " + user.getUsername());
        User res = userMapper.login(user.getUsername(), user.getPassword());
        if (res == null) {
            return Result.error("用户名或密码错误");
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("uid", res.getUid());
        userInfo.put("username", res.getUsername());
        userInfo.put("email", res.getEmail());
        userInfo.put("jd_cookie", res.getJd_cookie());
        userInfo.put("tb_cookie", res.getTb_cookie());
        
        return Result.success(userInfo);
    }

    @PutMapping("/update-jd-cookie")
    public Result updateJDCookie(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String jdCookie = params.get("jdCookie");
        
        if (email == null || jdCookie == null) {
            return Result.error("参数不完整");
        }

        try {
            int rows = userMapper.updateJDCookie(email, jdCookie);
            if (rows > 0) {
                return Result.success("京东 Cookie 更新成功");
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @PutMapping("/update-tb-cookie")
    public Result updateTBCookie(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String tbCookie = params.get("tbCookie");
        
        if (email == null || tbCookie == null) {
            return Result.error("参数不完整");
        }

        try {
            int rows = userMapper.updateTBCookie(email, tbCookie);
            if (rows > 0) {
                return Result.success("淘宝 Cookie 更新成功");
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}
