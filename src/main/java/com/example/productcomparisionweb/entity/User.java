package com.example.productcomparisionweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer uid;
    
    private String username;
    private String password;
    private String email;
    
    @TableField("jd_cookie")
    private String jd_cookie;
    
    @TableField("tb_cookie")
    private String tb_cookie;
    
    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJd_cookie() {
        return jd_cookie;
    }

    public void setJd_cookie(String jd_cookie) {
        this.jd_cookie = jd_cookie;
    }

    public String getTb_cookie() {
        return tb_cookie;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", jd_cookie='" + jd_cookie + '\'' +
                ", tb_cookie='" + tb_cookie + '\'' +
                '}';
    }

    public void setTb_cookie(String tb_cookie) {
        this.tb_cookie = tb_cookie;
    }
}
