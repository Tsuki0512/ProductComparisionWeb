package com.example.productcomparisionweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.productcomparisionweb.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where email = #{email}")
    User findByEmail(String email);
    
    @Insert("insert into user(email, username, password) values(#{email}, #{username}, #{password})")
    int insertUser(User user);

    @Delete("delete from user where email = #{email}")
    int deleteByEmail(String email);

    @Update("update user set password = #{password} where email = #{email} and username = #{username}")
    int updatePassword(@Param("email") String email, @Param("username") String username, @Param("password") String password);

    @Update("update user set username = #{newUsername} where email = #{email}")
    int updateUsername(@Param("email") String email, @Param("newUsername") String newUsername);
    
    @Update("update user set email = #{newEmail} where email = #{oldEmail}")
    int updateEmail(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);
}
