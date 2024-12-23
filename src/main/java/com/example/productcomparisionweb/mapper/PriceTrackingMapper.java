package com.example.productcomparisionweb.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import com.example.productcomparisionweb.entity.product;

import java.util.List;

@Mapper
@Repository
public interface PriceTrackingMapper {
    @Insert("INSERT INTO price_tracking (uid, pid) VALUES (#{uid}, #{pid})")
    int addTracking(@Param("uid") Integer uid, @Param("pid") Integer pid);

    @Delete("DELETE FROM price_tracking WHERE uid = #{uid} AND pid = #{pid}")
    int removeTracking(@Param("uid") Integer uid, @Param("pid") Integer pid);

    @Select("SELECT pid FROM price_tracking WHERE uid = #{uid}")
    List<Integer> getTrackedProducts(@Param("uid") Integer uid);

    @Select("SELECT COUNT(*) FROM price_tracking WHERE uid = #{uid} AND pid = #{pid}")
    int isProductTracked(@Param("uid") Integer uid, @Param("pid") Integer pid);
    
    @Select("SELECT p.* FROM product p " +
            "JOIN price_tracking pt ON p.pid = pt.pid " +
            "WHERE pt.uid = #{uid}")
    List<product> getTrackedProductDetails(@Param("uid") Integer uid);
} 