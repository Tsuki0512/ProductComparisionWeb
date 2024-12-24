package com.example.productcomparisionweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.productcomparisionweb.entity.product;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface ProductMapper {
    @Select({
        "<script>",
        "SELECT " +
        "  pid, " +
        "  productname, " +
        "  platform, " +
        "  CAST(current_price AS DECIMAL(10,2)) as current_price, " +
        "  CONVERT(image_url USING utf8) as image_url, " +
        "  barcode, " +
        "  specification, " +
        "  historical_prices, " +
        "  category " +
        "FROM product",
        "WHERE productname LIKE CONCAT('%', #{keyword}, '%')",
        "<if test='platform != null and platform != \"all\"'>",
        "  AND platform = #{platform}",
        "</if>",
        "AND current_price > 0",
        "AND image_url != ''",
        "</script>"
    })
    @Results({
        @Result(property = "pid", column = "pid"),
        @Result(property = "productname", column = "productname"),
        @Result(property = "platform", column = "platform"),
        @Result(property = "current_price", column = "current_price"),
        @Result(property = "image_url", column = "image_url"),
        @Result(property = "barcode", column = "barcode"),
        @Result(property = "specification", column = "specification"),
        @Result(property = "historical_prices", column = "historical_prices"),
        @Result(property = "category", column = "category")
    })
    List<product> searchByKeyword(@Param("keyword") String keyword, @Param("platform") String platform);

    @Select({
        "SELECT * FROM product",
        "WHERE barcode = #{barcode}",
        "AND platform = #{platform}"
    })
    @Results({
        @Result(property = "pid", column = "pid"),
        @Result(property = "productname", column = "productname"),
        @Result(property = "platform", column = "platform"),
        @Result(property = "current_price", column = "current_price"),
        @Result(property = "image_url", column = "image_url"),
        @Result(property = "barcode", column = "barcode"),
        @Result(property = "specification", column = "specification"),
        @Result(property = "historical_prices", column = "historical_prices"),
        @Result(property = "category", column = "category")
    })
    product findByBarcodeAndPlatform(@Param("barcode") String barcode, @Param("platform") String platform);

    @Update("UPDATE product SET " +
            "productname = #{productname}, " +
            "current_price = #{current_price}, " +
            "image_url = #{image_url}, " +
            "specification = #{specification}, " +
            "historical_prices = #{historical_prices} " +
            "WHERE barcode = #{barcode} AND platform = #{platform}")
    int updateProduct(product product);

    @Insert("INSERT INTO product(productname, platform, current_price, image_url, barcode, historical_prices, specification) " +
            "VALUES(#{productname}, #{platform}, #{current_price}, #{image_url}, #{barcode}, #{historical_prices}, #{specification})")
    @Options(useGeneratedKeys = true, keyProperty = "pid")
    int insertProduct(product product);
}
