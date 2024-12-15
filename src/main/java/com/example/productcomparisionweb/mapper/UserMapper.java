package com.example.productcomparisionweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.productcomparisionweb.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // mybatis实现：查询所有用户
    //@Select("select * from user")
    //public List<User> find();

    //@Insert("insert into user(email, username, password) values(#{email}, #{username}, #{password})")
    //public int insert(User user);
}
