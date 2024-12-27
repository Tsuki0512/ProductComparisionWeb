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
    
    @Results({
        @Result(property = "pid", column = "pid"),
        @Result(property = "productname", column = "productname"),
        @Result(property = "platform", column = "platform"),
        @Result(property = "current_price", column = "current_price"),
        @Result(property = "image_url", column = "image_url"),
        @Result(property = "barcode", column = "barcode"),
        @Result(property = "specification", column = "specification"),
        @Result(property = "historical_prices", column = "historical_prices")
    })
    @Select("SELECT DISTINCT p.* FROM product p " +
            "INNER JOIN price_tracking pt ON p.pid = pt.pid " +
            "WHERE pt.uid = #{uid} AND p.current_price IS NOT NULL")
    List<product> getTrackedProductDetails(@Param("uid") Integer uid);

    @Select("SELECT uid FROM price_tracking WHERE pid = #{pid}")
    List<Integer> getUsersByProduct(@Param("pid") Integer pid);

    @Insert("INSERT INTO price_tracking(uid, pid) VALUES(#{uid}, #{pid})")
    void trackProduct(Integer uid, Integer pid);

    @Delete("DELETE FROM price_tracking WHERE uid = #{uid} AND pid = #{pid}")
    void untrackProduct(Integer uid, Integer pid);
} 