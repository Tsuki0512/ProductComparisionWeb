package com.example.productcomparisionweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.productcomparisionweb.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.apache.ibatis.type.JdbcType;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user")
    @Results({
        @Result(property = "uid", column = "uid"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "password", column = "password"),
        @Result(property = "jd_cookie", column = "jd_cookie"),
        @Result(property = "tb_cookie", column = "tb_cookie")
    })
    List<User> selectList(Object o);

    @Select("SELECT * FROM user WHERE email = #{email}")
    @Results({
        @Result(property = "uid", column = "uid"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "password", column = "password"),
        @Result(property = "jd_cookie", column = "jd_cookie"),
        @Result(property = "tb_cookie", column = "tb_cookie")
    })
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

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    @Results({
        @Result(property = "uid", column = "uid"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "password", column = "password"),
        @Result(property = "jd_cookie", column = "jd_cookie"),
        @Result(property = "tb_cookie", column = "tb_cookie")
    })
    User login(@Param("username") String username, @Param("password") String password);

    @Update("UPDATE user SET jd_cookie = #{jd_cookie} WHERE email = #{email}")
    int updateJDCookie(@Param("email") String email, @Param("jd_cookie") String jdCookie);

    @Update("UPDATE user SET tb_cookie = #{tb_cookie} WHERE email = #{email}")
    int updateTBCookie(@Param("email") String email, @Param("tb_cookie") String tbCookie);
}
