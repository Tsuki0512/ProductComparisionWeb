package com.example.productcomparisionweb.controller;

import com.example.productcomparisionweb.entity.User;
import com.example.productcomparisionweb.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserMapper userMapper;
    @GetMapping("/user")
    public List query(){

        List<User> list = userMapper.selectList(null);
        System.out.println(list);
        return list;
    }

    @PostMapping("/user")
    public String save(User user){
        // todo：数据校验
        int i = userMapper.insert(user);
        if(i > 0){
            return "插入成功";
        } else {
            return "插入失败";
        }
    }
}
