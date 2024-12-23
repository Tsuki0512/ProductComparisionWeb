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

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;
    @GetMapping("")
    public List query(){

        List<User> list = userMapper.selectList(null);
        System.out.println(list);
        return list;
    }

    // 用户注册
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        // 检查邮箱是否已存在
        System.out.println(user);
        User existUser = userMapper.findByEmail(user.getEmail());
        if (existUser != null) {
            return Result.error("该邮箱已被注册");
        }
        
        // 插入用户数据
        try {
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
            return Result.error("用户名或邮箱不正确");
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

        // 检查新邮箱是否已被使用
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
        
        // 打印调试信息
        System.out.println("Found user: " + res);
        System.out.println("JD Cookie from DB: " + res.getJd_cookie());
        System.out.println("TB Cookie from DB: " + res.getTb_cookie());
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", res.getUsername());
        userInfo.put("email", res.getEmail());
        userInfo.put("jd_cookie", res.getJd_cookie());
        userInfo.put("tb_cookie", res.getTb_cookie());
        
        System.out.println("Response data: " + userInfo);
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
